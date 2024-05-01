package com.example.spruce.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.spruce.R
import com.example.spruce.api.response.HomeScreenResponse
import com.example.spruce.ui.theme.BottomCardBrush
import com.example.spruce.ui.theme.GradientCardBrush
import com.example.spruce.ui.theme.SpruceThemeColor
import com.example.spruce.ui.theme.ThemeColor
import com.example.spruce.ui.theme.fonts
import com.example.spruce.ui.theme.normalFonts
import com.example.spruce.utils.DataManager
import com.example.spruce.utils.DataManager.allPosts
import com.example.spruce.utils.Resource
import com.example.spruce.viewModels.HomeScreenViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeScreen(fromScreen:String,lazyListState: LazyListState = rememberLazyListState(), postDetails: (postModel:HomeScreenResponse) -> Unit) {

    val viewModel: HomeScreenViewModel = hiltViewModel()
    val postResult = viewModel.postDataFlowData.collectAsState()


    val postList by rememberSaveable {
        mutableStateOf(arrayListOf<HomeScreenResponse>())
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var isPageLoading by remember {
        mutableStateOf(false)
    }



    postResult.value.let {
        when (it) {
            is Resource.Error -> {
                isLoading = false
                isPageLoading = false
                if (it.message!!.contains("The page number requested is larger than the number of pages available")){
                    viewModel.isLastPageReached = true
                }else{
                    Toast.makeText(LocalContext.current, it.message, Toast.LENGTH_SHORT).show()
                }
            }

            is Resource.Loading -> {
                if (viewModel.mPage == 1){
                    isLoading = true
                    isPageLoading = false
                }else{
                    isPageLoading = true
                }


            }

            is Resource.Success -> {
                isLoading = false
                isPageLoading = false
                val newData = it.data!!.filter { newPost ->
                    postList.none { existingPost ->
                        newPost.id == existingPost.id
                    }
                }
                postList.addAll(newData)
                allPosts += newData


            }
            is Resource.Empty -> {

            }
        }
    }


    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (isLoading) {
            item {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column {
                        CircularProgressIndicator(modifier = Modifier.padding(start = 10.dp))
                        Text(
                            modifier = Modifier
                                .padding(top = 5.dp),
                            text = "Loading...",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = normalFonts,
                        )
                    }
                }
            }
        } else {
            items(postList.size) {
                if (it < 3) {
                    HomeScreenRecentTop(postModel = postList[it])
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "FEATURED POST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = normalFonts
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(.2f)
                            .clip(shape = CircleShape),
                        thickness = 3.dp,
                        color = SpruceThemeColor()
                    )

                }
            }

            items(postList.size) {
                FeaturedPostCard(postModel = postList[it]) { postDetails(postList[it]) }

                if (it == postList.size - 2 && !viewModel.isLastPageReached) {
                    viewModel.mPage++
                    viewModel.getPostData()

                    CircularProgressIndicator()

                }


            }




        }



    }
}




@Composable
fun HomeScreenRecentTop(postModel: HomeScreenResponse) {
    val context = LocalContext.current

    var categories by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val temp = StringBuilder()
        for (it in postModel.category.indices) {
            temp.append("${postModel.category[it]}${if (it < postModel.category.size - 1) ", " else ""}")
        }
        categories = temp.toString()
    }

    ElevatedCard(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .padding(vertical = 10.dp)
            .aspectRatio(5 / 4f)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Box {
            AsyncImage(
                model = postModel.imageShow,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(brush = GradientCardBrush())
                    .padding(8.dp)
            ) {

                Text(
                    text = "Categories : $categories",
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .padding(horizontal = 10.dp),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = normalFonts,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = postModel.title.rendered,
                    maxLines = 1,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = normalFonts,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = "Calendar Icon",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        text = DataManager.dateFormat(postModel.date),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fonts
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = "",
                        contentDescription = "Author Image",
                        error = painterResource(
                            id = R.drawable.person_image_place_holder
                        ),
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(25.dp)
                            .background(color = Color.LightGray, shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "Hesta-admin",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fonts,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        modifier = Modifier
                            .clickable {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, postModel.imageShow)
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        postModel.title.rendered
                                    )
                                )

                            }

                            .padding(end = 7.dp)
                            .width(30.dp)
                            .height(30.dp)
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .padding(8.dp),

                        colorFilter = ColorFilter.tint(Color.White),
                        painter = painterResource(id = R.drawable.share_icon),
                        contentDescription = "content",
                    )

                }

            }
        }

    }

}




@Composable
fun FeaturedPostCard(modifier: Modifier=Modifier, postModel: HomeScreenResponse, postDetails: () -> Unit) {
    val context =  LocalContext.current
    var categories by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val temp = StringBuilder()
        for (it in postModel.category.indices) {
            temp.append("${postModel.category[it]}${if (it < postModel.category.size - 1) ", " else ""}")
        }
        categories = temp.toString()
    }

    ElevatedCard(
        onClick = { postDetails() },
        modifier = modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .heightIn(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {

        Column {
            Box(contentAlignment = Alignment.BottomStart) {
                AsyncImage(
                    model = postModel.imageShow,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(100.dp)
                )


                LazyRow(
                    modifier = Modifier
                        .background(brush = BottomCardBrush())
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(postModel.hashTags.size) {
                        Text(
                            text = postModel.hashTags[it].name,
                            modifier = Modifier
                                .padding(
                                    start = if (it == 0) 12.dp else 0.dp,
                                    end = if (it == 4) 12.dp else 0.dp
                                )
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(7.dp)
                                )
                                .padding(horizontal = 17.dp, vertical = 5.dp),
                            fontSize = 13.sp,
                            fontFamily = fonts,
                            fontWeight = FontWeight.Normal,
                            color = Color.DarkGray
                        )
                    }
                }

            }

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {

                Text(
                    text = postModel.title.rendered,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 19.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Categories :",
                        fontFamily = fonts,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = categories,
                        fontSize = 14.sp,
                        maxLines = 1,
                        fontFamily = fonts,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 3.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(vertical = 5.dp),
                    color = SpruceThemeColor()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Calendar Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        text = DataManager.dateFormat(postModel.date),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fonts,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${HtmlCompat.fromHtml(postModel.content.rendered.trim(), HtmlCompat.FROM_HTML_MODE_COMPACT)}",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = normalFonts,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )


                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                        AsyncImage(
                        model = "",
                        contentDescription = "Author Image",
                        error = painterResource(
                            id = R.drawable.person_image_place_holder
                        ),
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(25.dp)
                            .background(color = Color.LightGray, shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "Hesta-admin",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fonts
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        modifier = Modifier
                            .clickable {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, postModel.imageShow)
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        postModel.title.rendered
                                    )
                                )
                            }
                            .width(30.dp)
                            .height(30.dp)
                            .border(
                                width = 1.dp,
                                color = ThemeColor.invoke(),
                                shape = RoundedCornerShape(2.dp)
                            )
                            .padding(8.dp)
                        ,

                        painter = painterResource(id = R.drawable.share_icon),
                        contentDescription = "content"
                    )

                    Text(modifier = Modifier
                        .clickable {
                            postDetails()
                        }
                        .padding(start = 10.dp)
                        .background(color = ThemeColor.invoke(), shape = RoundedCornerShape(3.dp))
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                        text = "Read More",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = normalFonts,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )


                }

            }

        }

    }

}

