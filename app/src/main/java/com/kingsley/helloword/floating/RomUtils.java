package com.kingsley.helloword.floating;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Vito on 2018/7/5.
 */
public class RomUtils {
    private static final String TAG = "RomUtils";

    static final String ROM_MIUI = "MIUI";
    static final String ROM_EMUI = "EMUI";
    static final String ROM_VIVO = "VIVO";
    static final String ROM_OPPO = "OPPO";
    static final String ROM_FLYME = "FLYME";
    static final String ROM_SMARTISAN = "SMARTISAN";
    static final String ROM_QIKU = "QIKU";
    static final String ROM_LETV = "LETV";
    static final String ROM_LENOVO = "LENOVO";
    static final String ROM_NUBIA = "NUBIA";
    static final String ROM_ZTE = "ZTE";
    static final String ROM_COOLPAD = "COOLPAD";
    static final String ROM_UNKNOWN = "UNKNOWN";

    @StringDef({
            ROM_MIUI, ROM_EMUI, ROM_VIVO, ROM_OPPO, ROM_FLYME,
            ROM_SMARTISAN, ROM_QIKU, ROM_LETV, ROM_LENOVO, ROM_ZTE,
            ROM_COOLPAD, ROM_UNKNOWN
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RomName {
    }

    private static final String SYSTEM_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String SYSTEM_VERSION_EMUI = "ro.build.version.emui";
    private static final String SYSTEM_VERSION_VIVO = "ro.vivo.os.version";
    private static final String SYSTEM_VERSION_OPPO = "ro.build.version.opporom";
    private static final String SYSTEM_VERSION_FLYME = "ro.build.display.id";
    private static final String SYSTEM_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String SYSTEM_VERSION_LETV = "ro.letv.eui";
    private static final String SYSTEM_VERSION_LENOVO = "ro.lenovo.lvp.version";

    private static String getSystemProperty(String propName) {
        return SystemProperties.get(propName, null);
    }

    @RomName
    public static String getRomName() {
        if (isMiuiRom()) {
            return ROM_MIUI;
        }
        if (isHuaweiRom()) {
            return ROM_EMUI;
        }
        if (isVivoRom()) {
            return ROM_VIVO;
        }
        if (isOppoRom()) {
            return ROM_OPPO;
        }
        if (isMeizuRom()) {
            return ROM_FLYME;
        }
        if (isSmartisanRom()) {
            return ROM_SMARTISAN;
        }
        if (is360Rom()) {
            return ROM_QIKU;
        }
        if (isLetvRom()) {
            return ROM_LETV;
        }
        if (isLenovoRom()) {
            return ROM_LENOVO;
        }
        if (isZTERom()) {
            return ROM_ZTE;
        }
        if (isCoolPadRom()) {
            return ROM_COOLPAD;
        }
        return ROM_UNKNOWN;
    }

    public static boolean isMiuiRom() {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_MIUI));
    }

    public static boolean isHuaweiRom() {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_EMUI));
    }

    public static boolean isVivoRom() {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_VIVO));
    }

    public static boolean isOppoRom() {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_OPPO));
    }

    public static boolean isMeizuRom() {
        String meiZuFlyMeOSFlag = getSystemProperty(SYSTEM_VERSION_FLYME);
        return !TextUtils.isEmpty(meiZuFlyMeOSFlag) && meiZuFlyMeOSFlag.toUpperCase().contains(ROM_FLYME);
    }

    public static boolean isSmartisanRom() {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_SMARTISAN));
    }

    public static boolean is360Rom() {
        String manufacturer = Build.MANUFACTURER;
        return !TextUtils.isEmpty(manufacturer) && manufacturer.toUpperCase().contains(ROM_QIKU);
    }

    public static boolean isLetvRom() {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LETV));
    }

    public static boolean isLenovoRom() {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LENOVO));
    }

    public static boolean isCoolPadRom() {
        String model = Build.MODEL;
        String fingerPrint = Build.FINGERPRINT;
        return (!TextUtils.isEmpty(model) && model.contains(ROM_COOLPAD))
                || (!TextUtils.isEmpty(fingerPrint) && fingerPrint.contains(ROM_COOLPAD));
    }

    public static boolean isZTERom() {
        String manufacturer = Build.MANUFACTURER;
        String fingerPrint = Build.FINGERPRINT;
        return (!TextUtils.isEmpty(manufacturer) && (fingerPrint.contains(ROM_NUBIA) || fingerPrint.contains(ROM_ZTE)))
                || (!TextUtils.isEmpty(fingerPrint) && (fingerPrint.contains(ROM_NUBIA) || fingerPrint.contains(ROM_ZTE)));
    }

    public static boolean isDomesticSpecialRom() {
        return RomUtils.isMiuiRom()
                || RomUtils.isHuaweiRom()
                || RomUtils.isMeizuRom()
                || RomUtils.is360Rom()
                || RomUtils.isOppoRom()
                || RomUtils.isVivoRom()
                || RomUtils.isLetvRom()
                || RomUtils.isZTERom()
                || RomUtils.isLenovoRom()
                || RomUtils.isCoolPadRom();
    }
}
