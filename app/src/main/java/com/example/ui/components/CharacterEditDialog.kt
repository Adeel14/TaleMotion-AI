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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Character
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CharacterEditDialog(
    character: Character?,
    onSave: (Character) -> Unit,
    onDismiss: () -> Unit,
    onAutoFillOriginal: () -> Unit
) {
    var name by remember { mutableStateOf(character?.name ?: "") }
    var age by remember { mutableStateOf(character?.age ?: "14") }
    var gender by remember { mutableStateOf(character?.gender ?: "Explorer") }
    var appearance by remember { mutableStateOf(character?.appearance ?: "") }
    var hair by remember { mutableStateOf(character?.hair ?: "") }
    var clothes by remember { mutableStateOf(character?.clothes ?: "") }
    var personality by remember { mutableStateOf(character?.personality ?: "") }
    var voiceType by remember { mutableStateOf(character?.voiceType ?: "Storyteller voice") }
    var characterStyle by remember { mutableStateOf(character?.characterStyle ?: "3D Pixar-like animated hero") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .testTag("character_edit_dialog")
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
                            imageVector = Icons.Default.Face,
                            contentDescription = "Character",
                            tint = AccentCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (character == null) "Create Original Character" else "Edit Character",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Auto-fill prompt helper button
                OutlinedButton(
                    onClick = {
                        name = listOf("Zarek", "Kira", "Tariq", "Nova", "Felix", "Orion").random()
                        age = "15"
                        appearance = "Adventurous character with bright determined eyes and warm smile"
                        hair = "Dynamic dark swept hair with subtle starlight streak"
                        clothes = "Utility explorer tunic with leather straps, high boots and glowing amulet"
                        personality = "Quick-witted, brave, fiercely loyal"
                        voiceType = "Young voice"
                        characterStyle = "3D Pixar-like animation"
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("auto_generate_character_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Auto", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "AI Generate Random Character", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name & Age Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Character Name", fontSize = 11.sp) },
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        colors = charFieldColors()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = charFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Appearance
                OutlinedTextField(
                    value = appearance,
                    onValueChange = { appearance = it },
                    label = { Text("Physical Appearance & Face", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = charFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Hair & Clothes Row
                OutlinedTextField(
                    value = hair,
                    onValueChange = { hair = it },
                    label = { Text("Hair Style & Color", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = charFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = clothes,
                    onValueChange = { clothes = it },
                    label = { Text("Clothes & Consistent Attire", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = charFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Personality
                OutlinedTextField(
                    value = personality,
                    onValueChange = { personality = it },
                    label = { Text("Personality Traits", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = charFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Voice & Style
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = voiceType,
                        onValueChange = { voiceType = it },
                        label = { Text("Voice Profile", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = charFieldColors()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = characterStyle,
                        onValueChange = { characterStyle = it },
                        label = { Text("Visual Style", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = charFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
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
                            val saved = Character(
                                id = character?.id ?: ("char_" + System.currentTimeMillis()),
                                name = name.ifBlank { "Original Hero" },
                                age = age,
                                gender = gender,
                                appearance = appearance,
                                hair = hair,
                                clothes = clothes,
                                personality = personality,
                                voiceType = voiceType,
                                characterStyle = characterStyle,
                                avatarResId = character?.avatarResId ?: com.example.R.drawable.demo_scene_3_1791295483544
                            )
                            onSave(saved)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("save_character_button")
                    ) {
                        Text("Save Character", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun charFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentCyan,
    unfocusedBorderColor = DarkSurfaceBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = AccentCyan,
    unfocusedLabelColor = TextSecondary
)
