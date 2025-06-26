package com.example.afinal.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.afinal.ProfileDBHelper
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userEmail: String,
    dbHelper: ProfileDBHelper,
    onBack: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 권한 유지
    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Log.e("ProfileScreen", "권한 획득 실패: ${e.message}")
            }
        }
    }

    // 이미지 선택 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // 이미지 URI로부터 Bitmap safely 로딩
    val bitmap = remember(imageUri) {
        try {
            imageUri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "이미지 로딩 실패: ${e.message}")
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("프로필 설정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("이메일: $userEmail")
            Spacer(modifier = Modifier.height(16.dp))

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { imagePickerLauncher.launch("image/*") }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text("이미지 선택", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                dbHelper.saveNickname(nickname)
                imageUri?.let { dbHelper.saveImageUri(it.toString()) }
                Toast.makeText(context, "저장 완료!", Toast.LENGTH_SHORT).show()
            }) {
                Text("저장")
            }
        }
    }
}
