package com.kingsley.skin

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.AttributeSet
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import com.kingsley.skin.util.L
import java.lang.reflect.Constructor


/**
 * 一个LayoutInflater Factory
 * 拦截所有XML中的View创建，检查View是否有换肤支持的属性，如果没有，则不拦截
 * Created by kingsley on 2020/9/9
 */
class SkinFactory2 : LayoutInflater.Factory2 {

    var mFactory: LayoutInflater.Factory? = null
    var mFactory2: LayoutInflater.Factory2? = null

    /**
     * Store the view item that need skin changing in the activity
     */
    private val mSkinItems: MutableMap<View, SkinElement> = mutableMapOf()

    private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
    private val sConstructorMap: MutableMap<String, Constructor<out View?>> = mutableMapOf()

    companion object {
        const val TAG = "SkinFactory2@@"
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        L.d(TAG, "onCreateView context : $context, name : $name")
        // 先收集属性，看是否有换肤支持的属性
        val skinAttrs = parseSkinAttr(context, attrs)
        var view: View? = null
        //防止与其他调用factory库冲突，例如字体、皮肤替换库，用已经设置的factory来创建view
        mFactory2?.let {
            view = it.onCreateView(parent, name, context, attrs)
            L.d(TAG, "onCreateView mFactory2 context : $context, name : $name $view")
        }

        if (view == null) {
            mFactory?.let {
                view = it.onCreateView(name, context, attrs)
                L.d(TAG, "onCreateView mFactory context : $context, name : $name $view")
            }
        }
        if (view == null) {
            view = createView(name, context, attrs)
            L.d(TAG, "onCreateView createView context : $context, name : $name $view")
        }
        if (view == null) {
            view = createViewFromTag(context, name, attrs)
            L.d(TAG, "onCreateView createViewFromTag context : $context, name : $name $view")
        }
        // 看是否有换肤支持的属性，如果没有，则不拦截
        if (view != null && skinAttrs.isNotEmpty()) {
            view?.let { mSkinItems[it] = SkinElement(it, skinAttrs).apply { initApply() } }
        }
        return view;
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    private fun createView(name: String, context: Context, attrs: AttributeSet): View? {
        var view: View? = null
        try {
            if (view == null) {
                if (-1 == name.indexOf('.')) { // 系统自带的widget
                    if ("View" == name) {
                        view = LayoutInflater.from(context).createView(name, "android.view.", attrs)
                    }
                    if (view == null) {
                        view =
                            LayoutInflater.from(context).createView(name, "android.widget.", attrs)
                    }
                    if (view == null) {
                        view =
                            LayoutInflater.from(context).createView(name, "android.webkit.", attrs)
                    }
                } else {
                    view = LayoutInflater.from(context).createView(name, null, attrs)
                }
            }
        } catch (e: Exception) {
            L.e(TAG, "createView error while create 【$name】 : ", e)
        }
        return view
    }


    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        if (TextUtils.isEmpty(name)) {
            return null
        }
        return try {
            if (-1 == name.indexOf('.')) {
                var view: View? = null
                if ("View" == name) {
                    view = createView(context, attrs, name, "android.view.")
                }
                if (view == null) {
                    view = createView(context, attrs, name, "android.widget.")
                }
                if (view == null) {
                    view = createView(context, attrs, name, "android.webkit.")
                }
                view
            } else {
                createView(context, attrs, name, null)
            }
        } catch (e: Exception) {
            L.e(TAG, "cannot create 【$name】 : ", e)
            null
        }
    }

    @Throws(InflateException::class)
    private fun createView(
        context: Context,
        attrs: AttributeSet,
        name: String,
        prefix: String?
    ): View? {
        var constructor: Constructor<out View>? = sConstructorMap[name]
        return try {
            if (constructor == null) {
                val clazz = context.classLoader.loadClass(
                    if (prefix != null) prefix + name else name
                ).asSubclass(View::class.java)
                constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap[name] = constructor
            }
            constructor?.isAccessible = true
            return constructor?.newInstance(context, attrs)
        } catch (e: Exception) {
            L.e(TAG, "cannot create 【$name】 : ", e)
            null
        }
    }

    /**
     * 收集属性是否有换肤支持的属性
     */
    private fun parseSkinAttr(context: Context, attrs: AttributeSet): ArrayList<SkinElementAttr> {
        val viewAttrs: ArrayList<SkinElementAttr> = ArrayList()
        L.d(TAG, "onCreateView : ${attrs.styleAttribute}")
        if (attrs.styleAttribute != 0) {
            val typedArray = context.obtainStyledAttributes(
                attrs.styleAttribute,
                SkinElementAttrFactory.getSkinStyle()
            )
            L.d(TAG, "onCreateView typedArray length : ${typedArray.length()}")
            L.d(TAG, "onCreateView typedArray indexCount : ${typedArray.indexCount}")
            for (i in 0 until typedArray.indexCount) {
                val index = typedArray.getIndex(i)
                val attr = typedArray.peekValue(index)
                if (attr?.resourceId != 0) {
                    try {
                        val id = attr.resourceId
                        val entryName = context.resources.getResourceEntryName(id)
                        val typeName = context.resources.getResourceTypeName(id)
                        val attrName = SkinElementAttrFactory.getStyleAttrName(index)
                        attrName?.let {
                            val skinAttr =
                                SkinElementAttrFactory.createSkinAttr(
                                    attrName,
                                    id,
                                    entryName,
                                    typeName
                                )
                            skinAttr?.let { viewAttrs.add(it) }
                        }
                    } catch (e: NumberFormatException) {
                        L.e(TAG, " parseSkinAttr error : ", e)
                    } catch (e: Resources.NotFoundException) {
                        L.e(TAG, " parseSkinAttr error : ", e)
                    }
                    L.d(TAG, "onCreateView typedArray attr : ${attr?.coerceToString()} ")
                    L.d(TAG, "onCreateView typedArray resourceId : ${attr?.resourceId}")
                    L.d(TAG, "onCreateView typedArray index : $index")
                }
            }
            typedArray.recycle()
        }
        for (i in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(i)
            val attrValue = attrs.getAttributeValue(i)
            L.d(TAG, " parseSkinAttr attrName : $attrName  attrValue : $attrValue")
            // 看属性是否是支持换肤的属性
            if (!SkinElementAttrFactory.isSupportedAttr(attrName)) {
                continue
            }
            if (attrValue.startsWith("@0")) {
                continue
            }

            // 只有@开头的才表示用了引用资源
            if (attrValue.startsWith("@")) {
                try {
                    val id = attrValue.substring(1).toInt()
                    val entryName = context.resources.getResourceEntryName(id)
                    val typeName = context.resources.getResourceTypeName(id)
                    val skinAttr =
                        SkinElementAttrFactory.createSkinAttr(attrName, id, entryName, typeName)
                    skinAttr?.let { viewAttrs.add(it) }
                } catch (e: NumberFormatException) {
                    L.e(TAG, " parseSkinAttr error : ", e)
                } catch (e: Resources.NotFoundException) {
                    L.e(TAG, " parseSkinAttr error : ", e)
                }
            }
        }
        return viewAttrs
    }

    fun applySkin() {
        mSkinItems.forEach {
            it.value.apply()
        }
    }

    fun clean() {
        mSkinItems.forEach {
            it.value.clean()
        }
        mSkinItems.clear()
    }
}