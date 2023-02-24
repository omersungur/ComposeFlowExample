package com.omersungur.composeflowexample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {

    val countDownTimerFlow = flow<Int> {
        val countDownFrom = 10
        var counter = countDownFrom
        emit(countDownFrom) // emit etmek collect ile yayılan değerleri toplamaktır.
        while(counter > 0) {
            delay(1000) // flowlar coroutine içinde çalışırlar o yüzden delay koyabiliriz.
            counter--
            emit(counter)
        }
    }

    init {
        collectInViewModel()
    }

    private fun collectInViewModel() {
        viewModelScope.launch {
            countDownTimerFlow // flow yapılarında filter ve map gibi fonskiyonları direkt içinde kullanabiliyoruz.
                .filter {
                    it %3 == 0
                }
                .map {
                    it + it
                }
                .collect {
                    println("counter is: ${it}") // verideki sayılardan, 3'e tam bölünenlerin 2 katını gösteriyoruz.
                }
            /*
                countDownTimerFlow.collectLatest { // bazı kontroller için kullanılabilir.
                    delay(2000) // 2 saniye bekletirsek her zaman yeni bir değer collect edileceğinden, değer sadece sabit kaldığında o değeri alabileceğiz yani 0'ı göreceğiz.
                    println("counter is: ${it}")
                }
             */

        }

        /*
        countDownTimerFlow.onEach { // Bu şekilde de kullanabiliriz.
            println(it)
        }.launchIn(viewModelScope)
         */
    }

    //LiveData comparison

    private val _liveData = MutableLiveData<String>("KotlinLiveData")
    val liveData : LiveData<String> = _liveData // bu mutable livedata'yı diğer classlardan değiştirilemez hale getirmek için live data yaptık.

    fun changeLiveDataValue() {
        _liveData.value = "Live Data"
    }

    private val _stateFlow = MutableStateFlow("KotlinStateFlow")
    val stateFlow = _stateFlow.asStateFlow()

    // SharedFlow, StateFlow'un daha özelleştirilebilir halidir. Custom durumlarında kullanabiliriz.

    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()


    fun changeStateFlowValue() {
        _stateFlow.value = "State Flow"
    }

    fun changeSharedFlowValue() {
        viewModelScope.launch {
            _sharedFlow.emit("Shared Flow")
        }
    }
}