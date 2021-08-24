package com.snowstorm.snowstorm_api.handler.base

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj

open class BaseHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass.simpleName)

    fun setResult(context: RoutingContext, result: JsonObject) {
        val jsonResult = Json.obj(
            "status" to "0",
            "errstr" to "",
            "result" to result,
            "trcid" to context.get("traceId") as String
        )
        context.response().end(jsonResult.toString())
    }

    fun setResult(context: RoutingContext, result: Future<JsonObject>) {
        val response = context.response()
        result.onComplete { ar ->
            val jsonResult = Json.obj(
                "status" to "0",
                "errstr" to "",
                "result" to "",
                "trcid" to context.get("traceId") as String
            )
            if (ar.succeeded()) {
                jsonResult.put("result", ar.result())
            } else {
                val error = ar.cause()
                logger.error(error.message, error)
                jsonResult.put("errstr", "Internal server error!")
                jsonResult.put("status", "500")
                response.statusCode = 500
            }
            response.end(jsonResult.toString())
        }
    }

    fun setError(context: RoutingContext, code: Int, msg: String) {
        val response = context.response()
        val errorInfo = Json.obj(
            "status" to code.toString(),
            "errstr" to msg,
            "result" to "",
            "trcid" to context.get("traceId") as String
        )
        response.end(errorInfo.toString())
    }
}