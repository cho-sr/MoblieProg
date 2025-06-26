# LoveMap - 위치 기반 Todo & 게시글 안드로이드 앱

**LoveMap**은 Firebase 로그인과 SQLite 기반 로컬 저장을 활용해 Todo 관리, 지도 기반 위치 등록, 게시글 작성 및 이미지 첨부 기능을 제공하는 Android 앱입니다.  
Jetpack Compose로 UI를 구성하고, Google Maps와 연동하여 직관적인 위치 기반 기능을 제공합니다.

---

## ✅ 주요 기능

- Firebase 이메일 로그인 / 회원가입
- 할 일(Todo) 목록 관리 (추가/삭제/완료 체크)
- 각 할 일에 지도 위치 등록 및 확인
- 이미지, 위치, 타임스탬프가 포함된 게시글 작성 및 수정/삭제
- 게시글 지도 마커 및 상세 보기
- 프로필 이미지 및 닉네임 설정
- Jetpack Navigation 기반 화면 전환

---

## 기술 스택

| 항목 | 사용 기술 |
|------|-----------|
| UI | Jetpack Compose |
| 지도 | Google Maps API, Compose MapView |
| 인증 | Firebase Auth |
| DB | SQLite (직접 구현: `PostDBHelper`, `TodoDBHelper`, `ProfileDBHelper`) |
| 상태 관리 | Jetpack ViewModel + Compose 상태 (`mutableStateOf`) |
| 이미지 처리 | Coil (`rememberAsyncImagePainter`) |
| 플랫폼 | Android Studio (Kotlin)

---

## 주요 화면 구성

| 화면 | 파일 | 설명 |
|------|------|------|
| 로그인 화면 | `LoginScreen.kt` | Firebase 인증 기반 로그인/회원가입 |
| 할 일 목록 | `TodoList.kt` | 완료 여부 체크, 지도 이동, 삭제 기능 |
| 지도 화면 | `MapScreen.kt` | Todo 또는 게시글 위치 선택 및 확인 |
| 게시글 목록 | `PostScreen.kt` | 전체 게시글 목록 렌더링 및 삭제 |
| 게시글 상세 | `PostDetailViewScreen.kt` | 이미지/위치 포함 게시글 보기 |
| 게시글 작성/수정 | `PostDetailScreen.kt` | 이미지, 위치 선택 포함 작성/수정 |
| 프로필 화면 | `ProfileScreen.kt` | 이미지 및 닉네임 등록 |
| 설정 화면 | `SettingsScreen.kt` | 로그아웃 및 프로필 이동 |

---

---

## DB 설명

| DB 이름 | 설명 |
|---------|------|
| `todo.db` | 할 일 목록 (제목, 완료 여부, 위치) |
| `post.db` | 게시글 (제목, 내용, 이미지 URI, 위도/경도, 생성시간) |
| `profile.db` | 사용자 프로필 (닉네임, 이미지 URI) |

---



