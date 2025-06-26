package com.example.afinal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.afinal.Post

// Cursor 확장 함수: null-safe하게 String 가져오기
fun Cursor.getStringOrNull(columnIndex: Int): String? =
    if (isNull(columnIndex)) null else getString(columnIndex)

// Cursor 확장 함수: null-safe하게 Double 가져오기
fun Cursor.getDoubleOrNull(columnIndex: Int): Double? =
    if (isNull(columnIndex)) null else getDouble(columnIndex)

// 게시글(Post) 데이터를 저장/조회하는 SQLite DB 헬퍼 클래스
class PostDBHelper(context: Context) :
    SQLiteOpenHelper(context, "post.db", null, 2) { // DB 이름: post.db, 버전 2

    // 테이블 생성
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS posts (
                id INTEGER PRIMARY KEY AUTOINCREMENT, -- 게시글 고유 ID
                title TEXT NOT NULL,                 -- 제목
                content TEXT NOT NULL,               -- 내용
                timestamp LONG,                      -- 작성 시각
                imageUri TEXT,                       -- 이미지 URI (nullable)
                lat REAL,                            -- 위도 (nullable)
                lng REAL                             -- 경도 (nullable)
            )""".trimIndent()
        )
    }

    // DB 업그레이드: 버전 2부터 imageUri, lat, lng 컬럼 추가
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE posts ADD COLUMN imageUri TEXT")
            db.execSQL("ALTER TABLE posts ADD COLUMN lat REAL")
            db.execSQL("ALTER TABLE posts ADD COLUMN lng REAL")
        }
    }

    // 게시글 추가
    fun addPost(post: Post): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", post.title)
            put("content", post.content)
            put("timestamp", post.timestamp)
            put("imageUri", post.imageUri)
            put("lat", post.lat)
            put("lng", post.lng)
        }
        return db.insert("posts", null, values)
    }

    // 게시글 수정
    fun updatePost(post: Post): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", post.title)
            put("content", post.content)
            put("timestamp", post.timestamp)
            put("imageUri", post.imageUri)
            put("lat", post.lat)
            put("lng", post.lng)
        }
        return db.update("posts", values, "id=?", arrayOf(post.id.toString()))
    }

    // 게시글 삭제
    fun deletePost(id: Int): Int {
        val db = writableDatabase
        return db.delete("posts", "id=?", arrayOf(id.toString()))
    }

    // 전체 게시글 불러오기 (최신순 정렬)
    fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM posts ORDER BY timestamp DESC", null)

        if (cursor.moveToFirst()) {
            do {
                posts.add(
                    Post(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        imageUri = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("imageUri")),
                        lat = cursor.getDoubleOrNull(cursor.getColumnIndexOrThrow("lat")),
                        lng = cursor.getDoubleOrNull(cursor.getColumnIndexOrThrow("lng"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return posts
    }

    // 특정 게시글 ID로 조회
    fun getPostById(id: Int): Post? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM posts WHERE id = ?", arrayOf(id.toString()))
        var post: Post? = null

        if (cursor.moveToFirst()) {
            post = Post(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                imageUri = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("imageUri")),
                lat = cursor.getDoubleOrNull(cursor.getColumnIndexOrThrow("lat")),
                lng = cursor.getDoubleOrNull(cursor.getColumnIndexOrThrow("lng"))
            )
        }

        cursor.close()
        return post
    }
}
