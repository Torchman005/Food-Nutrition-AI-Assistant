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
import org.bson.types.ObjectId

enum class LoginType {
    WECHAT, PHONE
}

enum class GroupCategory(val displayName: String) {
    HEALTH("养生"),
    FITNESS("健身"),
    TODDLER("幼儿");

    companion object {
        fun fromName(name: String): GroupCategory {
            return entries.find { it.name == name } ?: FITNESS
        }
    }
}

data class UserProfile(
    val id: String? = null, // MongoDB ObjectId as hex string
    var nickname: String = "",
    var gender: Int = 0, // 0: Unknown, 1: Male, 2: Female
    var birthDate: LocalDate = LocalDate.of(2000, 1, 1),
    var height: Float = 170f,
    var weight: Float = 60f,
    var loginType: LoginType = LoginType.PHONE,
    var phoneNumber: String = "",
    var wechatOpenId: String = "",
    var groupCategory: GroupCategory = GroupCategory.FITNESS
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
        viewModelScope.launch(Dispatchers.IO) {
            val exists = userRepository.checkUserExists(LoginType.PHONE, phoneNumber, verificationCode)
            
            withContext(Dispatchers.Main) {
                if (exists) {
                    // User exists, fetch data
                    viewModelScope.launch(Dispatchers.IO) {
                        val user = userRepository.getUser(LoginType.PHONE, phoneNumber, verificationCode)
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
                    // New User or Wrong Code (Assume New User for flow)
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

    fun updateGroupCategory(category: GroupCategory) {
        userProfile = userProfile.copy(groupCategory = category)
    }

    fun updateUserGroupInDb(category: GroupCategory) {
        userProfile = userProfile.copy(groupCategory = category)
        viewModelScope.launch(Dispatchers.IO) {
            val success = userRepository.updateUserGroup(userProfile)
            if (success) {
                println("User group updated successfully in DB")
            } else {
                println("Failed to update user group in DB")
            }
        }
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
