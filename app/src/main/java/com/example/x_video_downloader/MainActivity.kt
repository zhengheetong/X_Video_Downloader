package com.example.x_video_downloader

import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlInput = findViewById<EditText>(R.id.urlInput)
        val pasteButton = findViewById<Button>(R.id.pasteButton)
        val downloadButton = findViewById<Button>(R.id.downloadButton)

        // Initialize history on startup
        refreshHistory()

        pasteButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip() && clipboard.primaryClip?.getItemAt(0)?.text != null) {
                val copiedText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
                urlInput.setText(copiedText)
            } else {
                Toast.makeText(this, "SYSTEM: CLIPBOARD_EMPTY", Toast.LENGTH_SHORT).show()
            }
        }

        downloadButton.setOnClickListener {
            // Trigger Glitch Animation
            val glitchAnim = AnimationUtils.loadAnimation(this, R.anim.glitch)
            downloadButton.startAnimation(glitchAnim)

            val userLink = urlInput.text.toString()
            if (userLink.isNotEmpty()) {
                extractVideoUrlWithRapidApi(userLink)
            } else {
                Toast.makeText(this, "SYSTEM_ERROR: NULL_LINK", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractVideoUrlWithRapidApi(xLink: String) {
        Toast.makeText(this, "INTERCEPTING DATA STREAM...", Toast.LENGTH_SHORT).show()

        val formattedLink = xLink.replace("x.com", "twitter.com")
        val encodedUrl = URLEncoder.encode(formattedLink, "UTF-8")
        val apiUrl = "https://twitter-video-downloader2.p.rapidapi.com/?url=$encodedUrl"

        val request = Request.Builder()
            .url(apiUrl)
            .get()
            // Pulling the credentials dynamically from our new ApiConfig vault!
            .addHeader("x-rapidapi-host", ApiConfig.RAPID_API_HOST)
            .addHeader("x-rapidapi-key", ApiConfig.RAPID_API_KEY)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@MainActivity, "CONNECTION_FAILURE", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string() ?: ""
                if (response.isSuccessful && responseData.isNotEmpty()) {
                    try {
                        val jsonObject = JSONObject(responseData)
                        if (jsonObject.optString("status") == "success") {
                            val dataObj = jsonObject.getJSONObject("data")
                            val directVideoUrl = dataObj.getString("src")

                            runOnUiThread {
                                val themedContext = ContextThemeWrapper(this@MainActivity, R.style.CyberDialog)
                                val builder = android.app.AlertDialog.Builder(themedContext)
                                    .setTitle("> SECURE_DATA_FOUND")
                                    .setMessage("MP4 Source located. Execute download sequence?")
                                    .setPositiveButton("EXECUTE") { _, _ ->
                                        executeDownload(directVideoUrl, "X_DATA_${System.currentTimeMillis()}.mp4")
                                    }
                                    .setNegativeButton("ABORT", null)

                                val dialog = builder.create()
                                dialog.show()

                                // THE FIX: Manually force the buttons to be Neon Cyan after showing the dialog
                                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00FFFF"))
                                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF00FF")) // Pink for Abort!
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread { Toast.makeText(this@MainActivity, "PARSING_ERROR", Toast.LENGTH_LONG).show() }
                    }
                }
            }
        })
    }

    private fun executeDownload(videoUrl: String, fileName: String) {
        runOnUiThread { Toast.makeText(this, "DOWNLOADING...", Toast.LENGTH_SHORT).show() }

        val request = Request.Builder()
            .url(videoUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .addHeader("Referer", "https://twitter.com/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@MainActivity, "DOWNLOAD_FAILED", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                try {
                    val defaultDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val customDir = File(defaultDownloads, "X_Downloader")
                    if (!customDir.exists()) customDir.mkdirs()

                    val file = File(customDir, fileName)
                    val inputStream = response.body?.byteStream()
                    val outputStream = FileOutputStream(file)

                    inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }

                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "ARCHIVED: /Downloads/X_Downloader", Toast.LENGTH_LONG).show()
                        refreshHistory()

                        // THE FIX: Tell the Android Gallery to scan and index the new video!
                        android.media.MediaScannerConnection.scanFile(
                            this@MainActivity,
                            arrayOf(file.absolutePath),
                            arrayOf("video/mp4"),
                            null
                        )
                    }
                } catch (e: Exception) {
                    runOnUiThread { Toast.makeText(this@MainActivity, "SAVE_ERROR", Toast.LENGTH_LONG).show() }
                }
            }
        })
    }

    private fun refreshHistory() {
        val historyContainer = findViewById<LinearLayout>(R.id.historyContainer)
        runOnUiThread {
            historyContainer.removeAllViews()
            val defaultDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val customDir = File(defaultDownloads, "X_Downloader")

            if (!customDir.exists() || customDir.listFiles().isNullOrEmpty()) {
                val emptyText = TextView(this)
                emptyText.text = "> NO_DATA_FOUND"
                emptyText.setTextColor(Color.parseColor("#00FFFF"))
                historyContainer.addView(emptyText)
                return@runOnUiThread
            }

            val files = customDir.listFiles()?.filter { it.extension == "mp4" }?.sortedByDescending { it.lastModified() } ?: return@runOnUiThread
            val dateFormat = SimpleDateFormat("HH:mm:ss // yyyy.MM.dd", Locale.getDefault())

            for (file in files) {
                val textView = TextView(this)
                val dateString = dateFormat.format(Date(file.lastModified()))
                textView.text = ">> ${file.name}\n[TIMESTAMP]: $dateString"
                textView.setTextColor(Color.WHITE)
                textView.setPadding(40, 40, 40, 40)
                textView.setBackgroundColor(Color.parseColor("#1A1A1A"))

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, 20)
                textView.layoutParams = params

                textView.setOnClickListener { playVideoInApp(file.absolutePath) }
                historyContainer.addView(textView)
            }
        }
    }

    private fun playVideoInApp(videoPath: String) {
        val dialog = Dialog(this)
        val videoView = VideoView(this)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoPath(videoPath)

        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        videoView.layoutParams = layoutParams

        dialog.setContentView(videoView)
        dialog.show()
        videoView.start()
    }
}