package com.snowstorm.snowstorm_api.config

import lombok.Data

@Data
class RedisConfig {
    var host: String ?= null
    var port: Int ?= null

    fun getConnectionString(): String {
        var hostStr = this.host
        var portInt = this.port
        if (hostStr == null) {
            hostStr = "127.0.0.1"
        }
        if (portInt == null) {
            portInt = 6379
        }
        return "redis://{$hostStr}:{$portInt}"

    }
}