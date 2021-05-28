package com.apm29.phantomcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apm29.phantomcompose.ui.dashboard.DashboardScreen
import com.apm29.phantomcompose.ui.register.EnterRegisterScreen
import com.apm29.phantomcompose.ui.route.Routes
import com.apm29.phantomcompose.ui.sample.TodoScreen
import com.apm29.phantomcompose.ui.theme.PhantomComposeTheme

@ExperimentalFoundationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PhantomComposeTheme {
                NavHost(navController = navController, startDestination = Routes.Dashboard) {
                    composable(route = Routes.Dashboard) {
                        DashboardScreen(navController)
                    }
                    composable(Routes.EnterRegister) {
                        EnterRegisterScreen( )
                    }
                    composable(Routes.LeaveRegister) {
                        EnterRegisterScreen( )
                    }
                    composable(Routes.Contact) {
                        EnterRegisterScreen()
                    }
                    composable(Routes.VisitRecords) {
                        EnterRegisterScreen( )
                    }
                    composable(Routes.VideoConf) {
                        EnterRegisterScreen()
                    }
                    composable(Routes.Settings) {
                        TodoScreen()
                    }
                }
            }
        }
    }
}