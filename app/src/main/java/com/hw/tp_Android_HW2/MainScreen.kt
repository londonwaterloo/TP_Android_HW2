package com.hw.tp_Android_HW2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hw.tp_Android_HW2.viewmodel.MainViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val gifList = viewModel.gifList
    val loading by viewModel.loading
    val errorMessage by viewModel.errorMessage
    val currentMessage by viewModel.currentImage

    if (currentMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        viewModel.toggleFullScreen("")
                    }){
                     GifImage(currentMessage, viewModel)
                }
    } else if (loading && gifList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null && gifList.isEmpty()) {
        ErrorPlaceholder(message = errorMessage, onRetry = { viewModel.fetchGifs() })
    } else {
        LazyColumn {
            itemsIndexed(gifList) { index, gif ->
                GifImage(url = gif.images.original.url, viewModel)
                if (index == gifList.lastIndex && !loading && errorMessage == null) {
                    viewModel.fetchGifs()
                }
            }
            item {
                when {
                    loading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                    errorMessage != null -> {
                        RetryLoadMoreItem(message = errorMessage, onRetry = { viewModel.fetchGifs() })
                    }
                }
            }
        }
    }
}


@Composable
fun GifImage(url: String, viewModel: MainViewModel) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable{viewModel.toggleFullScreen(url)}


    )
}

@Composable
fun ErrorPlaceholder(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message ?: stringResource(id = R.string.error_message_network),
            color = colorResource(id = R.color.error_message_color) // Черный цвет для текста
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.button_background_color),
                contentColor = colorResource(id = R.color.my_button_color)
            )
        ) {
            Text(stringResource(id = R.string.button_retry))
        }
    }
}



@Composable
fun RetryLoadMoreItem(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(message ?: stringResource(id = R.string.error_message_generic))
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.button_background_color) // Цвет фона
            )
        ) {
            Text(stringResource(id = R.string.button_retry))
        }
    }
}