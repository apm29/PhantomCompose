package com.apm29.arcface


import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.apm29.arcface.model.DrawInfo
import com.apm29.arcface.util.ConfigUtil
import com.apm29.arcface.util.DrawHelper
import com.apm29.arcface.util.camera.CameraHelper
import com.apm29.arcface.util.camera.CameraListener
import com.apm29.arcface.util.face.RecognizeColor
import com.apm29.arcface.widget.FaceRectView
import com.arcsoft.face.*
import com.arcsoft.face.enums.DetectMode

data class Size(
    var height: Int,
    var width: Int
)

class FaceAttrPreviewFragment : Fragment(R.layout.activity_face_attr_preview){

    private val logTag = "FaceAttrPreviewActivity"
    private var cameraHelper: CameraHelper? = null
    private var drawHelper: DrawHelper? = null
    private val previewSize: Size = Size(0, 0)
    private val rgbCameraId: Int = Camera.CameraInfo.CAMERA_FACING_BACK
    private var faceEngine: FaceEngine? = null
    private var afCode = -1
    private val processMask =
        FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS

    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private lateinit var previewView: View
    private lateinit var faceRectView: FaceRectView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Activity启动后就锁定为启动时的方向
        previewView = view.findViewById(R.id.texture_preview)
        faceRectView = view.findViewById(R.id.face_rect_view)

        initEngine()
        initCamera()
    }

    private fun initEngine() {
        faceEngine = FaceEngine()
        afCode = faceEngine!!.init(
            requireContext(),
            DetectMode.ASF_DETECT_MODE_VIDEO,
            ConfigUtil.getFtOrient(requireContext()),
            16,
            20,
            FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS
        )
        Log.i(logTag, "initEngine:  init: $afCode")
        if (afCode != ErrorInfo.MOK) {
            Log.e(logTag, "初始化失败:$afCode")
        }
    }

    private fun unInitEngine() {
        if (afCode == 0) {
            afCode = faceEngine!!.unInit()
            Log.i(logTag, "unInitEngine: $afCode")
        }
    }


    override fun onDestroy() {
        if (cameraHelper != null) {
            cameraHelper?.release()
            cameraHelper = null
        }
        unInitEngine()
        super.onDestroy()
    }

    private fun initCamera() {
        val metrics = DisplayMetrics()
        val cameraListener: CameraListener = object : CameraListener {
            override fun onCameraOpened(
                camera: Camera,
                cameraId: Int,
                displayOrientation: Int,
                isMirror: Boolean
            ) {
                Log.i(logTag, "onCameraOpened: $cameraId  $displayOrientation $isMirror")
                previewSize.apply {
                    height = camera.parameters.previewSize.height
                    width = camera.parameters.previewSize.width
                }
                drawHelper = DrawHelper(
                    previewSize.width,
                    previewSize.height,
                    previewView.width,
                    previewView.height,
                    displayOrientation,
                    cameraId,
                    isMirror,
                    false,
                    false
                )
            }

            override fun onPreview(nv21: ByteArray?, camera: Camera?) {
                faceRectView.clearFaceInfo()
                val faceInfoList: List<FaceInfo> = ArrayList()
                //                long start = System.currentTimeMillis();
                var code = faceEngine!!.detectFaces(
                    nv21,
                    previewSize.width,
                    previewSize.height,
                    FaceEngine.CP_PAF_NV21,
                    faceInfoList
                )
                if (code == ErrorInfo.MOK && faceInfoList.size > 0) {
                    code = faceEngine!!.process(
                        nv21,
                        previewSize.width,
                        previewSize.height,
                        FaceEngine.CP_PAF_NV21,
                        faceInfoList,
                        processMask
                    )
                    if (code != ErrorInfo.MOK) {
                        return
                    }
                } else {
                    return
                }
                val ageInfoList: List<AgeInfo> = ArrayList()
                val genderInfoList: List<GenderInfo> = ArrayList()
                val face3DAngleList: List<Face3DAngle> = ArrayList()
                val faceLivenessInfoList: List<LivenessInfo> = ArrayList()
                val ageCode = faceEngine!!.getAge(ageInfoList)
                val genderCode = faceEngine!!.getGender(genderInfoList)
                val face3DAngleCode = faceEngine!!.getFace3DAngle(face3DAngleList)
                val livenessCode = faceEngine!!.getLiveness(faceLivenessInfoList)

                // 有其中一个的错误码不为ErrorInfo.MOK，return
                if (ageCode or genderCode or face3DAngleCode or livenessCode != ErrorInfo.MOK) {
                    return
                }
                if (faceRectView != null && drawHelper != null) {
                    val drawInfoList: MutableList<DrawInfo> = ArrayList()
                    for (i in faceInfoList.indices) {
                        drawInfoList.add(
                            DrawInfo(
                                drawHelper?.adjustRect(faceInfoList[i].rect),
                                genderInfoList[i].gender,
                                ageInfoList[i].age,
                                faceLivenessInfoList[i].liveness,
                                RecognizeColor.COLOR_UNKNOWN,
                                null
                            )
                        )
                    }
                    drawHelper?.draw(faceRectView, drawInfoList)
                }
            }

            override fun onCameraClosed() {
                Log.i(logTag, "onCameraClosed: ")
            }

            override fun onCameraError(e: Exception) {
                Log.i(logTag, "onCameraError: " + e.message)
            }

            override fun onCameraConfigurationChanged(cameraID: Int, displayOrientation: Int) {
                if (drawHelper != null) {
                    drawHelper?.cameraDisplayOrientation = displayOrientation
                }
                Log.i(logTag, "onCameraConfigurationChanged: $cameraID  $displayOrientation")
            }
        }
        cameraHelper = CameraHelper.Builder()
            .previewViewSize(Point(previewView.measuredWidth, previewView.measuredHeight))
            .rotation(0)
            .specificCameraId(rgbCameraId ?: Camera.CameraInfo.CAMERA_FACING_FRONT)
            .isMirror(false)
            .previewOn(previewView)
            .cameraListener(cameraListener)
            .build()
        cameraHelper?.init()
        cameraHelper?.start()
    }



    /**
     * 切换相机。注意：若切换相机发现检测不到人脸，则极有可能是检测角度导致的，需要销毁引擎重新创建或者在设置界面修改配置的检测角度
     *
     * @param view
     */
    fun switchCamera(view: View?) {
        if (cameraHelper != null) {
            val success: Boolean? = cameraHelper?.switchCamera()
        }
    }


}