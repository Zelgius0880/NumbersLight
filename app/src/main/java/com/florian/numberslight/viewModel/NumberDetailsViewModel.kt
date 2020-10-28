package com.florian.numberslight.viewModel

import androidx.lifecycle.*
import com.florian.numberslight.BuildConfig
import com.florian.numberslight.enums.NetworkError
import com.florian.numberslight.model.INumber
import com.florian.numberslight.model.Number
import com.florian.numberslight.repository.NumberRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class NumberDetailsViewModel(private val repository: NumberRepository) : ViewModel() {
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            modelClass.getConstructor(NumberRepository::class.java).newInstance(
                NumberRepository(logEnabled = BuildConfig.LOG_NETWORK)
            )
    }

    private val _networkError = MutableLiveData<NetworkError?>()
    val networkError: LiveData<NetworkError?>
        get() = _networkError

    private val _number = MutableLiveData<Number>()
    val number: LiveData<Number>
        get() = _number

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private var currentItem: INumber? = null

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _loading.value = false

        when (throwable) {
            is IOException -> _networkError.value = NetworkError.GENERAL_ERROR
        }
    }

    fun loadNumber(number: INumber) {
        currentItem = number
        viewModelScope.launch(coroutineExceptionHandler) {
            _loading.value = true
            delay(2000)
            with(repository.getNumber(number)) {
                if (this != null) {
                    _number.value = this
                    _networkError.value = null
                } else {
                    _networkError.value = NetworkError.NO_DATA
                }
            }

            _loading.value = false
        }

    }

    fun refresh() {
        viewModelScope.launch {
            currentItem?.let {
                loadNumber(it)
            }
        }

    }
}