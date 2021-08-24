package com.snowstorm.snowstorm_api.handler.user

import com.snowstorm.snowstorm_api.annotations.Api
import com.snowstorm.snowstorm_api.handler.base.BaseHandler
import com.snowstorm.snowstorm_api.params.user.LoginParam
import io.vertx.ext.web.RoutingContext

@Api
class UserHandler : BaseHandler() {

    fun login(context: RoutingContext, params: LoginParam) {

    }
}