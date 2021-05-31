package com.apm29.phantomcompose.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
fun DashboardScreen(navController: NavController = rememberNavController()) {
    BottomSheetScaffold(
        sheetContent = {

        },
        sheetPeekHeight = 0.dp,
        topBar = {
            PhantomTopBar("智能访客系统")
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            dashboardRows.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                        .padding(0.dp, 12.dp)
                ) {
                    it.forEach {
                        Card(
                            backgroundColor = it.color,
                            modifier = Modifier
                                .weight(1F)
                                .padding(18.dp, 18.dp)
                                .clickable {
                                    navController.navigate(it.route)
                                }
                                .fillMaxHeight(),
                            elevation = 6.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(30.dp, 18.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    imageVector = ImageVector.vectorResource(id = it.icon),
                                    contentDescription = null // decorative element
                                )
                                Text(text = it.name, color = Color.White)
                            }
                        }
                    }
                }
            }

        }
    }
}