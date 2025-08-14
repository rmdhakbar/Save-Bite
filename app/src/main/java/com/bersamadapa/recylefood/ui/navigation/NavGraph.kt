package com.bersamadapa.recylefood.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.model.Order
import com.bersamadapa.recylefood.ui.screen.CartScreen
import com.bersamadapa.recylefood.ui.screen.DashboardScreen
import com.bersamadapa.recylefood.ui.screen.EditProfileScreen
import com.bersamadapa.recylefood.ui.screen.LeaderboardPage
import com.bersamadapa.recylefood.ui.screen.LoginScreen
import com.bersamadapa.recylefood.ui.screen.MysteryBoxDetailScreen
import com.bersamadapa.recylefood.ui.screen.OnGoingOrdersDetailScreen
import com.bersamadapa.recylefood.ui.screen.OrderHistoryDetailScreen
import com.bersamadapa.recylefood.ui.screen.OrderHistoryScreen
import com.bersamadapa.recylefood.ui.screen.PaymentScreen
import com.bersamadapa.recylefood.ui.screen.ProfileScreen
import com.bersamadapa.recylefood.ui.screen.RegisterScreen
import com.bersamadapa.recylefood.ui.screen.RestaurantDetailScreen
import com.bersamadapa.recylefood.ui.screen.SearchWithButtonScreen
import com.bersamadapa.recylefood.ui.screen.SearchWithStringScreen
import com.bersamadapa.recylefood.ui.screen.VoucherScreen
import com.bersamadapa.recylefood.ui.screen.VoucherScreenPayment
import com.google.gson.Gson
import java.net.URLDecoder

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object DashboardSearchButton : Screen("dashboard/{selectedSearchButton}")
    object DashboardSearchString : Screen("dashboard/search/{selectedSearchString}")
    object MysteryBoxScreen : Screen("mysteryBox/{mysteryBoxId}")
    object OrderHistoryScreen : Screen("orderHistory")
    object LeaderBoardScreen: Screen("leaderboard")
    object CartScreen : Screen("cart")
    object PaymentScreen : Screen("payment/{mysteryBoxs}")
    object OrderOnGoingDetails: Screen("orderDetails/onGoing/{order}")
    object OrderHistoryDetails: Screen("orderDetails/history/{order}")
    object VoucherScreen : Screen("voucher")
    object VoucherPaymentScreen : Screen("payment_voucher")
    object Profile : Screen("profile")
    object RestaurantDetail : Screen("restaurant_detail/{restaurantId}") // updated route to include parameter
    object EditProfile : Screen("edit_profile/{userId}")
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController,userId:String?) {
    NavHost(
        navController = navController,
        startDestination = if (userId.isNullOrEmpty())  Screen.Login.route else Screen.Dashboard.route,
    ) {

        composable(
            route = Screen.PaymentScreen.route,
            arguments = listOf(navArgument("mysteryBoxs") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedMysteryBoxJson = backStackEntry.arguments?.getString("mysteryBoxs")

            encodedMysteryBoxJson?.let {
                // Decode the JSON string before deserializing
                val decodedMysteryBoxJson = URLDecoder.decode(it, "UTF-8")

                // Deserialize the decoded JSON string into a list of MysteryBox objects
                val mysteryBoxs: List<MysteryBox> = Gson().fromJson(decodedMysteryBoxJson, Array<MysteryBox>::class.java).toList()

                // Pass the list to the PaymentScreen composable
                PaymentScreen(navController, mysteryBoxs)
            }
        }

        composable(
            route = Screen.OrderOnGoingDetails.route,
            arguments = listOf(navArgument("order") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedMysteryBoxJson = backStackEntry.arguments?.getString("order")

            encodedMysteryBoxJson?.let {
                // Decode the JSON string before deserializing
                val decodedMysteryBoxJson = URLDecoder.decode(it, "UTF-8")

                // Deserialize the decoded JSON string into a list of MysteryBox objects
                val order: Order = Gson().fromJson(decodedMysteryBoxJson, Order::class.java)

                // Pass the list to the PaymentScreen composable
                OnGoingOrdersDetailScreen(navController, order)
            }
        }

        composable(
            route = Screen.OrderHistoryDetails.route,
            arguments = listOf(navArgument("order") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedMysteryBoxJson = backStackEntry.arguments?.getString("order")

            encodedMysteryBoxJson?.let {
                // Decode the JSON string before deserializing
                                // Pass the list to the PaymentScreen composable
                OrderHistoryDetailScreen(navController, encodedMysteryBoxJson)
            }
        }


        composable(
            route = Screen.VoucherPaymentScreen.route

        ){
            VoucherScreenPayment(navController)
        }





        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.CartScreen.route) {
            CartScreen(navController)
        }
        composable(Screen.LeaderBoardScreen.route){
            LeaderboardPage(navController)
        }
        composable(Screen.VoucherScreen.route) {
            VoucherScreen(navController)
        }

        composable(Screen.OrderHistoryScreen.route){
            OrderHistoryScreen(navController)
        }


        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.EditProfile.route) { backStackEntry ->
            // Retrieve userId from the route arguments
            val userId = backStackEntry.arguments?.getString("userId")
            userId?.let {
                // Navigate to EditProfileScreen with the userId
                EditProfileScreen(navController = navController)
            }
        }
        composable(Screen.RestaurantDetail.route) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId")
            restaurantId?.let {
                RestaurantDetailScreen(idRestaurant = it, navController = navController)
            }
        }

        composable(
            route = Screen.DashboardSearchButton.route,
            arguments = listOf(navArgument("selectedSearchButton") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 }, // Slide in from the right
                    animationSpec = tween(durationMillis = 1000)
                ) + fadeIn(animationSpec = tween(durationMillis = 1000))
            },
            exitTransition = {
                val previousScreen = navController.previousBackStackEntry?.destination?.route
                // Only apply exit transition when navigating to the dashboard
                if (previousScreen == Screen.Dashboard.route) {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 }, // Slide out to the left
                        animationSpec = tween(durationMillis = 1000)
                    ) + fadeOut(animationSpec = tween(durationMillis = 1000))
                } else {
                    null // Skip exit transition for other navigations
                }
            }
        ) { backStackEntry ->
            val selectedSearchButton = backStackEntry.arguments?.getString("selectedSearchButton")
            selectedSearchButton?.let {
                SearchWithButtonScreen(selectedSearchButton, navController)
            }
        }


        composable(
            route = Screen.DashboardSearchString.route,
            arguments = listOf(navArgument("selectedSearchString") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 }, // Slide in from the right
                    animationSpec = tween(durationMillis = 1000) // Adjust duration as needed
                ) + fadeIn(animationSpec = tween(durationMillis = 1000)) // Fade in effect
            },
            exitTransition = {
                val previousScreen = navController.previousBackStackEntry?.destination?.route
                // Only apply exit transition when navigating to the dashboard
                if (previousScreen == Screen.Dashboard.route) {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 }, // Slide out to the left
                        animationSpec = tween(durationMillis = 1000)
                    ) + fadeOut(animationSpec = tween(durationMillis = 1000))
                } else {
                    null // Skip exit transition for other navigations
                }
            }
        ) { backStackEntry ->
            val selectedSearchString =
                backStackEntry.arguments?.getString("selectedSearchString")
            selectedSearchString?.let {
                SearchWithStringScreen(selectedSearchString, navController)
            }
        }


        composable(Screen.MysteryBoxScreen.route) { backStackEntry ->
            val mysteryBoxId = backStackEntry.arguments?.getString("mysteryBoxId")
            mysteryBoxId?.let {
                MysteryBoxDetailScreen(idMysterBox = it, navController = navController)
            }
        }



    }
}
