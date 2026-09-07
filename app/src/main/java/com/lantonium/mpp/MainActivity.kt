package com.lantonium.mpp

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PdfConverter.cleanup(this)
        setContent { MppApp() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MppApp() {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = Color(0xFF128C7E),
                onPrimary = Color.White,
                secondary = Color(0xFF25D366),
                background = Color(0xFFF7F3E9),
                surface = Color(0xFFF7F3E9),
                onBackground = Color(0xFF1F2937),
                onSurface = Color(0xFF1F2937),
            )
        ) {
            val context = this
            var selectedUri by remember { mutableStateOf<Uri?>(null) }
            var fileName by remember { mutableStateOf<String?>(null) }
            var pageCount by remember { mutableStateOf<Int?>(null) }
            var previews by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
            var previewing by remember { mutableStateOf(false) }
            var converting by remember { mutableStateOf(false) }
            var status by remember { mutableStateOf("") }
            var askDelete by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            val pickPdf = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    selectedUri = uri
                    fileName = queryName(uri)
                    status = ""
                    pageCount = null
                    previews = emptyList()
                    previewing = true
                    scope.launch {
                        pageCount = runCatching { PdfConverter.pageCount(context, uri) }.getOrNull()
                        previews = runCatching { PdfConverter.renderPreview(context, uri) }.getOrDefault(emptyList())
                        previewing = false
                    }
                }
            }

            val shareLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                askDelete = true
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.lantonium_logo_black),
                        contentDescription = "Lantonium",
                        modifier = Modifier
                            .height(56.dp)
                            .padding(bottom = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.main_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF075E54)
                    )
                    Text(
                        text = stringResource(R.string.app_version),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF128C7E)
                    )
                    Text(
                        text = stringResource(R.string.main_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5B6472),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = stringResource(R.string.pick_pdf),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.pick_pdf_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                            )
                            OutlinedButton(
                                onClick = { pickPdf.launch(arrayOf("application/pdf")) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                enabled = !converting
                            ) {
                                Icon(painterResource(R.drawable.ic_pdf), contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(fileName?.let { it } ?: stringResource(R.string.pick_pdf))
                            }
                            pageCount?.let { count ->
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = stringResource(R.string.page_count, count),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFF128C7E),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (previewing) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.preview_generating),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5B6472)
                        )
                    } else if (previews.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.preview_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(10.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(previews.size) { i ->
                                        PreviewThumb(
                                            bitmap = previews[i],
                                            label = "${i + 1}"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val uri = selectedUri
                            if (uri == null) {
                                Toast.makeText(context, R.string.no_pdf, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            converting = true
                            status = ""
                            scope.launch {
                                try {
                                    val pages = PdfConverter.renderPdfPages(context, uri, PdfConverter.Quality.ALTA)
                                    converting = false
                                    status = context.getString(R.string.conversion_done, pages.size)
                                    sendToWhatsApp(pages, shareLauncher)
                                } catch (t: Throwable) {
                                    converting = false
                                    PdfConverter.cleanup(context)
                                    status = ""
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.error_opening, t.message ?: ""),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFB9C4BE),
                            disabledContentColor = Color.DarkGray
                        ),
                        enabled = !converting
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.convert_send),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (converting) {
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Text(
                            text = stringResource(R.string.converting),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (status.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F6EC)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = status,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                }

                FloatingActionButton(
                    onClick = { startActivity(Intent(context, AboutActivity::class.java)) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 10.dp, end = 12.dp)
                        .size(44.dp),
                    shape = CircleShape,
                    containerColor = Color(0xFF128C7E),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.about_screen))
                }
            }

            if (askDelete) {
                AlertDialog(
                    onDismissRequest = {
                        askDelete = false
                        status = context.getString(R.string.kept_images)
                    },
                    title = { Text(stringResource(R.string.sent_dialog_title)) },
                    text = { Text(stringResource(R.string.sent_dialog_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            askDelete = false
                            PdfConverter.cleanup(context)
                            status = context.getString(R.string.cleaned)
                            Toast.makeText(context, R.string.cleaned, Toast.LENGTH_LONG).show()
                        }) {
                            Text(stringResource(R.string.sent_yes))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            askDelete = false
                            status = context.getString(R.string.kept_images)
                        }) {
                            Text(stringResource(R.string.sent_no))
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun PreviewThumb(bitmap: Bitmap, label: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(86.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280)
            )
        }
    }

    private fun sendToWhatsApp(pages: List<File>, launcher: ActivityResultLauncher<Intent>) {
        val uris = pages.map {
            FileProvider.getUriForFile(this, "$packageName.fileprovider", it)
        }
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (uris.size == 1) {
            intent.clipData = ClipData.newUri(contentResolver, "png", uris.first())
        } else {
            val clip = ClipData.newUri(contentResolver, "png", uris.first())
            uris.drop(1).forEach { clip.addItem(ClipData.Item(it)) }
            intent.clipData = clip
        }
        val targets = listOf("com.whatsapp", "com.whatsapp.w4b")
        val installed = targets.firstOrNull {
            runCatching { packageManager.getLaunchIntentForPackage(it) != null }.getOrDefault(false)
        }
        if (installed != null) {
            intent.setPackage(installed)
        } else {
            Toast.makeText(this, R.string.no_whatsapp, Toast.LENGTH_SHORT).show()
        }
        try {
            launcher.launch(intent)
        } catch (_: ActivityNotFoundException) {
            PdfConverter.cleanup(this)
            Toast.makeText(this, R.string.no_whatsapp, Toast.LENGTH_LONG).show()
        }
    }

    private fun queryName(uri: Uri): String? {
        return runCatching {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && c.moveToFirst()) c.getString(idx) else null
            }
        }.getOrNull()
    }
}