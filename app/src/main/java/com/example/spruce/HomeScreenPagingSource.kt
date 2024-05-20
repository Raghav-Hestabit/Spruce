package com.example.spruce

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.spruce.api.response.HomeScreenResponse
import com.example.spruce.repo.Repository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class HomeScreenPagingSource(private val repo:Repository) : PagingSource<Int, HomeScreenResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeScreenResponse> {
        try {
            // Fetch data from your API using params.key (page number)
            val nextPageNumber = params.key ?: 1 // Default page number
            val data = repo.getPosts(page = nextPageNumber, limit = 10).map { response ->
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            }.single()

            return LoadResult.Page(
                data = data,
                prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, HomeScreenResponse>): Int? {
        TODO("Not yet implemented")
    }
}
