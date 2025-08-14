package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.viewmodel.RegisterViewModel
import com.bersamadapa.recylefood.viewmodel.RegisterState
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bersamadapa.recylefood.ui.component.textinput.TextFieldCustom
import com.bersamadapa.recylefood.ui.navigation.Screen

@Composable
fun RegisterScreen(
    navController: NavController,
) {
    val authRepository = RepositoryProvider.authRepository
    val factory = ViewModelFactory { RegisterViewModel(authRepository) } // Pass lambda to create the ViewModel instance
    val viewModel: RegisterViewModel = viewModel(factory = factory)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var retypedpassword by remember { mutableStateOf("") }
    var retypedpasswordVisibility by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var noHandphone by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val registerState by viewModel.registerState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.background_login_register),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 24.dp, horizontal = 38.dp)
        ) {
            Row(
                Modifier.clickable { navController.navigate(Screen.Login.route) },
                verticalAlignment = Alignment.CenterVertically, // Align icon and text vertically
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between the icon and text
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with your desired icon
                    contentDescription = "Back Icon",
                    modifier = Modifier.size(24.dp) // Adjust icon size if needed
                )
                Text(
                    "Kembali Ke Login",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Register", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(20.dp))

            TextFieldCustom(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text("Username", style = MaterialTheme.typography.labelSmall)
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Person Icon")
                },
            )
            Spacer(modifier = Modifier.height(16.dp))


            // Email Field
            TextFieldCustom(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text("Email", style = MaterialTheme.typography.labelSmall)
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon")
                },
            )
            Spacer(modifier = Modifier.height(16.dp))


            TextFieldCustom(
                value = noHandphone,
                onValueChange = { noHandphone = it },
                label = {
                    Text("Nomor Handphone", style = MaterialTheme.typography.labelSmall)
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone Icon")
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

            TextFieldCustom(
                value = retypedpassword,
                onValueChange = { retypedpassword = it },
                label = { Text("Re-typed Password", style = MaterialTheme.typography.labelSmall) },
                visualTransformation = if (retypedpasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon")
                },
                trailingIcon = {
                    IconButton(onClick = { retypedpasswordVisibility = !retypedpasswordVisibility }) {
                        Icon(
                            painter = painterResource(id = if (retypedpasswordVisibility) R.drawable.visibility_on else R.drawable.visibility_off),
                            contentDescription = if (retypedpasswordVisibility) "Hide Password" else "Show Password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            if (registerState is RegisterState.Error) {
                Text(
                    text = (registerState as RegisterState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Register Button
            Button(
                onClick = { viewModel.register(username, email, noHandphone, password, retypedpassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF402F2C)
                )
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator
            if (registerState is RegisterState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Media Login Section
            Text(
                text = "Or login with",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Google Login
                IconButton(onClick = { /* Handle Google Login */ }) {
                    AsyncImage(
                        model = R.drawable.ic_google,
                        contentDescription = "Google Login",
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, Color.Black, CircleShape)
                            .padding(8.dp)
                    )
                }
                // Facebook Login
                IconButton(onClick = { /* Handle Facebook Login */ }) {
                    AsyncImage(
                        model = R.drawable.ic_facebook,
                        contentDescription = "Facebook Login",
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, Color.Black, CircleShape)
                            .padding(8.dp)
                    )
                }
                // Twitter Login
                IconButton(onClick = { /* Handle Twitter Login */ }) {
                    AsyncImage(
                        model = R.drawable.ic_twitter,
                        contentDescription = "Twitter Login",
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, Color.Black, CircleShape)
                            .padding(8.dp)
                    )
                }
            }


            if (registerState is RegisterState.Error) {
                Text((registerState as RegisterState.Error).message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        LaunchedEffect(registerState) {
            if (registerState is RegisterState.Success) {
                navController.navigate(Screen.Login.route)
            }
        }
    }
}
