package com.example.spruce.api.response


data class HomeScreenResponse(
    val id: Int = 0,
    val date: String = "",
    var imageShow: String = "",
    var category: List<String> = emptyList(),
    val title: PostTitle = PostTitle(),
    val content: PostContent = PostContent(),
    val featured_media: Int = 0,
    val categories: List<Int> = emptyList(),
    var hashTags: List<MetaTagResponse> = emptyList(),
)



data class PostTitle(val rendered: String = "")

data class PostContent(var rendered: String = "")
