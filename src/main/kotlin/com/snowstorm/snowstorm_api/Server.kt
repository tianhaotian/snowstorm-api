package com.snowstorm.snowstorm_api

import com.snowstorm.snowstorm_api.handler.FailureHandler
import com.snowstorm.snowstorm_api.handler.HandlerRegister
import com.snowstorm.snowstorm_api.handler.HttpLogHandler
import com.snowstorm.snowstorm_api.util.ConfigUtil
import com.snowstorm.snowstorm_api.util.Constants
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.sstore.redis.RedisSessionStore
import java.util.*

class Server : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val serverStartTime = Date()

  private fun indexHandler(rc: RoutingContext) {
    val content = """
            <body style="background-color: black;color: greenyellow;">
                  <pre>                
 ___  _ __    ___ __      __
/ __|| '_ \  / _ \\ \ /\ / /
\__ \| | | || (_) |\ V  V / 
|___/|_| |_| \___/  \_/\_/  

 Started at: $serverStartTime
                        </pre>
            </body>
            """
    rc.response().end(content)
  }

  private fun setupRouter(): Router {
    val router = Router.router(vertx)
    router.route().handler(HttpLogHandler())
    router.route().handler(BodyHandler.create())

    /*
    // TODO set from config
    val sessionHandler = SessionHandler.create(RedisSessionStore(vertx, RedisHelper))
            .setSessionTimeout(1800 * 1000)
            .setSessionCookieName("sid")
    router.route().handler(sessionHandler)
            */

    router.route("/").handler(this::indexHandler)

    val apiRegister = HandlerRegister(router)
    apiRegister.register("com.snowstorm.snowstorm_api.handler")

    router.route().failureHandler(FailureHandler())
    return router
  }

  override fun start(startPromise: Promise<Void>) {
    val router: Router = setupRouter()

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(9070) { res ->
        if (res.succeeded()) {
          startPromise.complete()
          logger.info("HTTP Server is listening on: 9070")
        } else {
          res.cause().printStackTrace()
          logger.error("Failed to start HTTP server(port: 9070): ${res.cause()}")
          startPromise.fail(res.cause());
          vertx.close()
        }
      }
  }

  companion object {
    @JvmStatic fun main(args: Array<String>) {
      ConfigUtil.doInit()
      Vertx.vertx().deployVerticle(Server())
    }
  }
}
