package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultData
import com.example.model.StoryTemplate
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TaleMotionViewModel

@Composable
fun TemplatesScreen(viewModel: TaleMotionViewModel) {
    val templates = DefaultData.templates

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 90.dp)
            .testTag("templates_screen")
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
                Icon(imageVector = Icons.Default.AutoAwesomeMotion, contentDescription = "Templates", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Story Templates Library",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Pre-configured story blueprints with tailored styles & music",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(templates, key = { it.id }) { tmpl ->
                TemplateCard(
                    template = tmpl,
                    onApply = { viewModel.applyTemplate(tmpl) }
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: StoryTemplate,
    onApply: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onApply() }
            .testTag("template_card_item_${template.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentPurple.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = template.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }

                Text(
                    text = "${template.durationLabel} • ${template.format.displayName}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentPink
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = template.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = template.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sample story preview quote
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131A2B))
                    .padding(10.dp)
            ) {
                Text(
                    text = "\"${template.sampleStory}\"",
                    fontSize = 11.sp,
                    color = TextPrimary.copy(alpha = 0.85f),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer specs & Apply button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Style: ${template.visualStyle.displayName}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Music: ${template.musicMood.displayName}",
                        fontSize = 10.sp,
                        color = AccentCyan
                    )
                }

                Button(
                    onClick = onApply,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(32.dp).testTag("use_template_button_${template.id}")
                ) {
                    Text(text = "Use Template", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
