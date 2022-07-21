package com.liren.composetest

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.liren.composetest.ui.theme.ComposeTestTheme


@Preview
@Composable
fun TestTab() {
    ComposeTestTheme {
        TabTest()
    }
}
fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    width: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
//    val currentTabWidth by animateDpAsState(
//        targetValue = currentTabPosition.width,
//        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
//    )
    val indicatorOffset by animateDpAsState(
        targetValue = (currentTabPosition.left + currentTabPosition.right - width)/2,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth().
     wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(width)
}
@Composable
fun TabTest() {
    var selected by remember { mutableStateOf(0) }
    val values = listOf("first", "second", "third")
    val icons = listOf(Icons.Default.Home, Icons.Default.Notifications, Icons.Default.Info)
    TabRow(selectedTabIndex = selected,
        backgroundColor = Color.White,
        indicator = {
            Box(modifier = Modifier
                .tabIndicatorOffset(it[selected], 5.dp)
                .padding(bottom = 3.dp)
                .height(3.dp)
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(3.dp)
                )
            )
        }
    ) {
        values.forEachIndexed { index, s ->
            Tab(selected = index == selected, onClick = { selected = index }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 3.dp)
                    ) {
                    Text(text = s)
                    Icon(imageVector = icons[index], contentDescription = null)
                }
            }
        }
    }
}