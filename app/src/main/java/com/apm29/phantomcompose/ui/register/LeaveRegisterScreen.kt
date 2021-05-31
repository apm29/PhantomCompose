package com.apm29.phantomcompose.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apm29.phantomcompose.widget.PhantomTopBar
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun LeaveRegisterScreen() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showIdScanDialog by remember {
        mutableStateOf(true)
    }
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
        topBar = {
            PhantomTopBar("离开登记")
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clickable {
                    showIdScanDialog = true
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "扫描身份证，登记离开")
        }

        if (showIdScanDialog) {
            Dialog(onDismissRequest = {
                showIdScanDialog = false
            }) {
                Card {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Filled.QrCode, contentDescription = "扫码")
                        Text(text = "扫码登记离开")
                    }
                }
            }
        }
    }
}
