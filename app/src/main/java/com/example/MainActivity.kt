package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.BottomStudioNav
import com.example.ui.components.GenerationProgressDialog
import com.example.ui.components.SidebarStudioNav
import com.example.ui.components.TopStudioBar
import com.example.ui.screens.CharactersScreen
import com.example.ui.screens.CreateStoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.TaleMotionTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.TaleMotionViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TaleMotionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaleMotionTheme {
                TaleMotionApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TaleMotionApp(viewModel: TaleMotionViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val recentProjects by viewModel.projects.collectAsState()
    val creditState by viewModel.creditState.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationStep by viewModel.generationStep.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back press to return to Home from secondary screens
    if (currentScreen != Screen.Home) {
        BackHandler {
            viewModel.navigateTo(Screen.Home)
        }
    }

    // Display user messages / snackbars
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        val isWideScreen = maxWidth > 680.dp

        if (isWideScreen) {
            // Tablet / Desktop layout with persistent Sidebar Nav
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarStudioNav(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )

                Scaffold(
                    topBar = {
                        TopStudioBar(
                            creditState = creditState,
                            onOpenSettings = { viewModel.navigateTo(Screen.Settings) },
                            onUpgradeClick = { viewModel.upgradeToPro() }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = DarkBackground,
                    modifier = Modifier.weight(1f)
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ScreenContent(
                            currentScreen = currentScreen,
                            viewModel = viewModel,
                            activeProject = activeProject,
                            recentProjects = recentProjects
                        )
                    }
                }
            }
        } else {
            // Mobile handheld layout with TopBar and Bottom Navigation
            Scaffold(
                topBar = {
                    TopStudioBar(
                        creditState = creditState,
                        onOpenSettings = { viewModel.navigateTo(Screen.Settings) },
                        onUpgradeClick = { viewModel.upgradeToPro() }
                    )
                },
                bottomBar = {
                    BottomStudioNav(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = DarkBackground,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ScreenContent(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        activeProject = activeProject,
                        recentProjects = recentProjects
                    )
                }
            }
        }

        // Generation modal overlay
        if (isGenerating) {
            GenerationProgressDialog(
                stepText = generationStep,
                progress = generationProgress
            )
        }
    }
}

@Composable
private fun ScreenContent(
    currentScreen: Screen,
    viewModel: TaleMotionViewModel,
    activeProject: com.example.model.Project,
    recentProjects: List<com.example.model.Project>
) {
    when (currentScreen) {
        is Screen.Home -> {
            HomeScreen(
                activeProject = activeProject,
                recentProjects = recentProjects,
                onNavigate = { viewModel.navigateTo(it) },
                onOpenProject = { viewModel.openProjectInStudio(it) },
                onSelectTemplate = { viewModel.applyTemplate(it) }
            )
        }
        is Screen.CreateStory -> {
            CreateStoryScreen(viewModel = viewModel)
        }
        is Screen.Studio -> {
            StudioScreen(viewModel = viewModel)
        }
        is Screen.MyProjects -> {
            MyProjectsScreen(viewModel = viewModel)
        }
        is Screen.Characters -> {
            CharactersScreen(viewModel = viewModel)
        }
        is Screen.Templates -> {
            TemplatesScreen(viewModel = viewModel)
        }
        is Screen.Settings -> {
            SettingsScreen(viewModel = viewModel)
        }
    }
}
