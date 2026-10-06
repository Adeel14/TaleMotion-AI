package com.example.model

data class Character(
    val id: String,
    val name: String,
    val age: String = "12",
    val gender: String = "Male",
    val appearance: String = "Curious young explorer with bright eyes and friendly expression",
    val hair: String = "Messy dark brown hair with wind-swept bangs",
    val clothes: String = "Warm hooded adventurer jacket, sturdy boots, leather travel satchel",
    val personality: String = "Brave, inquisitive, kind-hearted",
    val voiceType: String = "Young voice",
    val characterStyle: String = "3D Pixar-like animated hero",
    val avatarResId: Int? = null,
    val avatarUri: String? = null
)
