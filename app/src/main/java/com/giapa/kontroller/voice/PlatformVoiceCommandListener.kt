package com.giapa.kontroller.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class PlatformVoiceCommandListener(
    private val context: Context,
) {
    private var recognizer: SpeechRecognizer? = null
    private var isListening: Boolean = false

    fun start(
        onText: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        if (isListening) return

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition service not available on this device")
            return
        }

        val sr = try {
            SpeechRecognizer.createSpeechRecognizer(context)
        } catch (t: Throwable) {
            onError("Failed to create speech recognizer: ${t.message ?: t::class.java.simpleName}")
            return
        }

        recognizer = sr
        isListening = true

        sr.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onResults(results: Bundle?) {
                val texts = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    .orEmpty()

                val best = texts.firstOrNull().orEmpty()
                if (best.isNotBlank()) onText(best)

                // Continue listening until caller stops.
                if (isListening) {
                    runCatching { sr.startListening(recognizerIntent()) }
                        .onFailure { t -> onError("startListening failed: ${t.message ?: t::class.java.simpleName}") }
                }
            }

            override fun onError(error: Int) {
                if (!isListening) return

                when (error) {
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        // Recreate recognizer.
                        stop()
                        start(onText, onError)
                    }

                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        runCatching { sr.startListening(recognizerIntent()) }
                    }

                    else -> {
                        onError("Voice recognition error ($error)")
                        runCatching { sr.startListening(recognizerIntent()) }
                    }
                }
            }
        })

        runCatching { sr.startListening(recognizerIntent()) }
            .onFailure { t ->
                // Most common cause if RECORD_AUDIO wasn't granted or recognition service misbehaves.
                onError("startListening failed: ${t.message ?: t::class.java.simpleName}")
                stop()
            }
    }

    fun stop() {
        isListening = false
        recognizer?.let {
            runCatching { it.stopListening() }
            runCatching { it.cancel() }
            runCatching { it.destroy() }
        }
        recognizer = null
    }

    fun isRunning(): Boolean = isListening

    private fun recognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
    }
}
