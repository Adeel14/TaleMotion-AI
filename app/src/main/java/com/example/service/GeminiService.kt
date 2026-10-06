package com.example.service

import com.example.BuildConfig
import com.example.R
import com.example.data.DefaultData
import com.example.model.Character
import com.example.model.LanguageOption
import com.example.model.MusicMood
import com.example.model.Project
import com.example.model.ProjectStatus
import com.example.model.Scene
import com.example.model.VideoFormat
import com.example.model.VisualStyle
import com.example.model.VoiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = runCatching { BuildConfig.GEMINI_API_KEY }.getOrDefault("")

    val hasValidApiKey: Boolean
        get() = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    /**
     * Analyzes user story input and creates a structured project with characters, scenes, and prompts.
     */
    suspend fun analyzeStory(
        titleInput: String,
        storyInput: String,
        language: LanguageOption,
        durationLabel: String,
        format: VideoFormat,
        visualStyle: VisualStyle,
        voiceType: VoiceType,
        musicMood: MusicMood,
        targetAudience: String,
        existingCharacters: List<Character>
    ): Project = withContext(Dispatchers.IO) {
        val cleanTitle = if (titleInput.isBlank()) extractTitleFromStory(storyInput) else titleInput
        val targetScenesCount = when (durationLabel) {
            "15 seconds" -> 3
            "30 seconds" -> 5
            "60 seconds" -> 6
            "2 minutes" -> 8
            else -> 5
        }
        val durationPerScene = when (durationLabel) {
            "15 seconds" -> 5
            "30 seconds" -> 6
            "60 seconds" -> 10
            "2 minutes" -> 15
            else -> 6
        }

        if (hasValidApiKey) {
            try {
                val prompt = buildStoryAnalysisPrompt(
                    cleanTitle, storyInput, language, targetScenesCount, durationPerScene,
                    format, visualStyle, voiceType, musicMood, targetAudience, existingCharacters
                )
                val responseJson = callGeminiRestApi(prompt)
                val parsedProject = parseStoryAnalysisJson(
                    responseJson, cleanTitle, storyInput, language, durationLabel,
                    targetScenesCount * durationPerScene, format, visualStyle, voiceType, musicMood, targetAudience
                )
                if (parsedProject != null && parsedProject.scenes.isNotEmpty()) {
                    return@withContext parsedProject
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Resilient fallback rule-based generation
        generateLocalFallbackProject(
            cleanTitle, storyInput, language, durationLabel, targetScenesCount, durationPerScene,
            format, visualStyle, voiceType, musicMood, targetAudience, existingCharacters
        )
    }

    /**
     * AI Studio Assistant applies targeted modifications to the active project.
     */
    suspend fun modifyProjectWithAssistant(
        project: Project,
        userInstruction: String
    ): Project = withContext(Dispatchers.IO) {
        if (hasValidApiKey) {
            try {
                val prompt = """
                    You are TaleMotion AI Studio Assistant. Modify the current video project based on this instruction: "$userInstruction".
                    Do not destroy existing scenes; update dialogues, actions, moods, or add/adjust scenes as requested.
                    Return ONLY valid JSON matching this structure:
                    {
                      "updatedTitle": "${project.title}",
                      "updatedDescription": "summary",
                      "characters": [ {"name": "...", "personality": "...", "appearance": "..."} ],
                      "scenes": [
                        {
                          "sceneNumber": 1,
                          "durationSeconds": 6,
                          "location": "...",
                          "timeOfDay": "...",
                          "action": "...",
                          "dialogue": "...",
                          "narration": "...",
                          "visualPrompt": "...",
                          "cameraMovement": "...",
                          "mood": "...",
                          "soundEffect": "..."
                        }
                      ]
                    }
                """.trimIndent()
                val responseJson = callGeminiRestApi(prompt)
                val updated = parseAssistantJson(responseJson, project)
                if (updated != null) return@withContext updated
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Offline Assistant logic
        applyLocalAssistantModifier(project, userInstruction)
    }

    private fun callGeminiRestApi(promptText: String): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val requestObj = JSONObject().apply {
            val partsArray = JSONArray().apply {
                put(JSONObject().apply { put("text", promptText) })
            }
            val contentsArray = JSONArray().apply {
                put(JSONObject().apply { put("parts", partsArray) })
            }
            put("contents", contentsArray)

            val genConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            }
            put("generationConfig", genConfig)
        }

        val request = Request.Builder()
            .url(endpoint)
            .post(requestObj.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("Gemini API error code: ${response.code}")
        }
        val bodyStr = response.body?.string() ?: throw RuntimeException("Empty response")
        val jsonRoot = JSONObject(bodyStr)
        val candidates = jsonRoot.optJSONArray("candidates") ?: throw RuntimeException("No candidates")
        val firstCand = candidates.getJSONObject(0)
        val content = firstCand.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        return parts.getJSONObject(0).getString("text")
    }

    private fun buildStoryAnalysisPrompt(
        title: String,
        story: String,
        language: LanguageOption,
        sceneCount: Int,
        durationPerScene: Int,
        format: VideoFormat,
        visualStyle: VisualStyle,
        voiceType: VoiceType,
        musicMood: MusicMood,
        targetAudience: String,
        existingChars: List<Character>
    ): String {
        return """
            You are TaleMotion AI, an expert cinematic director and story-to-video screenwriter.
            Analyze this story and turn it into a high-production video project.
            Story Title: "$title"
            Original Story Text: "$story"
            Language: ${language.displayName}
            Format: ${format.displayName}
            Visual Style: ${visualStyle.displayName} (${visualStyle.description})
            Narration Voice: ${voiceType.displayName}
            Music Mood: ${musicMood.displayName}
            Target Audience: $targetAudience
            Planned Scenes: $sceneCount scenes, each ~$durationPerScene seconds.

            IMPORTANT COPYRIGHT SAFETY:
            Ensure all characters are 100% original. Do not copy copyrighted characters (e.g. Disney, Marvel, Anime franchise icons).

            Return ONLY valid JSON with this exact schema:
            {
              "projectTitle": "$title",
              "summary": "Short 2-sentence synopsis",
              "characters": [
                {
                  "name": "Original Character Name",
                  "age": "Age",
                  "gender": "Gender",
                  "appearance": "Consistent physical description",
                  "hair": "Hair style and color",
                  "clothes": "Specific attire to keep consistent across all scenes",
                  "personality": "Core traits",
                  "voiceType": "Voice profile",
                  "characterStyle": "${visualStyle.displayName}"
                }
              ],
              "scenes": [
                {
                  "sceneNumber": 1,
                  "durationSeconds": $durationPerScene,
                  "location": "Specific environment",
                  "timeOfDay": "Day / Sunset / Night",
                  "characters": ["Character Name"],
                  "action": "Visual cinematic action happening on screen",
                  "dialogue": "Character quote or blank if only narration",
                  "narration": "Voiceover sentence in ${language.displayName} for subtitles and TTS",
                  "visualPrompt": "Detailed photorealistic/stylized image prompt with character description, environment, lighting, angle in ${visualStyle.displayName}",
                  "cameraMovement": "Camera direction (e.g., Slow Pan, Dolly In, Dutch Angle, Drone Sweep)",
                  "mood": "Emotional beat",
                  "soundEffect": "Ambient SFX"
                }
              ]
            }
        """.trimIndent()
    }

    private fun parseStoryAnalysisJson(
        jsonString: String,
        fallbackTitle: String,
        originalStory: String,
        language: LanguageOption,
        durationLabel: String,
        totalDurationSec: Int,
        format: VideoFormat,
        visualStyle: VisualStyle,
        voiceType: VoiceType,
        musicMood: MusicMood,
        targetAudience: String
    ): Project? {
        return try {
            val root = JSONObject(jsonString)
            val title = root.optString("projectTitle", fallbackTitle)
            val summary = root.optString("summary", originalStory)

            val parsedChars = mutableListOf<Character>()
            val charsArray = root.optJSONArray("characters")
            if (charsArray != null) {
                for (i in 0 until charsArray.length()) {
                    val cObj = charsArray.getJSONObject(i)
                    parsedChars.add(
                        Character(
                            id = "char_" + System.currentTimeMillis() + "_$i",
                            name = cObj.optString("name", "Hero $i"),
                            age = cObj.optString("age", "Young"),
                            gender = cObj.optString("gender", "Any"),
                            appearance = cObj.optString("appearance", ""),
                            hair = cObj.optString("hair", ""),
                            clothes = cObj.optString("clothes", ""),
                            personality = cObj.optString("personality", "Curious"),
                            voiceType = cObj.optString("voiceType", voiceType.displayName),
                            characterStyle = visualStyle.displayName
                        )
                    )
                }
            }

            val parsedScenes = mutableListOf<Scene>()
            val scenesArray = root.optJSONArray("scenes")
            if (scenesArray != null) {
                for (i in 0 until scenesArray.length()) {
                    val sObj = scenesArray.getJSONObject(i)
                    val scChars = mutableListOf<String>()
                    val scArr = sObj.optJSONArray("characters")
                    if (scArr != null) {
                        for (k in 0 until scArr.length()) scChars.add(scArr.getString(k))
                    }

                    // Choose image asset if matching demo style or procedural
                    val demoRes = when (i % 5) {
                        0 -> R.drawable.demo_scene_1_1791295457145
                        1 -> R.drawable.demo_scene_2_1791295663005
                        2 -> R.drawable.demo_scene_3_1791295483544
                        3 -> R.drawable.demo_scene_4_1791295685589
                        else -> R.drawable.demo_scene_5_1791295502224
                    }

                    parsedScenes.add(
                        Scene(
                            id = "scene_" + System.currentTimeMillis() + "_$i",
                            sceneNumber = sObj.optInt("sceneNumber", i + 1),
                            durationSeconds = sObj.optInt("durationSeconds", 6),
                            location = sObj.optString("location", "Studio Set"),
                            timeOfDay = sObj.optString("timeOfDay", "Twilight"),
                            characters = scChars,
                            action = sObj.optString("action", ""),
                            dialogue = sObj.optString("dialogue", ""),
                            narration = sObj.optString("narration", ""),
                            visualPrompt = sObj.optString("visualPrompt", ""),
                            cameraMovement = sObj.optString("cameraMovement", "Cinematic Pan"),
                            mood = sObj.optString("mood", "Engaging"),
                            soundEffect = sObj.optString("soundEffect", "Atmospheric chime"),
                            imageResId = demoRes
                        )
                    )
                }
            }

            Project(
                id = "proj_" + System.currentTimeMillis(),
                title = title,
                description = summary,
                language = language,
                durationLabel = durationLabel,
                durationSeconds = totalDurationSec,
                format = format,
                visualStyle = visualStyle,
                voiceType = voiceType,
                musicMood = musicMood,
                targetAudience = targetAudience,
                characters = parsedChars,
                scenes = parsedScenes,
                status = ProjectStatus.Ready,
                thumbnailResId = parsedScenes.firstOrNull()?.imageResId ?: R.drawable.demo_scene_1_1791295457145
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAssistantJson(jsonString: String, base: Project): Project? {
        return try {
            val root = JSONObject(jsonString)
            val updatedTitle = root.optString("updatedTitle", base.title)
            val updatedDesc = root.optString("updatedDescription", base.description)

            val updatedScenes = mutableListOf<Scene>()
            val scenesArray = root.optJSONArray("scenes")
            if (scenesArray != null) {
                for (i in 0 until scenesArray.length()) {
                    val sObj = scenesArray.getJSONObject(i)
                    val existingImg = base.scenes.getOrNull(i)?.imageResId ?: R.drawable.demo_scene_1_1791295457145
                    updatedScenes.add(
                        Scene(
                            id = base.scenes.getOrNull(i)?.id ?: ("scene_asst_" + System.currentTimeMillis() + "_$i"),
                            sceneNumber = sObj.optInt("sceneNumber", i + 1),
                            durationSeconds = sObj.optInt("durationSeconds", 6),
                            location = sObj.optString("location", "Setting"),
                            timeOfDay = sObj.optString("timeOfDay", "Day"),
                            action = sObj.optString("action", ""),
                            dialogue = sObj.optString("dialogue", ""),
                            narration = sObj.optString("narration", ""),
                            visualPrompt = sObj.optString("visualPrompt", ""),
                            cameraMovement = sObj.optString("cameraMovement", "Slow Pan"),
                            mood = sObj.optString("mood", "Expressive"),
                            soundEffect = sObj.optString("soundEffect", "Ambient sound"),
                            imageResId = existingImg
                        )
                    )
                }
            }

            base.copy(
                title = updatedTitle,
                description = updatedDesc,
                scenes = if (updatedScenes.isNotEmpty()) updatedScenes else base.scenes,
                updatedAt = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun generateLocalFallbackProject(
        title: String,
        story: String,
        language: LanguageOption,
        durationLabel: String,
        targetScenesCount: Int,
        durationPerScene: Int,
        format: VideoFormat,
        visualStyle: VisualStyle,
        voiceType: VoiceType,
        musicMood: MusicMood,
        targetAudience: String,
        existingChars: List<Character>
    ): Project {
        // Break story sentences into scenes
        val rawSentences = story.split(Regex("[.!?\\n]+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val scenes = mutableListOf<Scene>()
        val characters = if (existingChars.isNotEmpty()) existingChars else DefaultData.demoCharacters

        val cameraMoves = listOf(
            "Slow cinematic tracking dolly in",
            "Low-angle atmospheric pan",
            "Eye-level medium two-shot with subtle rotation",
            "Wide panoramic sweeping drone crane",
            "High-contrast Dutch angle tilt",
            "Floating handheld follow shot"
        )

        val moods = listOf(
            "Mysterious & Wonder",
            "Rising Excitement",
            "Heartwarming Connection",
            "Dramatic Climax",
            "Triumphant Resolution",
            "Serene Contemplation"
        )

        for (i in 0 until targetScenesCount) {
            val sentence = rawSentences.getOrNull(i)
                ?: rawSentences.getOrNull(i % (rawSentences.size.coerceAtLeast(1)))
                ?: "The story unfolds with breathtaking depth under the open sky."

            val imageRes = when (i % 5) {
                0 -> R.drawable.demo_scene_1_1791295457145
                1 -> R.drawable.demo_scene_2_1791295663005
                2 -> R.drawable.demo_scene_3_1791295483544
                3 -> R.drawable.demo_scene_4_1791295685589
                else -> R.drawable.demo_scene_5_1791295502224
            }

            val charNames = characters.map { it.name }.take(2)
            val promptStyle = "${visualStyle.displayName} style, ${charNames.joinToString(", ")}: $sentence, detailed lighting, cinematic depth, 8k resolution"

            scenes.add(
                Scene(
                    id = "scene_${System.currentTimeMillis()}_$i",
                    sceneNumber = i + 1,
                    durationSeconds = durationPerScene,
                    location = if (i == 0) "Opening Vista" else if (i == targetScenesCount - 1) "The Final Destination" else "Journey Pathway",
                    timeOfDay = if (i < targetScenesCount / 2) "Twilight" else "Midnight",
                    characters = charNames,
                    action = "Scene ${i + 1}: Character action emphasizing $sentence",
                    dialogue = if (charNames.isNotEmpty()) "${charNames.first()}: 'We must keep moving forward!'" else "",
                    narration = sentence,
                    visualPrompt = promptStyle,
                    cameraMovement = cameraMoves[i % cameraMoves.size],
                    mood = moods[i % moods.size],
                    soundEffect = "Atmospheric wind, gentle mystical shimmer",
                    imageResId = imageRes
                )
            )
        }

        return Project(
            id = "proj_" + System.currentTimeMillis(),
            title = title,
            description = story.take(150),
            language = language,
            durationLabel = durationLabel,
            durationSeconds = targetScenesCount * durationPerScene,
            format = format,
            visualStyle = visualStyle,
            voiceType = voiceType,
            musicMood = musicMood,
            targetAudience = targetAudience,
            characters = characters,
            scenes = scenes,
            status = ProjectStatus.Ready,
            thumbnailResId = scenes.firstOrNull()?.imageResId ?: R.drawable.demo_scene_1_1791295457145
        )
    }

    private fun applyLocalAssistantModifier(project: Project, instruction: String): Project {
        val lower = instruction.lowercase()
        val updatedScenes = project.scenes.mapIndexed { idx, scene ->
            when {
                lower.contains("funnier") || lower.contains("funny") -> {
                    scene.copy(
                        dialogue = "${scene.characters.firstOrNull() ?: "Hero"}: 'Well, that definitely didn't happen in the rehearsal!'",
                        mood = "Playful & Slapstick"
                    )
                }
                lower.contains("shorter") -> {
                    scene.copy(durationSeconds = (scene.durationSeconds - 2).coerceAtLeast(3))
                }
                lower.contains("twist") -> {
                    if (idx == project.scenes.size - 1) {
                        scene.copy(
                            action = "Sudden plot twist: An ancient secret is revealed hiding beneath the surface!",
                            dialogue = "${scene.characters.firstOrNull() ?: "Hero"}: 'Wait... it was here all along!'",
                            mood = "Shocking Twist"
                        )
                    } else scene
                }
                lower.contains("emotional") -> {
                    scene.copy(
                        dialogue = "${scene.characters.firstOrNull() ?: "Hero"}: 'I never realized how much this meant to both of us.'",
                        mood = "Deeply Emotional"
                    )
                }
                lower.contains("urdu") -> {
                    scene.copy(
                        narration = "${scene.narration} (اردو ترجمہ کے ساتھ)"
                    )
                }
                else -> scene
            }
        }.toMutableList()

        if (lower.contains("add 3") || lower.contains("three more")) {
            val startNum = updatedScenes.size + 1
            for (k in 0..2) {
                val num = startNum + k
                updatedScenes.add(
                    Scene(
                        id = "scene_added_${System.currentTimeMillis()}_$k",
                        sceneNumber = num,
                        durationSeconds = 6,
                        location = "Uncharted Sanctuary",
                        timeOfDay = "Starlit Midnight",
                        characters = project.characters.map { it.name },
                        action = "Extended scene exploring new wonders together.",
                        dialogue = "Hero: 'Look how far we've ventured!'",
                        narration = "The journey continues beyond the horizon, revealing untouched mysteries.",
                        visualPrompt = "${project.visualStyle.displayName}: Extended chapter scene $num in shimmering starlight.",
                        cameraMovement = "Sweeping Crane Shot",
                        mood = "Boundless Wonder",
                        imageResId = R.drawable.demo_scene_5_1791295502224
                    )
                )
            }
        }

        return project.copy(
            scenes = updatedScenes,
            description = "${project.description} [Modified: $instruction]",
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun extractTitleFromStory(story: String): String {
        val firstWords = story.trim().split(Regex("\\s+")).take(4).joinToString(" ")
        return if (firstWords.isNotBlank()) firstWords.replace(Regex("[^a-zA-Z0-9 ]"), "") else "My TaleMotion Story"
    }
}
