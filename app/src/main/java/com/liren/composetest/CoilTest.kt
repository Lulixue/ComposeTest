package com.liren.composetest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.liren.composetest.ui.theme.ComposeTestTheme



@Preview
@Composable
fun TextItalic() {
    MaterialTheme {
        Surface(color = Color.White) {
            Text("I Need Italic", fontStyle = FontStyle.Italic)
        }
    }
}