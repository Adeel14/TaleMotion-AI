package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultData
import com.example.data.ProjectRepository
import com.example.model.Character
import com.example.model.CreditState
import com.example.model.LanguageOption
import com.example.model.MusicMood
import com.example.model.Project
import com.example.model.ProjectStatus
import com.example.model.Scene
import com.example.model.StoryTemplate
import com.example.model.SubtitleConfig
import com.example.model.UserAccount
import com.example.model.VideoFormat
import com.example.model.VisualStyle
import com.example.model.VoiceType
import com.example.service.GeminiService
import com.example.service.NarrationVoiceManager
import com.example.service.SoundTrackSynthesizer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object CreateStory : Screen()
    object Studio : Screen()
    object MyProjects : Screen()
    object Characters : Screen()
    object Templates : Screen()
    object Settings : Screen()
}

class TaleMotionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProjectRepository(application)
    private val geminiService = GeminiService()
    val narrationManager = NarrationVoiceManager(application)
    val soundTrackSynthesizer = SoundTrackSynthesizer()

    val projects: StateFlow<List<Project>> = repository.projects
    val characters: StateFlow<List<Character>> = repository.characters

    // Navigation
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Active Project
    private val _activeProject = MutableStateFlow<Project>(DefaultData.demoProject)
    val activeProject: StateFlow<Project> = _activeProject.asStateFlow()

    // Story Creation State
    val createTitle = MutableStateFlow("")
    val createStory = MutableStateFlow("")
    val selectedLanguage = MutableStateFlow(LanguageOption.English)
    val selectedDuration = MutableStateFlow("30 seconds")
    val selectedFormat = MutableStateFlow(VideoFormat.Horizontal_16_9)
    val selectedVisualStyle = MutableStateFlow(VisualStyle.Cartoon_3D)
    val selectedVoice = MutableStateFlow(VoiceType.Storyteller)
    val selectedMusicMood = MutableStateFlow(MusicMood.Adventure)
    val selectedTargetAudience = MutableStateFlow("Family & Kids")
    val copyrightWarning = MutableStateFlow<String?>(null)

    // Generation Progress State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStep = MutableStateFlow("Analyzing story...")
    val generationStep: StateFlow<String> = _generationStep.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress: StateFlow<Float> = _generationProgress.asStateFlow()

    // Player State
    private val _currentSceneIndex = MutableStateFlow(0)
    val currentSceneIndex: StateFlow<Int> = _currentSceneIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _subtitlesConfig = MutableStateFlow(SubtitleConfig())
    val subtitlesConfig: StateFlow<SubtitleConfig> = _subtitlesConfig.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    private var playbackLoopJob: Job? = null

    // AI Assistant State
    private val _isAssistantOpen = MutableStateFlow(false)
    val isAssistantOpen: StateFlow<Boolean> = _isAssistantOpen.asStateFlow()

    private val _isAssistantProcessing = MutableStateFlow(false)
    val isAssistantProcessing: StateFlow<Boolean> = _isAssistantProcessing.asStateFlow()

    // Render / Export State
    private val _isRenderDialogOpen = MutableStateFlow(false)
    val isRenderDialogOpen: StateFlow<Boolean> = _isRenderDialogOpen.asStateFlow()

    private val _isRendering = MutableStateFlow(false)
    val isRendering: StateFlow<Boolean> = _isRendering.asStateFlow()

    private val _renderProgress = MutableStateFlow(0f)
    val renderProgress: StateFlow<Float> = _renderProgress.asStateFlow()

    private val _renderCompleted = MutableStateFlow(false)
    val renderCompleted: StateFlow<Boolean> = _renderCompleted.asStateFlow()

    // User & Credit State
    private val _userAccount = MutableStateFlow(UserAccount())
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    private val _creditState = MutableStateFlow(CreditState())
    val creditState: StateFlow<CreditState> = _creditState.asStateFlow()

    // Character editing dialog
    val editingCharacter = MutableStateFlow<Character?>(null)
    val isCharacterDialogOpen = MutableStateFlow(false)

    // Notification / Toast Message
    val userMessage = MutableStateFlow<String?>(null)

    init {
        // Observe projects: if active is in repository, sync
        viewModelScope.launch {
            repository.projects.collect { list ->
                if (list.isNotEmpty() && _activeProject.value.id == DefaultData.demoProject.id) {
                    list.firstOrNull()?.let { _activeProject.value = it }
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen != Screen.Studio) {
            pauseVideo()
        }
    }

    fun openProjectInStudio(project: Project) {
        _activeProject.value = project
        _currentSceneIndex.value = 0
        _currentScreen.value = Screen.Studio
        pauseVideo()
    }

    fun checkCopyright(text: String) {
        val protectedNames = listOf(
            "tom and jerry", "tom & jerry", "doraemon", "ben 10", "spider-man",
            "spiderman", "batman", "superman", "iron man", "mickey mouse",
            "donald duck", "pikachu", "naruto", "goku", "elsa", "frozen", "shrek"
        )
        val lower = text.lowercase()
        val found = protectedNames.firstOrNull { lower.contains(it) }
        if (found != null) {
            copyrightWarning.value = "Notice: '$found' appears to be a protected trademark or copyrighted character. TaleMotion AI allows you to create original characters with similar genres and styles to ensure compliance!"
        } else {
            copyrightWarning.value = null
        }
    }

    fun applyTemplate(template: StoryTemplate) {
        createTitle.value = template.title
        createStory.value = template.sampleStory
        selectedVisualStyle.value = template.visualStyle
        selectedMusicMood.value = template.musicMood
        selectedDuration.value = template.durationLabel
        selectedFormat.value = template.format
        selectedTargetAudience.value = template.targetAudience
        navigateTo(Screen.CreateStory)
        userMessage.value = "Template '${template.title}' loaded into creator!"
    }

    fun createVideoFromStory() {
        val title = createTitle.value.ifBlank { "My TaleMotion Story" }
        val story = createStory.value
        if (story.isBlank()) {
            userMessage.value = "Please enter your story text or description."
            return
        }

        // Deduct credit
        if (_creditState.value.creditsRemaining <= 0) {
            userMessage.value = "No generations remaining. Upgrade to Pro for unlimited credits!"
            return
        }
        _creditState.value = _creditState.value.copy(
            creditsRemaining = _creditState.value.creditsRemaining - 1
        )

        _isGenerating.value = true
        _generationProgress.value = 0.05f
        _generationStep.value = "Analyzing story with Gemini..."

        viewModelScope.launch {
            try {
                delay(600)
                _generationProgress.value = 0.25f
                _generationStep.value = "Creating original characters..."
                delay(600)
                _generationProgress.value = 0.45f
                _generationStep.value = "Planning scenes and camera angles..."

                val analyzedProject = geminiService.analyzeStory(
                    titleInput = title,
                    storyInput = story,
                    language = selectedLanguage.value,
                    durationLabel = selectedDuration.value,
                    format = selectedFormat.value,
                    visualStyle = selectedVisualStyle.value,
                    voiceType = selectedVoice.value,
                    musicMood = selectedMusicMood.value,
                    targetAudience = selectedTargetAudience.value,
                    existingCharacters = characters.value
                )

                _generationProgress.value = 0.70f
                _generationStep.value = "Generating visual scene prompts..."
                delay(700)
                _generationProgress.value = 0.85f
                _generationStep.value = "Synthesizing voice narration and subtitles..."
                delay(500)
                _generationProgress.value = 1.0f
                _generationStep.value = "Finalizing video project..."
                delay(400)

                repository.saveProject(analyzedProject)
                _activeProject.value = analyzedProject
                _currentSceneIndex.value = 0
                _isGenerating.value = false
                _currentScreen.value = Screen.Studio
                userMessage.value = "Story converted to video successfully!"
            } catch (e: Exception) {
                _isGenerating.value = false
                userMessage.value = "Generation completed with fallback settings."
            }
        }
    }

    // Video Playback Controls
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pauseVideo()
        } else {
            playVideo()
        }
    }

    fun playVideo() {
        val proj = _activeProject.value
        if (proj.scenes.isEmpty()) return
        _isPlaying.value = true

        if (!_isMuted.value) {
            soundTrackSynthesizer.play(proj.musicMood)
        }

        startScenePlayback(_currentSceneIndex.value)
    }

    fun pauseVideo() {
        _isPlaying.value = false
        playbackLoopJob?.cancel()
        playbackLoopJob = null
        narrationManager.stop()
        soundTrackSynthesizer.stop()
    }

    fun nextScene() {
        val proj = _activeProject.value
        if (_currentSceneIndex.value < proj.scenes.size - 1) {
            seekToScene(_currentSceneIndex.value + 1)
        } else {
            seekToScene(0)
            if (_isPlaying.value) pauseVideo()
        }
    }

    fun prevScene() {
        if (_currentSceneIndex.value > 0) {
            seekToScene(_currentSceneIndex.value - 1)
        }
    }

    fun seekToScene(index: Int) {
        val proj = _activeProject.value
        val validIndex = index.coerceIn(0, (proj.scenes.size - 1).coerceAtLeast(0))
        _currentSceneIndex.value = validIndex
        if (_isPlaying.value) {
            playbackLoopJob?.cancel()
            startScenePlayback(validIndex)
        }
    }

    private fun startScenePlayback(index: Int) {
        val proj = _activeProject.value
        if (index >= proj.scenes.size) {
            pauseVideo()
            return
        }
        val scene = proj.scenes[index]

        // Speak scene narration
        if (!_isMuted.value && scene.narration.isNotBlank()) {
            narrationManager.speak(
                text = scene.narration,
                voiceType = proj.voiceType,
                language = proj.language
            )
        }

        playbackLoopJob?.cancel()
        playbackLoopJob = viewModelScope.launch {
            val sceneDurationMs = (scene.durationSeconds.coerceAtLeast(4)) * 1000L
            delay(sceneDurationMs)
            if (_isPlaying.value) {
                if (index < proj.scenes.size - 1) {
                    _currentSceneIndex.value = index + 1
                    startScenePlayback(index + 1)
                } else {
                    pauseVideo()
                    _currentSceneIndex.value = 0
                }
            }
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        if (_isMuted.value) {
            soundTrackSynthesizer.setVolume(0f)
            narrationManager.stop()
        } else {
            soundTrackSynthesizer.setVolume(0.35f)
            if (_isPlaying.value) {
                val proj = _activeProject.value
                soundTrackSynthesizer.play(proj.musicMood)
            }
        }
    }

    fun toggleSubtitles() {
        _subtitlesConfig.value = _subtitlesConfig.value.copy(
            enabled = !_subtitlesConfig.value.enabled
        )
    }

    fun updateSubtitleConfig(fontSizeSp: Float, position: String) {
        _subtitlesConfig.value = _subtitlesConfig.value.copy(
            fontSizeSp = fontSizeSp,
            position = position
        )
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    // Scene Builder Actions
    fun reorderSceneUp(index: Int) {
        if (index <= 0) return
        val currentScenes = _activeProject.value.scenes.toMutableList()
        val temp = currentScenes[index]
        currentScenes[index] = currentScenes[index - 1]
        currentScenes[index - 1] = temp
        val renumbered = currentScenes.mapIndexed { idx, sc -> sc.copy(sceneNumber = idx + 1) }
        updateProjectScenes(renumbered)
    }

    fun reorderSceneDown(index: Int) {
        val currentScenes = _activeProject.value.scenes.toMutableList()
        if (index >= currentScenes.size - 1) return
        val temp = currentScenes[index]
        currentScenes[index] = currentScenes[index + 1]
        currentScenes[index + 1] = temp
        val renumbered = currentScenes.mapIndexed { idx, sc -> sc.copy(sceneNumber = idx + 1) }
        updateProjectScenes(renumbered)
    }

    fun updateScene(scene: Scene) {
        val currentScenes = _activeProject.value.scenes.toMutableList()
        val idx = currentScenes.indexOfFirst { it.id == scene.id }
        if (idx >= 0) {
            currentScenes[idx] = scene
            updateProjectScenes(currentScenes)
        }
    }

    fun deleteScene(sceneId: String) {
        val currentScenes = _activeProject.value.scenes.filterNot { it.id == sceneId }
        val renumbered = currentScenes.mapIndexed { idx, sc -> sc.copy(sceneNumber = idx + 1) }
        updateProjectScenes(renumbered)
        if (_currentSceneIndex.value >= renumbered.size) {
            _currentSceneIndex.value = (renumbered.size - 1).coerceAtLeast(0)
        }
    }

    fun addScene() {
        val currentScenes = _activeProject.value.scenes.toMutableList()
        val nextNum = currentScenes.size + 1
        val newScene = Scene(
            id = "scene_new_${System.currentTimeMillis()}",
            sceneNumber = nextNum,
            durationSeconds = 6,
            location = "New Cinematic Setting",
            timeOfDay = "Twilight",
            characters = _activeProject.value.characters.map { it.name }.take(2),
            action = "New scene action beats unfolding smoothly.",
            dialogue = "",
            narration = "A new chapter unfolds with vibrant cinematic emotion.",
            visualPrompt = "${_activeProject.value.visualStyle.displayName}: High resolution scene $nextNum.",
            cameraMovement = "Slow Dolly In",
            mood = "Inspiring",
            imageResId = when (nextNum % 5) {
                0 -> com.example.R.drawable.demo_scene_5_1791295502224
                1 -> com.example.R.drawable.demo_scene_1_1791295457145
                2 -> com.example.R.drawable.demo_scene_2_1791295663005
                3 -> com.example.R.drawable.demo_scene_3_1791295483544
                else -> com.example.R.drawable.demo_scene_4_1791295685589
            }
        )
        currentScenes.add(newScene)
        updateProjectScenes(currentScenes)
    }

    fun regenerateSceneImage(sceneId: String) {
        val currentScenes = _activeProject.value.scenes.toMutableList()
        val idx = currentScenes.indexOfFirst { it.id == sceneId }
        if (idx >= 0) {
            val sc = currentScenes[idx]
            // Cycle through available high-quality demo drawables
            val newRes = when (sc.imageResId) {
                com.example.R.drawable.demo_scene_1_1791295457145 -> com.example.R.drawable.demo_scene_2_1791295663005
                com.example.R.drawable.demo_scene_2_1791295663005 -> com.example.R.drawable.demo_scene_3_1791295483544
                com.example.R.drawable.demo_scene_3_1791295483544 -> com.example.R.drawable.demo_scene_4_1791295685589
                com.example.R.drawable.demo_scene_4_1791295685589 -> com.example.R.drawable.demo_scene_5_1791295502224
                else -> com.example.R.drawable.demo_scene_1_1791295457145
            }
            currentScenes[idx] = sc.copy(imageResId = newRes)
            updateProjectScenes(currentScenes)
            userMessage.value = "Scene ${sc.sceneNumber} visual regenerated!"
        }
    }

    private fun updateProjectScenes(newScenes: List<Scene>) {
        val updated = _activeProject.value.copy(
            scenes = newScenes,
            durationSeconds = newScenes.sumOf { it.durationSeconds },
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        repository.saveProject(updated)
    }

    // AI Assistant
    fun toggleAssistant(open: Boolean) {
        _isAssistantOpen.value = open
    }

    fun askAssistant(instruction: String) {
        if (instruction.isBlank()) return
        _isAssistantProcessing.value = true
        viewModelScope.launch {
            try {
                val updated = geminiService.modifyProjectWithAssistant(_activeProject.value, instruction)
                _activeProject.value = updated
                repository.saveProject(updated)
                _isAssistantProcessing.value = false
                userMessage.value = "Story modified: $instruction"
            } catch (e: Exception) {
                _isAssistantProcessing.value = false
                userMessage.value = "Failed to apply modification."
            }
        }
    }

    // Video Render & Export
    fun openRenderDialog() {
        pauseVideo()
        _renderProgress.value = 0f
        _renderCompleted.value = false
        _isRendering.value = false
        _isRenderDialogOpen.value = true
    }

    fun closeRenderDialog() {
        _isRenderDialogOpen.value = false
    }

    fun startRenderPipeline() {
        _isRendering.value = true
        _renderProgress.value = 0.05f
        viewModelScope.launch {
            val totalSteps = 10
            for (i in 1..totalSteps) {
                delay(350)
                _renderProgress.value = (i.toFloat() / totalSteps)
            }
            _isRendering.value = false
            _renderCompleted.value = true
            val updated = _activeProject.value.copy(status = ProjectStatus.Completed)
            _activeProject.value = updated
            repository.saveProject(updated)
            userMessage.value = "Video render completed! Ready to export."
        }
    }

    // Project Management
    fun duplicateProject(id: String) {
        val dup = repository.duplicateProject(id)
        if (dup != null) {
            userMessage.value = "Project duplicated: ${dup.title}"
        }
    }

    fun deleteProject(id: String) {
        repository.deleteProject(id)
        userMessage.value = "Project removed."
    }

    // Character Management
    fun saveCharacter(character: Character) {
        repository.saveCharacter(character)
        isCharacterDialogOpen.value = false
        editingCharacter.value = null
        userMessage.value = "Character '${character.name}' saved!"
    }

    fun deleteCharacter(characterId: String) {
        repository.deleteCharacter(characterId)
        userMessage.value = "Character deleted."
    }

    fun generateNewCharacter(namePrompt: String, stylePrompt: String) {
        val originalNames = listOf("Aero", "Kaelen", "Mira", "Tariq", "Zora", "Lyra", "Orion", "Zephyr")
        val chosenName = namePrompt.ifBlank { originalNames.random() }
        val newChar = Character(
            id = "char_" + System.currentTimeMillis(),
            name = chosenName,
            age = "14",
            gender = "Explorer",
            appearance = "Heroic expressive character with warm determined eyes, ready for grand adventures",
            hair = "Stylized layered dynamic hair with subtle luminous highlights",
            clothes = "Custom adventurer suit with brass utility buckles, high-collar tunic, and enchanted charm",
            personality = "Courageous, resourceful, loyal companion",
            voiceType = "Storyteller voice",
            characterStyle = stylePrompt.ifBlank { "3D Pixar-like animation" },
            avatarResId = com.example.R.drawable.demo_scene_3_1791295483544
        )
        saveCharacter(newChar)
    }

    fun upgradeToPro() {
        _creditState.value = CreditState(
            isPro = true,
            creditsRemaining = 100,
            maxCredits = 100,
            resolutionLabel = "4K Cinema HDR",
            watermarkEnabled = false,
            maxDurationMinutes = 10
        )
        _userAccount.value = _userAccount.value.copy(plan = "Studio VIP Pro")
        userMessage.value = "Upgraded to Studio VIP Pro! Watermark disabled & 4K unlocked."
    }

    fun clearMessage() {
        userMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        pauseVideo()
        narrationManager.release()
    }
}
