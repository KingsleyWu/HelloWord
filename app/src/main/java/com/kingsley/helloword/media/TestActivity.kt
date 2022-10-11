package com.kingsley.helloword.media

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.kingsley.base.activity.BaseActivity
import com.kingsley.common.L
import java.io.File

class TestActivity : BaseActivity(), ImageReader.OnImageAvailableListener {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mediaProjection: MediaProjection? = null
    private var mImageReader: ImageReader? = null
    private var width = 0 //屏幕寬度
    private var height = 0 //屏幕高度
    private var mMediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(this)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }
    private val SHOT_SCREEN_IMAGE_NAME = "shot_screen"

    @SuppressLint("WrongConstant")
    private var mCreateScreenCaptureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
//                mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1)
//                mImageReader?.setOnImageAvailableListener(this, null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaProjection = mediaProjectionManager?.getMediaProjection(it.resultCode, data)
                    mMediaRecorder.prepare()
                    mVirtualDisplay = mediaProjection?.createVirtualDisplay(
                        "capture", width, height, resources.displayMetrics.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mMediaRecorder.surface, null, null
                    )
                    mMediaRecorder.start()
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this))
        width = resources.displayMetrics.widthPixels
        height = resources.displayMetrics.heightPixels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val dm = resources.displayMetrics
            mMediaRecorder.apply {
                //setAudioSource(MediaRecorder.AudioSource.MIC) //音频载体
                setVideoSource(MediaRecorder.VideoSource.SURFACE) //视频载体
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //输出格式
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //音频格式
                setVideoEncoder(MediaRecorder.VideoEncoder.H264) //视频格式
                setVideoSize(dm.widthPixels, dm.heightPixels) //视频大小
                // 帧率，30是比较舒服的帧率
                setVideoFrameRate(30)
                // 比特率，不需要太高的比特率，3m就很清晰了
                setVideoEncodingBitRate(3 * 1024 * 1024)
                val savedDirec: File? = getExternalFilesDir("shotscreen")
                savedDirec?.let {
                    val saveFilePath = savedDirec.path + File.separator +"shotscreen.mp4"
                    //设置文件位置
                    setOutputFile(saveFilePath)
                }
            }
            val createScreenCaptureIntent = mediaProjectionManager?.createScreenCaptureIntent()
            createScreenCaptureIntent?.let { mCreateScreenCaptureLauncher.launch(it) }
        }
    }

    override fun onImageAvailable(reader: ImageReader?) {
        var image: Image? = null
        try {
            image = mImageReader?.acquireLatestImage()
            if (image != null) {
                //此高度和宽度似乎与ImageReader构造方法中的高和宽一致
                val iWidth = image.width
                val iHeight = image.height
                //panles的数量与图片的格式有关
                val plane = image.planes[0]
                val buffer = plane.buffer
                //计算偏移量
                val pixelStride = plane.pixelStride
                val rowStride = plane.rowStride
                val rowPadding = rowStride - pixelStride * iWidth
                var bitmap = Bitmap.createBitmap(
                    iWidth + rowPadding / pixelStride,
                    iHeight, Bitmap.Config.ARGB_8888
                )
                L.d("wwc 截圖成功 iWidth = $iWidth , iHeight = $iHeight , bytebuffer = $buffer, pixelStride = $pixelStride , rowStride = $rowStride , rowPadding =  $rowPadding , width = ${iWidth + rowPadding / pixelStride}")
                bitmap.copyPixelsFromBuffer(buffer)
                //bitmap = Bitmap.createBitmap(bitmap, 0, 0, iWidth, iHeight)
                val savedDirec: File? = getExternalFilesDir("shotscreen")
                savedDirec?.let {
                    val saveFilePath = savedDirec.path + File.separator + SHOT_SCREEN_IMAGE_NAME
                    //FileUtils.saveBitmapAsFile(File(saveFilePath), bitmap, 50)
                }
            } else {
                L.d("wwc 截圖失敗")
            }
        } catch (e: Exception) {
            L.d("wwc Exception=>$e")
        } finally {
            image?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaProjection?.stop()
            mImageReader?.setOnImageAvailableListener(null, null)
            mImageReader?.close()
            mVirtualDisplay?.release()

            mMediaRecorder.release()
        }
    }
}