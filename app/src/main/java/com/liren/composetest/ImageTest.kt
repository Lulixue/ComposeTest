package com.liren.composetest

import android.annotation.SuppressLint
import android.view.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageView() {
    var scale by remember { mutableStateOf(1f) }
    var scaling by remember {
        mutableStateOf(false)
    }
    var imageSize by remember {
        mutableStateOf(Size.Zero)
    }
    var dragging by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scaleListener by remember {
        mutableStateOf(object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private var beginScale = 1f
            private var beginOffset = Offset.Zero
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                println("onScale: ${detector.scaleFactor}, ${detector.focusX}, ${detector.focusY} ")
                println("current: $scale $offset")
                scale = max(min(beginScale * detector.scaleFactor, 5f), 0.75f)

                val offsetX = (beginOffset.x / beginScale) * scale
                val offsetY = (beginOffset.y / beginScale) * scale
                offset = Offset(offsetX, offsetY)
                return false
            }

            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                println("onScaleBegin: $detector")
                scaling = true
                beginScale = scale
                beginOffset = offset
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                println("onScaleEnd: $detector")
                scaling = false
                super.onScaleEnd(detector)
            }
        })
    }
    val scaleGestureDetector by remember {
        mutableStateOf(ScaleGestureDetector(context, scaleListener))
    }

    val dragListener by remember {
        mutableStateOf(object : View.OnTouchListener {
            private var downX = 0f
            private var downY = 0f
            private var downOffset = Offset.Zero
            private val touchSlop get() = ViewConfiguration.getTouchSlop() * scale
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, e: MotionEvent): Boolean {
                when (e.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = e.x
                        downY = e.y
                        downOffset = offset
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (!scaling && e.pointerCount == 1) {

                            val new = Offset(x = e.x - downX, y = e.y - downY)
                            if (sqrt(new.x * new.x + new.y * new.y) >= touchSlop) {
                                dragging = true
                            }
                            if (dragging) {
                                offset = downOffset + new
                                println("on drag $offset")
                            }
                        }
                    }
                    MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> {
                        // 有手指的切换
                        downX = e.x
                        downY = e.y
                        scaling = true
                    }
                }
                return true
            }

        })
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.mengdian),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter {
                    println("event ${MotionEvent.actionToString(it.actionMasked)}")
                    when (it.actionMasked) {
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            dragging = false
                            scaling = false
                        }
                    }
                    scaleGestureDetector.onTouchEvent(it)
                    dragListener.onTouch(null, it)
                    true
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ).background(Color.Blue)
                .onGloballyPositioned {
                    if (imageSize.isEmpty()) {
                        imageSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
                    }
                }
                .clipToBounds()
            ,

        )
    }
}