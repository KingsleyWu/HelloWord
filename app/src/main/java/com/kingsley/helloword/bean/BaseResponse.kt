package com.kingsley.helloword.bean

class BaseResponse<T> {
    var code = 0
    var message: String? = null
    var data: T? = null
}