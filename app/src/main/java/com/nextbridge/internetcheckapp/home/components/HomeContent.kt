package com.nextbridge.internetcheckapp.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nextbridge.internetcheckapp.ui.theme.InternetCheckAppTheme

@Composable
fun HomeContent(
    isConnected: Boolean
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Home",
            modifier = Modifier.align(Alignment.Center)
        )
    }

    if (!isConnected) {
        NoInternetComponent()
    }

}

@Composable
private fun NoInternetComponent() {
    Surface(color = Color.Red) {
        Text(
            "No Internet Connection",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    InternetCheckAppTheme {
        Surface {
            HomeContent(
                isConnected = false
            )
        }
    }
}