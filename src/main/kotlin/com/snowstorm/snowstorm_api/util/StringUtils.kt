package com.snowstorm.snowstorm_api.util

class StringUtils {

    companion object {
        fun toUnderline(origin: String): String {
            val sb = StringBuilder()
            for (i in 0 until origin.length) {
                val c = origin[i]
                if (c.isUpperCase()) {
                    if (i > 0 && origin[i - 1].isLowerCase()) {
                        sb.append('_')
                    }
                    sb.append(c.toLowerCase())
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        }
    }
}