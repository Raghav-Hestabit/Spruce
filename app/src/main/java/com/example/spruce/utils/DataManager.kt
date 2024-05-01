package com.example.spruce.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardTravel
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material.icons.rounded.Nature
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.SevereCold
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.Water
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.spruce.api.response.HomeScreenResponse
import java.text.SimpleDateFormat
import java.util.Locale

object DataManager {
    var postModel = HomeScreenResponse()
    var allPosts : List<HomeScreenResponse> = listOf()
    fun findIcon(slug: String): ImageVector {

        return when (slug) {
            "explorer" -> Icons.Rounded.TravelExplore
            "nature" -> Icons.Rounded.Nature
            "ocean" -> Icons.Rounded.Water
            "photography" -> Icons.Rounded.PhotoCamera
            "safari" -> Icons.Rounded.Directions
            "summer" -> Icons.Rounded.WbSunny
            "travelling" -> Icons.Rounded.CardTravel
            "value-for-money" -> Icons.Rounded.Money
            "winters" -> Icons.Rounded.SevereCold
            else -> {
                Icons.Rounded.Category
            }
        }

    }

    fun dateFormat(temp: Any): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())

        val inputDate = "2019-06-11T12:19:04"
        val date = inputFormat.parse(inputDate)
        val formattedDate = outputFormat.format(date)

        return formattedDate
    }

}