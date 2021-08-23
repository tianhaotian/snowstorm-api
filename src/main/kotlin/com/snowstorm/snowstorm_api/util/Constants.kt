package com.snowstorm.snowstorm_api.util

import io.vertx.core.Vertx

class Constants {
    companion object {
        val vertx: Vertx = Vertx.vertx()

        val CONFIG_HOST: String = "127.0.0.1"
        val CONFIG_PORT: Int = 9145
        val CONFIG_URI: String = "/config"
        val CONFIG_KEY: String = "snowstorm"
    }
}