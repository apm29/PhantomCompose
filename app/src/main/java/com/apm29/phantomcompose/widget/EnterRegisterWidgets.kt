package com.apm29.phantomcompose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.apm29.phantomcompose.ui.theme.Green300
import com.apm29.phantomcompose.ui.theme.Orange300


@Preview
@Composable
fun PhantomTopBar(
    title: String = "标题",
    onBack: (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = Modifier,
        shape = MaterialTheme.shapes.large,
        elevation = 6.dp,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier
                    .height(48.dp)
            ) {
                if (onBack != null) {
                    Icon(
                        Icons.Filled.ChevronLeft,
                        null,
                        Modifier
                            .clickable(onClick = onBack)
                            .align(Alignment.CenterVertically)
                            .size(36.dp)
                            .padding(8.dp),
                    )
                }
                Spacer(Modifier.weight(1f))
                actions?.invoke()
            }
            Text(title, Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun LabeledTextField(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    onValueChange: ((String) -> Unit)? = null
) {
    Row(
        modifier = modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.defaultMinSize(90.dp))
        OutlinedTextField(
            value = value ?: "",
            onValueChange = {
                onValueChange?.invoke(it)
            },
            placeholder = {
                Text(
                    text = "请输入${label}", fontSize = 12.sp, modifier = Modifier
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(0.dp),
            singleLine = true,
        )
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.15f),
    thickness: Dp = 1.dp,
    topIndent: Dp = 0.dp
) {
    val indentMod = if (topIndent.value != 0f) {
        Modifier.padding(top = topIndent)
    } else {
        Modifier
    }
    Box(
        modifier
            .then(indentMod)
            .fillMaxHeight()
            .width(thickness)
            .background(color = color)
    )
}


@Composable
inline fun <reified T, reified R> ItemPicker(
    label: String,
    value: R?,
    items: Array<T>,
    modifier: Modifier = Modifier,
    crossinline getItemString: (T?) -> String = {
        it?.toString() ?: "请选择$label"
    },
    crossinline getItemValue: (T) -> R? = {
        if (it is R) {
            it
        } else {
            null
        }
    },
    crossinline onValueChange: ((R?) -> Unit)
) {
    val openDialog = remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .padding(0.dp, 0.dp, 0.dp, 5.dp)
            .clickable {
                openDialog.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.defaultMinSize(90.dp))
        Text(
            text = getItemString(
                items.find {
                    getItemValue(it) == value
                }
            ),
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            color = Orange300
        )
        Icon(
            Icons.Filled.ChevronRight,
            null,
            Modifier
                .align(Alignment.CenterVertically)
                .size(36.dp)
                .padding(8.dp),
        )
    }
    if (openDialog.value) {
        Dialog(
            onDismissRequest = {
                // 当用户点击对话框以外的地方或者按下系统返回键将会执行的代码
                openDialog.value = false
            },
        ) {
            Card(backgroundColor = Color.White, modifier = Modifier.padding(vertical = 32.dp)) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp)
                ) {
                    items(items.size) { index ->
                        val selectable = items[index]
                        Row(
                            modifier = Modifier
                                .defaultMinSize(120.dp)
                                .clickable {
                                    onValueChange(getItemValue(selectable))
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = value == getItemValue(selectable),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(getItemString(selectable))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CameraImage() {
    Box(
        modifier = Modifier
            .width(80.dp)
            .aspectRatio(0.6F)
            .padding(6.dp)
            .background(Green300),
    ) {
        Icon(
            imageVector = Icons.Filled.Camera,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center),
            tint = Color.White
        )
    }
}