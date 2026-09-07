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

    suspend fun pageCount(context: Context, uri: Uri): Int = withContext(Dispatchers.IO) {
        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return@withContext 0
            try {
                PdfRenderer(pfd).use { it.pageCount }
            } finally {
                pfd.close()
            }
        } catch (t: Throwable) {
            0
        }
    }

    suspend fun renderPreview(
        context: Context,
        uri: Uri,
        maxPages: Int = 40,
        thumbWidth: Int = 300
    ): List<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return@withContext emptyList()
            try {
                PdfRenderer(pfd).use { renderer ->
                    val bitmaps = mutableListOf<Bitmap>()
                    val pages = minOf(renderer.pageCount, maxPages)
                    for (i in 0 until pages) {
                        val page = renderer.openPage(i)
                        try {
                            val width = thumbWidth
                            val height = (page.height * thumbWidth / page.width.toFloat()).toInt()
                            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            bmp.eraseColor(Color.WHITE)
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            bitmaps += bmp
                        } finally {
                            page.close()
                        }
                    }
                    bitmaps
                }
            } finally {
                pfd.close()
            }
        } catch (t: Throwable) {
            emptyList()
        }
    }

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