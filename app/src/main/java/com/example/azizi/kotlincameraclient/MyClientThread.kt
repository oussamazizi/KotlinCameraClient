package com.example.azizi.kotlincameraclient
/**
 * Created by Azizi on 12/07/2020.
 */
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import java.io.DataInputStream
import java.io.InputStream
import java.net.Socket

class MyClientThread(private val mSocket: Socket, private val mHandler: Handler) :
    Runnable {
    private val mRunFlag = true
    private val TAG = "MyClientThread"
    private val bitmap_options = BitmapFactory.Options()
    override fun run() {
        try {
            var inStream: InputStream? = null
            try {
                inStream = mSocket.getInputStream()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val `is` = DataInputStream(inStream)
            while (mRunFlag) {
                try {
                    val token = `is`.readInt()
                    if (token == 4) {
                        if (`is`.readUTF() == "#@@#") {
                            //System.out.println("before-token" + token);
                            val imgLength = `is`.readInt()
                            println("getLength:$imgLength")
                            println("back-token" + `is`.readUTF())
                            val buffer = ByteArray(imgLength)
                            var len = 0
                            while (len < imgLength) {
                                len += `is`.read(buffer, len, imgLength - len)
                            }
                            val m = mHandler.obtainMessage()
                            m.obj = BitmapFactory.decodeByteArray(
                                buffer,
                                0,
                                buffer.size,
                                bitmap_options
                            )
                            if (m.obj != null) {
                                mHandler.sendMessage(m)
                            } else {
                                println("Decode Failed")
                            }
                        }
                    } else {
                        Log.d(
                            TAG,
                            "Skip dirty bytes!!!!" + Integer.toString(token)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565
        //br = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
}


