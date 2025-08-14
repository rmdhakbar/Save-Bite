package com.bersamadapa.recylefood.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.textinput.TextFieldCustom
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.utils.getInputStreamFromUri
import com.bersamadapa.recylefood.viewmodel.UpdateState
import com.bersamadapa.recylefood.viewmodel.UserDataState
import com.bersamadapa.recylefood.viewmodel.UserViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun EditProfileScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val userRepository = RepositoryProvider.userRepository
    val factory = ViewModelFactory { UserViewModel(userRepository) }
    val viewModel: UserViewModel = viewModel(factory = factory)

    val userId = navController.currentBackStackEntry?.arguments?.getString("userId")
    val userDataState by viewModel.userDataState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    val nameState = remember { mutableStateOf("") }
    val noHandphoneState = remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()



    // Handle image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()

    ) { uri: Uri? ->
        if (uri != null) selectedImageUri.value = uri
        else coroutineScope.launch { snackbarHostState.showSnackbar("No image selected") }
    }


    // Load user data if needed
    LaunchedEffect(userId) {
        userId?.let { viewModel.fetchUserById(it) }
    }

    // Pre-fill name and image URI if user data is available
    LaunchedEffect(userDataState) {
        if (userDataState is UserDataState.Success) {
            val userData = (userDataState as UserDataState.Success).user
            nameState.value = userData.username.toString()
            selectedImageUri.value = userData.profilePicture?.url?.toUri()
            noHandphoneState.value = userData.noHandphone.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {


            Row(
                Modifier.clickable { navController.navigate(Screen.Profile.route) },
                verticalAlignment = Alignment.CenterVertically, // Align icon and text vertically
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between the icon and text
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with your desired icon
                    contentDescription = "Back Icon",
                    modifier = Modifier.size(24.dp) // Adjust icon size if needed
                )
                Text(
                    "Kembali",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                val painter = if (selectedImageUri.value == null) {
                    painterResource(id = R.drawable.baked_goods_1)
                } else {
                    rememberAsyncImagePainter(model = selectedImageUri.value)
                }

                Image(
                    painter = painter,
                    contentDescription = "Selected Profile Picture",
                    contentScale = ContentScale.Crop, // Ensures the image fits into the circle
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { imagePickerLauncher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name Input Field
            TextFieldCustom(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Name",style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Person Icon")
                },
            )

            TextFieldCustom(
                value = noHandphoneState.value,
                onValueChange = { noHandphoneState.value = it },
                label = {
                    Text("Nomor Handphone", style = MaterialTheme.typography.labelSmall)
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone Icon")
                },
            )
        }

        // Handle update states
        when (updateState) {
            is UpdateState.Loading -> {
                Text("Updating profile...", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is UpdateState.Error -> {
                val errorMessage = (updateState as UpdateState.Error).message
                Text("Error: $errorMessage", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is UpdateState.Success -> {
                Text("Profile updated successfully!", color = Color.Green, modifier = Modifier.align(Alignment.CenterHorizontally))
                LaunchedEffect(updateState) {
                    navController.navigate(Screen.Profile.route)
                }
            }

            UpdateState.Idle -> { }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                val updatedName = nameState.value
                val updatedUri = selectedImageUri.value

                if (updatedUri != null && userId != null) {
                    val inputStream = getInputStreamFromUri(context, updatedUri)
                    if (inputStream != null) {
                        val tempFile = File(context.cacheDir, "profile_picture.jpg")
                        inputStream.copyTo(tempFile.outputStream())

                        val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                        val profilePicturePart = MultipartBody.Part.createFormData(
                            "profile_picture",
                            tempFile.name,
                            requestBody
                        )

                        viewModel.updateUser(userId, updatedName, profilePicturePart)
                    } else {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Failed to retrieve the image") }
                    }
                } else {
                    coroutineScope.launch { snackbarHostState.showSnackbar("Please select an image") }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Save")
        }
    }
}
