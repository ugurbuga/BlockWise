package com.ugurbuga.blockwise

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform