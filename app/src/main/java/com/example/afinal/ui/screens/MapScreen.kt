package com.example.afinal.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView // 기존 Android View를 Compose에서 사용하기 위한 컴포넌트
import androidx.lifecycle.DefaultLifecycleObserver // 생명주기 관리를 위한 옵저버
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalContext // 현재 Context를 가져오는 Compose 함수
import androidx.compose.ui.platform.LocalLifecycleOwner // 현재 생명주기 소유자(LifecycleOwner)를 가져오는 함수
import com.google.android.gms.maps.CameraUpdateFactory // 카메라 이동을 위한 팩토리
import com.google.android.gms.maps.MapView // Android의 Google Map View
import com.google.android.gms.maps.model.LatLng // 위도/경도 위치 표현 클래스
import com.google.android.gms.maps.model.MarkerOptions // 지도에 마커 추가 옵션

@SuppressLint("MissingPermission") // 위치 권한 관련 경고 무시
@Composable
fun MapScreen(
    location: LatLng, // 초기 위치
    onLocationSelected: (LatLng) -> Unit // 위치 선택 시 콜백 함수
) {
    val mapView = rememberMapViewWithLifecycle() // 생명주기를 기억하는 MapView 생성

    AndroidView(factory = { mapView }) { view -> // Compose에서 MapView 사용
        mapView.getMapAsync { googleMap -> // 지도 준비 완료 시 실행
            // 초기 위치로 카메라 이동 및 마커 표시
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            googleMap.addMarker(MarkerOptions().position(location).title("기존 위치"))

            googleMap.uiSettings.isZoomControlsEnabled = true // 줌 컨트롤 UI 활성화

            // 지도 클릭 시
            googleMap.setOnMapClickListener { latLng ->
                googleMap.clear() // 기존 마커 제거
                googleMap.addMarker(MarkerOptions().position(latLng).title("선택된 위치")) // 새 마커 추가
                onLocationSelected(latLng) // 위치 선택 콜백 전달
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) } // MapView 인스턴스를 remember로 유지

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // 생명주기 상태에 따라 MapView의 동작을 연결
    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) = mapView.onCreate(Bundle())
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }

        lifecycle.addObserver(observer) // 생명주기 옵저버 등록
        onDispose { lifecycle.removeObserver(observer) } // 해제 시 옵저버 제거
    }

    return mapView // MapView 반환
}
