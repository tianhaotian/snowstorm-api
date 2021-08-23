package com.snowstorm.snowstorm_api.handler

import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.net.SocketAddress
import io.vertx.ext.web.RoutingContext

class HttpLogHandler : Handler<RoutingContext> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun handle(context: RoutingContext) {
        // common logging data
        val timestamp = System.currentTimeMillis()
        val remoteClient = getClientAddress(context.request().remoteAddress())
        val method = context.request().method()
        val uri = context.request().uri()
        /*
        if (immediate) {
            log(context, timestamp, remoteClient, method, uri)
        } else {
            context.addBodyEndHandler({ v -> log(context, timestamp, remoteClient, version, method, uri) })
        }
        */

        context.addBodyEndHandler({ log(context, timestamp, remoteClient, method, uri) })
        context.next()
    }

    private fun getClientAddress(inetSocketAddress: SocketAddress?): String? {
        return inetSocketAddress?.host()
    }

    private fun log(context: RoutingContext, timestamp: Long, remoteClient: String?, method: HttpMethod, uri: String) {
        val request = context.request()
        val headers = request.headers()
        val realIp = headers["X-Real-Ip"] ?: remoteClient
        val status = request.response().statusCode
        val els = System.currentTimeMillis() - timestamp

        val contextData = context.data()
        val traceId: String = if (contextData.containsKey("traceId")) {
            contextData["traceId"] as String
        } else {
            "no traceId"
        }

        doLog(status, "$realIp $method $uri [$traceId] $els ms")
    }

    private fun doLog(status: Int, message: String?) {
        when {
            status >= 500 -> logger.error(message)
            status >= 400 -> logger.warn(message)
            else -> logger.info(message)
        }
    }

}