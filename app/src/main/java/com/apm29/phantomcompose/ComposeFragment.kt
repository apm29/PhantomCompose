package com.apm29.phantomcompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apm29.phantomcompose.route.Routes
import com.apm29.phantomcompose.ui.contact.ContactsScreen
import com.apm29.phantomcompose.ui.dashboard.DashboardScreen
import com.apm29.phantomcompose.ui.register.EnterRegisterScreen
import com.apm29.phantomcompose.ui.register.LeaveRegisterScreen
import com.apm29.phantomcompose.ui.sample.Todo
import com.apm29.phantomcompose.ui.sample.TodoScreen
import com.apm29.phantomcompose.ui.theme.PhantomComposeTheme
import com.apm29.phantomcompose.ui.visitor.VisitorRecordScreen
import com.apm29.phantomcompose.vm.ContactViewModel
import com.apm29.phantomcompose.vm.TodoViewModel
import com.apm29.phantomcompose.vm.VisitorViewModel

class ComposeFragment:Fragment() {

    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            val todoViewModel: TodoViewModel by viewModels()
            val enterRegisterViewModel: VisitorViewModel by viewModels{
                VisitorViewModel.EnterRegisterViewModelFactory()
            }
            val contactViewModel: ContactViewModel by viewModels{
                ContactViewModel.ContactsViewModelFactory()
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
                            LeaveRegisterScreen()
                        }
                        composable(Routes.Contact) {
                            ContactsScreen(
                                contactViewModel.contactRecords,
                                contactViewModel.hasMoreContacts,
                                contactViewModel.loadingContacts,
                            ){
                                contactViewModel.getContacts()
                            }
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
}