package com.example.spruce.api

import com.example.spruce.api.response.CategoryResponse
import com.example.spruce.api.response.PostImageModel
import com.example.spruce.api.response.HomeScreenResponse
import com.example.spruce.api.response.MetaTagResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface  ApiInterface {

    @GET("wp-json/wp/v2/posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Response<List<HomeScreenResponse>>

    @GET("wp-json/wp/v2/categories")
    suspend fun getPostCategories(
        @Query("post") postId: Int
    ): List<CategoryResponse>

    @GET("index.php/wp-json/wp/v2/tags")
    suspend fun getPostTags(
        @Query("post") postId: Int
    ): Response<List<MetaTagResponse>>

    @GET("wp-json/wp/v2/media/{id}")
    suspend fun getPostImage(
        @Path("id") mediaId: Int
    ): Response<PostImageModel>


    @GET("wp-json/wp/v2/categories")
    suspend fun getCategories(): Response<List<CategoryResponse>>
}