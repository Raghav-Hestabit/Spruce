package com.example.spruce.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spruce.api.response.HomeScreenResponse
import com.example.spruce.utils.DataManager

@Composable
fun PostScreen(postDetail: (postModel: HomeScreenResponse) -> Unit) {

    LazyColumn {
        items(DataManager.allPosts.size){
            FeaturedPostCard(modifier=Modifier
                .padding(horizontal = 10.dp),postModel = DataManager.allPosts[it]) {
                postDetail(DataManager.allPosts[it])
            }
        }
    }

}