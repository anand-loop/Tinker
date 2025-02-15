package com.anandj.tinker.core.logging

import android.util.Log

class Logger(val name: String) {
    fun log(message: String) {
        Log.d(name, message)
    }
}