package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.textinput.TextFieldCustom
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.viewmodel.LoginState
import com.bersamadapa.recylefood.viewmodel.LoginViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@Composable
fun LoginScreen(
    navController: NavController,
) {
    val authRepository = RepositoryProvider.authRepository
    val factory = ViewModelFactory { LoginViewModel(authRepository) }
    val viewModel: LoginViewModel = viewModel(factory = factory)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()

    val dataStoreManager = DataStoreManager(LocalContext.current)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.background_login_register), // Replace with your image
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        // Top Greeting Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 150.dp)
        ) {
            Text(
                text = "Hello!",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 50.sp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Welcome to savebite!",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W300, fontSize = 25.sp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Login Form Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 24.dp, horizontal = 38.dp)
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            TextFieldCustom(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text("Email",style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon")
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            TextFieldCustom(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", style = MaterialTheme.typography.labelSmall) },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon")
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            painter = painterResource(id = if (passwordVisibility) R.drawable.visibility_on else R.drawable.visibility_off),
                            contentDescription = if (passwordVisibility) "Hide Password" else "Show Password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Login Button
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF402F2C) // Hex color converted to ARGB
                )
            ) {
                Text("LOGIN")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Media Login Section
            Text(
                text = "Atau login dengan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { /* Handle Google Login */ }) {
                    AsyncImage(
                        model = R.drawable.ic_google, // Replace with your SVG resource
                        contentDescription = "Google Login",
                        modifier = Modifier
                            .size(56.dp) // Size of the circular button
                            .border(1.dp, Color.Black, CircleShape) // Circular border
                            .padding(8.dp) // Padding inside the button for centering the image
                    )
                }
                IconButton(onClick = { /* Handle Facebook Login */ }) {
                    AsyncImage(
                        model = R.drawable.ic_facebook, // Replace with your SVG resource
                        contentDescription = "Facebook Login",
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, Color.Black, CircleShape)
                            .padding(8.dp)
                    )
                }
                IconButton(onClick = { /* Handle Twitter Login */ }) {
                    AsyncImage(
                        model = R.drawable.ic_twitter, // Replace with your SVG resource
                        contentDescription = "Twitter Login",
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, Color.Black, CircleShape)
                            .padding(8.dp)
                    )
                }
            }



            Spacer(modifier = Modifier.height(8.dp))

            // Navigation to Register
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically // Aligns items vertically
            ) {
                Text(
                    text = "Belum punya akun?",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text(
                        text = "Daftar disini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }


            // Navigate to Dashboard if Login is Successful
            LaunchedEffect(loginState) {
                when (loginState) {
                    is LoginState.Success -> {
                        val userId = (loginState as LoginState.Success).userId
                        dataStoreManager.saveUserId(userId)
                        navController.navigate(Screen.Dashboard.route)
                    }
                    else -> Unit
                }
            }
        }
    }
}
