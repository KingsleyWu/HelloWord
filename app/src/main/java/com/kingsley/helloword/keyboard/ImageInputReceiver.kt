package com.kingsley.helloword.keyboard

import android.content.ClipData
import android.content.ClipDescription
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.ContentInfoCompat
import androidx.core.view.OnReceiveContentListener
import com.kingsley.helloword.utils.CustomExecutors
import java.io.FileNotFoundException


class ImageInputReceiver(
    private val mAttachmentsRepo: AttachmentsRepo,
    private val mAttachmentsRecyclerViewAdapter: AttachmentsRecyclerViewAdapter
) : OnReceiveContentListener {

    override fun onReceiveContent(
        @NonNull view: View,
        @NonNull payload: ContentInfoCompat
    ): ContentInfoCompat? {
        // Split the incoming content into two groups: content URIs and everything else.
        // This way we can implement custom handling for URIs and delegate the rest.
        val (uriContent, remaining) = payload.partition { item: ClipData.Item -> item.uri != null }
        if (uriContent != null) {
            receive(view.context, uriContent)
        }
        // Return anything that we didn't handle ourselves. This preserves the default platform
        // behavior for text and anything else for which we are not implementing custom handling.
        return remaining
    }

    /**
     * Handles incoming content URIs. If the content is an image, stores it as an attachment in the
     * app's private storage. If the content is any other type, simply shows a toast with the type
     * of the content and its size in bytes.
     *
     *
     * **Important:** It is significant that we pass along the `payload`
     * object to the worker thread that will process the content, because URI permissions are tied
     * to the payload object's lifecycle. If that object is not passed along, it could be garbage
     * collected and permissions would be revoked prematurely (before we have a chance to process
     * the content).
     */
    private fun receive(@NonNull context: Context, @NonNull payload: ContentInfoCompat) {
        val applicationContext: Context = context.applicationContext
        val contentResolver: ContentResolver = applicationContext.contentResolver
        CustomExecutors.io().execute {
            val uris: List<Uri> =
                collectUris(payload.clip)
            val localUris: MutableList<Uri> = ArrayList(uris.size)
            for (uri in uris) {
                val mimeType = contentResolver.getType(uri)
                Log.i("TAG", "Processing URI: $uri (type: $mimeType)")
                if (ClipDescription.compareMimeTypes(mimeType, "image/*")) {
                    // Read the image at the given URI and write it to private storage.
                    localUris.add(mAttachmentsRepo.write(uri))
                } else {
                    showMessage(applicationContext, uri, mimeType)
                }
            }
            CustomExecutors.main().execute {
                mAttachmentsRecyclerViewAdapter.addAttachments(localUris);
                mAttachmentsRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Reads the size of the given content URI and shows a toast with the type of the content and
     * its size in bytes.
     */
    private fun showMessage(
        @NonNull applicationContext: Context,
        @NonNull uri: Uri, @NonNull mimeType: String?
    ) {
        CustomExecutors.io().execute {
            val contentResolver: ContentResolver = applicationContext.contentResolver
            val lengthBytes: Long = try {
                val fd = contentResolver.openAssetFileDescriptor(uri, "r")
                fd!!.length
            } catch (e: FileNotFoundException) {
                Log.e("TAG", "Error opening content URI: $uri", e)
                return@execute
            }
            val msg =
                "Content of type $mimeType ($lengthBytes bytes): $uri"
            Log.i("TAG", msg)
            CustomExecutors.main().execute {
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        val SUPPORTED_MIME_TYPES = arrayOf("image/*")
        private fun collectUris(clip: ClipData): List<Uri> {
            val uris: MutableList<Uri> = ArrayList(clip.itemCount)
            for (i in 0 until clip.itemCount) {
                val uri: Uri? = clip.getItemAt(i).uri
                if (uri != null) {
                    uris.add(uri)
                }
            }
            return uris
        }
    }
}
