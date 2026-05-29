package com.flagquest.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flagquest.app.ui.screens.HomeScreen
import com.flagquest.app.ui.screens.QuizScreen
import com.flagquest.app.ui.screens.ResultScreen
import com.flagquest.app.ui.screens.ExploreScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quiz : Screen("quiz")
    object Result : Screen("result/{score}/{total}") {
        fun createRoute(score: Int, total: Int) = "result/$score/$total"
    }
    object Explore : Screen("explore")
}

@Composable
fun FlagQuestNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onStartQuiz = { navController.navigate(Screen.Quiz.route) },
                onExplore = { navController.navigate(Screen.Explore.route) }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
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
                    navController.navigate(Screen.Quiz.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onHome = { navController.navigate(Screen.Home.route) { popUpTo(0) } }
            )
        }

        composable(Screen.Explore.route) {
            ExploreScreen(onBack = { navController.popBackStack() })
        }
    }
}
