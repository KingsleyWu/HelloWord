package com.kingsley.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kingsley.compose.nav.MainNavigationRoute
import com.kingsley.compose.nav.NavigationRoute
import com.kingsley.compose.ui.theme.HelloWordTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDark by remember { mutableStateOf(false) }
            HelloWordTheme(isDark) {
                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
                val navHostController = rememberNavController()
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val scaffoldState = rememberScaffoldState()
                Scaffold { paddingValues ->
                    NavHost(
                        navController = navHostController,
                        startDestination = NavigationRoute.MAIN.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // 首页
                        composable(NavigationRoute.MAIN.route) {
                            Log.d("wwc ", "composable 首页 NavGraphBuilder ${it.id}")

                            val homeNavHostController = rememberNavController()
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    NavHost(
                                        homeNavHostController,
                                        startDestination = MainNavigationRoute.HOME.route,
                                        modifier = Modifier.padding(bottom = 48.dp)
                                    ) {
                                        // 首页
                                        composable(MainNavigationRoute.HOME.route) {
                                            val homeLazyListState = remember { LazyListState() }

                                            val pagerState = rememberPagerState(
                                                initialPage = 0,
                                                initialPageOffsetFraction = 0f
                                            ) {
                                                2
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .statusBarsPadding()
                                                    .fillMaxSize()
                                            ) {
                                                HorizontalPager(
                                                    modifier = Modifier.fillMaxSize(),
                                                    state = pagerState
                                                ) {
                                                    LazyColumn(
                                                        Modifier.fillMaxSize(),
                                                        state = homeLazyListState,
                                                    ) {
                                                        // Add a single item
                                                        item {
                                                            Text(text = "First item")
                                                        }
                                                        // Add a single item
                                                        item {
                                                            Column(modifier = Modifier.fillMaxSize()) {
                                                                Text(text = MainNavigationRoute.HOME.route)
                                                                Button(onClick = {
                                                                    isDark = !isDark
                                                                }) {
                                                                    Text(text = "isDark $isDark!")
                                                                }
                                                            }
                                                        }

                                                        // Add 5 items
                                                        items(5) { index ->
                                                            Text(text = "Item: $index")
                                                        }

                                                        // Add another single item
                                                        item {
                                                            Text(text = "Last item")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        // 情報页面
                                        composable(MainNavigationRoute.NEWS.route) {
                                            Column {
                                                Text(text = MainNavigationRoute.NEWS.route)
                                                Button(onClick = {
                                                    isDark = !isDark
                                                }) {
                                                    Text(text = "isDark $isDark!")
                                                }
                                            }
                                        }
                                        // 遊戲页面
                                        composable(MainNavigationRoute.GAMES.route) {
                                            Column {
                                                Text(text = MainNavigationRoute.GAMES.route)
                                                Button(onClick = {
                                                    isDark = !isDark
                                                }) {
                                                    Text(text = "isDark $isDark!")
                                                }
                                            }
                                        }
                                        // 活動页面
                                        composable(MainNavigationRoute.EVENT.route) {
                                            Column {
                                                Text(text = MainNavigationRoute.EVENT.route)
                                                Button(onClick = {
                                                    isDark = !isDark
                                                }) {
                                                    Text(text = "isDark $isDark!")
                                                }
                                            }
                                        }
                                        // 我的页面
                                        composable(MainNavigationRoute.MINE.route) {
                                            Column {
                                                Text(text = MainNavigationRoute.MINE.route)
                                                Button(onClick = {
                                                    isDark = !isDark
                                                }) {
                                                    Text(text = "isDark $isDark!")
                                                }
                                            }
                                        }
                                    }
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .background(Color.Transparent)
                                        .selectableGroup(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    MainNavigationRoute.entries.forEach { route ->
                                        Text(text = route.route, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                    }
                }
//                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HelloWordTheme {
        Greeting("Android")
    }
}