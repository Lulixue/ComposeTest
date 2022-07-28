package com.liren.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.liren.composetest.ui.theme.ComposeTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    CustomLayout {
                        CustomViews(items = listOf("first", "second", "third"))
                    }
                }
            }
        }

        println("onCreate")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTestTheme {
        Greeting("Android")
    }
}

