package com.example.afinal

// 게시글(Post) 정보를 담는 데이터 클래스
data class Post(
    val id: Int = 0, // 게시글 고유 ID (SQLite에서 자동 증가)
    val title: String, // 게시글 제목
    val content: String, // 게시글 내용
    val imageUri: String? = null, // 선택한 이미지의 URI (nullable)
    val lat: Double? = null, // 위치 정보 - 위도 (nullable)
    val lng: Double? = null, // 위치 정보 - 경도 (nullable)
    val timestamp: Long = System.currentTimeMillis() // 게시글 생성 시각 (기본값은 현재 시간)
)
