package com.example.spruce.api.response

import com.google.gson.annotations.SerializedName

class ResponseDto<T : Any?> {
    @SerializedName("results")
    val results: T? = null

    @SerializedName("page")
    val page: Int? = null

    @SerializedName("total_pages")
    val totalPages: Int? = null

    @SerializedName("total_results")
    val totalResults: Int? = null
}