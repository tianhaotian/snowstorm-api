package com.snowstorm.snowstorm_api.util

import com.alibaba.fastjson.JSONObject
import com.snowstorm.snowstorm_api.config.MySQLConfig
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.PoolOptions

class MySQLUtil {
    internal var logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val USER_DB = "snowstorm_user"
        val API_DB = "snowstorm_api"

        var userDbPool: MySQLPool? = null
        var apiDbPool: MySQLPool? = null
        init {
            userDbPool = ConfigUtil.mysqlConfig?.let { initUserDB(it) }
            apiDbPool = ConfigUtil.mysqlConfig?.let { initApiDB(it) }
        }

        fun initUserDB(conf: MySQLConfig): MySQLPool {
            val connectOptions = conf.port?.let {
                MySQLConnectOptions()
                    .setPort(it)
                    .setHost(conf.host)
                    .setUser(conf.username)
                    .setPassword(conf.password)
                    .setDatabase(USER_DB)
            }
            val poolOptions = PoolOptions().setMaxSize(10)

            val ctx = Vertx.currentContext()
            val vertx = if (ctx != null) ctx.owner() else Vertx.vertx()
            return MySQLPool.pool(vertx, connectOptions, poolOptions)
        }

        fun initApiDB(conf: MySQLConfig): MySQLPool {
            val connectOptions = conf.port?.let {
                MySQLConnectOptions()
                    .setPort(it)
                    .setHost(conf.host)
                    .setUser(conf.username)
                    .setPassword(conf.password)
                    .setDatabase(API_DB)
            }
            val poolOptions = PoolOptions().setMaxSize(10)

            val ctx = Vertx.currentContext()
            val vertx = if (ctx != null) ctx.owner() else Vertx.vertx()
            return MySQLPool.pool(vertx, connectOptions, poolOptions)
        }

    }
}