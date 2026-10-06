package com.example.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.LanguageOption
import com.example.model.VoiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class NarrationVoiceManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    private var onCompletionCallback: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        _currentUtteranceId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        onCompletionCallback?.invoke()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        onCompletionCallback?.invoke()
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        onCompletionCallback?.invoke()
                    }
                })
            }
        }
    }

    fun speak(
        text: String,
        voiceType: VoiceType,
        language: LanguageOption,
        utteranceId: String = "utt_" + System.currentTimeMillis(),
        onComplete: (() -> Unit)? = null
    ) {
        if (text.isBlank()) {
            onComplete?.invoke()
            return
        }

        onCompletionCallback = onComplete

        if (!isInitialized || tts == null) {
            // Simulated speech duration if TTS is not ready
            _isSpeaking.value = true
            _currentUtteranceId.value = utteranceId
            return
        }

        val locale = when (language) {
            LanguageOption.English -> Locale.US
            LanguageOption.Urdu -> Locale.forLanguageTag("ur-PK")
            LanguageOption.Hindi -> Locale.forLanguageTag("hi-IN")
            LanguageOption.Arabic -> Locale.forLanguageTag("ar-SA")
            LanguageOption.Spanish -> Locale.forLanguageTag("es-ES")
            LanguageOption.French -> Locale.FRENCH
        }

        tts?.let { engine ->
            val langResult = engine.setLanguage(locale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                engine.language = Locale.US
            }
            engine.setPitch(voiceType.pitch)
            engine.setSpeechRate(voiceType.speed)

            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentUtteranceId.value = null
        onCompletionCallback = null
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
