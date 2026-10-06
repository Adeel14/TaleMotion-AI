package com.example.model

data class StoryTemplate(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val visualStyle: VisualStyle,
    val musicMood: MusicMood,
    val durationLabel: String,
    val format: VideoFormat,
    val sampleStory: String,
    val iconName: String,
    val targetAudience: String
)
