package com.example.afinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.afinal.ui.screens.TodoItem
import com.google.android.gms.maps.model.LatLng

// 할 일(Todo) 목록을 저장하고 불러오기 위한 SQLite 데이터베이스 헬퍼 클래스
class TodoDBHelper(context: Context) :
    SQLiteOpenHelper(context, "todo.db", null, 1) { // DB 이름: todo.db, 버전: 1

    // 앱에서 처음 이 DB를 사용할 때 호출됨
    // 할 일 항목을 저장할 테이블을 생성함
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE TODO_TB (
                id TEXT PRIMARY KEY,         -- 고유한 ID (String)
                title TEXT NOT NULL,         -- 할 일 제목
                isDone INTEGER NOT NULL,     -- 완료 여부 (0: 미완료, 1: 완료)
                latitude REAL,               -- 선택된 위치의 위도 (nullable)
                longitude REAL               -- 선택된 위치의 경도 (nullable)
            )
            """.trimIndent()
        )
    }

    // DB 버전이 변경될 때 호출됨
    // 현재 버전은 1이므로 이 로직은 실행되지 않음
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS TODO_TB") // 기존 테이블 제거
        onCreate(db!!) // 테이블 재생성
    }

    // Todo 항목을 DB에 추가하거나, 동일한 ID가 존재하면 해당 항목을 갱신
    fun insertOrUpdate(todo: TodoItem) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", todo.id) // 고유 ID (String)
            put("title", todo.title) // 제목
            put("isDone", if (todo.isDone) 1 else 0) // 완료 여부를 정수로 저장
            put("latitude", todo.location?.latitude) // 위치 정보 (nullable)
            put("longitude", todo.location?.longitude)
        }

        // insertWithOnConflict: 충돌 발생 시 덮어씀 (같은 ID가 있으면 UPDATE처럼 동작)
        db.insertWithOnConflict("TODO_TB", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // 특정 ID의 Todo 항목 삭제
    fun delete(id: String) {
        val db = writableDatabase
        db.delete("TODO_TB", "id=?", arrayOf(id))
    }

    // DB에 저장된 모든 Todo 항목을 불러와 리스트로 반환
    fun getAll(): List<TodoItem> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM TODO_TB", null)
        val todos = mutableListOf<TodoItem>()

        // 한 행씩 읽어서 TodoItem 객체로 변환
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone")) == 1 // 1이면 true

            // 위도/경도 값이 기본값(0.0)이 아닌 경우에만 LatLng 생성
            val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))
            val lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
            val location = if (lat != 0.0 || lng != 0.0) LatLng(lat, lng) else null

            todos.add(TodoItem(id, title, isDone, location))
        }

        cursor.close()
        return todos
    }
}
