package com.xm137.amapsdktest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val isAMapPrivacyAgree: StateFlow<IsAMapPrivacyUiState> =
        userPreferencesRepository.isAMapPrivacyAgree.map { isAMapPrivacyAgree ->
            IsAMapPrivacyUiState(isAMapPrivacyAgree)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking {
                IsAMapPrivacyUiState(
                    isAMapPrivacy = userPreferencesRepository.isAMapPrivacyAgree.first()
                )
            }
        )

    fun selectAMapPrivacy(aMapPrivacyAgree: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveIsAMapPrivacy(aMapPrivacyAgree)
        }
    }

    //  dataStore
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TestSDKApplication)

                TestViewModel(
                    userPreferencesRepository = application.userPreferencesRepository
                )
            }
        }
    }
}

// [DataStore] 数值初始值
data class IsAMapPrivacyUiState(
    val isAMapPrivacy: Boolean = false
)

