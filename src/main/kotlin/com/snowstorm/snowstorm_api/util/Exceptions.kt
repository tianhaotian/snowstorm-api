package com.snowstorm.snowstorm_api.util

class MissingParamException(val param: String) : Exception(param)