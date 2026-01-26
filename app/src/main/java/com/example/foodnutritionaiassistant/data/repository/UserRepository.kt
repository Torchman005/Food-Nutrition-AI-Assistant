package com.example.foodnutritionaiassistant.data.repository

import com.example.foodnutritionaiassistant.data.db.DatabaseHelper
import com.example.foodnutritionaiassistant.ui.viewmodel.GroupCategory
import com.example.foodnutritionaiassistant.ui.viewmodel.LoginType
import com.example.foodnutritionaiassistant.ui.viewmodel.UserProfile
import java.sql.Date
import java.time.LocalDate
import java.util.UUID

class UserRepository {

    fun checkUserExists(loginType: LoginType, identifier: String): Boolean {
        var exists = false
        val query = if (loginType == LoginType.PHONE) {
            "SELECT id FROM app_user WHERE phone_number = ? AND login_type = 2"
        } else {
            "SELECT id FROM app_user WHERE wechat_open_id = ? AND login_type = 1"
        }

        DatabaseHelper.getConnection()?.use { conn ->
            conn.prepareStatement(query).use { stmt ->
                stmt.setString(1, identifier)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    exists = true
                }
            }
        }
        return exists
    }

    fun getUser(loginType: LoginType, identifier: String): UserProfile? {
        var userProfile: UserProfile? = null
        val query = if (loginType == LoginType.PHONE) {
            "SELECT * FROM app_user WHERE phone_number = ? AND login_type = 2"
        } else {
            "SELECT * FROM app_user WHERE wechat_open_id = ? AND login_type = 1"
        }

        try {
            DatabaseHelper.getConnection()?.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, identifier)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        userProfile = UserProfile(
                            nickname = rs.getString("nickname") ?: "",
                            gender = rs.getInt("gender"),
                            birthDate = rs.getDate("birth_date")?.let { date ->
                                LocalDate.parse(date.toString())
                            } ?: LocalDate.of(2000, 1, 1),
                            height = rs.getFloat("height"),
                            weight = rs.getFloat("weight"),
                            loginType = loginType,
                            phoneNumber = if (loginType == LoginType.PHONE) identifier else "",
                            wechatOpenId = if (loginType == LoginType.WECHAT) identifier else "",
                            groupCategory = GroupCategory.fromName(rs.getString("group_category") ?: "FITNESS")
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return userProfile
    }

    fun registerUser(userProfile: UserProfile): Boolean {
        val query = """
            INSERT INTO app_user (
                user_uid, nickname, login_type, phone_number, wechat_open_id, 
                gender, birth_date, height, weight, group_category
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        return try {
            DatabaseHelper.getConnection()?.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, UUID.randomUUID().toString())
                    stmt.setString(2, userProfile.nickname)
                    stmt.setInt(3, if (userProfile.loginType == LoginType.WECHAT) 1 else 2)
                    stmt.setString(4, if (userProfile.loginType == LoginType.PHONE) userProfile.phoneNumber else null)
                    stmt.setString(5, if (userProfile.loginType == LoginType.WECHAT) userProfile.wechatOpenId else null)
                    stmt.setInt(6, userProfile.gender)
                    stmt.setDate(7, java.sql.Date.valueOf(userProfile.birthDate.toString()))
                    stmt.setFloat(8, userProfile.height)
                    stmt.setFloat(9, userProfile.weight)
                    stmt.setString(10, userProfile.groupCategory.name)
                    
                    stmt.executeUpdate() > 0
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun updateUserGroup(userProfile: UserProfile): Boolean {
        val query = if (userProfile.loginType == LoginType.PHONE) {
            "UPDATE app_user SET group_category = ? WHERE phone_number = ? AND login_type = 2"
        } else {
            "UPDATE app_user SET group_category = ? WHERE wechat_open_id = ? AND login_type = 1"
        }

        return try {
            DatabaseHelper.getConnection()?.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, userProfile.groupCategory.name)
                    stmt.setString(2, if (userProfile.loginType == LoginType.PHONE) userProfile.phoneNumber else userProfile.wechatOpenId)
                    stmt.executeUpdate() > 0
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
