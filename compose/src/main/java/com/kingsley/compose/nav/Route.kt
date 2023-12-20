package com.kingsley.compose.nav

enum class MainNavigationRoute(
    val route: String
) {
    // 主页面
    HOME("home"),

    // 情報
    NEWS("news"),

    // 遊戲
    GAMES("games"),

    // 活動
    EVENT("event"),

    // 我的
    MINE("mine")
}


enum class NavigationRoute(
    val route: String
) {
    // 歡迎頁
    SPLASH("splash"),

    //主页面
    MAIN("main"),

    //登录页面
    LOGIN("login"),
}