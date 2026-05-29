package com.flagquest.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flagquest.app.domain.model.QuizConfig
import com.flagquest.app.domain.model.QuizMode
import com.flagquest.app.ui.screens.*
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Auth    : Screen("auth")
    object Home    : Screen("home")
    object Setup   : Screen("setup")
    object Quiz    : Screen("quiz")
    object Result  : Screen("result/{score}/{total}") {
        fun createRoute(score: Int, total: Int) = "result/$score/$total"
    }
    object Explore : Screen("explore")
    object Profile : Screen("profile")
}

// Config partagée entre Setup et Quiz via un simple objet en mémoire
object QuizConfigHolder {
    var config: QuizConfig = QuizConfig()
}

@Composable
fun FlagQuestNavHost(navController: NavHostController = rememberNavController()) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null)
        Screen.Home.route else Screen.Auth.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onStartQuiz = { navController.navigate(Screen.Setup.route) },
                onExplore   = { navController.navigate(Screen.Explore.route) },
                onProfile   = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Setup.route) {
            QuizSetupScreen(
                onStartQuiz = { config ->
                    QuizConfigHolder.config = config
                    navController.navigate(Screen.Quiz.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                config = QuizConfigHolder.config,
                onFinished = { score, total ->
                    navController.navigate(Screen.Result.createRoute(score, total)) {
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Result.route) { backStack ->
            val score = backStack.arguments?.getString("score")?.toIntOrNull() ?: 0
            val total = backStack.arguments?.getString("total")?.toIntOrNull() ?: 10
            ResultScreen(
                score = score,
                total = total,
                onPlayAgain = {
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onHome = { navController.navigate(Screen.Home.route) { popUpTo(0) } }
            )
        }

        composable(Screen.Explore.route) {
            ExploreScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
