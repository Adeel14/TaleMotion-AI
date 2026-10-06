package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Scene
import com.example.ui.components.AiAssistantSheet
import com.example.ui.components.EditSceneDialog
import com.example.ui.components.RenderExportDialog
import com.example.ui.components.SceneCard
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TaleMotionViewModel

@Composable
fun StudioScreen(viewModel: TaleMotionViewModel) {
    val project by viewModel.activeProject.collectAsState()
    val currentSceneIndex by viewModel.currentSceneIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val subtitlesConfig by viewModel.subtitlesConfig.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()
    val creditState by viewModel.creditState.collectAsState()

    val isAssistantOpen by viewModel.isAssistantOpen.collectAsState()
    val isAssistantProcessing by viewModel.isAssistantProcessing.collectAsState()
    val isRenderDialogOpen by viewModel.isRenderDialogOpen.collectAsState()
    val isRendering by viewModel.isRendering.collectAsState()
    val renderProgress by viewModel.renderProgress.collectAsState()
    val renderCompleted by viewModel.renderCompleted.collectAsState()

    var editingScene by remember { mutableStateOf<Scene?>(null) }
    var showSubtitleCustomizer by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .padding(bottom = 90.dp)
                .testTag("studio_screen_container")
        ) {
            // Project Header Info & Top Studio Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${project.format.displayName} • ${project.visualStyle.displayName} • ${project.musicMood.displayName}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Render & Assistant Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleAssistant(true) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentPurple.copy(alpha = 0.25f))
                            .testTag("studio_open_assistant_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.openRenderDialog() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("studio_render_video_button")
                    ) {
                        Icon(imageVector = Icons.Default.MovieCreation, contentDescription = "Render", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Render", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CINEMATIC VIDEO PLAYER PREVIEW
            VideoPlayerView(
                project = project,
                currentSceneIndex = currentSceneIndex,
                isPlaying = isPlaying,
                isMuted = isMuted,
                subtitlesConfig = subtitlesConfig,
                isFullscreen = isFullscreen,
                isPro = creditState.isPro,
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onNextScene = { viewModel.nextScene() },
                onPrevScene = { viewModel.prevScene() },
                onSeekToScene = { viewModel.seekToScene(it) },
                onToggleMute = { viewModel.toggleMute() },
                onToggleSubtitles = { viewModel.toggleSubtitles() },
                onToggleFullscreen = { viewModel.toggleFullscreen() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Quick Toolbar Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showSubtitleCustomizer = !showSubtitleCustomizer }
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = "Subtitles",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (subtitlesConfig.enabled) "Subtitles (ON • ${project.language.displayName})" else "Subtitles (OFF)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Tune", tint = TextSecondary, modifier = Modifier.size(14.dp))
                }

                Text(
                    text = "🔊 Voice: ${project.voiceType.displayName}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            // Subtitle Customizer Bar
            if (showSubtitleCustomizer) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "Size:", fontSize = 11.sp, color = TextSecondary)
                        listOf(13f to "Small", 16f to "Medium", 20f to "Large").forEach { (sz, lbl) ->
                            Text(
                                text = lbl,
                                fontSize = 11.sp,
                                fontWeight = if (subtitlesConfig.fontSizeSp == sz) FontWeight.Bold else FontWeight.Normal,
                                color = if (subtitlesConfig.fontSizeSp == sz) AccentCyan else TextPrimary,
                                modifier = Modifier
                                    .clickable { viewModel.updateSubtitleConfig(sz, subtitlesConfig.position) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Pos:", fontSize = 11.sp, color = TextSecondary)
                        listOf("BOTTOM", "CENTER", "TOP").forEach { pos ->
                            Text(
                                text = pos,
                                fontSize = 10.sp,
                                fontWeight = if (subtitlesConfig.position == pos) FontWeight.Bold else FontWeight.Normal,
                                color = if (subtitlesConfig.position == pos) AccentCyan else TextPrimary,
                                modifier = Modifier
                                    .clickable { viewModel.updateSubtitleConfig(subtitlesConfig.fontSizeSp, pos) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SCENE BUILDER & TIMELINE SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Scene Timeline",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${project.scenes.size} Scenes • Total ${project.durationSeconds}s",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { viewModel.addScene() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E283E),
                        contentColor = AccentCyan
                    ),
                    modifier = Modifier.testTag("add_scene_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Scene", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of Scene Cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                project.scenes.forEachIndexed { idx, sc ->
                    SceneCard(
                        scene = sc,
                        index = idx,
                        isSelected = currentSceneIndex == idx,
                        totalScenes = project.scenes.size,
                        onSelect = { viewModel.seekToScene(idx) },
                        onEdit = { editingScene = sc },
                        onRegenerateImage = { viewModel.regenerateSceneImage(sc.id) },
                        onReorderUp = { viewModel.reorderSceneUp(idx) },
                        onReorderDown = { viewModel.reorderSceneDown(idx) },
                        onDelete = { viewModel.deleteScene(sc.id) }
                    )
                }
            }
        }

        // AI ASSISTANT SHEET OVERLAY
        if (isAssistantOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.toggleAssistant(false) }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clickable(enabled = false) {}
                ) {
                    AiAssistantSheet(
                        isProcessing = isAssistantProcessing,
                        onClose = { viewModel.toggleAssistant(false) },
                        onAsk = { viewModel.askAssistant(it) }
                    )
                }
            }
        }

        // RENDER EXPORT MODAL
        if (isRenderDialogOpen) {
            RenderExportDialog(
                project = project,
                isRendering = isRendering,
                renderProgress = renderProgress,
                renderCompleted = renderCompleted,
                isPro = creditState.isPro,
                onStartRender = { viewModel.startRenderPipeline() },
                onDismiss = { viewModel.closeRenderDialog() },
                onDownloadVideo = {
                    viewModel.closeRenderDialog()
                    viewModel.userMessage.value = "Project video exported to device!"
                }
            )
        }

        // EDIT SCENE MODAL
        editingScene?.let { sc ->
            EditSceneDialog(
                scene = sc,
                onSave = { updated ->
                    viewModel.updateScene(updated)
                    editingScene = null
                },
                onDismiss = { editingScene = null }
            )
        }
    }
}
