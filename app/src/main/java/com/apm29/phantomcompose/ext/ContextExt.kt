package com.apm29.phantomcompose.ext

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX


val PERMISSION_MAP = mapOf(
    Manifest.permission.RECORD_AUDIO to "录音权限",
    Manifest.permission.WRITE_EXTERNAL_STORAGE to "手机内部存储权限",
    Manifest.permission.CAMERA to "手机相机权限",
    Manifest.permission.ACCESS_COARSE_LOCATION to "手机定位权限",
)

fun List<String>.asPermissionString(): String {
    return map {
        PERMISSION_MAP[it]
    }.joinToString(",")
}

fun FragmentActivity.doRequestPermissions(
    permissions: List<String> = arrayListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    ),
    callback: (() -> Unit)? = null
) {
    PermissionX.init(this).permissions(
        permissions
    )
        .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList ->
            if (deniedList.isNotEmpty())
                scope.showRequestReasonDialog(
                    deniedList,
                    "App运行需要获取${deniedList.asPermissionString()}！",
                    "好的",
                    "取消"
                )
        }
        .onForwardToSettings { scope, deniedList ->
            if (deniedList.isNotEmpty())
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "请到设置中心打开${deniedList.asPermissionString()}",
                    "好的",
                    "取消"
                )
        }
        .request { allGranted, _, deniedList ->
            if (allGranted && deniedList.isEmpty()) {
                callback?.invoke()
            } else {
                doRequestPermissions(deniedList)
            }
        }
}

fun Fragment.doRequestPermissions(
    permissions: List<String> = arrayListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    ),
    callback: (() -> Unit)? = null
) {
    PermissionX.init(this).permissions(
        permissions
    )
        .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList ->
            if (deniedList.isNotEmpty())
                scope.showRequestReasonDialog(
                    deniedList,
                    "App运行需要获取${deniedList.asPermissionString()}！",
                    "好的",
                    "取消"
                )
        }
        .onForwardToSettings { scope, deniedList ->
            if (deniedList.isNotEmpty())
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "请到设置中心打开${deniedList.asPermissionString()}",
                    "好的",
                    "取消"
                )
        }
        .request { allGranted, _, deniedList ->
            if (allGranted && deniedList.isEmpty()) {
                callback?.invoke()
            } else {
                doRequestPermissions(deniedList)
            }
        }
}
