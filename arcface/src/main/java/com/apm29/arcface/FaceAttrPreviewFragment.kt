package com.apm29.arcface


import android.graphics.Bitmap
import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.apm29.arcface.model.DrawInfo
import com.apm29.arcface.util.DrawHelper
import com.apm29.arcface.util.camera.CameraHelper
import com.apm29.arcface.util.camera.CameraListener
import com.apm29.arcface.util.face.RecognizeColor
import com.apm29.arcface.widget.FaceRectView
import com.arcsoft.face.*
import com.arcsoft.face.enums.CompareModel
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.arcsoft.face.enums.DetectModel
import com.arcsoft.imageutil.ArcSoftImageFormat
import com.arcsoft.imageutil.ArcSoftImageUtil
import com.arcsoft.imageutil.ArcSoftImageUtilError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class FaceAttrPreviewFragment(
    val onFaceInfo: (
        List<FaceInfo>,
        List<AgeInfo>,
        List<GenderInfo>,
        List<Face3DAngle>,
        List<LivenessInfo>
    ) -> Unit
) : Fragment(R.layout.activity_face_attr_preview), FaceCommand, CoroutineScope {

    private val logTag = "FaceAttrPreviewActivity"
    private var cameraHelper: CameraHelper? = null
    private var drawHelper: DrawHelper? = null
    private val previewSize: Size = Size(0, 0)


    private val mJob
        get() = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob

    val coroutineIoContext: CoroutineContext
        get() = Dispatchers.IO + mJob

    /**
     * VIDEO模式人脸检测引擎，用于预览帧人脸追踪
     */
    private val videoFaceEngine: FaceEngine by lazy {
        val faceEngine = FaceEngine()
        val afCode = faceEngine.init(
            requireContext(),
            DetectMode.ASF_DETECT_MODE_VIDEO,
            DetectFaceOrientPriority.ASF_OP_ALL_OUT,
            16,
            10,
            FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS
        )
        Log.i(logTag, "initEngine:  init: $afCode")
        if (afCode != ErrorInfo.MOK) {
            Log.e(logTag, "初始化引擎失败:$afCode")
        }
        faceEngine
    }

    private val imageFaceEngine: FaceEngine by lazy {
        val faceEngine = FaceEngine()
        val afCode = faceEngine.init(
            requireContext(),
            DetectMode.ASF_DETECT_MODE_IMAGE,
            DetectFaceOrientPriority.ASF_OP_ALL_OUT,
            16,
            10,
            FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS
        )
        Log.i(logTag, "initEngine:  init: $afCode")
        if (afCode != ErrorInfo.MOK) {
            Log.e(logTag, "初始化引擎失败:$afCode")
        }
        faceEngine
    }

    private val processMask =
        FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS

    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private lateinit var previewView: View
    private lateinit var faceRectView: FaceRectView


    /**
     * 用于特征提取的引擎
     */
    private val faceFeatureEngine: FaceEngine by lazy {
        val faceEngine = FaceEngine()
        val afCode = faceEngine.init(
            requireContext(),
            DetectMode.ASF_DETECT_MODE_IMAGE,
            DetectFaceOrientPriority.ASF_OP_0_ONLY,
            16,
            10,
            FaceEngine.ASF_FACE_RECOGNITION
        )
        Log.i(logTag, "initEngine:  init: $afCode")
        if (afCode != ErrorInfo.MOK) {
            Log.e(logTag, "初始化引擎失败:$afCode")
        }
        faceEngine
    }

    /**
     * 数据流
     */
    val nv21DataFlow by lazy {
        MutableSharedFlow<ByteArray>(
            replay = 0,
            extraBufferCapacity = 10,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Activity启动后就锁定为启动时的方向
        previewView = view.findViewById(R.id.texture_preview)
        faceRectView = view.findViewById(R.id.face_rect_view)

        previewView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                previewView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                activeEngine()
            }
        })

    }


    /**
     * 激活引擎
     * @param view
     */
    private fun activeEngine() {

        val runtimeABI = FaceEngine.getRuntimeABI()
        Log.i(logTag, "subscribe: getRuntimeABI() $runtimeABI")
        val activeCode = FaceEngine.activeOnline(
            requireContext(),
            Constants.APP_ID,
            Constants.SDK_KEY
        )
        Log.i(logTag, "activeCode: $activeCode")
        val activeFileInfo = ActiveFileInfo()
        val res =
            FaceEngine.getActiveFileInfo(requireContext(), activeFileInfo)
        if (res == ErrorInfo.MOK) {
            Log.i(logTag, activeFileInfo.toString())
            initEngine()
            initCamera()
        }
    }


    private fun initEngine() {
        videoFaceEngine
        faceFeatureEngine
    }

    private fun unInitEngine() {
        videoFaceEngine.unInit()
        faceFeatureEngine.unInit()
        Log.i(logTag, "unInitEngine")
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
                nv21?.let {
                    nv21DataFlow.tryEmit(it)
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
            .rotation(Surface.ROTATION_270)
            .specificCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
            .isMirror(false)
            .previewOn(previewView)
            .cameraListener(cameraListener)
            .build()
        cameraHelper?.init()
        cameraHelper?.start()


        launch {
            nv21DataFlow
                .filter {
                    !inProcessOfRegister
                }
                .collect { nv21 ->
                    faceRectView.clearFaceInfo()
                    val faceInfoList: List<FaceInfo> = ArrayList()
                    //                long start = System.currentTimeMillis();
                    var code = videoFaceEngine.detectFaces(
                        nv21,
                        previewSize.width,
                        previewSize.height,
                        FaceEngine.CP_PAF_NV21,
                        faceInfoList
                    )
                    if (code == ErrorInfo.MOK && faceInfoList.isNotEmpty()) {
                        code = videoFaceEngine.process(
                            nv21,
                            previewSize.width,
                            previewSize.height,
                            FaceEngine.CP_PAF_NV21,
                            faceInfoList,
                            processMask
                        )
                        if (code != ErrorInfo.MOK) {
                            sendEmptyFaces()
                            return@collect
                        }
                    } else {
                        sendEmptyFaces()
                        return@collect
                    }
                    val ageInfoList: List<AgeInfo> = ArrayList()
                    val genderInfoList: List<GenderInfo> = ArrayList()
                    val face3DAngleList: List<Face3DAngle> = ArrayList()
                    val faceLivenessInfoList: List<LivenessInfo> = ArrayList()
                    val ageCode = videoFaceEngine.getAge(ageInfoList)
                    val genderCode = videoFaceEngine.getGender(genderInfoList)
                    val face3DAngleCode = videoFaceEngine.getFace3DAngle(face3DAngleList)
                    val livenessCode = videoFaceEngine.getLiveness(faceLivenessInfoList)

                    // 有其中一个的错误码不为ErrorInfo.MOK，return
                    if (ageCode != ErrorInfo.MOK || genderCode != ErrorInfo.MOK || face3DAngleCode != ErrorInfo.MOK || livenessCode != ErrorInfo.MOK) {
                        sendEmptyFaces()
                        return@collect
                    }
                    if (drawHelper != null) {
                        val drawInfoList = faceInfoList.mapIndexed { i, faceInfo ->
                            DrawInfo(
                                drawHelper?.adjustRect(faceInfo.rect),
                                genderInfoList[i].gender,
                                ageInfoList[i].age,
                                faceLivenessInfoList[i].liveness,
                                RecognizeColor.COLOR_UNKNOWN,
                                null
                            )
                        }
                        drawHelper?.draw(faceRectView, drawInfoList)
                    }
                    onFaceInfo(
                        faceInfoList,
                        ageInfoList,
                        genderInfoList,
                        face3DAngleList,
                        faceLivenessInfoList
                    )
                }
        }
    }

    private fun sendEmptyFaces() {
        onFaceInfo(
            arrayListOf(),
            arrayListOf(),
            arrayListOf(),
            arrayListOf(),
            arrayListOf()
        )
    }

    private var inProcessOfRegister = false

    override suspend fun commandStartRegister(idCardAvatar: Bitmap): Boolean {
        inProcessOfRegister = true
        try {
            val faceFeature = nv21DataFlow
                .onStart {
                    Log.e(logTag, "开始筛选")
                }.onCompletion {
                    Log.e(logTag, "筛选完成")
                }.map {
                    val faceInfoList = arrayListOf<FaceInfo>()
                    videoFaceEngine.detectFaces(
                        it,
                        previewSize.width,
                        previewSize.height,
                        FaceEngine.CP_PAF_NV21,
                        faceInfoList
                    )
                    faceInfoList to it
                }.filter {
                    it.first.isNotEmpty()
                }.map {
                    getFaceFeatureInfo(
                        it.second,
                        it.first.first(),
                        previewSize.width,
                        previewSize.height
                    )
                }.flowOn(Dispatchers.IO)
                .filterNotNull()
                .first()

            // 图像对齐
            // 图像对齐
            val bitmap = ArcSoftImageUtil.getAlignedBitmap(idCardAvatar, false)
            val width = bitmap.width
            val height = bitmap.height
            val bgr24 =
                ArcSoftImageUtil.createImageData(
                    bitmap.width,
                    bitmap.height,
                    ArcSoftImageFormat.BGR24
                )
            val transformCode =
                ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24)
            if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
                Log.e(logTag, "转换失败: $transformCode")
                return false
            }
            val faceInfoList: List<FaceInfo> = arrayListOf()
            var detectCode = -1
            while (detectCode != ErrorInfo.MOK){
                detectCode = imageFaceEngine.detectFaces(
                    bgr24,
                    width,
                    height,
                    FaceEngine.CP_PAF_BGR24,
                    DetectModel.RGB,
                    faceInfoList
                )
                Log.e(logTag, "人脸检测: $detectCode")
            }
            if ( faceInfoList.isNullOrEmpty()) {
                Log.e(logTag, "人脸检测失败: ${faceInfoList.size}")
                return false
            }
            //保留最大人脸
            val idFaceInfo = keepMaxFace(faceInfoList)
            val idFaceFeature = getFaceFeatureInfo(
                bgr24, idFaceInfo, width, height,  FaceEngine.CP_PAF_BGR24
            )

            if (idFaceFeature == null) {
                Log.e(logTag, "人脸特征为空")
                return false
            }


            val matching = FaceSimilar()

            faceFeatureEngine.compareFaceFeature(
                faceFeature, idFaceFeature, CompareModel.ID_CARD, matching
            )

            Log.e(logTag, "比对结果：${matching.score}")
            return matching.score >= 0.82
        } finally {
            inProcessOfRegister = false
        }
    }

    private fun keepMaxFace(ftFaceList: List<FaceInfo>?): FaceInfo {
        if (ftFaceList.isNullOrEmpty()) {
            throw IllegalArgumentException("人脸数为空")
        }
        var maxFaceInfo = ftFaceList[0]
        for (faceInfo in ftFaceList) {
            if (faceInfo.rect.width() > maxFaceInfo.rect.width()) {
                maxFaceInfo = faceInfo
            }
        }
        return maxFaceInfo
    }

    private fun getFaceFeatureInfo(
        nv21Data: ByteArray,
        faceInfo: FaceInfo,
        width: Int,
        height: Int,
        format: Int = FaceEngine.CP_PAF_NV21
    ): FaceFeature? {
        val faceFeature = FaceFeature()
        val frCode =
            faceFeatureEngine.extractFaceFeature(
                nv21Data,
                width,
                height,
                format,
                faceInfo,
                faceFeature
            )
        return if (frCode == ErrorInfo.MOK) {
            faceFeature
        } else {
            Log.e(logTag, "特征处理失败：$frCode")
            null
        }
    }

}