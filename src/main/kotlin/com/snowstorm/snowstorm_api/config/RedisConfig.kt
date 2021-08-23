package com.snowstorm.snowstorm_api.config

import lombok.Data

@Data
class RedisConfig {
    var host: String ?= null
    var port: Int ?= null
}