package com.example.afinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 사용자 프로필 정보를 저장하는 SQLite DB 헬퍼 클래스
// nickname과 imageUri를 저장하며, 테이블은 단일 행을 사용
class ProfileDBHelper(context: Context) :
    SQLiteOpenHelper(context, "profile.db", null, 2) { // DB 이름: profile.db, 버전: 2

    // 테이블이 처음 생성될 때 호출됨
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE profile (
                id INTEGER PRIMARY KEY,     -- 고정된 단일 행을 위한 ID (예: 1)
                nickname TEXT,              -- 사용자 닉네임
                image_uri TEXT              -- 프로필 이미지 URI
            )
            """.trimIndent()
        )
        val values = ContentValues().apply {
            put("id", 1)
            put("nickname", "")
            put("image_uri", "")
        }
        db.insert("profile", null, values)
    }

    // DB 버전이 증가했을 때 호출됨
    // 단순하게 테이블을 삭제하고 다시 생성하는 방식으로 구현되어 있음
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS profile")
        onCreate(db)
    }

    // 닉네임 저장 함수: 기존 행을 삭제하고 새로운 행을 삽입
    // 단일 사용자 기준으로 항상 하나의 프로필 정보만 유지
    fun saveNickname(nickname: String) {
        val db = writableDatabase
        db.delete("profile", null, null) // 테이블 내 기존 데이터 전부 삭제
        val values = ContentValues().apply {
            put("nickname", nickname)
        }
        db.insert("profile", null, values) // 새로운 닉네임 삽입
    }

    // 저장된 닉네임을 읽어오는 함수
    fun getNickname(): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nickname FROM profile LIMIT 1", null)
        var nickname: String? = null
        if (cursor.moveToFirst()) {
            nickname = cursor.getString(0)
        }
        cursor.close()
        return nickname
    }

    // 이미지 URI를 가져오는 함수
    // id가 1인 행이 있다고 가정하고 조회
    fun getImageUri(): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT image_uri FROM profile WHERE id = 1", null)
        return if (cursor.moveToFirst()) cursor.getString(0) else null
    }

    // 이미지 URI를 저장(갱신)하는 함수
    // 항상 id가 1인 행에만 저장한다는 전제 하에 실행
    fun saveImageUri(uri: String) {
        val db = writableDatabase
        db.execSQL("UPDATE profile SET image_uri = ? WHERE id = 1", arrayOf(uri))
    }

    // 이메일을 기준으로 프로필 정보를 가져오는 함수
    // 현재 구조상 profile 테이블에 email 컬럼은 존재하지 않기 때문에 주의 필요
    fun getProfile(email: String): Profile? {
        val db = readableDatabase
        val cursor = db.query(
            "profile",
            arrayOf("email", "imageUri"),  // 주의: email 컬럼이 실제 존재하지 않음
            "email = ?",
            arrayOf(email),
            null, null, null
        )

        // 커서에서 데이터가 있으면 Profile 객체로 반환
        return if (cursor.moveToFirst()) {
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
            Profile(email, imageUri)
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }
}
