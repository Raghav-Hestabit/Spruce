package com.example.spruce.api.response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    val id: Int = 0,
    val count: Int = 0,
    val description: String = "",
    val name: String = "",
    val slug: String = "",
    val _links: LinksObj = LinksObj()
)

data class LinksObj(
    @SerializedName("wp:post_type")
    val postsList: List<PostsLink> = emptyList()
)

data class PostsLink(val href: String = "")
