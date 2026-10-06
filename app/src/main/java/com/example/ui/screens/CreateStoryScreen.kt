package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageOption
import com.example.model.MusicMood
import com.example.model.VideoFormat
import com.example.model.VisualStyle
import com.example.model.VoiceType
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TaleMotionViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateStoryScreen(viewModel: TaleMotionViewModel) {
    val title by viewModel.createTitle.collectAsState()
    val story by viewModel.createStory.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()
    val duration by viewModel.selectedDuration.collectAsState()
    val format by viewModel.selectedFormat.collectAsState()
    val style by viewModel.selectedVisualStyle.collectAsState()
    val voice by viewModel.selectedVoice.collectAsState()
    val musicMood by viewModel.selectedMusicMood.collectAsState()
    val targetAudience by viewModel.selectedTargetAudience.collectAsState()
    val copyrightWarning by viewModel.copyrightWarning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
            .testTag("create_story_screen")
    ) {
        // Page Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AccentPurple, AccentCyan))),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Movie, contentDescription = "Create", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Story to Video Creator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Write your story and generate consistent cinematic scenes",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // COPYRIGHT WARNING BANNER
        if (copyrightWarning != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF451A03),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                modifier = Modifier.fillMaxWidth().testTag("copyright_warning_banner")
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = copyrightWarning ?: "",
                        fontSize = 11.sp,
                        color = Color(0xFFFEF3C7),
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Story Title Input
        Text(text = "Story Title", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = {
                viewModel.createTitle.value = it
                viewModel.checkCopyright(it)
            },
            placeholder = { Text("e.g. The Forgotten Starlight Harbor", fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth().testTag("story_title_input"),
            singleLine = true,
            colors = formFieldColors()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Story Description / Story text
        Text(text = "Story Script & Narrative", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = story,
            onValueChange = {
                viewModel.createStory.value = it
                viewModel.checkCopyright(it)
            },
            placeholder = {
                Text(
                    "Write your story beats, dialogue, or description here. For example:\n" +
                    "\"Two young inventors discover a hidden solar airship in the mountains. Together with their robotic owl companion, they take to the skies to restore power to the crystal city.\"",
                    fontSize = 12.sp
                )
            },
            modifier = Modifier.fillMaxWidth().testTag("story_text_input"),
            minLines = 5,
            colors = formFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language Options
        Text(text = "Narration & Subtitle Language", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LanguageOption.values().forEach { lang ->
                val isSelected = language == lang
                ChipButton(
                    text = lang.displayName,
                    isSelected = isSelected,
                    onClick = { viewModel.selectedLanguage.value = lang },
                    testTag = "lang_chip_${lang.name}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Duration Options
        Text(text = "Video Duration", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("15 seconds", "30 seconds", "60 seconds", "2 minutes", "Custom").forEach { dur ->
                val isSelected = duration == dur
                ChipButton(
                    text = dur,
                    isSelected = isSelected,
                    onClick = { viewModel.selectedDuration.value = dur },
                    testTag = "dur_chip_${dur.take(4)}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Format Options
        Text(text = "Video Format / Aspect Ratio", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VideoFormat.values().forEach { fmt ->
                val isSelected = format == fmt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AccentPurple.copy(alpha = 0.35f) else Color(0xFF161E30))
                        .border(1.dp, if (isSelected) AccentCyan else DarkSurfaceBorder, RoundedCornerShape(10.dp))
                        .clickable { viewModel.selectedFormat.value = fmt }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = fmt.aspectRatioString,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) AccentCyan else TextPrimary
                        )
                        Text(
                            text = fmt.displayName.split(" ").getOrNull(1) ?: "",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Style Options
        Text(text = "Visual Animation / Film Style", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            VisualStyle.values().forEach { st ->
                val isSelected = style == st
                ChipButton(
                    text = st.displayName,
                    isSelected = isSelected,
                    onClick = { viewModel.selectedVisualStyle.value = st },
                    testTag = "style_chip_${st.name}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Narration Voice Options
        Text(text = "AI Narration Voice", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            VoiceType.values().forEach { v ->
                val isSelected = voice == v
                ChipButton(
                    text = v.displayName,
                    isSelected = isSelected,
                    onClick = { viewModel.selectedVoice.value = v },
                    testTag = "voice_chip_${v.name}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Background Music Mood Options
        Text(text = "Background Music Mood", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MusicMood.values().forEach { m ->
                val isSelected = musicMood == m
                ChipButton(
                    text = m.displayName,
                    isSelected = isSelected,
                    onClick = { viewModel.selectedMusicMood.value = m },
                    testTag = "music_chip_${m.name}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Audience
        Text(text = "Target Audience", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = targetAudience,
            onValueChange = { viewModel.selectedTargetAudience.value = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = formFieldColors()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CREATE VIDEO BUTTON
        Button(
            onClick = { viewModel.createVideoFromStory() },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPurple,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("create_video_button")
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Create", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create Video (${duration})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) AccentPurple.copy(alpha = 0.35f) else Color(0xFF161E30))
            .border(1.dp, if (isSelected) AccentCyan else DarkSurfaceBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AccentCyan else TextPrimary
        )
    }
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentCyan,
    unfocusedBorderColor = DarkSurfaceBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedPlaceholderColor = TextSecondary,
    unfocusedPlaceholderColor = TextSecondary
)
