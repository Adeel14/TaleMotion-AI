package com.example.service

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.MusicMood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundTrackSynthesizer {

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volume = MutableStateFlow(0.35f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _customTrackName = MutableStateFlow<String?>(null)
    val customTrackName: StateFlow<String?> = _customTrackName.asStateFlow()

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0f, 1f)
        audioTrack?.setVolume(_volume.value)
    }

    fun setCustomTrack(name: String?) {
        _customTrackName.value = name
    }

    fun play(mood: MusicMood) {
        stop()
        _isPlaying.value = true

        playbackJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.setVolume(_volume.value)
            track.play()

            // Musical chords for each mood (base frequencies in Hz)
            val chords = when (mood) {
                MusicMood.Adventure -> listOf(220.0, 277.18, 329.63, 440.0) // A major epic
                MusicMood.Cinematic -> listOf(146.83, 220.0, 261.63, 329.63) // D minor cinematic
                MusicMood.Fantasy -> listOf(261.63, 329.63, 392.0, 523.25) // C major ethereal
                MusicMood.Emotional -> listOf(174.61, 220.0, 261.63, 349.23) // F major warm
                MusicMood.Happy -> listOf(293.66, 369.99, 440.0, 587.33) // D major bright
                MusicMood.Suspense -> listOf(110.0, 116.54, 164.81, 220.0) // A diminished dark drone
                MusicMood.Sad -> listOf(164.81, 196.0, 246.94, 329.63) // E minor melancholy
                MusicMood.Funny -> listOf(329.63, 415.3, 493.88, 659.25) // E major bounce
            }

            val pcmBuffer = ShortArray(bufferSize / 2)
            var phase = 0.0
            val twoPi = 2.0 * Math.PI

            while (isActive && _isPlaying.value) {
                for (i in pcmBuffer.indices) {
                    val t = phase / sampleRate
                    var sampleSum = 0.0

                    // Layer harmonic sine waves with soft attack
                    for ((idx, freq) in chords.withIndex()) {
                        val amp = 0.22 / (idx + 1)
                        val mod = 1.0 + 0.15 * sin(twoPi * 0.4 * t)
                        sampleSum += amp * sin(twoPi * freq * t) * mod
                    }

                    // Soft ambient fade limiter
                    val clamped = (sampleSum * 32767.0 * 0.4).toInt().coerceIn(-32767, 32767)
                    pcmBuffer[i] = clamped.toShort()
                    phase += 1.0
                }

                track.write(pcmBuffer, 0, pcmBuffer.size)
            }
        }
    }

    fun stop() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }
}
