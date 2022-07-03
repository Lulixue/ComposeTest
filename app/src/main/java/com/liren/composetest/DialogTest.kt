package com.liren.composetest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.liren.composetest.ui.theme.AppColors
import com.liren.composetest.ui.theme.AppDimens


@Composable
@Preview
fun PreviewAppDialog() {
    AppDialog(title = "提示", message = "你好吗", okTitle = "好", cancelTitle = "取消", cancel = {} )
}



@Composable
fun AppDialogButton(title: String, modifier: Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .padding(horizontal = 5.dp, vertical = 12.dp)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color.Blue)
    }
}

@Composable
fun AppDialog(
    title: String,
    message: String,
    okTitle: String,
    cancelTitle: String? = null,
    ok: () -> Unit = {},
    cancel: () -> Unit = {}) {

    val openDialog = remember {
        mutableStateOf(true)
    }
    val doOk = {
        ok()
        openDialog.value = false
    }
    val doCancel = {
        cancel()
        openDialog.value = false
    }
    if (openDialog.value) {
        AppDialogDo(title = title, message = message, okTitle = okTitle,
            cancelTitle, doOk, doCancel)
    }
}


@Composable
private fun AppDialogDo(
    title: String,
    message: String,
    okTitle: String,
    cancelTitle: String? = null,
    ok: () -> Unit = {},
    cancel: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { },
        shape = RoundedCornerShape(10.dp),
        buttons = {
            Column(
                Modifier
                    .background(Color.White)
                    .widthIn(200.dp, Dp.Infinity)
                    .padding(top = 10.dp)
            ) {
                Column(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()) {
                    Text(
                        text = title,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = message,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(AppDimens.LightDividerSize), color = AppColors.LightDivider
                )
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .background(Color.White)
                        .fillMaxWidth()
                ) {
                    cancelTitle?.also {
                        AppDialogButton(it, Modifier.weight(1f), cancel)
                        Divider(
                            Modifier
                                .fillMaxHeight()
                                .width(AppDimens.LightDividerSize),
                            color = AppColors.LightDivider
                        )
                    }
                    AppDialogButton(okTitle, Modifier.weight(1f), ok)
                }
            }
        }
    )
}

