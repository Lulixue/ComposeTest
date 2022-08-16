package com.liren.composetest

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.*
import androidx.annotation.FloatRange
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


class OffsetValueAnimator(private val fromOffset: Offset, destX: Float, destY: Float, private val onOffsetChange: (Offset) -> Unit) {

    constructor(fromOffset: Offset, toOffset: Offset, onOffsetChange: (Offset) -> Unit) : this(fromOffset, toOffset.x, toOffset.y, onOffsetChange)

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
        this.duration = 200L + max(abs(destX), abs(destY)).toInt() / 10
    }

    fun isRunning() = animator.isRunning

    fun stop() {
        if (animator.isRunning) {
            animator.pause()
        }
        animationDisposed = true
    }

    fun start() {
        if (!animationDisposed) {
            animator.start()
        }
    }
}

data class OffsetRange(
    var minOffset: PointF = PointF(),
    var maxOffset: PointF = PointF(),
    var minShowOffset: PointF = PointF(),
    var maxShowOffset: PointF = PointF()
) {

    private fun offsetXOverflow(offset: Float): Boolean {
        return offset !in minOffset.x..maxOffset.x
    }
    private fun offsetYOverflow(offset: Float): Boolean {
        return offset !in minOffset.y..maxOffset.y
    }
    fun offsetOverflow(offset: Offset): Boolean {
        return offsetXOverflow(offset.x) || offsetYOverflow(offset.y)
    }

    private fun Float.clap(min: Float, max: Float): Float {
        return min(max(min, this), max)
    }

    fun getShowOffset(offset: Offset): Offset {
        val newOffsetX = if (offsetXOverflow(offset.x)) offset.x.clap(minOffset.x, maxOffset.x) else offset.x
        val newOffsetY = if (offsetYOverflow(offset.y)) offset.y.clap(minOffset.y, maxOffset.y) else offset.y
        return Offset(newOffsetX, newOffsetY)
    }
    fun getRecoveryOffset(offset: Offset): Offset {
        val newOffsetX = if (offsetXOverflow(offset.x)) offset.x.clap(minShowOffset.x, maxShowOffset.x) else offset.x
        val newOffsetY = if (offsetYOverflow(offset.y)) offset.y.clap(minShowOffset.y, maxShowOffset.y) else offset.y
        return Offset(newOffsetX, newOffsetY)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageView() {
    var range by remember { mutableStateOf(OffsetRange())}
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
    fun imageShowWidth() = parentSize.width
    fun imageShowHeight() = imageShowWidth() / imageSize.width * imageSize.height

    fun updateRange() {
        range.minOffset.x = -(imageShowWidth() * scale - parentSize.width) / 2
        range.maxOffset.x = (imageShowWidth() * scale - parentSize.width) / 2

        range.maxOffset.y = (parentSize.height - imageShowHeight()) * 0.5f * scale
        range.minOffset.y = (imageShowHeight() - parentSize.height) * 0.5f * scale

        println("min: ${range.minOffset}, max: ${range.maxOffset}")
        range.minShowOffset = PointF(range.minOffset.x - 100f, range.minOffset.y + 100f)
        range.maxShowOffset = PointF(range.maxOffset.x + 100f, range.maxOffset.y + 100f)
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
                updateRange()
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
                                val overflow = range.offsetOverflow(newOffset)
                                if (overflow) {
                                    offset = range.getShowOffset(newOffset)
                                    if (overflow) {
                                        valueAnimation = OffsetValueAnimator(offset, range.getRecoveryOffset(offset)) {
                                            offset = it
                                        }
                                    }
                                } else {
                                    offset = newOffset
                                    valueAnimation?.stop()
                                }
                                println("new offset: $newOffset")
//                                offset = newOffset
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
        .background(Color.Black)
        .onGloballyPositioned {
            if (parentSize.isEmpty()) {
                parentSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
            }
        },
        contentAlignment = Alignment.Center) {

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.mengdian)
                .allowHardware(false)
                .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                .build(),
            onState = {
                if (imageSize.isEmpty() && it.painter != null) {
                    imageSize = it.painter!!.intrinsicSize
                    updateRange()
                }
            }
        )

        Image(painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter {
//                    println("event ${MotionEvent.actionToString(it.actionMasked)}")
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
                .clipToBounds()
            ,
            contentScale = ContentScale.FillWidth
        )
    }
}