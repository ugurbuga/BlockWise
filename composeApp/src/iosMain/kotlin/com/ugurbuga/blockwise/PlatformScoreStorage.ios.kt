package com.ugurbuga.blockwise

import platform.Foundation.NSUserDefaults

internal actual object PlatformScoreStorage {
    private val defaults: NSUserDefaults
        get() = NSUserDefaults.standardUserDefaults

    actual fun getInt(key: String): Int? {
        defaults.objectForKey(key) ?: return null
        return defaults.integerForKey(key).toInt()
    }

    actual fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), forKey = key)
    }
}

