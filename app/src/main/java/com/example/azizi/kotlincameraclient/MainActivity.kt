package com.example.azizi.kotlincameraclient

import android.graphics.*
import android.media.FaceDetector
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.Socket

class MainActivity : AppCompatActivity() {
    private val mStatus: TextView? = null
    private var mCameraView: ImageView? = null
    var SERVERIP = "xxx.xxx.xxx.xxx" //Change it to your server IP
    val SERVERPORT = 9191
    var mClient: MyClientThread? = null
    var mLastFrame: Bitmap? = null

    private var face_count = 0
    private val handler: MyHandler = MyHandler(this)

    private val mFaceDetector: FaceDetector = FaceDetector(320, 240, 10)
    private val faces: Array<FaceDetector.Face?> = arrayOfNulls<FaceDetector.Face>(10)
    private val tmp_point: PointF = PointF()
    private val tmp_paint: Paint = Paint()

    private val mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                handler.post(Runnable {
                    if (mLastFrame != null) {
                        val mutableBitmap = mLastFrame!!.copy(Bitmap.Config.RGB_565, true)
                        face_count = mFaceDetector.findFaces(mLastFrame, faces)
                        Log.d("Face_Detection", "Face Count: $face_count")
                        val canvas = Canvas(mutableBitmap)
                        for (i in 0 until face_count) {
                            val face = faces[i]!!
                            tmp_paint.color = Color.RED
                            tmp_paint.alpha = 100
                            face.getMidPoint(tmp_point)
                            canvas.drawCircle(tmp_point.x, tmp_point.y, face.eyesDistance(),
                                    tmp_paint)
                        }
                        mCameraView!!.setImageBitmap(mutableBitmap)
                    }
                }) //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                handler.postDelayed(this, 1000 / 15.toLong())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCameraView = findViewById(R.id.camera_preview) as ImageView
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                // Background Code
                val s: Socket
                try {
                    s = Socket(SERVERIP, SERVERPORT)
                    mClient = MyClientThread(s, handler)
                    Thread(mClient).start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }


        }.execute()
        mStatusChecker.run()
    }

    fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        if (source != null) {
            val retVal: Bitmap
            val matrix = Matrix()
            matrix.postRotate(angle)
            retVal = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
            source.recycle()
            return retVal
        }
        return null
    }
}