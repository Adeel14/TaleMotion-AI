package com.example.model

data class UserAccount(
    val id: String = "user_default",
    val name: String = "Creator",
    val email: String = "creator@talemotion.ai",
    val isLoggedIn: Boolean = true,
    val plan: String = "Pro Plan",
    val avatarInitial: String = "T"
)

data class CreditState(
    val isPro: Boolean = true,
    val creditsRemaining: Int = 42,
    val maxCredits: Int = 50,
    val resolutionLabel: String = "1080p Full HD",
    val watermarkEnabled: Boolean = false,
    val maxDurationMinutes: Int = 3
)
