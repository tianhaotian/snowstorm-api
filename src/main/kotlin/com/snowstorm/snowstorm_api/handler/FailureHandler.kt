package com.snowstorm.snowstorm_api.handler

import com.snowstorm.snowstorm_api.util.MissingParamException
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import java.lang.reflect.InvocationTargetException

class FailureHandler : Handler<RoutingContext> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun handle(rc: RoutingContext) {
        var error = rc.failure()
        if (error is InvocationTargetException) {
            error = error.targetException
        }
        logger.error("Request to ${rc.request().path()} failed: $error", error)

        val errorStr = when (error) {
            is MissingParamException -> "请求参数缺失: ${error.param}"
            else -> "服务器内部错误"
        }

        val response = rc.response()
        response.statusCode = 500
        response.headers().add(HttpHeaders.CONTENT_TYPE, "application/json")
        val data = JsonObject()
            .put("status", "1")
            .put("errstr", errorStr)
            .put("result", "")
        val rcData = rc.data()
        if (rcData.containsKey("traceId")) {
            data.put("trcid", rcData["traceId"])
        }
        response.end(data.toString())
    }
}