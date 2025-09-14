package dev.sixdev.sghost.media

import android.media.MediaRecorder
import java.io.File

class AudioRecorder(private val outFile: File) {
    private var rec: MediaRecorder? = null

    fun start() {
        val r = MediaRecorder()
        r.setAudioSource(MediaRecorder.AudioSource.MIC)
        r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        r.setAudioEncodingBitRate(128000)
        r.setAudioSamplingRate(44100)
        r.setOutputFile(outFile.absolutePath)
        r.prepare()
        r.start()
        rec = r
    }

    fun stop() {
        try { rec?.stop() } catch (_:Exception) {}
        rec?.release()
        rec = null
    }
}
