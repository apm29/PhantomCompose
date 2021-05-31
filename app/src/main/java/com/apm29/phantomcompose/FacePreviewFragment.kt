package com.apm29.phantomcompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.apm29.arcface.FaceAttrPreviewFragment
import com.apm29.phantomcompose.model.FaceCommonInfo
import com.apm29.phantomcompose.vm.FaceAttrViewModel

class FacePreviewFragment : Fragment() {

    private val faceAttrViewModel: FaceAttrViewModel by viewModels()

    private val faceAttrPreviewFragment by lazy {
        FaceAttrPreviewFragment(faceAttrViewModel::onFaceInfoGet)
    }

    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row {
                        val modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                        FaceInfoList(modifier, faceAttrViewModel.faceSet)
                        AndroidView(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(
                                    1.2f,
                                    true
                                ), // Occupy the max size in the Compose UI tree
                            factory = { context ->
                                // Creates custom view
                                FrameLayout(context).apply {
                                    id = R.id.id_fragment_face_preview
                                    childFragmentManager.commit {
                                        add(R.id.id_fragment_face_preview, faceAttrPreviewFragment)
                                    }
                                }
                            },
                        )
                    }
                    //CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }

    @ExperimentalFoundationApi
    @Composable
    private fun FaceInfoList(modifier: Modifier, faceSet: List<FaceCommonInfo>) {
        LazyColumn(
            modifier = modifier
                .background(color = Color.LightGray)
        ) {
            stickyHeader {
                Text(
                    text = "人脸信息",
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
            items(faceSet) {
                Text(text = faceSet.toString())
            }
        }
    }
}