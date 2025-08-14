package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.bottomBar.BottomNavBar
import com.bersamadapa.recylefood.ui.component.profile.ProfileMenuItem
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.viewmodel.UserDataState
import com.bersamadapa.recylefood.viewmodel.UserViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
) {
    val userRepository = RepositoryProvider.userRepository
    val factory = ViewModelFactory { UserViewModel(userRepository) }
    val viewModel: UserViewModel = viewModel(factory = factory)
    val userDataState by viewModel.userDataState.collectAsState()
    val dataStoreManager = DataStoreManager(LocalContext.current)
    val userId by dataStoreManager.userId.collectAsState("")

    if (userDataState is UserDataState.Idle && userId?.isNotEmpty() == true) {
        userId?.let { viewModel.fetchUserById(it) }
    }

    val coroutineScope = rememberCoroutineScope() // To launch a coroutine


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            when (val state = userDataState) {
                is UserDataState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UserDataState.Success -> {
                    val user = state.user

                    // Profile Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile Picture
                        val painter = if (user.profilePicture == null) {
                            painterResource(id = R.drawable.default_profile_picture)
                        } else {
                            rememberAsyncImagePainter(
                                model = user.profilePicture!!.url,
                                placeholder = painterResource(R.drawable.loading_placeholder),
                                error = painterResource(R.drawable.default_profile_picture)
                            )
                        }

                        Image(
                            painter = painter,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop, // Ensures the image fits into the circle
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape) // Makes the image circular
                                .background(Color.Gray) // Adds a gray background
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = user.username ?: "Unknown",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = user.email ?: "Unknown",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                )
                            )
                            Text(
                                text = user.noHandphone ?: "Unknown",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                )
                            )
                        }

                        IconButton(
                            onClick = { navController.navigate("edit_profile/${userId}") }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit Profile"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()

                    // Menu Section
                    ProfileMenuItem(
                        icon = R.drawable.ic_activity,
                        title = "Aktivitasku",
                        subtitle = "Cek riwayat & aktivitas aktif",
                        onClick = { /* Navigate to activity screen */ }
                    )

                    HorizontalDivider()

                    ProfileMenuItem(
                        icon = R.drawable.ic_promo,
                        title = "Promo",
                        subtitle = "",
                        onClick = { navController.navigate(Screen.VoucherScreen.route) }
                    )

                    HorizontalDivider()

                    ProfileMenuItem(
                        icon = R.drawable.ic_logout,
                        title = "Logout",
                        subtitle = "",
                        onClick = {
                            coroutineScope.launch {
                                dataStoreManager.clearData() // Clear user data from DataStore
                                navController.navigate(Screen.Login.route) // Navigate to the login screen
                            }
                        }
                    )
                }

                is UserDataState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Error: ${state.message}")
                    }
                }

                else -> Unit
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Bottom Navigation Bar
            BottomNavBar(
                navController = navController,
                selectedTab = "Profile",
            )

        }


    }
}
