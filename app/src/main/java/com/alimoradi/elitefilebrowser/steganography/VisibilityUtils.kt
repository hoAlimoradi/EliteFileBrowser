package com.alimoradi.elitefilebrowser.steganography

import android.view.View

class VisibilityUtils {

    companion object {
        fun toVisibility(visible: Boolean): Int {
            if (visible) {
                return View.VISIBLE
            }

            return View.GONE
        }
    }
}