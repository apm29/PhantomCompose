package com.apm29.phantomcompose.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.apm29.phantomcompose.model.Gender
import com.apm29.phantomcompose.model.IntervieweeDetail
import com.apm29.phantomcompose.model.VisitorDetail
import com.apm29.phantomcompose.ui.theme.Green600
import com.apm29.phantomcompose.ui.theme.Orange500
import com.apm29.phantomcompose.widget.*
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun EnterRegisterScreen(
    onAgree: (VisitorDetail, IntervieweeDetail) -> Unit,
    onReject: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sheet content")
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        scope.launch { scaffoldState.bottomSheetState.collapse() }
                    }
                ) {
                    Text("Click to collapse sheet")
                }
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        topBar = {
            PhantomTopBar("进入登记")
        }
    ) { innerPadding ->
        var name by remember {
            mutableStateOf("")
        }
        var gender by remember {
            mutableStateOf(0)
        }
        var idCard by remember {
            mutableStateOf("")
        }
        var phone by remember {
            mutableStateOf("")
        }
        var address by remember {
            mutableStateOf("")
        }
        var company by remember {
            mutableStateOf("")
        }
        var guestId by remember {
            mutableStateOf("")
        }
        var passId by remember {
            mutableStateOf("")
        }
        var count by remember {
            mutableStateOf(1)
        }
        var reason by remember {
            mutableStateOf("")
        }
        var vehicleLicense by remember {
            mutableStateOf("")
        }
        Row(
            modifier = Modifier.padding(innerPadding)
        ) {
            VisitorInfoForm(
                modifier = Modifier.weight(3f),
                name = name,
                gender = gender,
                idCard = idCard,
                phone = phone,
                address = address,
                company = company,
                guestId = guestId,
                passId = passId,
                count = count,
                reason = reason,
                vehicleLicense = vehicleLicense,
                onNameChange = {
                    name = it
                },
                onGenderChange = {
                    gender = it
                },
                onIdCardChange = {
                    idCard = it
                },
                onPhoneChange = {
                    phone = it
                },
                onAddressChange = {
                    address = it
                },
                onCompanyChange = {
                    company = it
                },
                onGuestIdChange = {
                    guestId = it
                },
                onPassIdChange = {
                    passId = it
                },
                onCountChange = {
                    count = it
                },
                onReasonChange = {
                    reason = it
                },
                onVehicleLicenseChange = {
                    vehicleLicense = it
                }
            )
            VerticalDivider()
            IntervieweeInfoForm(
                modifier = Modifier.weight(2f),
                onAgree = {
                    onAgree(
                        VisitorDetail(
                            name,
                            gender,
                            idCard,
                            phone,
                            address,
                            company,
                            guestId,
                            passId,
                            count,
                            reason,
                            vehicleLicense
                        ),
                        IntervieweeDetail(it)
                    )
                },
                onReject = onReject
            )
        }
    }
}


@Composable
fun VisitorInfoForm(
    modifier: Modifier = Modifier,
    name: String,
    gender: Int,
    idCard: String,
    phone: String,
    address: String,
    company: String,
    guestId: String,
    passId: String,
    count: Int,
    reason: String,
    vehicleLicense: String,
    onNameChange: (String) -> Unit,
    onGenderChange: (Int) -> Unit,
    onIdCardChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onCompanyChange: (String) -> Unit,
    onGuestIdChange: (String) -> Unit,
    onPassIdChange: (String) -> Unit,
    onCountChange: (Int) -> Unit,
    onReasonChange: (String) -> Unit,
    onVehicleLicenseChange: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(12.dp)
    ) {
        item {
            Text(text = "访客信息", color = Orange500, modifier = Modifier.padding(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraImage()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(6.dp)
                ) {
                    LabeledTextField("姓名", name, onValueChange = onNameChange)
                    ItemPicker(
                        "性别",
                        value = gender,
                        items = arrayOf(Gender.Male, Gender.Female),
                        getItemString = {
                            it?.text ?: "请选择性别"
                        },
                        getItemValue = {
                            it.value
                        },
                        onValueChange = {
                            it?.let(onGenderChange)
                        },
                    )
                    LabeledTextField("身份证", idCard, onValueChange = onIdCardChange)
                    LabeledTextField("电话", phone, onValueChange = onPhoneChange)
                }
            }
            LabeledTextField(
                "住址",
                address,
                onValueChange = onAddressChange
            )
            LabeledTextField("所在公司", company, onValueChange = onCompanyChange)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabeledTextField(
                    "来宾证号",
                    guestId,
                    onValueChange = onGuestIdChange,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                LabeledTextField(
                    "通行证号",
                    passId,
                    onValueChange = onPassIdChange,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemPicker(
                    "人数",
                    value = count,
                    items = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                    onValueChange = {
                        it?.let(onCountChange)
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                ItemPicker(
                    "事由", value = reason,
                    items = arrayOf("访问", "外卖", "接送"),
                    onValueChange = {
                        it?.let(onReasonChange)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            LabeledTextField(
                "车牌号",
                vehicleLicense,
                onValueChange = onVehicleLicenseChange,
            )
        }
    }
}


@Composable
fun IntervieweeInfoForm(
    modifier: Modifier = Modifier,
    onAgree: (String) -> Unit,
    onReject: () -> Unit,
) {
    var name by remember {
        mutableStateOf("")
    }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(12.dp)
    ) {
        Text(text = "被访人信息", color = Orange500, modifier = Modifier.padding(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            placeholder = {
                Text("搜索教职工")
            },
            modifier = Modifier.heightIn(32.dp)
        )
        LazyColumn(
            modifier = modifier
                .weight(1f)
        ) {

            item {
                Spacer(modifier = Modifier.width(5.dp))
                Text("搜索历史")
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onReject,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Orange500,
                )
            ) {
                Text(text = "拒绝进入", color = Color.White)
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    onAgree(name)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Green600,
                )
            ) {
                Text(text = "同意进入", color = Color.White)
            }
        }
    }
}