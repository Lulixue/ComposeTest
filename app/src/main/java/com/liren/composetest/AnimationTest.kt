package com.liren.composetest

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.liren.composetest.ui.theme.ComposeTestTheme

@Preview
@Composable
fun AnimationTest() {
  ComposeTestTheme {
    var state by remember { mutableStateOf(false)}

    Column(modifier = Modifier.size(300.dp)) {
      Button(onClick = { state = !state  }) {
        Text("Toggle")
      }
      Crossfade(targetState = state) {
        when (state) {
          false -> PageFirst()
          else -> PageSecond()
        }
      }
    }
  }
}

@Composable
fun PageFirst() {
  Box(modifier = Modifier
    .fillMaxSize()
    .background(Color.Cyan)
  )
}

@Composable
fun PageSecond() {
  Box(modifier = Modifier
    .fillMaxSize()
    .background(Color.Blue))
}

@Preview
@Composable
fun PreviewSlideIn() {
  ComposeTestTheme {
    Surface(color = Color.White) {
      SlideInAnimationScreen()
    }
  }
}

@Composable
fun SlideInAnimationScreen() {
  // I'm using the same duration for all animations.
  val animationTime = 300

  // This state is controlling if the second screen is being displayed or not
  var showScreen2 by remember { mutableStateOf(false) }

  // This is just to give that dark effect when the first screen is closed...
  val color = animateColorAsState(
    targetValue = if (showScreen2) Color.DarkGray else Color.Red,
    animationSpec = tween(
      durationMillis = animationTime,
      easing = LinearEasing
    )
  )
  Box(Modifier.fillMaxSize()) {
    // Both Screen1 and Screen2 are declared here...
    // Screen 1
    AnimatedVisibility(
      !showScreen2,
      modifier = Modifier.fillMaxSize(),
      enter = slideInHorizontally(
        initialOffsetX = { -300 }, // small slide 300px
        animationSpec = tween(
          durationMillis = animationTime,
          easing = LinearEasing // interpolator
        )
      ),
      exit = slideOutHorizontally(
        targetOffsetX = { -300 },
        animationSpec = tween(
          durationMillis = animationTime,
          easing = LinearEasing
        )   )
    ) {
      Box(
        Modifier
          .fillMaxSize()
          .background(color.value) // animating the color
      ) {
        Button(modifier = Modifier.align(Alignment.Center),
          onClick = {
            showScreen2 = true
          }) {
          Text(text = "Ok")
        }
      }
    }

    // Screen 2
    AnimatedVisibility(
      showScreen2,
      modifier = Modifier.fillMaxSize(),
      enter = slideInHorizontally(
        initialOffsetX = { it }, // it == fullWidth
        animationSpec = tween(
          durationMillis = animationTime,
          easing = LinearEasing
        )
      ),
      exit = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(
          durationMillis = animationTime,
          easing = LinearEasing
        )
      )
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Blue)
      ) {
        Button(modifier = Modifier.align(Alignment.Center),
          onClick = {
            showScreen2 = false
          }) {
          Text(text = "Back")
        }
      }
    }
  }
}

@Composable
fun Screen1() {

}