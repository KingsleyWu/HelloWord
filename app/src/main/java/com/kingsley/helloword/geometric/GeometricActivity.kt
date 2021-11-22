package com.kingsley.helloword.geometric

import android.app.AlertDialog
import android.content.Intent
import com.kingsley.helloword.R
import android.os.Bundle
import com.kingsley.helloword.geometric.DisplayActivity
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.Spinner
import android.widget.ToggleButton
import android.widget.SimpleAdapter
import com.kingsley.base.activity.BaseActivity
import java.util.ArrayList
import java.util.HashMap

class GeometricActivity : BaseActivity() {
    /**
     * 模型库对话框
     */
    private var dbDialog: AlertDialog? = null

    /**
     * 个性化对话框
     */
    private var styleDialog: AlertDialog? = null

    /**
     * 要启动activity参数载体
     */
    private var intent: Intent? = null

    /**
     * 模型库对话框字符串组
     */
    private val DBitems = arrayOf("餐桌", "蛋糕")

    /**
     * 颜色图片
     */
    private val colorImage = intArrayOf(
        R.drawable.black, R.drawable.white, R.drawable.gray,
        R.drawable.red, R.drawable.orange, R.drawable.yellow,
        R.drawable.green, R.drawable.blue, R.drawable.purple,
        R.drawable.pink, R.drawable.coffee
    )

    /**
     * 个人化对话框字符串组
     */
    private val colorString = arrayOf(
        "黑色", "白色", "灰色",
        "红色", "橙色", "黄色",
        "绿色", "蓝色", "紫色",
        "粉红色", "咖啡色"
    )

    /**
     * 颜色
     */
    private val color = intArrayOf(
        Color.BLACK, Color.WHITE, -0x808081,
        Color.RED, -0x80d9, Color.YELLOW,
        -0x8a3e80, -0xff5c18, -0x5cb65c,
        -0x5137, -0x4685a9
    )

    /**
     * 个性化数据(0背景,1线段颜色,2线段粗细,3中心颜色,4中心显示)
     * 中心显示     0：不显示  1：显示
     */
    private val styleData = intArrayOf(color[1], color[0], 1, color[0], 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        intent = Intent(this@GeometricActivity, DisplayActivity::class.java)
    }

    fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.homepage_showdemo -> intent!!.putExtra("startActivity", 1)
                R.id.homepage_designsimple -> intent!!.putExtra("startActivity", 2)
                R.id.homepage_designfont -> intent!!.putExtra("startActivity", 3)
                R.id.homepage_shapedb -> {
                    intent!!.putExtra("startActivity", 4)
                    if (dbDialog != null) {
                        dbDialog!!.show()
                    } else {
                        createShapeDBDialog()
                    }
                    return
                }
                R.id.homepage_style -> {
                    if (styleDialog != null) {
                        styleDialog!!.show()
                    } else {
                        createStyleDialog()
                    }
                    return
                }
                R.id.homepage_exit -> {
                    finish()
                    return
                }
                else -> {
                }
            }
        }
        intent!!.putExtra("styleData", styleData) //个性化数据
        startActivity(intent)
    }

    /**
     * 创建模型库对话框
     */
    private fun createShapeDBDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
        builder.setTitle("模型库")
        builder.setItems(DBitems) { dialog, which ->
            intent!!.putExtra("choiceShape", which)
            onClick(null)
        }
        dbDialog = builder.show()
    }

    /**
     * 创建个性化对话框
     */
    private fun createStyleDialog() {
        val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_style, null)
        //初始化控件
        val backgroundSpinner = view.findViewById<View>(R.id.background_spinner) as Spinner
        val lineColorSpinner = view.findViewById<View>(R.id.linecolor_spinner) as Spinner
        val centerColorSpinner = view.findViewById<View>(R.id.centercolor_spinner) as Spinner
        val lineThickSpinner = view.findViewById<View>(R.id.linethick_spinner) as Spinner
        val centerShowSpinner = view.findViewById<View>(R.id.centershow_button) as ToggleButton
        //3个颜色适配器
        val colorAdapter = SimpleAdapter(
            this,
            colorData,
            R.layout.spinner_item_color,
            arrayOf("pic", "text"),
            intArrayOf(R.id.spinner_item_color_image, R.id.spinner_item_color_text)
        )
        backgroundSpinner.adapter = colorAdapter
        lineColorSpinner.adapter = colorAdapter
        centerColorSpinner.adapter = colorAdapter
        //线段粗细适配器
        val thickAdapter = SimpleAdapter(
            this, thickData,
            R.layout.spinner_item_color, arrayOf("text"), intArrayOf(R.id.spinner_item_color_text)
        )
        lineThickSpinner.adapter = thickAdapter
        //设置默认属性
        centerShowSpinner.isChecked = true //默认显示中心
        backgroundSpinner.setSelection(1) //背景白色
        lineColorSpinner.setSelection(0) //线段黑色
        centerColorSpinner.setSelection(0) //中心黑色
        lineThickSpinner.setSelection(0) //线段宽度1
        //点击提交，读取个性化数据
        view.findViewById<View>(R.id.stylesubmit).setOnClickListener {
            styleData[0] = color[backgroundSpinner.selectedItemPosition]
            styleData[1] = color[lineColorSpinner.selectedItemPosition]
            styleData[2] = lineThickSpinner.selectedItemPosition + 1
            styleData[3] = color[centerColorSpinner.selectedItemPosition]
            styleData[4] = if (centerShowSpinner.isChecked) 1 else 0
            styleDialog!!.dismiss()
        }
        builder.setView(view)
        styleDialog = builder.show()
    }

    /**
     * 获取颜色数据
     *
     * @return
     */
    private val colorData: List<Map<String, Any?>>
        private get() {
            val dataList: MutableList<Map<String, Any?>> = ArrayList()
            for (i in colorString.indices) {
                val map: MutableMap<String, Any?> = HashMap()
                map["pic"] = colorImage[i]
                map["text"] = colorString[i]
                dataList.add(map)
            }
            return dataList
        }
    private val thickData: List<Map<String, Any?>>
        private get() {
            val dataList: MutableList<Map<String, Any?>> = ArrayList()
            for (i in 0..4) {
                val map: MutableMap<String, Any?> = HashMap()
                map["text"] = i + 1 + "px"
                dataList.add(map)
            }
            return dataList
        }
}