package com.apm29.arcface

import android.graphics.Bitmap
import com.arcsoft.face.FaceFeature
import com.arcsoft.face.FaceSimilar

interface FaceCommand {
    suspend fun commandStartRegister(idCardAvatar: Bitmap):CompareResult

    sealed class CompareResult(
        val similar: FaceSimilar = FaceSimilar(),
        val faceFeatureId: FaceFeature = FaceFeature(),
        val faceFeatureVideo: FaceFeature = FaceFeature(),
        val errorMessage:String = ""
    ){
        class Error(errorMessage: String):CompareResult(errorMessage = errorMessage)
        class Success(
            similar: FaceSimilar,
            faceFeatureId: FaceFeature,
            faceFeatureVideo: FaceFeature
        ):CompareResult(
            similar, faceFeatureId, faceFeatureVideo
        )

    }
}