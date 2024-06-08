package com.example.viewreferee

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun exportData(context: Context, events: List<String>) {
    val fileName = "Spielbericht.txt"
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { outputStream ->
        outputStream.write("Ereignisse und Notizen:\n".toByteArray())
        events.forEach { event ->
            outputStream.write("$event\n".toByteArray())
        }
    }

    val uri = Uri.fromFile(file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Spielbericht")
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    context.startActivity(Intent.createChooser(intent, "Daten senden"))
}
