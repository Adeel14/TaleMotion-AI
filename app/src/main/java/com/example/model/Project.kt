package com.example.model

enum class ProjectStatus(val label: String) {
    Draft("Draft"),
    Generating("Generating"),
    Ready("Ready"),
    Rendering("Rendering"),
    Completed("Completed"),
    Failed("Failed")
}

enum class VideoFormat(val displayName: String, val aspectRatioString: String, val aspectWidth: Float, val aspectHeight: Float) {
    Vertical_9_16("9:16 Vertical", "9:16", 9f, 16f),
    Horizontal_16_9("16:9 YouTube", "16:9", 16f, 9f),
    Square_1_1("1:1 Square", "1:1", 1f, 1f)
}

enum class VisualStyle(val displayName: String, val description: String) {
    Cartoon_3D("3D Cartoon", "Modern vibrant 3D stylized animation with soft lighting"),
    Cartoon_2D("2D Cartoon", "Hand-drawn whimsical storybook animation style"),
    Anime("Anime", "High-detail Japanese cinematic anime illustration"),
    Cinematic("Cinematic", "Photorealistic film still with dramatic lighting and depth of field"),
    Fantasy("Fantasy", "Luminous mystical fantasy aesthetic with magical glows"),
    Realistic("Realistic", "Authentic contemporary cinematic realism"),
    PixarFamily("Pixar-like family animation", "Whimsical, emotionally expressive family 3D animation style")
}

enum class LanguageOption(val displayName: String, val code: String) {
    English("English", "en"),
    Urdu("Urdu", "ur"),
    Hindi("Hindi", "hi"),
    Arabic("Arabic", "ar"),
    Spanish("Spanish", "es"),
    French("French", "fr")
}

enum class VoiceType(val displayName: String, val pitch: Float, val speed: Float) {
    Storyteller("Storyteller voice", 0.95f, 0.95f),
    Male("Male voice", 0.85f, 1.0f),
    Female("Female voice", 1.15f, 1.0f),
    Young("Young voice", 1.35f, 1.05f),
    Deep("Deep voice", 0.70f, 0.90f),
    Friendly("Friendly voice", 1.05f, 1.02f)
}

enum class MusicMood(val displayName: String, val tempoBpm: Int) {
    Adventure("Adventure", 120),
    Cinematic("Cinematic", 90),
    Fantasy("Fantasy", 100),
    Emotional("Emotional", 75),
    Happy("Happy", 128),
    Suspense("Suspense", 85),
    Sad("Sad", 65),
    Funny("Funny", 135)
}

data class SubtitleConfig(
    val enabled: Boolean = true,
    val fontSizeSp: Float = 16f,
    val position: String = "BOTTOM", // "BOTTOM", "CENTER", "TOP"
    val showBackground: Boolean = true
)

data class Project(
    val id: String,
    val title: String,
    val description: String,
    val language: LanguageOption = LanguageOption.English,
    val durationLabel: String = "30 seconds",
    val durationSeconds: Int = 30,
    val format: VideoFormat = VideoFormat.Horizontal_16_9,
    val visualStyle: VisualStyle = VisualStyle.Cartoon_3D,
    val voiceType: VoiceType = VoiceType.Storyteller,
    val musicMood: MusicMood = MusicMood.Adventure,
    val targetAudience: String = "Family & Kids",
    val characters: List<Character> = emptyList(),
    val scenes: List<Scene> = emptyList(),
    val subtitleConfig: SubtitleConfig = SubtitleConfig(),
    val status: ProjectStatus = ProjectStatus.Ready,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailResId: Int? = null,
    val thumbnailUri: String? = null
)
