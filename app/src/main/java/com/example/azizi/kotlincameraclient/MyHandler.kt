package com.example.azizi.kotlincameraclient

import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

internal class MyHandler(activity: MainActivity) : Handler() {
    private val mActivity: WeakReference<MainActivity>
    override fun handleMessage(msg: Message) {
        val activity = mActivity.get()
        if (activity != null) {
            try {
                activity.mLastFrame = msg.obj as Bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
            super.handleMessage(msg)
        }
    }

    init {
        mActivity = WeakReference(activity)
    }
}
