package com.apm29.arcface

import android.graphics.Bitmap

interface FaceCommand {
    suspend fun commandStartRegister(idCardAvatar: Bitmap):Boolean
}