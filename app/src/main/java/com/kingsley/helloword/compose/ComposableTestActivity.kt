package com.kingsley.helloword.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kingsley.base.activity.BaseActivity
import kotlin.random.Random

class ComposableTestActivity : BaseActivity() {
    private val mCurrentNum = mutableIntStateOf(0)

    // 这个注释打开、关闭会影响WrapperBox进行重组
//     private var mTemp = "Hello"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Row {
                    Text(
                        text = "当前数量：${mCurrentNum.intValue}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .weight(1f)
                            .colorBg()
                    )

                    WrapperBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .weight(1f),
                        onClick = {
                            mCurrentNum.intValue++
                        }
                    )
                }
                InlineSample1("${mCurrentNum.intValue}")

                Button(
                    onClick = {
                        mCurrentNum.intValue++
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "点击增加数量")
                }
            }
        }
    }

    @Composable
    private fun InlineSample1(changeText: String) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .colorBg(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Text1
            Text(text = "${randomComposeColor()} changeText=$changeText", modifier = Modifier.colorBg())

            Column(modifier = Modifier.colorBg()) {
                // Text2
                Text(text = "${randomComposeColor()} 无参数的文本", modifier = Modifier.colorBg())
            }
        }
    }

    @Composable
    private fun WrapperBox(
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        Box(
            modifier = modifier
                .clickable {
                    onClick()
                }
                .colorBg()
        )
    }
}

// 扩展的随机背景色修饰符，每次重组都会显示不同颜色
fun Modifier.colorBg() = this
    .background(
        color = randomComposeColor(),
        shape = RoundedCornerShape(4.dp)
    )
    .padding(4.dp)

fun randomComposeColor() = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))