package com.apm29.phantomcompose

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.apm29.phantomcompose.ext.CoroutineScopeContext
import com.apm29.phantomcompose.route.Routes
import com.apm29.phantomcompose.ui.contact.ContactsScreen
import com.apm29.phantomcompose.ui.dashboard.DashboardScreen
import com.apm29.phantomcompose.ui.register.EnterRegisterScreen
import com.apm29.phantomcompose.ui.register.LeaveRegisterScreen
import com.apm29.phantomcompose.ui.sample.Todo
import com.apm29.phantomcompose.ui.sample.TodoScreen
import com.apm29.phantomcompose.ui.theme.PhantomComposeTheme
import com.apm29.phantomcompose.ui.visitor.VisitorRecordScreen
import com.apm29.phantomcompose.vm.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComposeFragment : Fragment(), CoroutineScopeContext {

    private val qrCodeViewModel:QRCodeViewModel by viewModels()

    private val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 0) {
            val qrcode: String? = result.data?.getStringExtra("qrCode")
            qrCodeViewModel.setCode(qrcode)
        } else {
            qrCodeViewModel.setCode(null)
        }
    }


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
            val enterRegisterViewModel: VisitorViewModel by viewModels {
                VisitorViewModel.EnterRegisterViewModelFactory()
            }
            val contactViewModel: ContactViewModel by viewModels {
                ContactViewModel.ContactsViewModelFactory()
            }
            val workViewModel: WorkViewModel by viewModels()
            val fragmentNavController = findNavController()
            setContent {
                val navController = rememberNavController()
                PhantomComposeTheme {
                    NavHost(navController = navController, startDestination = Routes.Dashboard) {
                        composable(route = Routes.Dashboard) {
                            DashboardScreen(
                                onNavigateCompose = {
                                    navController.navigate(it)
                                },
                                onNavigateFragment = {
                                    fragmentNavController.navigate(it)
                                },
                                onScanQrCode = {
                                    val intent = Intent()
                                    intent.setClassName("com.telpo.tps550.api", "com.telpo.tps550.api.barcode.Capture")
                                    launcher.launch(intent)
                                },
                                syncWorkerState = workViewModel.syncWorkerState
                            )
                        }
                        composable(Routes.EnterRegister) {
                            EnterRegisterScreen(
                                onAgree = enterRegisterViewModel::onSubmitRegister,
                                onReject = enterRegisterViewModel::onReject
                            )
                        }
                        composable(Routes.LeaveRegister) {
                            LeaveRegisterScreen()
                        }
                        composable(Routes.Contact) {
                            ContactsScreen(
                                records = contactViewModel.contactRecords,
                                hasMore = contactViewModel.hasMoreContacts,
                                loading = contactViewModel.loadingContacts,
                                onLoadMore = contactViewModel::getContacts,
                            )
                        }
                        composable(Routes.VisitRecords) {
                            VisitorRecordScreen(
                                records = enterRegisterViewModel.visitorRecords,
                                hasMore = enterRegisterViewModel.hasMoreVisitRecords,
                                loading = enterRegisterViewModel.loadingVisitRecords,
                                onLoadMore = enterRegisterViewModel::getVisitRecords,
                            )
                        }
                        composable(Routes.VideoConf) {
                            EnterRegisterScreen(
                                onAgree = enterRegisterViewModel::onSubmitRegister,
                                onReject = enterRegisterViewModel::onReject
                            )
                        }
                        composable(Routes.Settings) {
                            TodoScreen(
                                todoList =  todoViewModel.todoList,
                                onAdd = todoViewModel::addItem,
                                onChange = todoViewModel::changeStatus,
                            )
                        }
                    }
                }
            }
        }
    }
}