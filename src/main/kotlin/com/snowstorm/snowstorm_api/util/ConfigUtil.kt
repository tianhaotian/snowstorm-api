package com.snowstorm.snowstorm_api.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.snowstorm.snowstorm_api.config.MySQLConfig
import com.snowstorm.snowstorm_api.config.PostgreConfig
import com.snowstorm.snowstorm_api.config.RedisConfig
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient

class ConfigUtil {

    companion object {
        val logger = LoggerFactory.getLogger(this.javaClass)

        var mysqlConfig: MySQLConfig ?= null
        var redisConfig: RedisConfig ?= null
        var postgreConfig: PostgreConfig ?= null

        @JvmStatic fun doInit() {
            try {
                WebClient.create(Constants.vertx)
                    .get(Constants.CONFIG_PORT, Constants.CONFIG_HOST, Constants.CONFIG_URI)
                    .addQueryParam("key", Constants.CONFIG_KEY)
                    .send()
                    .onSuccess { response -> initConfig(response.bodyAsString()) }
                    .onFailure { err ->
                        logger.error("http request error: " + err.message)
                        initOfflineConfig()
                    }
            } catch (ex: Exception) {
                logger.error("config init error occur: " + ex.message)
            }
        }

        @JvmStatic fun initConfig(rawResponse: String) {
            var rawConfig: JSONObject = JSON.parseObject(rawResponse)
            mysqlConfig = rawConfig.getJSONObject("mysql").toJavaObject(MySQLConfig::class.java)
            redisConfig = rawConfig.getJSONObject("redis").toJavaObject(RedisConfig::class.java)
            postgreConfig = rawConfig.getJSONObject("postgre").toJavaObject(PostgreConfig::class.java)
            logger.info("update config by raw config=$rawConfig")
        }

        @JvmStatic fun initOfflineConfig() {
            // TODO 如果读取配置服务出现问题，则取默认配置
        }

    }
}