package com.snowstorm.snowstorm_api.annotations

/**
 * 用于Web端的Handler注解
 */
@Target(AnnotationTarget.CLASS)
annotation class Api

/**
 * 用于移动端的Handler注解
 */
@Target(AnnotationTarget.CLASS)
annotation class Mob

/**
 * 用于标注，Handler内部用于处理http请求的方法
 */
@Target(AnnotationTarget.FUNCTION)
annotation class HttpMethod(val type: String)

/**
 * Api接口说明文档
 */
annotation class Document(val content: String)

/**
 * 用于标注认证的注解
 * 1、方法注解，对方法注解，该方法的路由需要认证
 * 2、类注解，对类注解，该类下所有方法的路由均需要认证，优先级比方法注解高
 */
annotation class Auth

/**
 * HttpMethod types
 */
const val GET = "GET"
const val POST = "POST"
const val PUT = "PUT"
const val DELETE = "DELETE"
