package com.snowstorm.snowstorm_api.handler

import com.snowstorm.snowstorm_api.annotations.*
import com.snowstorm.snowstorm_api.util.DataBinder
import com.snowstorm.snowstorm_api.util.StringUtils
import io.vertx.core.http.HttpHeaders.CONTENT_LENGTH
import io.vertx.core.http.HttpHeaders.CONTENT_TYPE
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.reflections.Reflections
import java.lang.reflect.Method
import java.util.UUID

/**
 * 将@Api及@Mob类型的Handler中的@HttpMethod自动注册到路由中
 * 带有@Auth则添加AuthHandler认证Handler
 * 处理规则示例：
 *   @Api
 *   class AdvData {
 *       @Auth
 *       @HttpMethod(POST)
 *       fun modify(context: RoutingContext) {
 *           // ...
 *       }
 *   }
 * 映射为：/api/adv_date/modify (只接收POST请求）
 */
class HandlerRegister(private val router: Router) {
    companion object {
        // 保证多线程模式下打印的日志只出现一次
        private var logPrinted = false
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun register(basePackage: String) {
        synchronized(Companion) {
            // 并发执行Reflection会有问题
            val reflection = Reflections(basePackage)
            val scanAnnotations = listOf(Api::class.java, Mob::class.java)
            for (annotation in scanAnnotations) {
                val classes = reflection.getTypesAnnotatedWith(annotation)
                for (c in classes) {
                    extractHandler(c, annotation.simpleName.toLowerCase())
                }
            }
            logPrinted = true
        }
    }

    private fun extractHandler(handlerClass: Class<*>, prefix: String) {
        val apiInstance = handlerClass.newInstance()
        val rootAuth = handlerClass.annotations.firstOrNull { annotation -> annotation is Auth }
        for (method in handlerClass.methods) {
            val httpMethod = method.annotations.firstOrNull { annotation -> annotation is HttpMethod }
            val auth = rootAuth ?: method.annotations.firstOrNull { annotation -> annotation is Auth }
            if (httpMethod != null) {
                val methodType = (httpMethod as HttpMethod).type
                createRoute(apiInstance, methodType, prefix, handlerClass.simpleName, method, auth)
            }
        }
    }

    private fun createRoute(instance: Any, httpMethod: String, prefix: String, routeName: String, handler: Method,
                            auth: Annotation?) {
        val replacePrefix = prefix.split("_handler").firstOrNull()
        val path = "/$replacePrefix/${StringUtils.toUnderline(routeName)}/${StringUtils.toUnderline(handler.name)}"

        fun handlerFun(context: RoutingContext) {
            setTraceId(context)
            context.addHeadersEndHandler({
                val headers = context.response().headers()
                headers.add("Server", "Snow Server")
                headers.add("Access-Control-Allow-Origin", "*")
                if (!headers.contains(CONTENT_TYPE)) {
                    if ("0" != headers.get(CONTENT_LENGTH)) {
                        headers.add(CONTENT_TYPE, "application/json")
                    }
                }
            })
            if (handler.parameterCount == 1) {
                handler.invoke(instance, context)
            } else {
                val paramType = handler.parameterTypes[1]
                val data = DataBinder.bind(context.request().params(), paramType)
                handler.invoke(instance, context, data)
            }
        }

        val route = when (httpMethod) {
            GET -> router.get(path)
            POST -> router.post(path)
            PUT -> router.put(path)
            DELETE -> router.delete(path)
            else -> null
        }
        if (auth != null) {
            route?.handler(AuthHandler())
        }
        route?.handler(::handlerFun)

        if (logPrinted) {
            return
        }

        if (route != null) {
            logger.info("create route: $httpMethod $path")
        } else {
            logger.error("can not create route: $path with such method: $httpMethod")
        }
    }

    private fun setTraceId(context: RoutingContext) {
        val requestParams = context.request().params()
        val traceId = if (requestParams.contains("trace_id")) {
            requestParams["trace_id"]
        } else {
            UUID.randomUUID().toString()
        }
        // 将traceId设置到vertx实例中
        val localData = context.vertx().sharedData().getLocalMap<String, String>("http")
        localData["traceId"] = traceId
        // 便于设置到handler的返回结果中
        context.put("traceId", traceId)
    }
}