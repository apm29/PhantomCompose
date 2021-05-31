package com.apm29.phantomcompose.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.apm29.phantomcompose.model.FaceCommonInfo
import com.arcsoft.face.*

class FaceAttrViewModel : ViewModel() {

    private val _faceSet: SnapshotStateList<FaceCommonInfo> = mutableStateListOf()

    val faceSet: List<FaceCommonInfo> = _faceSet

    fun onFaceInfoGet(
        faceInfo: List<FaceInfo>,
        ageInfo: List<AgeInfo>,
        genderInfo: List<GenderInfo>,
        face3dAngle: List<Face3DAngle>,
        livenessInfo: List<LivenessInfo>
    ) {
        _faceSet.clear()
        _faceSet.addAll(
            faceInfo.mapIndexed { index, face ->
                FaceCommonInfo(
                    id = face.faceId,
                    gender = when (genderInfo[index].gender) {
                        0 -> "男"
                        1 -> "女"
                        else -> "未知"
                    },
                    age = ageInfo[index].age,
                    yaw = face3dAngle[index].yaw,
                    pitch = face3dAngle[index].pitch,
                    roll = face3dAngle[index].roll,
                    liveness = when (livenessInfo[index].liveness) {
                        -1 -> "未知"
                        0 -> "非活体"
                        1 -> "活体"
                        -2 -> "人脸数过多"
                        -3 -> "人脸太小"
                        -4 -> "人脸太大"
                        -5 -> "人脸越界"
                        else -> "未知"
                    }
                )
            }
        )
    }

}