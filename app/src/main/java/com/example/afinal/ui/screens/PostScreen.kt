package com.example.afinal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.afinal.PostDBHelper
import com.example.afinal.PostViewModel
import androidx.navigation.NavController // 화면 이동을 위한 NavController

@Composable
fun PostScreen(
    dbHelper: PostDBHelper, // SQLite DB 도우미
    viewModel: PostViewModel, // 게시글 상태를 관리하는 ViewModel
    onAddPost: () -> Unit, // 새 글 추가 시 호출할 콜백
    navController: NavController // 화면 이동에 사용
) {
    val posts = viewModel.posts // ViewModel에서 게시글 목록 가져오기

    Column(modifier = Modifier
        .fillMaxSize() // 화면을 꽉 채움
        .padding(16.dp)) {

        // 새 게시글 추가 버튼
        Button(onClick = {
            viewModel.reset() // 작성 폼 초기화
            onAddPost() // 외부 콜백 호출 (예: postDetail 화면 이동)
        }) {
            Text("Add Post")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 게시글 목록 반복 렌더링
        posts.forEach { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp), // 그림자 효과
                shape = RoundedCornerShape(16.dp) // 카드 모서리 둥글게
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 게시글 제목 (클릭 시 상세 뷰로 이동)
                    Text(
                        post.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("postView/${post.id}") // postViewScreen으로 이동
                            }
                    )

                    // 게시글 내용, 작성 시간
                    Text(post.content, style = MaterialTheme.typography.bodyMedium)
                    Text(post.timestamp.toString(), style = MaterialTheme.typography.labelSmall)

                    // 삭제 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End // 오른쪽 정렬
                    ) {
                        TextButton(onClick = {
                            dbHelper.deletePost(post.id) // DB에서 삭제
                            viewModel.loadPosts(dbHelper) // 목록 새로고침
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    // 최초 진입 시 게시글 불러오기
    LaunchedEffect(Unit) {
        viewModel.loadPosts(dbHelper)
    }
}
