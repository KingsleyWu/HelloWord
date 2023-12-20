package com.kingsley.compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)
//
//var LocalSkinColors = staticCompositionLocalOf {
//    SkinColors(
//        id = 0,
//        isClassic = 1,
//        background = WhiteColor,
//        mainColor = DefaultMainColor,
//        mainTextColor = LightTextColor,
//        subTextColor = LightSubTextColor,
//        subTextColor2 = LightSubTextColor2,
//        subTextColor3 = LightSubTextColor3,
//        iconTextColor = IconTextColor,
//        tipColor = LightTipColor,
//        lineColor = LightLineColor,
//        linkBgColor = LightLinkBgColor,
//        isLight = true
//    )
//}

@Stable
class SkinColors(
    /** 主题id */
    id: Int,
    /** 是否是經典模塊，0代表不是，1代表是 */
    isClassic: Int,
    /** 背景色 */
    background: Color,
    /** 主題色  */
    mainColor: Color,
    /** 亮色文本强调的颜色 0xFF333333, 0xFFFFFFFF */
    mainTextColor: Color,
    /** 亮色文本提示顔色 0xFF666666, 0x99FFFFFF */
    subTextColor: Color,
    /** 亮色文本提示顔色2 0xFF999999, 0x66FFFFFF */
    subTextColor2: Color,
    /** 亮色文本提示顔色3 0xFFCCCCCC, 0x33FFFFFF */
    subTextColor3: Color,
    /** 頁面背景圖片url */
    pageBackgroundUrl: String? = null,
    /** tab欄背景圖片url */
    tabBackgroundUrl: String? = null,
    /** 個人頁面背景墻底部的顏色 */
    personalBackgroundColor: Color = background,
    /** 主題tab 未选中的 icon圖片的url數組 */
    unselectIconUrls: List<String>? = null,
    /** 主題tab 选中的 icon圖片的url數組  */
    selectedIconUrls: List<String>? = null,
    /** 主題的文字顏色， LIGHT 為亮色， DARK 為暗色， null 為默認 */
    fontColor: String? = null,
    /** 喜歡，評論，分享 icon 的平常字體顏色 */
    iconTextColor: Color,
    /** 提示颜色，如广告，本周热门，晒卡人物等 ，默认 0xfff5f5f5, 暗黑模式 0x26fff7f0, 暗色主题 0x19000000 ， 亮色主题 0x19ffffff */
    tipColor: Color,
    /** line顔色 ，默认 0xFFEBEBEB, 暗黑模式 0x1AFFFFFF, 暗色主题 0x1A000000 ， 亮色主题 0x1AFFFFFF */
    lineColor: Color,
    /** linkBg 顔色 ，默认 0xFFF5F5F5, 暗黑模式 0xFF222426, 暗色主题 0x19000000 ， 亮色主题 0x19FFFFFF */
    linkBgColor: Color,
    /** 是否是亮色 */
    isLight: Boolean = false
) {

    /**
     * 是否是常規主題，即非默認經典皮膚
     */
    var isThemeSkin = isClassic == 0

    /**
     * 是否是亮色主題，即需要字體顏色為黑色
     * 字體為深色字體
     */
    var isLightTheme = fontColor == "DARK"

    var id by mutableStateOf(id, structuralEqualityPolicy())
        internal set
    var isClassic by mutableStateOf(isClassic, structuralEqualityPolicy())
        internal set
    var background by mutableStateOf(background, structuralEqualityPolicy())
        internal set
    var mainColor by mutableStateOf(mainColor, structuralEqualityPolicy())
        internal set
    var mainTextColor by mutableStateOf(mainTextColor, structuralEqualityPolicy())
        internal set
    var subTextColor by mutableStateOf(subTextColor, structuralEqualityPolicy())
        internal set
    var subTextColor2 by mutableStateOf(subTextColor2, structuralEqualityPolicy())
        internal set
    var subTextColor3 by mutableStateOf(subTextColor3, structuralEqualityPolicy())
        internal set
    var pageBackgroundUrl by mutableStateOf(pageBackgroundUrl, structuralEqualityPolicy())
        internal set
    var tabBackgroundUrl by mutableStateOf(tabBackgroundUrl, structuralEqualityPolicy())
        internal set
    var personalBackgroundColor by mutableStateOf(personalBackgroundColor, structuralEqualityPolicy())
        internal set
    var unselectIconUrls by mutableStateOf(unselectIconUrls, structuralEqualityPolicy())
        internal set
    var selectedIconUrls by mutableStateOf(selectedIconUrls, structuralEqualityPolicy())
        internal set
    var fontColor by mutableStateOf(fontColor, structuralEqualityPolicy())
        internal set
    var iconTextColor by mutableStateOf(iconTextColor, structuralEqualityPolicy())
        internal set
    var tipColor by mutableStateOf(tipColor, structuralEqualityPolicy())
        internal set
    var lineColor by mutableStateOf(lineColor, structuralEqualityPolicy())
        internal set
    var linkBgColor by mutableStateOf(linkBgColor, structuralEqualityPolicy())
        internal set
    var isLight by mutableStateOf(isLight, structuralEqualityPolicy())
        internal set

    fun updateColorsFrom(colors: SkinColors) {
        id = colors.id
        isClassic = colors.isClassic
        background = colors.background
        mainColor = colors.mainColor
        mainTextColor = colors.mainTextColor
        subTextColor = colors.subTextColor
        subTextColor2 = colors.subTextColor2
        subTextColor3 = colors.subTextColor3
        pageBackgroundUrl = colors.pageBackgroundUrl
        tabBackgroundUrl = colors.tabBackgroundUrl
        personalBackgroundColor = colors.personalBackgroundColor
        unselectIconUrls = colors.unselectIconUrls
        selectedIconUrls = colors.selectedIconUrls
        fontColor = colors.fontColor
        iconTextColor = colors.iconTextColor
        tipColor = colors.tipColor
        lineColor = colors.lineColor
        linkBgColor = colors.linkBgColor
        isLight = colors.isLight
    }

    fun copy(
        id: Int = this.id,
        isClassic: Int = this.isClassic,
        background: Color = this.background,
        mainColor: Color = this.mainColor,
        mainTextColor: Color = this.mainTextColor,
        subTextColor: Color = this.subTextColor,
        subTextColor2: Color = this.subTextColor2,
        subTextColor3: Color = this.subTextColor3,
        pageBackgroundUrl: String? = this.pageBackgroundUrl,
        tabBackgroundUrl: String? = this.tabBackgroundUrl,
        personalBackgroundColor: Color = this.personalBackgroundColor,
        unselectIconUrls: List<String>? = this.unselectIconUrls,
        selectedIconUrls: List<String>? = this.selectedIconUrls,
        fontColor: String? = this.fontColor,
        iconTextColor: Color = this.iconTextColor,
        tipColor: Color = this.tipColor,
        lineColor: Color = this.lineColor,
        linkBgColor: Color = this.linkBgColor,
        isLight: Boolean = this.isLight
    ): SkinColors = SkinColors(
        id,
        isClassic,
        background,
        mainColor,
        mainTextColor,
        subTextColor,
        subTextColor2,
        subTextColor3,
        pageBackgroundUrl,
        tabBackgroundUrl,
        personalBackgroundColor,
        unselectIconUrls,
        selectedIconUrls,
        fontColor,
        iconTextColor,
        tipColor,
        lineColor,
        linkBgColor,
        isLight,
    )
}

// Use with eg. SkinTheme.colors.mainColor
//object SkinTheme {
//    val colors: SkinColors
//        @Composable
//        get() = LocalSkinColors.current
//}

@Composable
fun HelloWordTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
//    val background = if (darkTheme) DarkColor else WhiteColor
//    val mainColor = if (darkTheme) DefaultMainColor else GirlMainColor
//    val mainTextColor = if (darkTheme) DarkTextColor else LightTextColor
//    val subTextColor = if (darkTheme) DarkSubTextColor else LightSubTextColor
//    val subTextColor2 = if (darkTheme) DarkSubTextColor2 else LightSubTextColor2
//    val subTextColor3 = if (darkTheme) DarkSubTextColor3 else LightSubTextColor3
//    val iconTextColor = if (darkTheme) IconTextColor else IconTextColor
//    val tipColor = if (darkTheme) DarkTipColor else LightTipColor
//    val lineColor = if (darkTheme) DarkLineColor else LightLineColor
//    val linkBgColor = if (darkTheme) DarkLinkBgColor else LightLinkBgColor
//    val isLight = if (darkTheme) 0 else 1
//    val skinColors = SkinColors(
//        id = if (darkTheme) 0 else 1,
//        isClassic = 1,
//        background = background,
//        mainColor = mainColor,
//        mainTextColor = mainTextColor,
//        subTextColor = subTextColor,
//        subTextColor2 = subTextColor2,
//        subTextColor3 = subTextColor3,
//        iconTextColor = iconTextColor,
//        tipColor = tipColor,
//        lineColor = lineColor,
//        linkBgColor = linkBgColor,
//        isLight = isLight == 1
//    )
//
//    val rememberedSkinColors = remember {
//        // Explicitly creating a new object here so we don't mutate the initial [colors]
//        // provided, and overwrite the values set in it.
//        skinColors.copy()
//    }.apply { updateColorsFrom(skinColors) }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
//    CompositionLocalProvider(LocalSkinColors provides rememberedSkinColors) {
//
//    }
}
