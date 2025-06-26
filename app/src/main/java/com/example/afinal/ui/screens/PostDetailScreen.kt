package com.example.afinal.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.afinal.Post
import com.example.afinal.PostDBHelper
import com.example.afinal.PostViewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun PostDetailScreen(
    post: Post?, // 수정 중인 Post 객체 (null이면 새 글 작성)
    dbHelper: PostDBHelper, // DB 헬퍼 클래스
    viewModel: PostViewModel, // ViewModel을 통해 상태 공유
    onBack: () -> Unit, // 뒤로 가기 콜백
    onSelectLocation: (LatLng?) -> Unit // 지도 위치 선택 콜백
) {
    // ViewModel에서 상태값 가져오기
    val title = viewModel.title.value
    val content = viewModel.content.value
    val imageUri = viewModel.imageUri.value?.toUri()
    val latLng by viewModel.location // 위치 정보

    // 이미지 선택 런처 등록
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri.value = uri?.toString()
    }

    // UI 시작
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 제목 입력 필드
        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.title.value = it },
            label = { Text("제목") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 내용 입력 필드
        OutlinedTextField(
            value = content,
            onValueChange = { viewModel.content.value = it },
            label = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 이미지 선택 버튼
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("이미지 선택")
        }

        // 선택된 이미지 미리보기
        imageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 위치 선택 버튼
        Button(
            onClick = { onSelectLocation(null) }, // 위치 선택 화면 이동
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (latLng != null) Color(0xFF81C784) else Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (latLng != null) "📍 위치 선택됨" else "📍 위치 선택")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 저장 / 취소 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 저장 버튼
            Button(
                onClick = {
                    val result = if (post == null) {
                        // 새 글 작성
                        dbHelper.addPost(
                            Post(
                                title = title,
                                content = content,
                                imageUri = imageUri?.toString(),
                                lat = latLng?.latitude,
                                lng = latLng?.longitude
                            )
                        )
                    } else {
                        // 기존 글 수정
                        dbHelper.updatePost(
                            post.copy(
                                title = title,
                                content = content,
                                imageUri = imageUri?.toString(),
                                lat = latLng?.latitude,
                                lng = latLng?.longitude
                            )
                        )
                    }
                    viewModel.loadPosts(dbHelper) // 게시글 목록 새로고침
                    onBack() // 뒤로 이동
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("저장")
            }

            // 취소 버튼
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("취소")
            }
        }
    }
}
