package com.liren.composetest

import android.graphics.Point
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liren.composetest.ui.theme.ComposeTestTheme
import kotlin.math.max


@Composable
fun CustomViews(items: List<String>) {
    repeat(10) {
        items.forEachIndexed { index, item ->
            Text(item, fontSize = (index + 12).sp,
                        modifier = Modifier.background(Color.Gray))
        }
    }
}


@Composable
fun CustomLayout(contents: @Composable () -> Unit) {
    val xGap = 20.dp.value.toInt()
    val yGap = 10.dp.value.toInt()
    Layout(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)
        .background(Color.Blue), content = contents) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }
        if (placeables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }

        val coordinates = mutableListOf<Point>()
        val maxLineHeights = linkedMapOf<Int, Int>()

        var yPosition = 0
        var xPosition = 0
        var maxLineHeight = 0
        // Place children in the parent layout

        val calculate = { i: Int, placeable: Placeable ->
            var result = false
            for (j in 0..2) {
                maxLineHeight = max(placeable.height, maxLineHeight)
                if ((xPosition + placeable.width) > constraints.maxWidth) {
                    if (xPosition == 0) {
                        maxLineHeights[i] = maxLineHeight
                        coordinates.add(Point(xPosition, yPosition))
                        yPosition += maxLineHeight
                        maxLineHeight = 0
                        xPosition = 0
                        result = true
                        break
                    } else {
                        maxLineHeights[i-1] = maxLineHeight
                    }
                    yPosition += maxLineHeight
                    maxLineHeight = 0
                    xPosition = 0
                }
            }
            result
        }

        placeables.forEachIndexed { i, placeable ->
            calculate(i, placeable)
            if (!calculate(i, placeable)) {
                coordinates.add(Point(xPosition, yPosition))
                xPosition += xGap
                xPosition += placeable.width
                if (i == placeables.lastIndex) {
                    maxLineHeights[i] = maxLineHeight
                }
            }

        }

        var previous = 0
        var gap = 0
        var totalHeight = 0L

        maxLineHeights.toList().forEachIndexed { j, it ->
            for (i in previous..it.first) {
                val height = placeables[i].height
                coordinates[i].y = coordinates[i].y + (it.second - height) / 2 + gap
            }
            previous = it.first+1
            gap += yGap
            totalHeight += it.second
            totalHeight += yGap
        }


        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, (totalHeight - yGap).toInt()) {
            // Track the y co-ord we have placed children up to
            coordinates.forEachIndexed { i, coord ->
                placeables[i].placeRelative(coord.x, coord.y)
            }
        }
    }
}


@Preview
@Composable
fun TestCustomLayout() {
    ComposeTestTheme {
        Surface(color = Color.White) {
            CustomLayout {
                CustomViews(items = listOf("first", "second", "third"))
            }
        }
    }
}