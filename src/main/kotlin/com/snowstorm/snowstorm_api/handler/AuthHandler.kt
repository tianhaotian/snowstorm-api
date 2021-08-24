package com.snowstorm.snowstorm_api.handler

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import io.vertx.ext.web.sstore.redis.RedisSessionStore
import io.vertx.ext.web.sstore.redis.impl.RedisSessionStoreImpl

class AuthHandler : Handler<RoutingContext> {

    override fun handle(context: RoutingContext) {
        val session = context.session()
        println("session: ${session.id()}, ${session.data()}")
        val isAuth = session.get("is_auth") ?: false
        if (isAuth) {
            context.next()
        } else {
            /**
             * TODO 若session未认证，则向其他模块或服务认证，认证后向session写入认证标记
             */
            if (true) {
                println("auth successfully")
                session.put("is_auth", true)
                context.next()
            } else {
                // reject request, return 403 or other forbidden response
                context.response().setStatusCode(403).end()
            }
        }
    }

}