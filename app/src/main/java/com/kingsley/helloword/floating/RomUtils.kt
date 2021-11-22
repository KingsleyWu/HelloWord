package com.kingsley.helloword.floating

import android.os.Build
import com.kingsley.helloword.floating.RomUtils.RomName
import com.kingsley.helloword.floating.RomUtils
import android.text.TextUtils
import androidx.annotation.StringDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * RomUtils
 */
object RomUtils {
    private const val TAG = "RomUtils"
    const val ROM_MIUI = "MIUI"
    const val ROM_EMUI = "EMUI"
    const val ROM_VIVO = "VIVO"
    const val ROM_OPPO = "OPPO"
    const val ROM_FLYME = "FLYME"
    const val ROM_SMARTISAN = "SMARTISAN"
    const val ROM_QIKU = "QIKU"
    const val ROM_LETV = "LETV"
    const val ROM_LENOVO = "LENOVO"
    const val ROM_NUBIA = "NUBIA"
    const val ROM_ZTE = "ZTE"
    const val ROM_COOLPAD = "COOLPAD"
    const val ROM_UNKNOWN = "UNKNOWN"
    private const val SYSTEM_VERSION_MIUI = "ro.miui.ui.version.name"
    private const val SYSTEM_VERSION_EMUI = "ro.build.version.emui"
    private const val SYSTEM_VERSION_VIVO = "ro.vivo.os.version"
    private const val SYSTEM_VERSION_OPPO = "ro.build.version.opporom"
    private const val SYSTEM_VERSION_FLYME = "ro.build.display.id"
    private const val SYSTEM_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val SYSTEM_VERSION_LETV = "ro.letv.eui"
    private const val SYSTEM_VERSION_LENOVO = "ro.lenovo.lvp.version"
    private fun getSystemProperty(propName: String): String {
        return SystemProperties.get(propName, null)
    }

    @get:RomName
    val romName: String
        get() {
            if (isMiuiRom) {
                return ROM_MIUI
            }
            if (isHuaweiRom) {
                return ROM_EMUI
            }
            if (isVivoRom) {
                return ROM_VIVO
            }
            if (isOppoRom) {
                return ROM_OPPO
            }
            if (isMeizuRom) {
                return ROM_FLYME
            }
            if (isSmartisanRom) {
                return ROM_SMARTISAN
            }
            if (is360Rom()) {
                return ROM_QIKU
            }
            if (isLetvRom) {
                return ROM_LETV
            }
            if (isLenovoRom) {
                return ROM_LENOVO
            }
            if (isZTERom) {
                return ROM_ZTE
            }
            return if (isCoolPadRom) {
                ROM_COOLPAD
            } else ROM_UNKNOWN
        }
    val isMiuiRom: Boolean
        get() = !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_MIUI))
    val isHuaweiRom: Boolean
        get() = !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_EMUI))
    val isVivoRom: Boolean
        get() = !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_VIVO))
    val isOppoRom: Boolean
        get() = !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_OPPO))
    val isMeizuRom: Boolean
        get() {
            val meiZuFlyMeOSFlag = getSystemProperty(SYSTEM_VERSION_FLYME)
            return !TextUtils.isEmpty(meiZuFlyMeOSFlag) && meiZuFlyMeOSFlag.toUpperCase().contains(ROM_FLYME)
        }
    val isSmartisanRom: Boolean
        get() = !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_SMARTISAN))

    fun is360Rom(): Boolean {
        val manufacturer = Build.MANUFACTURER
        return !TextUtils.isEmpty(manufacturer) && manufacturer.toUpperCase().contains(ROM_QIKU)
    }

    val isLetvRom: Boolean
        get() = !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LETV))
    val isLenovoRom: Boolean
        get() = !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LENOVO))
    val isCoolPadRom: Boolean
        get() {
            val model = Build.MODEL
            val fingerPrint = Build.FINGERPRINT
            return (!TextUtils.isEmpty(model) && model.contains(ROM_COOLPAD)
                    || !TextUtils.isEmpty(fingerPrint) && fingerPrint.contains(ROM_COOLPAD))
        }
    val isZTERom: Boolean
        get() {
            val manufacturer = Build.MANUFACTURER
            val fingerPrint = Build.FINGERPRINT
            return (!TextUtils.isEmpty(manufacturer) && (fingerPrint.contains(ROM_NUBIA) || fingerPrint.contains(ROM_ZTE))
                    || !TextUtils.isEmpty(fingerPrint) && (fingerPrint.contains(ROM_NUBIA) || fingerPrint.contains(
                ROM_ZTE
            )))
        }
    val isDomesticSpecialRom: Boolean
        get() = (isMiuiRom
                || isHuaweiRom
                || isMeizuRom
                || is360Rom()
                || isOppoRom
                || isVivoRom
                || isLetvRom
                || isZTERom
                || isLenovoRom
                || isCoolPadRom)

    @StringDef(
        ROM_MIUI,
        ROM_EMUI,
        ROM_VIVO,
        ROM_OPPO,
        ROM_FLYME,
        ROM_SMARTISAN,
        ROM_QIKU,
        ROM_LETV,
        ROM_LENOVO,
        ROM_ZTE,
        ROM_COOLPAD,
        ROM_UNKNOWN
    )
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class RomName
}