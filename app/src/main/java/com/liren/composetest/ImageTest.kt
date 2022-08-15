package com.liren.composetest

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TwoWayConverter
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


class OffsetValueAnimator(private val fromOffset: Offset, destX: Float, destY: Float, private val onOffsetChange: (Offset) -> Unit) {
    private val deltaX = destX - fromOffset.x
    private val deltaY = destY - fromOffset.y
    private var animationDisposed = false
    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
            val newOffset = Offset(fromOffset.x + deltaX * it.animatedFraction,
                fromOffset.y + deltaY * it.animatedFraction)
            println("from $fromOffset animate to $newOffset")
            onOffsetChange(newOffset)
            if (it.animatedFraction == 1f) {
                animationDisposed = true
            }
        }
        this.duration = 300L
    }

    fun isRunning() = animator.isRunning

    fun stop() {
        if (animator.isRunning) {
            animator.pause()
            animationDisposed = true
        }
    }

    fun start() {
        if (!animationDisposed) {
            animator.start()
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageView() {
    var scale by remember { mutableStateOf(1f) }
    var scaling by remember {
        mutableStateOf(false)
    }
    var parentSize by remember {
        mutableStateOf(Size.Zero)
    }
    var imageSize by remember {
        mutableStateOf(Size.Zero)
    }
    var dragging by remember {
        mutableStateOf(false)
    }
    var fromOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    var valueAnimation by remember {
        mutableStateOf<OffsetValueAnimator?>(null)
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
    fun imageShowWidth() = parentSize.width
    fun imageShowHeight() = (parentSize.width / imageSize.width) * imageSize.height
    fun getOffsetWidthLimit(percent: Float) = parentSize.width * scale * percent
    fun getOffsetHeightLimit(percent: Float) = parentSize.height * scale * percent

    val dragListener by remember {
        mutableStateOf(object : View.OnTouchListener {
            private var downX = 0f
            private var downY = 0f
            private var downOffset = Offset.Zero
            private val touchSlop get() = ViewConfiguration.getTouchSlop() * scale


            private val criticalOffsetX get() = parentSize.width *  scale * 0.5f
            private val criticalOffsetY get() = parentSize.height * (scale * 0.5f)
            private val maxAnimationOffsetX get() = parentSize.width * (scale * 0.25f)
            private val maxAnimationOffsetY get() = parentSize.height * (scale * 0.25f)
            private val offsetXRange get() = -criticalOffsetX..criticalOffsetX
            private val offsetYRange get() = -criticalOffsetY..criticalOffsetY

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, e: MotionEvent): Boolean {
                when (e.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        valueAnimation?.stop()
                        valueAnimation = null
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
                                val newOffset = downOffset + new
                                var newOffsetX = newOffset.x
                                var newOffsetY = newOffset.y
                                var overflow = false

                                if (newOffset.x !in offsetXRange) {
                                    val overflowLT = newOffset.x > criticalOffsetX
                                    overflow = true
                                    newOffsetX = if (overflowLT) min(maxAnimationOffsetX, newOffset.x) else
                                                    max(-maxAnimationOffsetX, newOffset.x)
                                }
                                if (newOffset.y !in offsetYRange) {
                                    overflow = true
                                    newOffsetY = if (newOffset.y > criticalOffsetY) min(maxAnimationOffsetY, newOffset.y)
                                                else max(-maxAnimationOffsetY, newOffset.y)

                                }

                                offset = Offset(newOffsetX, newOffsetY)
                                if (overflow) {
                                    val destOffsetX = max(min(newOffsetX, criticalOffsetX), -criticalOffsetX)
                                    val destOffsetY = max(min(newOffsetY, criticalOffsetY), -criticalOffsetY)
                                    valueAnimation = OffsetValueAnimator(offset, destOffsetX, destOffsetY) {
                                        offset = it
                                    }
                                }
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
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        valueAnimation?.also {
                            it.start()
                        }
                    }
                }
                return true
            }

        })
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned {
            if (parentSize.isEmpty()) {
                parentSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
            }
        },
        contentAlignment = Alignment.Center) {
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
                )
                .background(Color.Blue)
                .onGloballyPositioned {
                    if (imageSize.isEmpty()) {
                        imageSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
                    }
                }
                .clipToBounds()
            ,
            contentScale = ContentScale.FillWidth
        )
    }
}