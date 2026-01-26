package com.example.foodnutritionaiassistant.data.db

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

object MongoDBHelper {
    // Replace <db_password> with the actual password
    private const val CONNECTION_STRING = "mongodb+srv://luminous:X4Gms1AZtzGmzULe@cluster0.mujnycz.mongodb.net/?appName=Cluster0"
    private const val DB_NAME = "food_agent"

    private var client: MongoClient? = null
    private var database: MongoDatabase? = null

    fun getDatabase(): MongoDatabase {
        if (database == null) {
            try {
                client = MongoClient.create(CONNECTION_STRING)
                database = client?.getDatabase(DB_NAME)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return database ?: throw IllegalStateException("Could not connect to MongoDB")
    }

    fun close() {
        client?.close()
        client = null
        database = null
    }
}
