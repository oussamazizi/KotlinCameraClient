package com.example.azizi.kotlincameraclient
/**
 * Created by Azizi on 12/07/2020.
 */
import android.content.Context
import android.graphics.*
import android.media.FaceDetector
import android.util.AttributeSet
import android.util.Log
import android.view.View

class MyCameraView : View {
    var mNextFrame: Bitmap? = null
    var face_count = 0
    private val mFaceDetector = FaceDetector(320, 240, 10)
    private var faces =
        arrayOfNulls<FaceDetector.Face>(10)
    private val tmp_point = PointF()
    private val tmp_paint = Paint()

    constructor(context: Context?) : super(context) {
        faces = arrayOfNulls(10)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        faces = arrayOfNulls(10)
        // The bitmap must be in 565 format (for now).
    }

    fun updateImage(frame: Bitmap?) {
        // Set internal configuration to RGB_565
        mNextFrame = frame
        face_count = mFaceDetector.findFaces(mNextFrame, faces)
        Log.d("Face_Detection", "Face Count: $face_count")
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(mNextFrame!!, 0f, 0f, null)
        for (i in 0 until face_count) {
            val face = faces[i]
            tmp_paint.color = Color.RED
            tmp_paint.alpha = 100
            face!!.getMidPoint(tmp_point)
            canvas.drawCircle(
                tmp_point.x, tmp_point.y, face.eyesDistance(),
                tmp_paint
            )
        }
    }
}
