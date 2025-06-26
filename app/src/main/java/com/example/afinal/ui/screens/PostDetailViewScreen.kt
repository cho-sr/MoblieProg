package com.example.afinal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.afinal.Post
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@Composable
fun PostDetailViewScreen(
    post: Post,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(post.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Text(post.content, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 이미지 URI 유효성 검사
        val isValidUri = post.imageUri?.let {
            it.startsWith("content://") || it.startsWith("file://") || it.startsWith("http")
        } == true

        if (isValidUri) {
            Image(
                painter = rememberAsyncImagePainter(post.imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (post.lat != null && post.lng != null) {
            val location = LatLng(post.lat, post.lng)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 15f)
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                cameraPositionState = cameraPositionState
            ) {
                Marker(state = rememberMarkerState(position = location), title = post.title)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onBack) {
                Text("뒤로가기")
            }

            Button(onClick = onEdit) {
                Text("수정")
            }
        }
    }
}
