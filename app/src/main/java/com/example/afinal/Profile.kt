package com.example.afinal

// 사용자 프로필 정보를 저장하는 데이터 클래스
// SQLite 또는 앱 전역에서 사용자 정보를 표현할 때 사용됨
data class Profile(
    val email: String,       // 사용자의 이메일 주소 (Firebase 로그인 정보와 일치)
    val imageUri: String     // 사용자가 선택한 프로필 이미지의 URI 문자열
)
