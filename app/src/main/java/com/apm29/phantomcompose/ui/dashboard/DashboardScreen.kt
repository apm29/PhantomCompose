package com.apm29.phantomcompose.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.apm29.phantomcompose.route.Routes
import com.apm29.phantomcompose.ui.theme.*
import com.apm29.phantomcompose.R
import com.apm29.phantomcompose.widget.PhantomTopBar

val dashboardRows: Array<Array<RouteCard>> = arrayOf(
    arrayOf(
        RouteCard(
            "进入登记",
            Routes.EnterRegister,
            Orange300,
            R.drawable.baseline_exit_to_app_white_48dp
        ),
        RouteCard(
            "离开登记",
            Routes.LeaveRegister,
            Green300,
            R.drawable.baseline_open_in_browser_white_48dp
        ),
        RouteCard(
            "通讯录",
            Routes.Contact,
            Blue300,
            R.drawable.baseline_perm_contact_calendar_white_48dp
        )
    ),
    arrayOf(
        RouteCard(
            "访客记录",
            Routes.VisitRecords,
            Orange500,
            R.drawable.baseline_history_toggle_off_white_48dp
        ),
        RouteCard(
            "视频会议",
            Routes.VideoConf,
            Pink300,
            R.drawable.baseline_videocam_white_48dp
        ),
        RouteCard(
            "系统设置",
            Routes.Settings,
            Purple700,
            R.drawable.baseline_settings_white_48dp
        )
    )
)

data class RouteCard(
    val name: String,
    val route: String,
    val color: Color,
    val icon: Int
)

@ExperimentalMaterialApi
@Stable
@Composable
fun DashboardScreen(
    onNavigateCompose: (String) -> Unit,
    onNavigateFragment: (Int) -> Unit,
    onScanQrCode: () -> Unit,
    syncWorkerState: LiveData<MutableList<WorkInfo>>,
) {
    Scaffold(
        topBar = {
            PhantomTopBar("智能访客系统") {
                TextButton(onClick = { onNavigateFragment(R.id.face_preview_fragment) }) {
                    Text(text = "人证比对")
                }

                TextButton(onClick = onScanQrCode) {
                    Text(text = "二维码扫描")
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            DashboardContent(onNavigateCompose)
            SyncWorkerStateIndicator(syncWorkerState)
        }
    }
}

@Stable
@ExperimentalMaterialApi
@Composable
private fun DashboardContent(onNavigateCompose: (String) -> Unit) {
    Column {
        dashboardRows.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .padding(0.dp, 12.dp)
            ) {
                it.forEach { routeCard ->
                    Card(
                        backgroundColor = routeCard.color,
                        modifier = Modifier
                            .weight(1F)
                            .padding(18.dp, 18.dp)
                            .fillMaxHeight(),
                        elevation = 6.dp,
                        onClick = { onNavigateCompose(routeCard.route) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(30.dp, 18.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(id = routeCard.icon),
                                contentDescription = null // decorative element
                            )
                            Text(text = routeCard.name, color = Color.White)
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun BoxScope.SyncWorkerStateIndicator(syncWorkerState: LiveData<MutableList<WorkInfo>>) {
    val value: State<MutableList<WorkInfo>?> = syncWorkerState.observeAsState()
    Column(
        modifier = Modifier.align(Alignment.TopEnd)
    ) {
        value.value?.forEach {
                workInfo->
            Card(
                modifier = Modifier
                    .padding(6.dp),
                elevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.padding(18.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            Text(text = "进入同步队列")
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "ENQUEUED",
                                tint = Color.Gray
                            )
                        }
                        WorkInfo.State.RUNNING -> {
                            ProgressContent(workInfo)
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            Text(text = "同步成功")
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "SUCCEEDED",
                                tint = Color.Green
                            )
                        }
                        WorkInfo.State.FAILED -> {
                            Text(text = "同步失败")
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "FAILED",
                                tint = Color.Red
                            )
                        }
                        WorkInfo.State.BLOCKED -> {
                            Text(text = "等待同步")
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = "BLOCKED",
                                tint = Color.Blue
                            )
                        }
                        WorkInfo.State.CANCELLED -> {
                            Text(text = "同步取消")
                            Icon(
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = "CANCELLED",
                                tint = Color.Magenta
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ProgressContent(workInfo: WorkInfo) {
    Text(text = "正在同步")
    val progress = workInfo.progress.getInt("progress", 0) ?: 0
    CircularProgressIndicator(modifier = Modifier.size(24.dp), progress = progress / 30000F)
}