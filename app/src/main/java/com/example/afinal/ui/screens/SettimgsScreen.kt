package com.example.afinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,       // 로그아웃 버튼 클릭 시 실행할 콜백 함수
    onEditProfile: () -> Unit   // 프로필 편집 버튼 클릭 시 실행할 콜백 함수
) {
    Column(
        modifier = Modifier
            .fillMaxSize()     // 화면 전체 채우기
            .padding(16.dp)    // 바깥 여백 설정
    ) {
        // 프로필 편집 버튼
        Button(
            onClick = onEditProfile,               // 콜백 호출
            modifier = Modifier.fillMaxWidth()     // 버튼 너비를 가득 채움
        ) {
            Text("프로필 편집")
        }

        Spacer(modifier = Modifier.height(12.dp))  // 버튼 간 간격

        // 로그아웃 버튼
        Button(
            onClick = onLogout,                    // 콜백 호출
            modifier = Modifier.fillMaxWidth()     // 버튼 너비를 가득 채움
        ) {
            Text("로그아웃")
        }
    }
}
