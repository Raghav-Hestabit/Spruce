package com.example.spruce.repo

import androidx.core.text.HtmlCompat
import com.example.spruce.api.ApiInterface
import com.example.spruce.api.response.CategoryResponse
import com.example.spruce.api.response.HomeScreenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


class Repository @Inject constructor(private val apiService: ApiInterface) {

    fun getCategories(): Flow<Response<List<CategoryResponse>>> = flow {
        emit(apiService.getCategories())
    }.flowOn(Dispatchers.IO)

    fun getPosts(page: Int, limit: Int): Flow<Response<List<HomeScreenResponse>>> = flow {
        val response = apiService.getPosts(perPage = limit, page = page)

        if (response.isSuccessful) {
            val categoriesResponse = apiService.getCategories()
            val categories = if (categoriesResponse.isSuccessful) {
                categoriesResponse.body()
            } else {
                emptyList()
            }

            val posts = response.body() ?: emptyList()

            coroutineScope {
                posts.forEach { post ->
                    val postCategories = post.categories
                    val metaTag= apiService.getPostTags(postId = post.id)

                    categories!!.forEach { category ->
                        if (category.id in postCategories) {
                            post.category += category.name
                        }
                    }
                    post.hashTags =  metaTag.body()!!
                    launch {
                        val featuredMediaId = post.featured_media
                        val mediaResponse = apiService.getPostImage(featuredMediaId)
                        val image = mediaResponse.body()?.guid?.rendered ?: ""
                        post.imageShow = image
                    }

                    post.content.rendered = HtmlCompat.fromHtml(post.content.rendered.trim(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString()


                }
            }
        }

        emit(response)
    }.flowOn(Dispatchers.IO)



}

