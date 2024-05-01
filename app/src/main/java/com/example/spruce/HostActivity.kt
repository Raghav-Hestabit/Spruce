package com.example.spruce

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spruce.presentation.CategoryScreen
import com.example.spruce.utils.DataManager
import com.example.spruce.utils.Resource
import com.example.spruce.api.response.SideMenu
import com.example.spruce.presentation.DetailScreen
import com.example.spruce.presentation.HomeScreen
import com.example.spruce.presentation.navigationRoutes.Routes
import com.example.spruce.presentation.PostScreen
import com.example.spruce.ui.theme.SpruceTheme
import com.example.spruce.ui.theme.SpruceThemeColor
import com.example.spruce.ui.theme.fonts
import com.example.spruce.viewModels.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpruceTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationSetup()
                }
            }
        }
    }
}

@Composable
fun NavigationSetup(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
) {

    val viewModel: CategoryViewModel = hiltViewModel()
    val categories = viewModel.listCategoryObserveData.collectAsState()

    val menus by remember {
        mutableStateOf(
            arrayListOf(
                SideMenu(0,Icons.Rounded.Home, "Home", Routes.Home.name),
                SideMenu(0,Icons.Rounded.PermMedia, "Posts", Routes.Post.name),
                SideMenu(0,Icons.Rounded.Category, "Category", Routes.Category.name)
            )
        )
    }

    categories.value.let {
        when (it) {
            is Resource.Error -> {
                Log.d(
                    "categoryResponse",
                    "NavigationSetup: CategoryFailure - ${it.message}"
                )
            }

            is Resource.Loading -> {

            }

            is Resource.Success -> {
                Log.d("categoryResponse", "NavigationSetup: CategorySuccess - ${it.data}")
                val tempList = it.data!!

                for (categoryModel in tempList) {
                    menus.add(
                        SideMenu(
                            categoryModel.id,
                            DataManager.findIcon(categoryModel.slug),
                            categoryModel.name,
                            Routes.Category.name,
                            Modifier.padding(start = 20.dp)
                        )
                    )
                }
            }
            is Resource.Empty -> {

            }

        }
    }


    var icon: ImageVector by remember {
        mutableStateOf(menus[0].icon)
    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus = menus) { route, itIcon, id,titleName ->

                    scope.launch {
                        drawerState.close()
                    }
                    icon = itIcon

                    navController.navigate(route = if(route == Routes.Category.name) "${route}/${id}/${titleName}" else route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
    ) {

        val lazyListState: LazyListState = rememberLazyListState()

        Scaffold(topBar = {
            CustomTopAppBar(menuIcon = {
                Icon(imageVector = icon, contentDescription = "Side Drawer Icon")
            }, lazyListState) {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()

                    }
                }
            }
        }) {

            val host = NavHost(
                navController = navController,
                startDestination = Routes.Home.name,
                modifier = Modifier.padding(it)
            ) {

                composable(route = Routes.Home.name) {
                    HomeScreen("Main Activity",lazyListState = lazyListState) {
                        DataManager.postModel = it
                        navController.navigate(Routes.PostDetails.name)
                    }
                }

                composable(route = Routes.Post.name) {
                    PostScreen()
                }

                composable(route = "${Routes.Category.name}/{id}/{title}") { backStackEntry ->

                    val id = backStackEntry.arguments?.getString("id")!!
                    val title = backStackEntry.arguments?.getString("title")!!

                    CategoryScreen(id.toInt(), title = title) { postData ->
                        DataManager.postModel = postData
                        navController.navigate(Routes.PostDetails.name)
                    }


                }

                composable(route = Routes.PostDetails.name) {

                    DetailScreen(postModel =DataManager.postModel)
                }

            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    menuIcon: @Composable () -> Unit,
    lazyListState: LazyListState,
    onMenuClicked: () -> Unit
) {

    val size by animateIntAsState(
        targetValue = if (0 != lazyListState.firstVisibleItemIndex) {
            30
        } else {
            40
        },
        label = "dpAnimation",
    )

    CenterAlignedTopAppBar(
        title = {
            Box(modifier = Modifier
                .wrapContentSize(), contentAlignment = Alignment.Center) {
                Image(modifier = Modifier, painter = painterResource(id = R.drawable.app_image), contentDescription = "Header")

            }

        },

        navigationIcon = {
            IconButton(onClick = {
                onMenuClicked()
            }) {
                menuIcon()
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon", modifier = Modifier.size(24.dp))
            }
        }

    )
}

@Composable
private fun DrawerContent(
    menus: ArrayList<SideMenu>,
    onMenuClick: (String, ImageVector,Int,String) -> Unit
) {
    var selected by remember {
        mutableIntStateOf(0)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Spruce",
                fontFamily = fonts,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 2.5.dp)
                        .size(10.dp)
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                        .padding(1.dp)
                        .clip(CircleShape)
                        .background(color = SpruceThemeColor())

                )
                Spacer(modifier = Modifier.width(23.dp))
            }

        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(menus) {

                NavigationDrawerItem(
                    label = { Text(text = it.title) },
                    icon = {

                        Icon(
                            imageVector = it.icon,
                            contentDescription = null,
                            modifier = it.modifier
                        )
                    },
                    selected = menus.indexOf(it) == selected,
                    onClick = {
                        onMenuClick(it.route, it.icon, it.id,it.title)
                        selected = menus.indexOf(it)
                    }
                )

                if (menus.indexOf(it) < 3)
                    HorizontalDivider(thickness = 1.dp)
            }

        }
    }
}
