package com.ssafy.ganhoho.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ssafy.ganhoho.data.model.response.auth.SearchResultItem
import com.ssafy.ganhoho.viewmodel.HospitalSearchViewModel

@Composable
fun SearchHospital(navController: NavController) {
    val context = LocalContext.current
    val hospitalSearchViewModel: HospitalSearchViewModel = viewModel()
    var searchQuery by remember { mutableStateOf("") }
    val hospitals by hospitalSearchViewModel.searchResults.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // 현재 위치 가져오기
    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }
    val currentLocation = remember { mutableStateOf<Pair<Double, Double>?>(null) }

    SideEffect {
//        requestLocationPermission(context) // 🔥 권한 요청 추가
        getCurrentLocation(context, fusedLocationProviderClient) { location ->
            currentLocation.value = location
        }
    }
    Log.d("map", currentLocation.value.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔍 검색창
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp)),
            placeholder = { Text(text = "병원 이름을 입력해주세요.", color = Color.LightGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    currentLocation.value?.let { (lat, lng) ->
                        hospitalSearchViewModel.searchHospitals(searchQuery, lng, lat)
                    }
                    keyboardController?.hide()
                }
            ),
            trailingIcon = {
                IconButton(onClick = {
                    currentLocation.value?.let { (lat, lng) ->
                        hospitalSearchViewModel.searchHospitals(searchQuery, lng, lat)
                    }
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        // 📌 검색 결과 리스트
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(12.dp))
        ) {
            items(hospitals ?: emptyList()) { hospital ->
                HospitalItem(hospital, navController)
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFetched: (Pair<Double, Double>?) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationFetched(Pair(location.latitude, location.longitude))
            } else {
                // 🔥 위치가 없을 경우 강제로 업데이트 요청
                val locationRequest =
                    com.google.android.gms.location.LocationRequest.create().apply {
                        priority =
                            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                        interval = 1000
                        fastestInterval = 500
                    }
                val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        locationResult.lastLocation?.let {
                            onLocationFetched(Pair(it.latitude, it.longitude))
                        }
                    }
                }
                val locationProvider = LocationServices.getFusedLocationProviderClient(context)
                locationProvider.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }
    } else {
        onLocationFetched(null)
    }
}


@Composable
fun HospitalItem(hospital: SearchResultItem, navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable {
                // ✅ 병원을 선택하면 이전 화면(JoinScreen)으로 돌아가면서 데이터 전달
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "selectedHospital",
                    hospital
                )
                navController.popBackStack()
            }
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(text = "🏥 ${hospital.name}", fontSize = 18.sp)
    }
}
