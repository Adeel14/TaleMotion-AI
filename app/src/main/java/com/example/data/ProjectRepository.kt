package com.example.data

import android.content.Context
import com.example.model.Character
import com.example.model.Project
import com.example.model.ProjectStatus
import com.example.model.Scene
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectRepository(private val context: Context) {

    private val projectsFile = File(context.filesDir, "talemotion_projects.json")
    private val charactersFile = File(context.filesDir, "talemotion_characters.json")

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    init {
        loadCharacters()
        loadProjects()
    }

    private fun loadCharacters() {
        val list = mutableListOf<Character>()
        if (charactersFile.exists()) {
            try {
                val jsonStr = charactersFile.readText()
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(jsonToCharacter(obj))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (list.isEmpty()) {
            list.addAll(DefaultData.demoCharacters)
            saveCharactersInternal(list)
        }
        _characters.value = list
    }

    private fun loadProjects() {
        val list = mutableListOf<Project>()
        if (projectsFile.exists()) {
            try {
                val jsonStr = projectsFile.readText()
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(jsonToProject(obj))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (list.isEmpty()) {
            list.add(DefaultData.demoProject)
            saveProjectsInternal(list)
        }
        _projects.value = list
    }

    fun saveProject(project: Project) {
        val current = _projects.value.toMutableList()
        val index = current.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            current[index] = project.copy(updatedAt = System.currentTimeMillis())
        } else {
            current.add(0, project.copy(updatedAt = System.currentTimeMillis()))
        }
        _projects.value = current
        saveProjectsInternal(current)
    }

    fun duplicateProject(projectId: String): Project? {
        val original = _projects.value.find { it.id == projectId } ?: return null
        val copy = original.copy(
            id = "proj_" + System.currentTimeMillis(),
            title = "${original.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            status = ProjectStatus.Ready
        )
        saveProject(copy)
        return copy
    }

    fun deleteProject(projectId: String) {
        val current = _projects.value.filterNot { it.id == projectId }
        _projects.value = current
        saveProjectsInternal(current)
    }

    fun saveCharacter(character: Character) {
        val current = _characters.value.toMutableList()
        val index = current.indexOfFirst { it.id == character.id }
        if (index >= 0) {
            current[index] = character
        } else {
            current.add(character)
        }
        _characters.value = current
        saveCharactersInternal(current)
    }

    fun deleteCharacter(characterId: String) {
        val current = _characters.value.filterNot { it.id == characterId }
        _characters.value = current
        saveCharactersInternal(current)
    }

    private fun saveProjectsInternal(list: List<Project>) {
        try {
            val array = JSONArray()
            for (p in list) {
                array.put(projectToJson(p))
            }
            projectsFile.writeText(array.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveCharactersInternal(list: List<Character>) {
        try {
            val array = JSONArray()
            for (c in list) {
                array.put(characterToJson(c))
            }
            charactersFile.writeText(array.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // JSON serialization helpers
    private fun projectToJson(p: Project): JSONObject {
        val obj = JSONObject()
        obj.put("id", p.id)
        obj.put("title", p.title)
        obj.put("description", p.description)
        obj.put("language", p.language.name)
        obj.put("durationLabel", p.durationLabel)
        obj.put("durationSeconds", p.durationSeconds)
        obj.put("format", p.format.name)
        obj.put("visualStyle", p.visualStyle.name)
        obj.put("voiceType", p.voiceType.name)
        obj.put("musicMood", p.musicMood.name)
        obj.put("targetAudience", p.targetAudience)
        obj.put("status", p.status.name)
        obj.put("createdAt", p.createdAt)
        obj.put("updatedAt", p.updatedAt)
        if (p.thumbnailResId != null) obj.put("thumbnailResId", p.thumbnailResId)
        if (p.thumbnailUri != null) obj.put("thumbnailUri", p.thumbnailUri)

        val charsArray = JSONArray()
        for (c in p.characters) {
            charsArray.put(characterToJson(c))
        }
        obj.put("characters", charsArray)

        val scenesArray = JSONArray()
        for (s in p.scenes) {
            val sObj = JSONObject()
            sObj.put("id", s.id)
            sObj.put("sceneNumber", s.sceneNumber)
            sObj.put("durationSeconds", s.durationSeconds)
            sObj.put("location", s.location)
            sObj.put("timeOfDay", s.timeOfDay)
            val scChars = JSONArray()
            s.characters.forEach { scChars.put(it) }
            sObj.put("characters", scChars)
            sObj.put("action", s.action)
            sObj.put("dialogue", s.dialogue)
            sObj.put("narration", s.narration)
            sObj.put("visualPrompt", s.visualPrompt)
            sObj.put("cameraMovement", s.cameraMovement)
            sObj.put("mood", s.mood)
            sObj.put("soundEffect", s.soundEffect)
            if (s.imageResId != null) sObj.put("imageResId", s.imageResId)
            if (s.imageUri != null) sObj.put("imageUri", s.imageUri)
            scenesArray.put(sObj)
        }
        obj.put("scenes", scenesArray)
        return obj
    }

    private fun jsonToProject(obj: JSONObject): Project {
        val charsList = mutableListOf<Character>()
        val charsArray = obj.optJSONArray("characters")
        if (charsArray != null) {
            for (i in 0 until charsArray.length()) {
                charsList.add(jsonToCharacter(charsArray.getJSONObject(i)))
            }
        }

        val scenesList = mutableListOf<Scene>()
        val scenesArray = obj.optJSONArray("scenes")
        if (scenesArray != null) {
            for (i in 0 until scenesArray.length()) {
                val sObj = scenesArray.getJSONObject(i)
                val scChars = mutableListOf<String>()
                val scArray = sObj.optJSONArray("characters")
                if (scArray != null) {
                    for (k in 0 until scArray.length()) scChars.add(scArray.getString(k))
                }
                scenesList.add(
                    Scene(
                        id = sObj.optString("id", "scene_$i"),
                        sceneNumber = sObj.optInt("sceneNumber", i + 1),
                        durationSeconds = sObj.optInt("durationSeconds", 6),
                        location = sObj.optString("location", "Location"),
                        timeOfDay = sObj.optString("timeOfDay", "Night"),
                        characters = scChars,
                        action = sObj.optString("action", ""),
                        dialogue = sObj.optString("dialogue", ""),
                        narration = sObj.optString("narration", ""),
                        visualPrompt = sObj.optString("visualPrompt", ""),
                        cameraMovement = sObj.optString("cameraMovement", "Pan"),
                        mood = sObj.optString("mood", "Cinematic"),
                        soundEffect = sObj.optString("soundEffect", ""),
                        imageResId = if (sObj.has("imageResId")) sObj.getInt("imageResId") else null,
                        imageUri = if (sObj.has("imageUri")) sObj.getString("imageUri") else null
                    )
                )
            }
        }

        return Project(
            id = obj.getString("id"),
            title = obj.getString("title"),
            description = obj.optString("description", ""),
            language = runCatching { com.example.model.LanguageOption.valueOf(obj.optString("language", "English")) }.getOrDefault(com.example.model.LanguageOption.English),
            durationLabel = obj.optString("durationLabel", "30 seconds"),
            durationSeconds = obj.optInt("durationSeconds", 30),
            format = runCatching { com.example.model.VideoFormat.valueOf(obj.optString("format", "Horizontal_16_9")) }.getOrDefault(com.example.model.VideoFormat.Horizontal_16_9),
            visualStyle = runCatching { com.example.model.VisualStyle.valueOf(obj.optString("visualStyle", "Cartoon_3D")) }.getOrDefault(com.example.model.VisualStyle.Cartoon_3D),
            voiceType = runCatching { com.example.model.VoiceType.valueOf(obj.optString("voiceType", "Storyteller")) }.getOrDefault(com.example.model.VoiceType.Storyteller),
            musicMood = runCatching { com.example.model.MusicMood.valueOf(obj.optString("musicMood", "Adventure")) }.getOrDefault(com.example.model.MusicMood.Adventure),
            targetAudience = obj.optString("targetAudience", "Family"),
            characters = charsList,
            scenes = scenesList,
            status = runCatching { ProjectStatus.valueOf(obj.optString("status", "Ready")) }.getOrDefault(ProjectStatus.Ready),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
            thumbnailResId = if (obj.has("thumbnailResId")) obj.getInt("thumbnailResId") else null,
            thumbnailUri = if (obj.has("thumbnailUri")) obj.getString("thumbnailUri") else null
        )
    }

    private fun characterToJson(c: Character): JSONObject {
        val obj = JSONObject()
        obj.put("id", c.id)
        obj.put("name", c.name)
        obj.put("age", c.age)
        obj.put("gender", c.gender)
        obj.put("appearance", c.appearance)
        obj.put("hair", c.hair)
        obj.put("clothes", c.clothes)
        obj.put("personality", c.personality)
        obj.put("voiceType", c.voiceType)
        obj.put("characterStyle", c.characterStyle)
        if (c.avatarResId != null) obj.put("avatarResId", c.avatarResId)
        if (c.avatarUri != null) obj.put("avatarUri", c.avatarUri)
        return obj
    }

    private fun jsonToCharacter(obj: JSONObject): Character {
        return Character(
            id = obj.getString("id"),
            name = obj.getString("name"),
            age = obj.optString("age", "12"),
            gender = obj.optString("gender", "Male"),
            appearance = obj.optString("appearance", ""),
            hair = obj.optString("hair", ""),
            clothes = obj.optString("clothes", ""),
            personality = obj.optString("personality", ""),
            voiceType = obj.optString("voiceType", "Young voice"),
            characterStyle = obj.optString("characterStyle", "3D Cartoon"),
            avatarResId = if (obj.has("avatarResId")) obj.getInt("avatarResId") else null,
            avatarUri = if (obj.has("avatarUri")) obj.getString("avatarUri") else null
        )
    }
}
