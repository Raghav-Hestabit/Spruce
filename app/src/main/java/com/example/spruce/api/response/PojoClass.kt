package com.example.spruce.api.response

data class PojoClass(
    var code: String="",
    var message : String="",
    val data: StatusCode = StatusCode()
    )

data class StatusCode(
    val status:Int = 0
)

