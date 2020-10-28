package com.florian.numberslight.viewModel

import androidx.lifecycle.*
import com.florian.numberslight.BuildConfig
import com.florian.numberslight.enums.NetworkError
import com.florian.numberslight.model.NumberSummary
import com.florian.numberslight.repository.NumberRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.IOError
import java.io.IOException

class NumberListViewModel(private val repository: NumberRepository) : ViewModel() {
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            modelClass.getConstructor(NumberRepository::class.java).newInstance(
                NumberRepository(logEnabled = BuildConfig.LOG_NETWORK)
            )
    }

    private val _networkError = MutableLiveData<NetworkError?>()
    val networkError: LiveData<NetworkError?>
        get() = _networkError

    private val _list = MutableLiveData<List<NumberSummary>>()
    val list: LiveData<List<NumberSummary>>
        get() = _list

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when(throwable) {
            is IOException -> _networkError.value = NetworkError.GENERAL_ERROR
        }
    }
    fun refreshList() {
            viewModelScope.launch(coroutineExceptionHandler) {
                with(repository.getList()) {
                    if (this != null) {
                        _list.value = this
                        _networkError.value = null
                    } else {
                        _networkError.value = NetworkError.NO_DATA
                    }
                }

            }

    }
}