package com.apm29.phantomcompose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.apm29.arcface.FaceAttrPreviewFragment
import com.apm29.arcface.FaceCommand
import com.apm29.phantomcompose.ext.CoroutineScopeContext
import com.apm29.phantomcompose.model.FaceCommonInfo
import com.apm29.phantomcompose.vm.FaceAttrViewModel
import com.apm29.telpo.IdCardFragment
import com.apm29.telpo.utils.asText
import com.telpo.servicelibrary.IdcardMsg
import kotlinx.coroutines.launch

class FacePreviewFragment : Fragment(), CoroutineScopeContext {

    private val faceAttrViewModel: FaceAttrViewModel by viewModels()

    private val faceAttrPreviewFragment by lazy {
        FaceAttrPreviewFragment(faceAttrViewModel::onFaceInfoGet)
    }

    private val idCardFragment by lazy {
        IdCardFragment()
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FacePreviewScreen()
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    private fun FacePreviewScreen() {
        var loading: Boolean by remember {
            mutableStateOf(false)
        }

        var showResultDialog by remember {
            mutableStateOf(false)
        }

        var idCardDetail by remember {
            mutableStateOf<IdcardMsg?>(null)
        }

        var idCardAvatar by remember {
            mutableStateOf<Bitmap?>(null)
        }

        var compareResult by remember {
            mutableStateOf<FaceCommand.CompareResult?>(null)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    FrameLayout(context).apply {
                        id = R.id.id_fragment_face_preview
                        childFragmentManager.commit {
                            add(R.id.id_fragment_face_preview, faceAttrPreviewFragment)
                        }
                    }
                },
            )
            AndroidView(
                modifier = Modifier
                    .width(0.dp)
                    .height(0.dp),
                factory = { context ->
                    // Creates custom view
                    FrameLayout(context).apply {
                        id = R.id.id_fragment_id_card_detect
                        childFragmentManager.commit {
                            add(R.id.id_fragment_id_card_detect, idCardFragment)
                        }
                    }
                },
            )
            Row {
                val modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(0.6f,true)
                FaceInfoList(modifier, faceAttrViewModel.faceSet, readIdCard = {
                    launch(coroutineIoContext) {
                        loading = true
                        idCardDetail = idCardFragment.checkIdCard()

                        idCardDetail?.let {
                            val idCardAvatarResult = idCardFragment.decodeIdImage(it)
                            compareResult = faceAttrPreviewFragment.commandStartRegister(
                                idCardAvatarResult
                            )
                            idCardAvatar = idCardAvatarResult.copy(
                                Bitmap.Config.ARGB_8888, false
                            )
                        }
                        loading = false
                        showResultDialog = true
                    }
                })
            }
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (showResultDialog) {
                Dialog(
                    onDismissRequest = { showResultDialog = false },
                ){
                    Card {
                        Column(
                            Modifier.padding(16.dp)
                        ) {
                            idCardAvatar?.let {
                                Image(bitmap = it.asImageBitmap(), contentDescription = "证件头像")
                            }
                            compareResult?.let {
                                Text(
                                    text = if (it.errorMessage.isNotBlank()) it.errorMessage else "相似度：${it.similar.score}",
                                    fontSize = 35.sp,
                                    color = Color.Magenta
                                )
                            }
                            idCardDetail?.let {
                                Text(text = it.asText())
                            }?: Text(text = "身份证读取失败")

                        }
                    }
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    private fun FaceInfoList(
        modifier: Modifier,
        faceSet: List<FaceCommonInfo>,
        captureFace: ((FaceCommonInfo) -> Unit)? = null,
        readIdCard: (() -> Unit)? = null
    ) {
        Column(
            modifier = modifier
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    Text(
                        text = "人脸信息",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
                items(faceSet) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = 5.dp,
                        onClick = {
                            captureFace?.invoke(it)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.toString(), modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Filled.CameraEnhance,
                                contentDescription = "拍照",
                                tint = Color.Green,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }

                if (faceSet.isNullOrEmpty()) {
                    item {
                        Text(
                            text = "未检测到人脸",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }

            }
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    readIdCard?.invoke()
                },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(text = "读取身份证信息")
            }

        }

    }
}