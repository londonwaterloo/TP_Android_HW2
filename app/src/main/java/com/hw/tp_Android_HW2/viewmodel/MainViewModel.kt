package com.hw.tp_Android_HW2.viewmodel

import android.media.Image
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hw.tp_Android_HW2.model.GifData
import com.hw.tp_Android_HW2.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _gifList = mutableStateListOf<GifData>()
    val gifList: List<GifData> = _gifList

    private var _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private var _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private var _currentImage = mutableStateOf<String>("")
    val currentImage: State<String> = _currentImage

    private var offset = 0
    private val limit = 20

    fun toggleFullScreen(url:String) {
        Log.i("qwerty", url)
        _currentImage.value = url
    }

    init {
        fetchGifs()
    }

    fun fetchGifs() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitInstance.api.getTrendingGifs(
                    apiKey = "UYPipg5KmrkIyt1CrLCPCFHaplCRG9lW",
                    limit = limit,
                    offset = offset
                )

                if (response.isSuccessful && response.body() != null) {
                    _gifList.addAll(response.body()!!.data)
                    offset += limit
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Ошибка сервера: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

}
