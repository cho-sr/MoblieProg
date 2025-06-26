package com.example.afinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng

@Composable
fun TodoList(
    todos: List<TodoItem>, // 할 일 목록
    onMapClick: (String, LatLng) -> Unit, // 지도 버튼 클릭 시 콜백 (id와 위치 전달)
    onCheckToggle: (String, Boolean) -> Unit, // 체크박스 토글 시 콜백
    onDelete: (String) -> Unit, // 삭제 버튼 클릭 시 콜백
    onAdd: (String) -> Unit, // 할 일 추가 시 콜백
) {
    var newTodo by remember { mutableStateOf(TextFieldValue("")) } // 새 할 일 입력 상태

    Column(modifier = Modifier.padding(16.dp)) {
        // 입력창과 추가 버튼
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newTodo,
                onValueChange = { newTodo = it },
                label = { Text("할 일 추가") },
                modifier = Modifier.weight(1f) // TextField는 너비 최대
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newTodo.text.isNotBlank()) {
                    onAdd(newTodo.text) // 외부 콜백으로 추가 요청
                    newTodo = TextFieldValue("") // 입력창 초기화
                }
            }) {
                Text("추가")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 할 일 목록 출력
        todos.forEach { todo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), // 각 카드 간격
                elevation = CardDefaults.cardElevation() // 그림자 효과
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 체크박스 (완료 여부 토글)
                    Checkbox(
                        checked = todo.isDone,
                        onCheckedChange = {
                            onCheckToggle(todo.id, it) // 외부에서 copy로 처리
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 할 일 텍스트
                    Text(text = todo.title, modifier = Modifier.weight(1f))

                    // 지도 버튼 (위치가 있는 경우에만 클릭 가능)
                    IconButton(onClick = {
                        todo.location?.let { loc -> onMapClick(todo.id, loc) }
                    }) {
                        Icon(Icons.Default.Map, contentDescription = "지도")
                    }

                    // 삭제 버튼
                    IconButton(onClick = { onDelete(todo.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제")
                    }
                }
            }
        }
    }
}

// id: 고유 식별자, title: 할 일 내용, isDone: 완료 여부, location: 지도 위치
data class TodoItem(
    val id: String,
    val title: String,
    var isDone: Boolean,
    val location: LatLng? = null
)
