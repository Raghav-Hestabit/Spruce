package com.example.spruce.api.response


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class SideMenu(
    val id:Int = 0,
    val icon: ImageVector,
    val title: String,
    val route: String,
    val modifier: Modifier = Modifier,
    var isSubcategoryVisible: Boolean = false
)
