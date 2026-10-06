package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Scene
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EditSceneDialog(
    scene: Scene,
    onSave: (Scene) -> Unit,
    onDismiss: () -> Unit
) {
    var location by remember { mutableStateOf(scene.location) }
    var action by remember { mutableStateOf(scene.action) }
    var dialogue by remember { mutableStateOf(scene.dialogue) }
    var narration by remember { mutableStateOf(scene.narration) }
    var cameraMovement by remember { mutableStateOf(scene.cameraMovement) }
    var durationSeconds by remember { mutableStateOf(scene.durationSeconds.toString()) }
    var mood by remember { mutableStateOf(scene.mood) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_scene_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit Scene ${scene.sceneNumber}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Location & Duration Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location", fontSize = 11.sp) },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        colors = editFieldColors()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = durationSeconds,
                        onValueChange = { durationSeconds = it },
                        label = { Text("Seconds", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = editFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Narration Field
                OutlinedTextField(
                    value = narration,
                    onValueChange = { narration = it },
                    label = { Text("Narration (Voiceover & Subtitles)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = editFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Dialogue Field
                OutlinedTextField(
                    value = dialogue,
                    onValueChange = { dialogue = it },
                    label = { Text("Character Dialogue", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = editFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action Field
                OutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = { Text("Action & Visual Beat", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = editFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Camera & Mood Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = cameraMovement,
                        onValueChange = { cameraMovement = it },
                        label = { Text("Camera Direction", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = editFieldColors()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = mood,
                        onValueChange = { mood = it },
                        label = { Text("Mood", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = editFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val dur = durationSeconds.toIntOrNull()?.coerceIn(2, 60) ?: scene.durationSeconds
                            onSave(
                                scene.copy(
                                    location = location,
                                    durationSeconds = dur,
                                    narration = narration,
                                    dialogue = dialogue,
                                    action = action,
                                    cameraMovement = cameraMovement,
                                    mood = mood
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        modifier = Modifier.testTag("save_scene_changes_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentCyan,
    unfocusedBorderColor = DarkSurfaceBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = AccentCyan,
    unfocusedLabelColor = TextSecondary
)
