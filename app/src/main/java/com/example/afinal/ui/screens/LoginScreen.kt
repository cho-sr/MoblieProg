package com.example.afinal.ui.screens

import android.widget.Toast // 토스트 메시지 사용을 위한 import
import androidx.compose.foundation.layout.* // 레이아웃 관련 컴포넌트 import
import androidx.compose.material3.* // Material3 UI 컴포넌트 import
import androidx.compose.runtime.* // 상태 관리 관련 함수 import
import androidx.compose.ui.Modifier // Modifier는 UI 구성 요소의 속성을 설정하는 데 사용
import androidx.compose.ui.platform.LocalContext // 현재 Context를 가져오기 위한 composable 함수
import androidx.compose.ui.unit.dp // dp 단위 설정을 위한 import
import com.google.firebase.auth.FirebaseAuth // Firebase 인증 기능 import

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // 이메일, 비밀번호 상태 변수 선언
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current // 현재 컴포지션의 Context
    val auth = FirebaseAuth.getInstance() // Firebase 인증 객체

    Column(modifier = Modifier
        .padding(24.dp) // 전체 Column 패딩
        .fillMaxWidth()) { // 가로 길이 최대

        Text("LoveMap 로그인", style = MaterialTheme.typography.titleLarge) // 제목 텍스트
        Spacer(modifier = Modifier.height(12.dp)) // 간격 설정

        // 이메일 입력 필드
        OutlinedTextField(
            value = email,
            onValueChange = { email = it }, // 값이 바뀔 때 상태 업데이트
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp)) // 간격 설정

        // 비밀번호 입력 필드
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp)) // 간격 설정

        // 로그인 버튼
        Button(onClick = {
            // Firebase 이메일/비밀번호 로그인
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onLoginSuccess() // 로그인 성공 시 콜백 호출
                    } else {
                        // 실패 시 Toast 메시지 출력
                        Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }) {
            Text("로그인") // 버튼 텍스트
        }

        // 회원가입 텍스트 버튼
        TextButton(onClick = {
            // Firebase 회원가입 처리
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }) {
            Text("회원가입") // 버튼 텍스트
        }
    }
}
