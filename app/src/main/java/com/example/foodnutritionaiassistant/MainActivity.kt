package com.example.foodnutritionaiassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodnutritionaiassistant.ui.analysis.AnalysisScreen
import com.example.foodnutritionaiassistant.ui.screens.*
import com.example.foodnutritionaiassistant.ui.theme.FoodNutritionAIAssistantTheme
import com.example.foodnutritionaiassistant.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodNutritionAIAssistantTheme {
                FoodNutritionApp()
            }
        }
    }
}

enum class RegistrationStep {
    LOGIN,
    NICKNAME,
    GENDER_AGE,
    GROUP_SELECTION,
    HEIGHT_WEIGHT,
    COMPLETED
}

@Composable
fun FoodNutritionApp() {
    val userViewModel: UserViewModel = viewModel()
    
    // Overall App State
    var registrationStep by rememberSaveable { mutableStateOf(RegistrationStep.LOGIN) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // If step is COMPLETED, show MainScreen. Otherwise show Registration Flow.
    
    AnimatedContent(
        targetState = registrationStep,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> width } + fadeOut()
            }
        },
        label = "RegistrationTransition"
    ) { step ->
        when (step) {
            RegistrationStep.LOGIN -> {
                LoginScreen(
                    viewModel = userViewModel,
                    onLoginSuccess = { isFirstLogin ->
                        if (isFirstLogin) {
                            registrationStep = RegistrationStep.NICKNAME
                        } else {
                            registrationStep = RegistrationStep.COMPLETED
                            currentDestination = AppDestinations.HOME
                        }
                    },
                    onSkip = {
                        registrationStep = RegistrationStep.COMPLETED
                        currentDestination = AppDestinations.HOME
                    }
                )
            }
            RegistrationStep.NICKNAME -> {
                NicknameScreen(
                    viewModel = userViewModel,
                    onNext = { registrationStep = RegistrationStep.GENDER_AGE }
                )
            }
            RegistrationStep.GENDER_AGE -> {
                GenderAgeScreen(
                    viewModel = userViewModel,
                    onNext = { registrationStep = RegistrationStep.GROUP_SELECTION },
                    onBack = { registrationStep = RegistrationStep.NICKNAME }
                )
            }
            RegistrationStep.GROUP_SELECTION -> {
                GroupSelectionScreen(
                    viewModel = userViewModel,
                    onNext = { registrationStep = RegistrationStep.HEIGHT_WEIGHT },
                    onBack = { registrationStep = RegistrationStep.GENDER_AGE }
                )
            }
            RegistrationStep.HEIGHT_WEIGHT -> {
                HeightWeightScreen(
                    viewModel = userViewModel,
                    onFinish = {
                        registrationStep = RegistrationStep.COMPLETED
                        currentDestination = AppDestinations.HOME
                    },
                    onBack = { registrationStep = RegistrationStep.GROUP_SELECTION }
                )
            }
            RegistrationStep.COMPLETED -> {
                MainScreen(
                    currentDestination = currentDestination,
                    onDestinationChanged = { currentDestination = it },
                    isLoggedIn = userViewModel.isLoggedIn,
                    onLoginClick = { registrationStep = RegistrationStep.LOGIN },
                    userViewModel = userViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    currentDestination: AppDestinations,
    onDestinationChanged: (AppDestinations) -> Unit,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    userViewModel: UserViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color.Gray
            ) {
                AppDestinations.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    val isScan = destination == AppDestinations.SCAN
                    
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onDestinationChanged(destination) },
                        icon = {
                            if (isScan) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32)), // Dark Green
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label,
                                        tint = Color.White
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                    tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray
                                )
                            }
                        },
                        label = {
                            if (!isScan) {
                                Text(
                                    text = destination.label,
                                    color = if (isSelected) Color(0xFF2E7D32) else Color.Gray
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Global Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF1F8E9), // Very Light Green
                            Color(0xFFFFFFFF)  // White
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        // Sliding to right (e.g. Home -> Profile)
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        // Sliding to left (e.g. Profile -> Home)
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "MainContentTransition"
            ) { destination ->
                when (destination) {
                    AppDestinations.HOME -> HomeScreen(isLoggedIn, onLoginClick)
                    AppDestinations.GROUP -> CommunityScreen(userViewModel = userViewModel)
                    AppDestinations.SCAN -> Box(Modifier.fillMaxSize()) // Placeholder
                    AppDestinations.ANALYSIS -> AnalysisScreen(userViewModel)
                    AppDestinations.ME -> ProfileScreen(userViewModel, onLoginClick = onLoginClick)
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("首页", Icons.Default.Home),
    GROUP("群体", Icons.Default.Person), // Using Person as group placeholder
    SCAN("扫描", Icons.Default.Search), // Using Search/Lens as scan placeholder
    ANALYSIS("分析", Icons.Default.DateRange),
    ME("我的", Icons.Default.AccountCircle),
}

