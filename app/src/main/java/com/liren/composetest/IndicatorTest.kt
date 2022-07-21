package com.liren.composetest

import android.animation.TypeConverter
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import kotlin.math.max
import kotlin.math.min

class IndicatorTest {
}

@Composable
fun Test() {
    // We want to have 30.sp distance from the top of the layout box to the baseline of the
// first line of text.
    val distanceToBaseline = 30.sp
// We convert the 30.sp value to dps, which is required for the paddingFrom API.
    val distanceToBaselineDp = with(LocalDensity.current) { distanceToBaseline.toDp() }
// The result will be a layout with 30.sp distance from the top of the layout box to the
// baseline of the first line of text.
    Text(
        text = "This is an example.",
        modifier = Modifier.paddingFrom(FirstBaseline, before = distanceToBaselineDp)
    )
}

@Preview
@Composable
fun TestLinearIndicator() {

    Column(modifier = Modifier
        .width(300.dp)
        .background(Color.White)
        .height(300.dp),
        Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val progressLinear =  remember { mutableStateOf(0.1f) }
        val value = animateFloatAsState(targetValue = progressLinear.value)
        Box(modifier = Modifier
            .background(Color.Blue, shape = RoundedCornerShape(3.dp))
            .width(200.dp)
        ) {
            Row(modifier = Modifier
                .height(5.dp)
                .fillMaxWidth(value.value)
                .background(Color.Red, shape = RoundedCornerShape(3.dp))) {}
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            progressLinear.value = min(progressLinear.value + 0.1f, 1f)
        }, modifier = Modifier
            .background(Color.White)
            .padding(0.dp, 0.dp)
            .wrapContentHeight()
            .height(IntrinsicSize.Min)
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(5.dp))
            .shadow(6.dp, shape = RoundedCornerShape(5.dp))) {
            Text("Test")
        }
        TextButton(
            onClick = {
                progressLinear.value = max(progressLinear.value - 0.1f, 0f)
            },
            modifier = Modifier
                .background(Color.White)
                .padding(0.dp)
                .wrapContentHeight()
                .height(IntrinsicSize.Min)
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(5.dp))
                ,
            elevation = ButtonDefaults.elevation(),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = "增加线性进度")
        }
        Text(text =  HtmlCompat.fromHtml("<big>Hello</big> world <b>it's me</b>", HtmlCompat.FROM_HTML_MODE_COMPACT).toAnnotatedString())
    }
}

/**
 * Converts a [Spanned] into an [AnnotatedString] trying to keep as much formatting as possible.
 *
 * Currently supports `bold`, `italic`, `underline` and `color`.
 */
fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end)
            }
            is UnderlineSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            is ForegroundColorSpan -> addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
        }
    }
}