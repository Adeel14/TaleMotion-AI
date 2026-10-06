package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Project
import com.example.model.SubtitleConfig
import com.example.model.VideoFormat
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoPlayerView(
    project: Project,
    currentSceneIndex: Int,
    isPlaying: Boolean,
    isMuted: Boolean,
    subtitlesConfig: SubtitleConfig,
    isFullscreen: Boolean,
    isPro: Boolean,
    onTogglePlayPause: () -> Unit,
    onNextScene: () -> Unit,
    onPrevScene: () -> Unit,
    onSeekToScene: (Int) -> Unit,
    onToggleMute: () -> Unit,
    onToggleSubtitles: () -> Unit,
    onToggleFullscreen: () -> Unit
) {
    val currentScene = project.scenes.getOrNull(currentSceneIndex)
    var showControlsOverlay by remember { mutableStateOf(true) }

    // Ken Burns slow cinematic camera motion animation
    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom_scale"
    )

    // Aspect Ratio container based on project format
    val aspect = when (project.format) {
        VideoFormat.Vertical_9_16 -> 9f / 16f
        VideoFormat.Horizontal_16_9 -> 16f / 9f
        VideoFormat.Square_1_1 -> 1f / 1f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .clickable { showControlsOverlay = !showControlsOverlay }
            .testTag("video_player_container"),
        contentAlignment = Alignment.Center
    ) {
        // Aspect framed viewport
        Box(
            modifier = Modifier
                .fillMaxWidth(if (project.format == VideoFormat.Vertical_9_16) 0.65f else 1.0f)
                .aspectRatio(aspect)
                .background(Color(0xFF070A12)),
            contentAlignment = Alignment.Center
        ) {
            // Visual Frame Layer
            if (currentScene?.imageResId != null) {
                Image(
                    painter = painterResource(id = currentScene.imageResId),
                    contentDescription = "Scene visual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(zoomScale)
                )
            } else if (!currentScene?.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = currentScene?.imageUri,
                    contentDescription = "Scene visual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(zoomScale)
                )
            } else {
                // Procedural Cinematic Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(AccentPurple.copy(alpha = 0.4f), Color(0xFF0A0F1D))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Visualizer",
                            tint = AccentCyan,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentScene?.location ?: "Cinematic Vista",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Dark vignette overlay for cinematic readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.65f)
                            )
                        )
                    )
            )

            // Watermark (in Free mode)
            if (!isPro) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "TaleMotion AI Free",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            // Camera movement indicator badge
            currentScene?.let { sc ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🎥 ${sc.cameraMovement}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentCyan
                    )
                }
            }

            // SUBTITLE OVERLAY
            if (subtitlesConfig.enabled && currentScene != null) {
                val subtitleText = currentScene.narration.ifBlank { currentScene.dialogue }
                if (subtitleText.isNotBlank()) {
                    val alignment = when (subtitlesConfig.position) {
                        "TOP" -> Alignment.TopCenter
                        "CENTER" -> Alignment.Center
                        else -> Alignment.BottomCenter
                    }

                    Box(
                        modifier = Modifier
                            .align(alignment)
                            .padding(horizontal = 16.dp, vertical = if (alignment == Alignment.BottomCenter) 36.dp else 16.dp)
                            .then(
                                if (subtitlesConfig.showBackground) {
                                    Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                } else Modifier
                            )
                    ) {
                        Text(
                            text = subtitleText,
                            color = Color(0xFFFFFA65), // Classic readable yellow subtitle color
                            fontSize = subtitlesConfig.fontSizeSp.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // CONTROLS OVERLAY
            AnimatedVisibility(
                visible = showControlsOverlay,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                ) {
                    // Center Big Play / Pause Button
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AccentPurple.copy(alpha = 0.9f))
                            .clickable { onTogglePlayPause() }
                            .testTag("player_center_play_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Bottom Bar Controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Timeline Scrubber
                        val totalScenes = project.scenes.size.coerceAtLeast(1)
                        Slider(
                            value = currentSceneIndex.toFloat(),
                            onValueChange = { onSeekToScene(it.toInt()) },
                            valueRange = 0f..(totalScenes - 1).toFloat(),
                            steps = (totalScenes - 2).coerceAtLeast(0),
                            colors = SliderDefaults.colors(
                                thumbColor = AccentCyan,
                                activeTrackColor = AccentPurple,
                                inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .testTag("player_timeline_slider")
                        )

                        // Transport Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onPrevScene,
                                    enabled = currentSceneIndex > 0,
                                    modifier = Modifier.size(32.dp).testTag("player_prev_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipPrevious,
                                        contentDescription = "Previous scene",
                                        tint = if (currentSceneIndex > 0) Color.White else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onTogglePlayPause,
                                    modifier = Modifier.size(32.dp).testTag("player_bottom_play_button")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onNextScene,
                                    modifier = Modifier.size(32.dp).testTag("player_next_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Next scene",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Scene ${currentSceneIndex + 1}/${totalScenes}",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Right controls: Mute, Subtitles, Fullscreen
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onToggleMute,
                                    modifier = Modifier.size(32.dp).testTag("player_mute_button")
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = "Mute/Unmute",
                                        tint = if (isMuted) Color(0xFFEF4444) else AccentCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onToggleSubtitles,
                                    modifier = Modifier.size(32.dp).testTag("player_subtitles_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (subtitlesConfig.enabled) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionDisabled,
                                        contentDescription = "Subtitles",
                                        tint = if (subtitlesConfig.enabled) AccentCyan else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onToggleFullscreen,
                                    modifier = Modifier.size(32.dp).testTag("player_fullscreen_button")
                                ) {
                                    Icon(
                                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = "Fullscreen",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
