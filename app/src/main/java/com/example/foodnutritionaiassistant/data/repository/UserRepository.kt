package com.example.foodnutritionaiassistant.data.repository

import com.example.foodnutritionaiassistant.data.network.LoginRequest
import com.example.foodnutritionaiassistant.data.network.RetrofitClient
import com.example.foodnutritionaiassistant.ui.viewmodel.LoginType
import com.example.foodnutritionaiassistant.ui.viewmodel.UserProfile

class UserRepository {

    private val api = RetrofitClient.apiService

    suspend fun checkUserExists(loginType: LoginType, identifier: String, passwordOrCode: String? = null): Boolean {
        return try {
            val request = LoginRequest(
                phoneNumber = if (loginType == LoginType.PHONE) identifier else null,
                wechatOpenId = if (loginType == LoginType.WECHAT) identifier else null,
                loginType = loginType.name,
                password = passwordOrCode
            )
            val response = api.login(request)
            response.isSuccessful && response.body() != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUser(loginType: LoginType, identifier: String, passwordOrCode: String? = null): UserProfile? {
        return try {
            val request = LoginRequest(
                phoneNumber = if (loginType == LoginType.PHONE) identifier else null,
                wechatOpenId = if (loginType == LoginType.WECHAT) identifier else null,
                loginType = loginType.name,
                password = passwordOrCode
            )
            val response = api.login(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun registerUser(userProfile: UserProfile): Boolean {
        return try {
            val response = api.register(userProfile)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateUserGroup(userProfile: UserProfile): Boolean {
        return try {
            // Assuming we have an update endpoint or use register (save) for update if ID exists
            // Since backend register does save, it acts as upsert if ID is present.
            // But UserProfile might not have ID yet if created locally?
            // Actually, after login/register, we should have ID.
            // For now, I'll assume we use register (save) or a specific update endpoint.
            // My backend has PUT /api/users/{id}
            if (userProfile.id != null) {
                val response = api.updateUser(userProfile.id!!, userProfile)
                response.isSuccessful
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
