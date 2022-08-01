package com.liren.composetest

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val list = listOf("first", "second", "third")
val map = linkedMapOf<Int, List<String>>().apply {
    repeat(15) {
        this[it] = list
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun TestLazyGrid() {
    MaterialTheme {
        Surface(color = Color.White) {
            Column {
                LazyVerticalGrid(columns = GridCells.Adaptive(50.dp), content = {
                    items(list.size) {
                        Text(list[it])
                    }
                })
                LazyColumnTest()
                VerticalGridTest()
            }

        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumnTest() {
    LazyColumn() {
        map.forEach { (t, u) ->
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                ) {
                    Text(t.toString())
                }
                Divider()
            }
            item {
                Box(modifier = Modifier.wrapContentWidth()) {
                    CustomLayout {
                        u.forEach {
                            Text(it)
                        }
                    }
                }
            }
//            items(u.size) {
//                Text(text = u[it])
//            }

        }
    }
}

@Composable
fun VerticalGridTest() {
    LazyVerticalGrid(columns = GridCells.Adaptive(50.dp), modifier = Modifier.wrapContentHeight()) {

    }
}