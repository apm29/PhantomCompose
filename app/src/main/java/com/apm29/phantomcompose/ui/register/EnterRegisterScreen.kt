package com.apm29.phantomcompose.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Preview(widthDp = 480, heightDp = 320)
@ExperimentalMaterialApi
@Composable
fun EnterRegisterScreen() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val (gesturesEnabled, toggleGesturesEnabled) = remember { mutableStateOf(true) }
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    BottomSheetScaffold(
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sheet content")
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        scope.launch { scaffoldState.bottomSheetState.collapse() }
                    }
                ) {
                    Text("Click to collapse sheet")
                }
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = {
                scope.launch { scaffoldState.bottomSheetState.expand() }
            }) {
                Text(text = "BottomSheet")
            }
            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(onClick = { /* Handle refresh! */ }) {
                        Text("Refresh")
                    }
                    DropdownMenuItem(onClick = { /* Handle settings! */ }) {
                        Text("Settings")
                    }
                    Divider()
                    DropdownMenuItem(onClick = { /* Handle send feedback! */ }) {
                        Text("Send Feedback")
                    }
                }
            }
            Text(text = "123456")
        }
    }
}