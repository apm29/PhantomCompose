package com.apm29.phantomcompose.providers

import androidx.compose.runtime.staticCompositionLocalOf
import com.telpo.servicelibrary.Idcard2And1Util

val localContextProvider = staticCompositionLocalOf<Idcard2And1Util?> {
    null
}