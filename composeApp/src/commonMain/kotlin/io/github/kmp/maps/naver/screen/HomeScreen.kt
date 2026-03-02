package io.github.kmp.maps.naver.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kmp.maps.naver.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    val screens = listOf(
        Screen.Marker,
        Screen.Path,
        Screen.ArrowPath,
        Screen.InfoWindow,
        Screen.Circle,
        Screen.MapControls,
        Screen.Location
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Naver Map Compose Demos") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            items(screens) { screen ->
                ListItem(
                    headlineContent = { Text(screen.title) },
                    modifier = Modifier.clickable { onNavigate(screen) }
                )
                HorizontalDivider()
            }
        }
    }
}
