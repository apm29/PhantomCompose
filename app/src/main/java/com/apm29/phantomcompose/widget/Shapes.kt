package com.apm29.phantomcompose.widget

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Rect
import kotlin.math.abs

val Circle = GenericShape { size, _ ->
    val (width, height) = size
    val sizeBias = abs(width - height) / 2f + 6
    when {
        width > height -> {
            addOval(
                Rect(
                    sizeBias,
                    0f,
                    width - sizeBias,
                    height
                )
            )
        }
        width < height -> {
            addOval(
                Rect(
                    0f,
                    sizeBias,
                    width,
                    height - sizeBias
                )
            )
        }
        else -> {
            addOval(
                Rect(
                    0f,
                    0f,
                    width,
                    height
                )
            )
        }
    }

}