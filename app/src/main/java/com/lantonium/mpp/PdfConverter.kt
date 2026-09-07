package com.lantonium.mpp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfConverter {

    enum class Quality(val dpi: Int) {
        BAJA(96), MEDIA(144), ALTA(200)
    }

fun outputDir(context: Context): File =
    File(context.filesDir, "pdf2png").apply { mkdirs() }

    suspend fun renderPdfPages(context: Context, uri: Uri, quality: Quality): List<File> =
        withContext(Dispatchers.IO) {
            val pfd: ParcelFileDescriptor =
                context.contentResolver.openFileDescriptor(uri, "r")
                    ?: throw IllegalStateException("No se pudo abrir el PDF")

            val outDir = outputDir(context).apply {
                deleteRecursively()
                mkdirs()
            }
            File(outDir, ".nomedia").writeBytes(ByteArray(0))

            val renderer = PdfRenderer(pfd)
            val files = mutableListOf<File>()
            try {
                val scale = quality.dpi / 72f
                for (i in 0 until renderer.pageCount) {
                    val page = renderer.openPage(i)
                    try {
                        val width = (page.width * scale).toInt()
                        val height = (page.height * scale).toInt()
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        bitmap.eraseColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        val out = File(outDir, "pagina_${i + 1}.png")
                        FileOutputStream(out).use {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        bitmap.recycle()
                        files += out
                    } finally {
                        page.close()
                    }
                }
            } finally {
                renderer.close()
                pfd.close()
            }
            files
        }

    fun cleanup(context: Context) {
        outputDir(context).deleteRecursively()
    }
}