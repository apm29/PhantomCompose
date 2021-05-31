package com.apm29.phantomcompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.apm29.arcface.FaceAttrPreviewFragment

class FacePreviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row {
                        Text(text = "Composing!", modifier = Modifier.fillMaxHeight().weight(1f).background(color = Color.LightGray))
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
                                        add(R.id.id_fragment_face_preview,FaceAttrPreviewFragment())
                                    }
                                }
                            },
                            update = { _ ->

                            }
                        )
                    }
                }
            }
        }
    }
}