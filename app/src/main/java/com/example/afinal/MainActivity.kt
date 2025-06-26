package com.example.afinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.afinal.ui.screens.*
import com.example.afinal.ui.theme.FinalTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    lateinit var dbHelper: TodoDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = TodoDBHelper(this)

        enableEdgeToEdge()
        setContent {
            FinalTheme {
                val navController = rememberNavController()
                var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
                val profileDBHelper = remember { ProfileDBHelper(this@MainActivity) }
                val postDBHelper = remember { PostDBHelper(this@MainActivity) }
                val postViewModel: PostViewModel = viewModel()

                val todoItems = remember {
                    mutableStateListOf<TodoItem>().apply {
                        addAll(dbHelper.getAll())
                    }
                }

                var selectedTodoId by remember { mutableStateOf<String?>(null) }

                val screens = listOf(Screen.Todo, Screen.Map, Screen.Blog, Screen.Settings)
                var currentScreen: Screen by remember { mutableStateOf(Screen.Todo) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (currentScreen == Screen.Todo && user != null) {
                                        val imageUri = profileDBHelper.getImageUri()
                                        val isValidUri = !imageUri.isNullOrBlank() &&
                                                (imageUri.startsWith("content://")
                                                        || imageUri.startsWith("file://")
                                                        || imageUri.startsWith("http"))

                                        if (isValidUri) {
                                            Image(
                                                painter = rememberAsyncImagePainter(imageUri),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                    }
                                    Text(currentScreen.title)
                                }
                            },
                            actions = {
                                if (user != null) {
                                    TextButton(onClick = {
                                        FirebaseAuth.getInstance().signOut()
                                        user = null
                                    }) {
                                        Text("로그아웃", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        if (user != null) {
                            NavigationBar {
                                screens.forEach { screen ->
                                    NavigationBarItem(
                                        selected = currentScreen == screen,
                                        onClick = {
                                            currentScreen = screen
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(screen.icon, contentDescription = null) },
                                        label = { Text(screen.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (user == null) {
                            LoginScreen(onLoginSuccess = {
                                user = FirebaseAuth.getInstance().currentUser
                            })
                        } else {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Todo.route
                            ) {
                                composable(Screen.Todo.route) {
                                    TodoList(
                                        todos = todoItems,
                                        onMapClick = { id, latLng ->
                                            selectedTodoId = id
                                            navController.navigate("map/${latLng.latitude}/${latLng.longitude}")
                                        },
                                        onCheckToggle = { id, isDone ->
                                            val index = todoItems.indexOfFirst { it.id == id }
                                            if (index != -1) {
                                                val newItem = todoItems[index].copy(isDone = isDone)
                                                todoItems[index] = newItem
                                                dbHelper.insertOrUpdate(newItem)
                                            }
                                        },
                                        onDelete = { id ->
                                            todoItems.removeAll { it.id == id }
                                            dbHelper.delete(id)
                                        },
                                        onAdd = { title ->
                                            val newId = System.currentTimeMillis().toString()
                                            val newItem = TodoItem(
                                                newId,
                                                title,
                                                false,
                                                LatLng(37.5665, 126.9780)
                                            )
                                            todoItems.add(newItem)
                                            dbHelper.insertOrUpdate(newItem)
                                        }
                                    )
                                }

                                composable(Screen.Map.route) {
                                    MapScreen(location = LatLng(37.5665, 126.9780)) {}
                                }

                                composable(Screen.Settings.route) {
                                    SettingsScreen(
                                        onEditProfile = {
                                            navController.navigate("profile")
                                        },
                                        onLogout = {
                                            FirebaseAuth.getInstance().signOut()
                                            user = null
                                            navController.navigate(Screen.Todo.route) {
                                                popUpTo(0)
                                            }
                                        }
                                    )
                                }

                                composable(Screen.Blog.route) {
                                    PostScreen(
                                        dbHelper = postDBHelper,
                                        viewModel = postViewModel,
                                        onAddPost = {
                                            postViewModel.reset()
                                            navController.navigate("postDetail?postId=-1")
                                        },
                                        navController = navController
                                    )
                                }

                                composable("profile") {
                                    ProfileScreen(
                                        userEmail = user?.email ?: "unknown",
                                        dbHelper = profileDBHelper,
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(
                                    "postView/{postId}",
                                    arguments = listOf(navArgument("postId") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val postId = backStackEntry.arguments?.getInt("postId")
                                    val post = postId?.let { postDBHelper.getPostById(it) }

                                    if (post != null) {
                                        PostDetailViewScreen(
                                            post = post,
                                            onBack = { navController.popBackStack() },
                                            onEdit = {
                                                postViewModel.setPost(post)
                                                navController.navigate("postDetail?postId=${post.id}")
                                            }
                                        )
                                    }
                                }

                                composable(
                                    "postDetail?postId={postId}",
                                    arguments = listOf(
                                        navArgument("postId") {
                                            type = NavType.IntType
                                            defaultValue = -1
                                        }
                                    )
                                ) { backStackEntry ->
                                    val postId = backStackEntry.arguments?.getInt("postId")
                                    val post = if (postId != null && postId != -1) postDBHelper.getPostById(postId) else null
                                    PostDetailScreen(
                                        dbHelper = postDBHelper,
                                        viewModel = postViewModel,
                                        post = post,
                                        onBack = {
                                            postViewModel.loadPosts(postDBHelper)
                                            navController.popBackStack()
                                        },
                                        onSelectLocation = { latLng: LatLng? ->
                                            postViewModel.setLocation(latLng)
                                            navController.navigate("selectLocation")
                                        }
                                    )
                                }

                                composable("selectLocation") {
                                    MapScreen(
                                        location = LatLng(37.5665, 126.9780),
                                        onLocationSelected = { newLatLng: LatLng ->
                                            postViewModel.setLocation(newLatLng)
                                            navController.popBackStack()
                                        }
                                    )
                                }

                                composable(
                                    "map/{lat}/{lng}",
                                    arguments = listOf(
                                        navArgument("lat") { type = NavType.FloatType },
                                        navArgument("lng") { type = NavType.FloatType }
                                    )
                                ) { backStackEntry ->
                                    val lat = backStackEntry.arguments?.getFloat("lat") ?: 0f
                                    val lng = backStackEntry.arguments?.getFloat("lng") ?: 0f
                                    MapScreen(
                                        location = LatLng(lat.toDouble(), lng.toDouble()),
                                        onLocationSelected = { newLatLng ->
                                            selectedTodoId?.let { id ->
                                                val index = todoItems.indexOfFirst { it.id == id }
                                                if (index != -1) {
                                                    val newItem = todoItems[index].copy(location = newLatLng)
                                                    todoItems[index] = newItem
                                                    dbHelper.insertOrUpdate(newItem)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    sealed class Screen(val route: String, val label: String, val icon: ImageVector, val title: String) {
        object Todo : Screen("todoList", "할일", Icons.Default.List, "LoveMap")
        object Map : Screen("mapScreen", "지도", Icons.Default.Map, "지도")
        object Blog : Screen("blogScreen", "게시글", Icons.Default.Edit, "게시글")
        object Settings : Screen("settings", "설정", Icons.Default.Settings, "설정")
    }
}
