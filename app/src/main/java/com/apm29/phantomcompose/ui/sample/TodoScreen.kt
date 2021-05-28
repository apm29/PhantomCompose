package com.apm29.phantomcompose.ui.sample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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

@ExperimentalFoundationApi
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, widthDp = 480, heightDp = 320)
@Composable
fun TodoScreen() {
    var value by rememberSaveable {
        mutableStateOf("")
    }
    val todoList = remember {
        mutableStateListOf(
            *((0..30).map { Todo(it.toString()) }).toTypedArray()
        )
    }
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader {
            Box(
                modifier = Modifier
                    .background(Color.White)
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            createTodo(todoList, value)
                            value = ""
                        }) {
                            Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "add")
                        }
                    },
                    placeholder = {
                        Text(text = "输入待办事项")
                    }
                )
            }
        }
        items(todoList.filter { it.status != TodoStatus.DELETED }, key = { it.id }) { item ->
            val index = todoList.indexOf(item)
            TodoItems(item, todoList, index)
        }

        item {
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
    }
}

@Composable
private fun TodoItems(
    item: Todo,
    todoList: SnapshotStateList<Todo>,
    index: Int
) {
    Card(
        elevation = 6.dp,
        modifier = Modifier.padding(3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .defaultMinSize(minHeight = 48.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (item.status) {
                TodoStatus.CREATED -> {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "todo",
                        tint = MaterialTheme.colors.secondaryVariant
                    )
                    Text(text = item.task, modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            todoList[index] = item.copy(status = TodoStatus.FINISHED)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "todo")
                    }
                }
                TodoStatus.FINISHED -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "todo",
                        tint = Green600
                    )
                    Text(text = item.task, modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            todoList[index] = item.copy(status = TodoStatus.DELETED)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "todo")
                    }
                }
                TodoStatus.DELETED -> {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "todo",
                        tint = MaterialTheme.colors.error
                    )
                    Text(text = item.task, modifier = Modifier.weight(1f))
                    TextButton(onClick = {}, enabled = false) {
                        Text(text = "已删除")
                    }
                }
            }

        }
    }
}

private fun createTodo(
    todoList: SnapshotStateList<Todo>,
    value: String
) {
    if (value.isNotBlank()) {
        todoList.add(Todo(task = value))
    }
}