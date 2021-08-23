package com.snowstorm.snowstorm_api.config

import lombok.Data

@Data
class PostgreConfig {
    var host: String ?= null
    var port: Int ?= null
    var username: String ?= null
    var password: String ?= null
}