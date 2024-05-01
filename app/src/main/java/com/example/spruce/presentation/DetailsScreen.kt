package com.example.spruce.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.example.spruce.R
import com.example.spruce.api.response.HomeScreenResponse
import com.example.spruce.ui.theme.fonts
import com.example.spruce.ui.theme.normalFonts
import com.example.spruce.utils.DataManager

@Composable
fun DetailScreen(lazyListState: LazyListState = rememberLazyListState(), postModel: HomeScreenResponse) {

    var tags by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val temp = StringBuilder()
        for (it in postModel.hashTags.indices) {
            temp.append("${postModel.hashTags[it].name}${if (it < postModel.hashTags.size - 1) ", " else ""}")
        }
        tags = temp.toString()
    }

    LazyColumn(
        state = lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        item {
            Text(
                text = postModel.title.rendered,
                fontFamily = normalFonts,
                fontWeight = FontWeight.SemiBold,
                fontSize = 27.sp,
                textAlign = TextAlign.Center,
                lineHeight = TextUnit(1.5f, TextUnitType.Em),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)

            ) {

                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = "Calendar Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = DataManager.dateFormat(postModel.date),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = normalFonts
                )

            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Filled.Tag,
                    contentDescription = "Callender Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = tags,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = normalFonts
                )

            }

            AsyncImage(
                model = postModel.imageShow,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5 / 4f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "${HtmlCompat.fromHtml(postModel.content.rendered.trim(), HtmlCompat.FROM_HTML_MODE_COMPACT)}",
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp),
                fontSize = 16.sp
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = Color.LightGray
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 15.dp)
                    .fillMaxWidth(),
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
                        .size(50.dp)
                        .background(color = Color.LightGray, shape = CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Hesta-Admin",
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 5.dp)
                )

                Spacer(modifier = Modifier.weight(1f))



                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painterResource(id = R.drawable.social_facebook_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(3.dp)
                            .size(30.dp),
                        tint = Color.Black
                    )
                }


                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painterResource(id = R.drawable.twitter_bird_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(3.dp)
                            .size(30.dp),
                        tint = Color.Black
                    )
                }


                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painterResource(id = R.drawable.wifi_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(3.dp)
                            .size(30.dp)
                            .rotate(45f),
                        tint = Color.Black
                    )
                }
            }

        }

    }

}