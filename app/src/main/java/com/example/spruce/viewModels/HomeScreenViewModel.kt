package com.example.spruce.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.spruce.HomeScreenPagingSource
import com.example.spruce.api.Constants
import com.example.spruce.repo.Repository
import com.example.spruce.api.response.PojoClass
import com.example.spruce.api.response.HomeScreenResponse
import com.example.spruce.utils.Resource
import com.example.spruce.utils.NetworkHelper
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repo: Repository, private val networkHelper: NetworkHelper) : ViewModel() {


    private val config = PagingConfig(pageSize = 10)
    private val pager = Pager(config = config) {
        HomeScreenPagingSource(repo)
    }

    fun getPosts(): Flow<PagingData<HomeScreenResponse>> {
        return pager.flow
    }

    val postDataFlowDataPager: Flow<PagingData<HomeScreenResponse>> = pager.flow



    private val postDataFlow: MutableStateFlow<Resource<List<HomeScreenResponse>>> = MutableStateFlow(Resource.Empty())
    val postDataFlowData: StateFlow<Resource<List<HomeScreenResponse>>> = postDataFlow


    var isLastPageReached = false

    var mPage = 1
    private var perPage = 10


    init {
        getPosts()
    }





    fun getPostData() = viewModelScope.launch {
        postDataFlow.value = Resource.Loading()

        if (networkHelper.hasInternetConnection()) {

            repo.getPosts(limit = perPage, page = mPage)
                .catch { e ->
                    postDataFlow.value = Resource.Error(e.message.toString())
                }.collect { data ->
                    postDataFlow.value = postResponseHandle(data)
                }
        } else {
            postDataFlow.value = Resource.Error(Constants.NO_INTERNET)
        }

    }

    private fun postResponseHandle(response: Response<List<HomeScreenResponse>>): Resource<List<HomeScreenResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return Resource.Success(data)
            }
        }
        val gson = GsonBuilder().create()
        var pojo = PojoClass()

        try {
            pojo = gson.fromJson(response.errorBody()!!.string(), pojo::class.java)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Resource.Error(pojo.message)
    }



}
