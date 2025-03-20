package com.example.iimusica

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.iimusica.ui.theme.IIMusicaTheme


private const val REQUEST_CODE = 1

fun checkAndRequestPermissions(activity: Activity) {
    val permissions = mutableListOf<String>()

    // For devices running Android 10 and below
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    // For devices running Android 13 and above
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
        != PackageManager.PERMISSION_GRANTED
    ) {
        permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
    }

    if (permissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), REQUEST_CODE)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions(this)
        enableEdgeToEdge()


        setContent {
            IIMusicaTheme {
                MusicFilesScreen( context = this)

                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicFilesScreen(context: Context) {
    var mFiles by remember { mutableStateOf(emptyList<MusicFile>()) }

    LaunchedEffect(Unit) {
        mFiles = getAllMusicFiles(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Music Files", style= TextStyle(fontWeight = FontWeight.W500,  fontSize = 28.sp, color = Color(0xFFFFFFFF), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF030310),
                    titleContentColor = Color(0xFFFFFFFF),
                )

            )
        }


    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0B1045), Color(0xFF5B245A))
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(mFiles) { music ->
                    MusicItem(music)
                }
            }

            FloatingActionButton(
                onClick = { mFiles = getAllMusicFiles(context) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)


            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    }


}


@Composable
fun MusicItem(music: MusicFile) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO music */ }
            .padding(vertical = 2.dp)
            .background(color = Color(0xFF030310)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Use Coil to load the image
        val painter = rememberAsyncImagePainter(
            model = music.albumArtUri ?: R.drawable.default_image, // Provide default if null
            error = painterResource(id = R.drawable.default_image), // Use same fallback for errors
            placeholder = painterResource(id = R.drawable.default_image) // Optional loading image
        )

        Image(
            painter = painter,
            contentDescription = "Album Art",
            modifier = Modifier
                .size(60.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(text = music.name, style= TextStyle(fontWeight = FontWeight.W500, fontSize = 20.sp, color = Color(0xFFFFFFFF)
            ), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth() )
            Text(text = music.artist, style= TextStyle(fontSize = 18.sp, color = Color(0xFFFFFFFF)))
            Text(text = "Duration ${music.duration}", style=TextStyle(color = Color(0xFFFFFFFF)))
        }

    }
}