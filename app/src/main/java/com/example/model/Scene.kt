package com.example.model

data class Scene(
    val id: String,
    val sceneNumber: Int,
    val durationSeconds: Int = 6,
    val location: String = "Mystical Forest",
    val timeOfDay: String = "Night",
    val characters: List<String> = emptyList(),
    val action: String = "",
    val dialogue: String = "",
    val narration: String = "",
    val visualPrompt: String = "",
    val cameraMovement: String = "Slow Pan Right",
    val mood: String = "Mysterious",
    val soundEffect: String = "Crickets, soft wind",
    val imageResId: Int? = null,
    val imageUri: String? = null,
    val isGenerating: Boolean = false
)
