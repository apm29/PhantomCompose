package com.apm29.phantomcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apm29.phantomcompose.ui.dashboard.DashboardScreen
import com.apm29.phantomcompose.ui.register.EnterRegisterScreen
import com.apm29.phantomcompose.route.Routes
import com.apm29.phantomcompose.ui.sample.Todo
import com.apm29.phantomcompose.ui.sample.TodoScreen
import com.apm29.phantomcompose.ui.theme.PhantomComposeTheme
import com.apm29.phantomcompose.ui.visitor.VisitorRecordScreen
import com.apm29.phantomcompose.vm.EnterRegisterViewModel
import com.apm29.phantomcompose.vm.TodoViewModel

@ExperimentalFoundationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todoViewModel: TodoViewModel by viewModels()
        val enterRegisterViewModel: EnterRegisterViewModel by viewModels{
            EnterRegisterViewModel.EnterRegisterViewModelFactory()
        }
        setContent {
            val navController = rememberNavController()
            PhantomComposeTheme {
                NavHost(navController = navController, startDestination = Routes.Dashboard) {
                    composable(route = Routes.Dashboard) {
                        DashboardScreen(navController)
                    }
                    composable(Routes.EnterRegister) {
                        EnterRegisterScreen(
                            enterRegisterViewModel::onSubmitRegister,
                            enterRegisterViewModel::onReject
                        )
                    }
                    composable(Routes.LeaveRegister) {
                        EnterRegisterScreen(
                            enterRegisterViewModel::onSubmitRegister,
                            enterRegisterViewModel::onReject
                        )
                    }
                    composable(Routes.Contact) {
                        EnterRegisterScreen(
                            enterRegisterViewModel::onSubmitRegister,
                            enterRegisterViewModel::onReject
                        )
                    }
                    composable(Routes.VisitRecords) {
                        VisitorRecordScreen(
                            enterRegisterViewModel.visitorRecords,
                            enterRegisterViewModel.hasMoreVisitRecords,
                            enterRegisterViewModel.loadingVisitRecords,
                        ){
                            enterRegisterViewModel.getVisitRecords()
                        }
                    }
                    composable(Routes.VideoConf) {
                        EnterRegisterScreen(
                            enterRegisterViewModel::onSubmitRegister,
                            enterRegisterViewModel::onReject
                        )
                    }
                    composable(Routes.Settings) {
                        val todoList: List<Todo> = todoViewModel.todoList
                        TodoScreen(
                            todoList,
                            todoViewModel.currentEdit,
                            todoViewModel::addItem,
                            todoViewModel::changeStatus,
                            todoViewModel::editItem
                        )
                    }
                }
            }
        }
    }
}