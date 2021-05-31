package com.apm29.phantomcompose.ui.contact

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apm29.phantomcompose.R
import com.apm29.phantomcompose.model.Contacts
import com.apm29.phantomcompose.widget.Circle
import com.apm29.phantomcompose.widget.ListFooter
import com.apm29.phantomcompose.widget.PhantomTopBar

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ContactsScreen(
    records: List<Contacts>,
    hasMore: Boolean,
    loading: Boolean,
    onLoadMore: suspend () -> Unit
) {
    BottomSheetScaffold(
        sheetContent = {},
        sheetPeekHeight = 0.dp,
        topBar = {
            PhantomTopBar("通讯录")
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {

            items(records) { visitRecord ->
                ContactItem(visitRecord)
            }

            item {
                ListFooter(hasMore, loading, onLoadMore)
            }
        }
    }
}

@Stable
@Preview(widthDp = 480)
@Composable
private fun ContactItem(contact: Contacts = Contacts("123", "1281982182", "XXXXX")) {
    Card(
        Modifier.padding(6.dp)
    ) {
        Row(Modifier.padding(6.dp), verticalAlignment = Alignment.Top) {
            Image(
                painter = painterResource(id = R.mipmap.avatar),
                contentDescription = "头像",
                Modifier
                    .clip(
                        shape = Circle
                    )
                    .wrapContentSize()
            )
            Column(
                modifier = Modifier
                    .weight(3f)
            ) {
                Text(
                    text = contact.name,
                    modifier = Modifier
                        .padding(2.dp, 4.dp),
                    fontSize = 16.sp
                )
                Text(
                    text = contact.phone, modifier = Modifier
                        .padding(2.dp, 4.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = contact.address, modifier = Modifier
                        .padding(2.dp, 4.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}