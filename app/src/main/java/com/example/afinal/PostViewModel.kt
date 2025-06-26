package com.example.afinal

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//  Jetpack ViewModel을 상속받아 Compose 화면에서 게시글 관련 상태를 관리하는 클래스
class PostViewModel : ViewModel() {

    //  게시글 입력 폼에 대한 상태 변수들
    // mutableStateOf는 Compose에서 UI 상태로 인식되며, 값이 바뀌면 자동으로 recomposition됨

    var title = mutableStateOf("") // 게시글 제목 상태
    var content = mutableStateOf("") // 게시글 내용 상태
    var imageUri = mutableStateOf<String?>(null) // 이미지 경로 (nullable, 선택 안 했을 수 있음)
    var location = mutableStateOf<LatLng?>(null) // 지도에서 선택한 위치 (nullable)

    //  게시글 전체 목록을 메모리에 유지하기 위한 상태 리스트
    // mutableStateListOf는 리스트가 바뀌면 UI가 자동으로 갱신됨
    var posts = mutableStateListOf<Post>()
        private set // 외부에서 직접 수정하지 못하도록 setter를 제한

    //  SQLite DB에서 게시글 목록을 불러와 상태에 저장하는 함수
    // 화면에 게시글 목록을 표시할 때 호출됨
    fun loadPosts(dbHelper: PostDBHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = dbHelper.getAllPosts()
            withContext(Dispatchers.Main) {
                this@PostViewModel.posts.clear()
                this@PostViewModel.posts.addAll(posts)
            }
        }
    }


    //  기존 게시글을 수정하려 할 때, 해당 게시글의 데이터를 상태 변수에 반영
    fun setPost(post: Post) {
        title.value = post.title
        content.value = post.content
        imageUri.value = post.imageUri

        // 위치 정보가 존재하면 LatLng 객체로 변환
        location.value = if (post.lat != null && post.lng != null) {
            LatLng(post.lat, post.lng)
        } else {
            null // 위치 정보가 없을 경우 null
        }
    }

    //  새 게시글 작성 시, 입력폼 초기화에 사용
    fun reset() {
        title.value = ""
        content.value = ""
        imageUri.value = null
        location.value = null
    }

    // 지도에서 위치를 선택했을 때 해당 위치를 상태에 저장
    fun setLocation(latLng: LatLng?) {
        location.value = null
        location.value = latLng
    }
}
