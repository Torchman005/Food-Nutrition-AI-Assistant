package com.example.foodnutritionaiassistant.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodnutritionaiassistant.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

enum class LoginType {
    WECHAT, PHONE
}

data class UserProfile(
    var nickname: String = "",
    var gender: Int = 0, // 0: Unknown, 1: Male, 2: Female
    var birthDate: LocalDate = LocalDate.of(2000, 1, 1),
    var height: Float = 170f,
    var weight: Float = 60f,
    var loginType: LoginType = LoginType.PHONE,
    var phoneNumber: String = "",
    var wechatOpenId: String = ""
)

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()

    var userProfile by mutableStateOf(UserProfile())
        private set

    // Phone Login State
    var phoneNumber by mutableStateOf("")
    var verificationCode by mutableStateOf("")
    var isCodeSent by mutableStateOf(false)
    var countdown by mutableStateOf(60)

    // Registration Flow State
    var isLoggedIn by mutableStateOf(false)
    var isFirstLogin by mutableStateOf(false) // If true, go through profile setup

    fun sendVerificationCode() {
        if (phoneNumber.isBlank()) return
        
        // Mock sending code (In real app, call API)
        viewModelScope.launch {
            // Simulate receiving the code instantly for demo purposes
            // In a real app, this would be handled by the user receiving an SMS
            verificationCode = "123456" 
            
            isCodeSent = true
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            isCodeSent = false
        }
    }

    fun loginWithPhone(onSuccess: (Boolean) -> Unit) {
        if (verificationCode == "123456") { 
             viewModelScope.launch(Dispatchers.IO) {
                val exists = userRepository.checkUserExists(LoginType.PHONE, phoneNumber)
                
                withContext(Dispatchers.Main) {
                    if (exists) {
                        // User exists, fetch data
                        viewModelScope.launch(Dispatchers.IO) {
                            val user = userRepository.getUser(LoginType.PHONE, phoneNumber)
                            withContext(Dispatchers.Main) {
                                if (user != null) {
                                    userProfile = user
                                    isLoggedIn = true
                                    isFirstLogin = false
                                    onSuccess(false)
                                }
                            }
                        }
                    } else {
                        // New User
                        userProfile = userProfile.copy(
                            loginType = LoginType.PHONE,
                            phoneNumber = phoneNumber
                        )
                        isLoggedIn = true
                        isFirstLogin = true
                        onSuccess(true)
                    }
                }
            }
        }
    }

    fun loginWithWeChat(onSuccess: (Boolean) -> Unit) {
        val mockOpenId = "wx_123456" // Mock OpenID
        
        viewModelScope.launch(Dispatchers.IO) {
            val exists = userRepository.checkUserExists(LoginType.WECHAT, mockOpenId)
            
            withContext(Dispatchers.Main) {
                 if (exists) {
                     // User exists, fetch data
                     viewModelScope.launch(Dispatchers.IO) {
                        val user = userRepository.getUser(LoginType.WECHAT, mockOpenId)
                        withContext(Dispatchers.Main) {
                            if (user != null) {
                                userProfile = user
                                isLoggedIn = true
                                isFirstLogin = false
                                onSuccess(false)
                            }
                        }
                    }
                 } else {
                     // New User
                     userProfile = userProfile.copy(
                        loginType = LoginType.WECHAT,
                        wechatOpenId = mockOpenId
                    )
                    isLoggedIn = true
                    isFirstLogin = true
                    onSuccess(true)
                 }
            }
        }
    }

    fun updateNickname(name: String) {
        userProfile = userProfile.copy(nickname = name)
    }

    fun updateGender(gender: Int) {
        userProfile = userProfile.copy(gender = gender)
    }

    fun updateBirthDate(date: LocalDate) {
        userProfile = userProfile.copy(birthDate = date)
    }

    fun updateHeight(height: Float) {
        userProfile = userProfile.copy(height = height)
    }

    fun updateWeight(weight: Float) {
        userProfile = userProfile.copy(weight = weight)
    }

    fun submitProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val success = userRepository.registerUser(userProfile)
            if (success) {
                println("User registered successfully to DB")
            } else {
                println("Failed to register user to DB")
            }
            withContext(Dispatchers.Main) {
                isFirstLogin = false
            }
        }
    }
}
