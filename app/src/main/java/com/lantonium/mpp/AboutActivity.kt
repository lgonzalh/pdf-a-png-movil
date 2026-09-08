package com.lantonium.mpp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AboutScreen(onBack = { finish() }) }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    fun AboutScreen(onBack: () -> Unit) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = Color(0xFF128C7E),
                background = Color(0xFFF7F3E9),
                surface = Color(0xFFF7F3E9),
                onBackground = Color(0xFF1F2937),
                onSurface = Color(0xFF1F2937),
            )
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.about_screen)) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF075E54),
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.luis),
                        contentDescription = "Luis Gonzalez",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Luis Gonzalez",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF075E54)
                    )
                    Text(
                        text = "AI Engineer | Programmer | Entrepreneur",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF5B6472)
                    )
                    Spacer(Modifier.height(18.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Soy Luis Gonzalez, AI Engineer especializado en diseñar herramientas y " +
                                        "plataformas que combinan inteligencia humana e inteligencia artificial para " +
                                        "ampliar las capacidades de los equipos de desarrollo.",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Construyo plataformas B2B, Micro-SaaS y herramientas de ingeniería de software " +
                                        "principalmente sobre .NET/C#, arquitecturas web y serverless, con especial interés " +
                                        "en sistemas local-first y aplicaciones donde la IA puede trabajar directamente " +
                                        "sobre el entorno y los datos del usuario.",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "La IA no debería limitarse a generar código: debe comprender el proyecto, " +
                                        "conservar su contexto y participar en su evolución de forma trazable. " +
                                        "Ese principio está en acción en herramientas como Condor y en las soluciones " +
                                        "empresariales de Lantonium para automatizar cargas masivas de datos.",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Pila tecnológica",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            FlowRow(
                                modifier = Modifier.padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    ".NET C#", "ASP.NET Core", "PostgreSQL", "Supabase", "Kotlin",
                                    "Android SDK", "Python", "Ollama", "SignalR", "Git", "Firebase",
                                    "Google Cloud Run"
                                ).forEach { tag ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(tag) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Contacto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ContactIcon(R.drawable.lantonium_logo_black, "Sitio web", Modifier.weight(1f)) {
                                    openUrl("https://lantonium.com/acerca")
                                }
                                ContactIcon(R.drawable.github, "GitHub", Modifier.weight(1f)) {
                                    openUrl("https://github.com/lgonzalh")
                                }
                                ContactIcon(R.drawable.linkedin, "LinkedIn", Modifier.weight(1f)) {
                                    openUrl("https://www.linkedin.com/in/luis-gonzalez")
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ContactIcon(R.drawable.whatsapp, "WhatsApp", Modifier.weight(1f)) {
                                    openUrl("https://wa.me/573246864991")
                                }
                                ContactIcon(R.drawable.outlook, "Email", Modifier.weight(1f)) {
                                    openUrl("mailto:lgonzalh@outlook.com")
                                }
                                Spacer(Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
text = "Si quieres conversar sobre un proyecto, una oportunidad laboral o simplemente " +
                                    "intercambiar ideas sobre ingeniería de software, puedes escribirme a lgonzalh@outlook.com " +
                                    "o conectarte conmigo en LinkedIn o GitHub o por WhatsApp.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5B6472),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "© 2021–2026 Luis Gonzalez. Todos los derechos reservados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    private fun ContactIcon(drawableRes: Int, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
        Column(
            modifier = modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6))
            ) {
                Image(
                    painter = painterResource(drawableRes),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF374151)
            )
        }
    }

    private fun openUrl(url: String) {
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    }
}