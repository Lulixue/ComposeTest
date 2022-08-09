package com.liren.composetest

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SidebarIndexer(
    shortItems: List<String>,
    normalItems: List<String> = shortItems,
    onSelect: (Int) -> Unit,
    fontSize: TextUnit = 12.sp
) {
    var animate by remember { mutableStateOf(false) }
    var itemHeight by remember {
        mutableStateOf(0)
    }
    val size = shortItems.size
    var selectIndex by remember {
        mutableStateOf(-1)
    }
    val coroutine = rememberCoroutineScope()
    var previousJob by remember {
        mutableStateOf<Job?>(null)
    }
    Row(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = animate && selectIndex > -1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LaunchedEffect(animate) {
                    delay(100)
                    animate = false
                }
                if (selectIndex > -1) {
                    val item = normalItems[selectIndex]
                    Box(modifier = Modifier
                        .wrapContentSize()
                        .widthIn(min = 100.dp)
                        .background(Color.Green, shape = RoundedCornerShape(5.dp))
                        .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item,
                            fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier
                .wrapContentSize()
                .pointerInteropFilter { e ->
                    if (e.actionMasked == MotionEvent.ACTION_DOWN || e.actionMasked == MotionEvent.ACTION_MOVE) {
                        if (itemHeight > 0) {
                            previousJob?.cancel()
                            val newItem = (e.y / itemHeight).toInt()
                            if (newItem < size && newItem != selectIndex) {
                                selectIndex = newItem
                                onSelect(selectIndex)
                                animate = true
                            }
                        }
                    } else {
                        if (animate) {
                            previousJob = coroutine.launch(Dispatchers.IO) {
                                delay(200)
                                selectIndex = -1
                            }
                        }
                    }
                    true
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                shortItems.forEach {
                    Text(text = it, fontSize = fontSize, modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .padding(horizontal = 5.dp)
                        .onGloballyPositioned { lc ->
                            itemHeight = lc.size.height
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewSidebarIndexer() {
    val items = mutableListOf<String>().apply {
        for (i in 'A'..'Z') {
            add(i.toString())
        }
    }
    MaterialTheme {
        val state = rememberLazyListState()
        val coroutine = rememberCoroutineScope()
        Box(Modifier.background(Color.White)) {
            LazyColumn(state = state) {
                items.forEach {
                    stickyHeader {
                        Row(modifier = Modifier.background(Color.LightGray).fillMaxWidth()) {
                            Text(it)
                        }
                    }
                    items(items.size) {
                        Text(it.toString())
                    }
                }
            }
            SidebarIndexer(items, items, onSelect = {
                coroutine.launch {
                    state.scrollToItem(it *26)
                }
            })
        }

    }
}