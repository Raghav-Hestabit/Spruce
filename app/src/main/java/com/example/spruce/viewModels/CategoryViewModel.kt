package com.example.spruce.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spruce.api.Constants
import com.example.spruce.repo.Repository
import com.example.spruce.api.response.PojoClass
import com.example.spruce.api.response.CategoryResponse
import com.example.spruce.utils.Resource
import com.example.spruce.utils.NetworkHelper
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val repository: Repository, private val networkHelper: NetworkHelper) :
    ViewModel() {

    private val listCategoryData: MutableStateFlow<Resource<List<CategoryResponse>>> = MutableStateFlow(
        Resource.Empty())
    val listCategoryObserveData: StateFlow<Resource<List<CategoryResponse>>> = listCategoryData


    init {
        listCategoryApi()
    }

    private fun listCategoryApi() = viewModelScope.launch {
        listCategoryData.value = Resource.Loading()

        if (networkHelper.hasInternetConnection()) {

            repository.getCategories()
                .catch { e ->
                    listCategoryData.value = Resource.Error(e.message.toString())
                }.collect { data ->
                    listCategoryData.value = lisCategoryResponseHandle(data)
                }
        } else {
            listCategoryData.value = Resource.Error(Constants.NO_INTERNET)
        }

    }

    private fun lisCategoryResponseHandle(response: Response<List<CategoryResponse>>): Resource<List<CategoryResponse>> {
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