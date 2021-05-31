package com.apm29.phantomcompose.ui.sample

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.EditAttributes
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.apm29.phantomcompose.ui.theme.Green600
import java.util.*

data class Todo(
    val task: String,
    var status: TodoStatus = TodoStatus.CREATED,
    val id: UUID = UUID.randomUUID()
)

enum class TodoStatus {
    CREATED, FINISHED, DELETED
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun TodoScreen(
    todoList: List<Todo>,
    currentEdit: Todo?,
    onAdd: (String) -> Unit,
    onChange: (Todo, TodoStatus) -> Unit,
    onEditTodo: (Todo) -> Unit
) {
    var value by rememberSaveable {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val onAddAction = {
        onAdd(value)
        value = ""
        keyboardController?.hide()
    }
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader (key = "Header") {
            Box(
                modifier = Modifier
                    .background(Color.White)
            ) {
                OutlinedTextField(
                    value = value,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        onAddAction()
                    }),
                    onValueChange = {
                        value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            onAddAction()
                        }) {
                            Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "add")
                        }
                    },
                    placeholder = {
                        Text(text = "输入待办事项")
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(modifier = Modifier.padding(12.dp), text = "全部:${todoList.count()}项")
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "待处理:${todoList.count { it.status === TodoStatus.CREATED }}项"
                )
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "已删除:${todoList.count { it.status === TodoStatus.DELETED }}项"
                )
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "已完成:${todoList.count { it.status === TodoStatus.FINISHED }}项"
                )
            }
        }
        items(todoList, key = { it.id }) { item ->
            TodoItems(item, currentEdit, onChange, onEditTodo)
        }

    }
}

@Stable
@Composable
inline fun TodoItems(
    item: Todo,
    currentEdit: Todo?,
    crossinline onChange: (Todo, TodoStatus) -> Unit,
    crossinline onEditTodo: (Todo) -> Unit
) {
    Card(
        elevation = 6.dp,
        modifier = Modifier.padding(3.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    onEditTodo(item)
                }
                .padding(16.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (item.status) {
                TodoStatus.CREATED -> {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "todo",
                        tint = MaterialTheme.colors.secondaryVariant
                    )
                }
                TodoStatus.FINISHED -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "todo",
                        tint = Green600
                    )
                }
                TodoStatus.DELETED -> {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "todo",
                        tint = MaterialTheme.colors.error
                    )
                }
            }


            Text(
                text = item.task,
                modifier = Modifier.weight(1f),
                textDecoration = if(item.status == TodoStatus.DELETED) TextDecoration.LineThrough else null
            )

            if (currentEdit == item) {

                IconButton(onClick = {
                    onChange(item, TodoStatus.FINISHED)
                }) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircleOutline,
                        contentDescription = "完成"
                    )
                }

                IconButton(onClick = {
                    onChange(item, TodoStatus.DELETED)
                }) {
                    Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "删除")
                }

                IconButton(onClick = {
                    onChange(item, TodoStatus.CREATED)
                }) {
                    Icon(imageVector = Icons.Outlined.Circle, contentDescription = "未完成")
                }
            }
        }
    }
}