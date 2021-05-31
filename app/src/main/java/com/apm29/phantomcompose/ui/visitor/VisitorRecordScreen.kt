package com.apm29.phantomcompose.ui.visitor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apm29.phantomcompose.model.VisitRecord
import com.apm29.phantomcompose.widget.ListFooter
import com.apm29.phantomcompose.widget.PhantomTopBar

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun VisitorRecordScreen(
    records: List<VisitRecord>,
    hasMore: Boolean,
    loading: Boolean,
    onLoadMore: suspend () -> Unit
) {
    BottomSheetScaffold(
        sheetContent = {

        },
        sheetPeekHeight = 0.dp,
        topBar = {
            PhantomTopBar("访客记录")
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            stickyHeader {
                Row {
                    Text(
                        text = "访客", modifier = Modifier
                            .weight(1f)
                            .padding(2.dp, 4.dp)
                    )
                    Text(
                        text = "被访人", modifier = Modifier
                            .weight(1f)
                            .padding(2.dp, 4.dp)
                    )
                    Text(
                        text = "到访时间", modifier = Modifier
                            .weight(2f)
                            .padding(2.dp, 4.dp)
                    )
                    Text(
                        text = "离开时间", modifier = Modifier
                            .weight(2f)
                            .padding(2.dp, 4.dp)
                    )
                    Text(
                        text = "滞留时间", modifier = Modifier
                            .weight(2f)
                            .padding(2.dp, 4.dp)
                    )
                }
            }

            items(records) { visitRecord ->
                VisitRecordItem(visitRecord)
            }

            item {
                ListFooter(hasMore, loading, onLoadMore)
            }
        }
    }
}

@Stable
@Composable
private fun VisitRecordItem(visitRecord: VisitRecord) {
    Row(Modifier.padding(vertical = 6.dp)) {
        Text(
            text = visitRecord.name, modifier = Modifier
                .weight(1f)
                .padding(2.dp, 4.dp)
        )
        Text(
            text = visitRecord.interviewee, modifier = Modifier
                .weight(1f)
                .padding(2.dp, 4.dp)
        )
        Text(
            text = visitRecord.visitTime, modifier = Modifier
                .weight(2f)
                .padding(2.dp, 4.dp)
        )
        Text(
            text = visitRecord.leaveTime, modifier = Modifier
                .weight(2f)
                .padding(2.dp, 4.dp)
        )
        Text(
            text = visitRecord.stayDuration, modifier = Modifier
                .weight(2f)
                .padding(2.dp, 4.dp)
        )
    }
    Divider()
}