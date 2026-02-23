package com.nextbridge.internetcheckapp.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nextbridge.internetcheckapp.home.components.HomeContent

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()

    HomeContent(
        isConnected = isConnected
    )
}