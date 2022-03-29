package com.kingsley.skin

import com.kingsley.skin.attrs.*

/**
 * 在这里可以添加动态换肤的属性
 *
 */
object SkinElementAttrFactory {

    private val supportSkinAttrs = mutableMapOf<String, Class<out SkinElementAttr>>()

    private val mSkinStyle = mutableMapOf<Int, String>()

    /**
     * 添加一个换肤
     *
     * @param attrName
     * @param attrElementClazz
     *
     */
    fun registerSkinAttr(attrName: String, attrElementClazz: Class<out SkinElementAttr>): SkinElementAttrFactory {
        supportSkinAttrs[attrName] = attrElementClazz
        return this
    }

    /**
     * 添加一个换肤 style
     *
     * @param attr
     * @param attrName
     *
     */
    fun registerSkinStyle(attrName: String, attr: Int): SkinElementAttrFactory {
        mSkinStyle[attr] = attrName
        return this
    }

    init {
        registerSkinAttr("background", BackgroundAttr::class.java)
        registerSkinAttr("src", ImageSrcAttr::class.java)
        registerSkinAttr("srcCompat", ImageSrcAttr::class.java)

        registerSkinStyle("background", android.R.attr.background)
        registerSkinStyle("src", android.R.attr.src)
        registerSkinStyle("srcCompat", androidx.appcompat.R.attr.srcCompat)

        // TextView drawable
        registerSkinAttr("textColor", TextColorAttr::class.java)
        registerSkinAttr("textColorHint", TextColorAttr::class.java)

        registerSkinStyle("textColor", android.R.attr.textColor)
        registerSkinStyle("textColorHint", android.R.attr.textColorHint)

        registerSkinAttr("drawableLeft", TextViewDrawableAttr::class.java)
        registerSkinAttr("drawableStart", TextViewDrawableAttr::class.java)
        registerSkinAttr("drawableRight", TextViewDrawableAttr::class.java)
        registerSkinAttr("drawableEnd", TextViewDrawableAttr::class.java)
        registerSkinAttr("drawableTop", TextViewDrawableAttr::class.java)
        registerSkinAttr("drawableBottom", TextViewDrawableAttr::class.java)

        registerSkinStyle("drawableLeft", android.R.attr.drawableLeft)
        registerSkinStyle("drawableStart", android.R.attr.drawableStart)
        registerSkinStyle("drawableRight", android.R.attr.drawableRight)
        registerSkinStyle("drawableEnd", android.R.attr.drawableEnd)
        registerSkinStyle("drawableTop", android.R.attr.drawableTop)
        registerSkinStyle("drawableBottom", android.R.attr.drawableBottom)
    }

    fun registerTextSizeSkinAttr(): SkinElementAttrFactory {
        registerSkinAttr("textSize", TextSizeAttr::class.java)
        registerSkinStyle("textSize", android.R.attr.textSize)
        return this
    }

    fun registerPaddingSkinAttr(): SkinElementAttrFactory {
        // padding
        registerSkinAttr("padding", PaddingAttr::class.java)
        registerSkinAttr("paddingLeft", PaddingAttr::class.java)
        registerSkinAttr("paddingStart", PaddingAttr::class.java)
        registerSkinAttr("paddingRight", PaddingAttr::class.java)
        registerSkinAttr("paddingEnd", PaddingAttr::class.java)
        registerSkinAttr("paddingTop", PaddingAttr::class.java)
        registerSkinAttr("paddingBottom", PaddingAttr::class.java)

        registerSkinStyle("padding", android.R.attr.padding)
        registerSkinStyle("paddingLeft", android.R.attr.paddingLeft)
        registerSkinStyle("paddingStart", android.R.attr.paddingStart)
        registerSkinStyle("paddingRight", android.R.attr.paddingRight)
        registerSkinStyle("paddingEnd", android.R.attr.paddingEnd)
        registerSkinStyle("paddingTop", android.R.attr.paddingTop)
        registerSkinStyle("paddingBottom", android.R.attr.paddingBottom)
        return this
    }

    fun registerMarginSkinAttr(): SkinElementAttrFactory {
        // margin
        registerSkinAttr("layout_margin", MarginAttr::class.java)
        registerSkinAttr("layout_marginLeft", MarginAttr::class.java)
        registerSkinAttr("layout_marginStart", MarginAttr::class.java)
        registerSkinAttr("layout_marginRight", MarginAttr::class.java)
        registerSkinAttr("layout_marginEnd", MarginAttr::class.java)
        registerSkinAttr("layout_marginTop", MarginAttr::class.java)
        registerSkinAttr("layout_marginBottom", MarginAttr::class.java)

        registerSkinStyle("layout_margin", android.R.attr.layout_margin)
        registerSkinStyle("layout_marginLeft", android.R.attr.layout_marginLeft)
        registerSkinStyle("layout_marginStart", android.R.attr.layout_marginStart)
        registerSkinStyle("layout_marginRight", android.R.attr.layout_marginRight)
        registerSkinStyle("layout_marginEnd", android.R.attr.layout_marginEnd)
        registerSkinStyle("layout_marginTop", android.R.attr.layout_marginTop)
        registerSkinStyle("layout_marginBottom", android.R.attr.layout_marginBottom)
        return this
    }

    /**
     *  创建换肤节点
     */
    fun createSkinAttr(
        attrName: String,
        attrValueRefId: Int,
        attrValueRefName: String?,
        typeName: String?
    ): SkinElementAttr? {
        val skinAttr = supportSkinAttrs[attrName]?.newInstance()
        skinAttr?.run {
            this.attrName = attrName
            this.attrValueRefId = attrValueRefId
            this.attrValueRefName = attrValueRefName
            this.attrValueTypeName = typeName
        }
        return skinAttr
    }

    fun isSupportedAttr(attrName: String): Boolean {
        return supportSkinAttrs.keys.contains(attrName)
    }

    fun isSupportedAttr(attr: Int): Boolean {
        return mSkinStyle.keys.contains(attr)
    }

    fun getSkinStyle(): IntArray {
        return mSkinStyle.keys.toIntArray()
    }

    fun getStyleAttrName(index: Int): String? {
        return mSkinStyle[getSkinStyle()[index]]
    }

}
