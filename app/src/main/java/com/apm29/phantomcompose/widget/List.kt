package com.apm29.phantomcompose.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ListFooter(hasMore: Boolean, loading: Boolean, onLoadMore: suspend () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LaunchedEffect(hasMore) {
            if (!loading && hasMore) {
                onLoadMore()
            }
        }
        if (loading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "加载中...", color = Color.Gray.copy(alpha = 0.7f))
        } else {
            if (hasMore) {
                val scope = rememberCoroutineScope()
                TextButton(onClick = { scope.launch { onLoadMore() } }) {
                    Text(text = "点击加载更多")
                }
            } else {
                Text(text = "没有更多数据了~", color = Color.Gray.copy(alpha = 0.7f))
            }
        }
    }
}
