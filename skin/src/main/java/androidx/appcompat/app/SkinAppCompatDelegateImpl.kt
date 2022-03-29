package androidx.appcompat.app

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.view.LayoutInflaterCompat
import com.kingsley.skin.SkinElement
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinElementAttrFactory
import com.kingsley.skin.listener.OnSkinChangedListener
import com.kingsley.skin.util.L
import java.lang.ref.WeakReference
import java.util.*


/**
 * @author Kingsley
 * Created on 2021/7/1.
 */
class SkinAppCompatDelegateImpl private constructor(
    val context: Context,
    private val delegateImpl: AppCompatDelegate
) : AppCompatDelegate(), LayoutInflater.Factory2  {

    /**
     * Store the view item that need skin changing in the activity
     */
    internal val mSkinItems: MutableMap<View, SkinElement> = mutableMapOf()

    override fun getSupportActionBar() = delegateImpl.supportActionBar

    override fun setSupportActionBar(toolbar: Toolbar?) {
        delegateImpl.setSupportActionBar(toolbar)
    }

    override fun getMenuInflater(): MenuInflater = delegateImpl.menuInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        delegateImpl.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        delegateImpl.onPostCreate(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        delegateImpl.onConfigurationChanged(newConfig)
    }

    override fun onStart() {
        delegateImpl.onStart()
    }

    override fun onStop() {
        delegateImpl.onStop()
    }

    override fun onPostResume() {
        delegateImpl.onPostResume()
    }

    override fun <T : View?> findViewById(id: Int): T? = delegateImpl.findViewById(id)

    override fun setContentView(v: View?) {
        println("createView setContentView = $v")
        delegateImpl.setContentView(v)
    }

    override fun setContentView(resId: Int) {
        delegateImpl.setContentView(resId)
    }

    override fun setContentView(v: View?, lp: ViewGroup.LayoutParams?) {
        delegateImpl.setContentView(v, lp)
    }

    override fun addContentView(v: View?, lp: ViewGroup.LayoutParams?) {
        delegateImpl.addContentView(v, lp)
    }

    override fun setTitle(title: CharSequence?) {
        delegateImpl.setTitle(title)
    }

    override fun invalidateOptionsMenu() {
        delegateImpl.invalidateOptionsMenu()
    }

    override fun onDestroy() {
        delegateImpl.onDestroy()
    }

    override fun getDrawerToggleDelegate(): ActionBarDrawerToggle.Delegate? {
        return delegateImpl.drawerToggleDelegate
    }

    override fun requestWindowFeature(featureId: Int): Boolean {
        return delegateImpl.requestWindowFeature(featureId)
    }

    override fun hasWindowFeature(featureId: Int): Boolean {
        return delegateImpl.hasWindowFeature(featureId)
    }

    override fun startSupportActionMode(callback: ActionMode.Callback): ActionMode? {
        return delegateImpl.startSupportActionMode(callback)
    }

    override fun installViewFactory() {
        println("createView installViewFactory")
        val layoutInflater = LayoutInflater.from(context)
        println("createView installViewFactory layoutInflater = $layoutInflater")
        if (layoutInflater.factory == null) {
            LayoutInflaterCompat.setFactory2(layoutInflater, this)
        }
        println("createView installViewFactory layoutInflater2 = ${LayoutInflater.from(context)}")
    }

    override fun createView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {

        // 先收集属性，看是否有换肤支持的属性
        val skinAttrs = parseSkinAttr(context, attrs)
        println("createView 1111 = $parent name = $name ")
        var createView = delegateImpl.createView(parent, name, context, attrs)
        println("createView 2222 = $createView")
        if (createView == null) {
            if (-1 == name.indexOf('.')) {
                // 系统自带的widget
                if ("View" == name || "ViewStub" == name) {
                    createView = createView(context, name, "android.view.", attrs)
                }
                if (createView == null) {
                    createView = createView(context, name, "android.widget.", attrs)
                }
                if (createView == null) {
                    createView = createView(context, name, "android.webkit.", attrs)
                }
            } else {
                // 自定義的 View
                createView = createView(context, name, null, attrs)
            }
            //createView = mSkinLayoutInflater.onCreateView(context, parent, name, attrs)
            println("createView 3333 = $createView")
        }
        // 看是否有换肤支持的属性，如果没有，则不拦截
        if (createView != null && skinAttrs.isNotEmpty()) {
            mSkinItems[createView] = SkinElement(createView, skinAttrs).apply { initApply() }
        }
        return createView
    }

    private fun createView(context: Context, name: String, prefix: String?, attrs: AttributeSet): View? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            LayoutInflater.from(context)?.createView(context, name, prefix, attrs)
        } else {
            LayoutInflater.from(context)?.createView(name, prefix, attrs)
        }
    }

    override fun setHandleNativeActionModesEnabled(enabled: Boolean) {
        delegateImpl.isHandleNativeActionModesEnabled = enabled
    }

    override fun isHandleNativeActionModesEnabled(): Boolean {
       return delegateImpl.isHandleNativeActionModesEnabled
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        delegateImpl.onSaveInstanceState(outState)
    }

    override fun applyDayNight(): Boolean {
        return delegateImpl.applyDayNight()
    }

    override fun setLocalNightMode(mode: Int) {
        delegateImpl.localNightMode = mode
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return createView(parent, name, context, attrs)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
       return createView(null, name, context, attrs)
    }

    /**
     * 收集属性是否有换肤支持的属性
     */
    private fun parseSkinAttr(context: Context, attrs: AttributeSet): MutableList<SkinElementAttr> {
        val viewAttrs = mutableListOf<SkinElementAttr>()
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
                    val skinAttr = SkinElementAttrFactory.createSkinAttr(attrName, id, entryName, typeName)
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
        if (context is OnSkinChangedListener) {
            context.onSkinChanged()
        }
    }

    fun clean() {
        mSkinItems.forEach {
            it.value.clean()
        }
        mSkinItems.clear()
    }

    companion object {
        private val sDelegateMap: MutableMap<Activity, WeakReference<AppCompatDelegate?>?> = WeakHashMap<Activity, WeakReference<AppCompatDelegate?>?>()

        operator fun get(activity: Activity, appCompatDelegate: AppCompatDelegate): AppCompatDelegate {
            var delegate = sDelegateMap[activity]?.get()
            if (delegate == null) {
                delegate = SkinAppCompatDelegateImpl(activity, appCompatDelegate)
                sDelegateMap[activity] = WeakReference(delegate)
            }
            return delegate
        }

        fun remove(activity: Activity) {
            sDelegateMap.remove(activity)
        }
    }
}
