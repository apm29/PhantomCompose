package com.apm29.phantomcompose.ui.visitor

import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.apm29.phantomcompose.widget.PhantomTopBar

@ExperimentalMaterialApi
@Composable
fun VisitorRecordScreen() {
    BottomSheetScaffold(
        sheetContent = {

        },
        sheetPeekHeight = 0.dp,
        topBar = {
            PhantomTopBar("访客记录")
        }
    ) {

    }
}