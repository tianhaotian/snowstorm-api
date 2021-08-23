package com.snowstorm.snowstorm_api.util

import io.vertx.core.MultiMap
import io.vertx.core.json.JsonObject
import kotlin.reflect.KParameter
import kotlin.reflect.KType

object DataBinder {
    private val STRING = String::class.starProjectedType
    private val INT = Int::class.starProjectedType
    private val DOUBLE = Double::class.starProjectedType
    private val FLOAT = Float::class.starProjectedType
    private val BOOLEAN = Boolean::class.starProjectedType
    private val JSON_OBJECT = JsonObject::class.starProjectedType

    fun bind(data: MultiMap, type: Class<*>): Any {
        val constructor = type.kotlin.constructors.first()
        val args = mutableMapOf<KParameter, Any?>()
        for (kp in constructor.parameters) {
            val paramName = kp.name
            var realParamName: String? = null
            if (data.contains(paramName)) {
                realParamName = paramName
            } else {
                val underlineParamName = StringUtils.toUnderline(paramName!!)
                if (data.contains(underlineParamName)) {
                    realParamName = underlineParamName
                }
            }
            if (realParamName == null) {
                if (!kp.isOptional) {
                    throw MissingParamException(paramName!!)
                }
            } else {
                val paramValue = data[realParamName]
                args[kp] = castValue(paramValue, kp.type)
            }
        }
        return constructor.callBy(args)
    }

    private fun castValue(value: String, type: KType): Any = when (type) {
        STRING -> value
        INT -> value.toInt()
        DOUBLE -> value.toDouble()
        FLOAT -> value.toFloat()
        BOOLEAN -> value.toBoolean()
        JSON_OBJECT -> JsonObject(value)
        else -> throw Exception("can not cast $value to $type")
    }
}