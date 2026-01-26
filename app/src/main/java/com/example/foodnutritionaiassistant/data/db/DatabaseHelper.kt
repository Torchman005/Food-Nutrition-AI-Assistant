package com.example.foodnutritionaiassistant.data.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseHelper {
    // Android Emulator refers to host localhost as 10.0.2.2
    private const val URL = "jdbc:mysql://10.0.2.2:3306/food_agent?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    private const val USER = "luminous"
    private const val PASSWORD = "123456"

    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getConnection(): Connection? {
        return try {
            // Check if network is available before connecting?
            // For now, let's assume network is fine or handle exception gracefully
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLException) {
            e.printStackTrace()
            // Log the error or return null
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
