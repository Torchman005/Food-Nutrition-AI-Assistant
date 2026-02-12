package com.example.foodnutritionaiassistant

import android.app.Application
import android.util.Log
import java.security.Security

class FoodNutritionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Register the SASL provider manually since Android stripped it
        // This is a workaround for MongoDB driver requiring javax.security.sasl
        try {
            // Try to find if a provider is available or force registration if using a backport lib
            // In many cases just having the class on classpath via apache-directory-api helps, 
            // but sometimes we need to ensure the Security Provider is registered.
            
            // However, the error is specifically NoClassDefFoundError for SaslClient.
            // This means the classloader cannot find the class at runtime even if we added the jar.
            // Android's boot classpath might be taking precedence or there's a multidex issue.
            // But since we are on minSdk 26, multidex is native.
            
            // The Apache Directory API jar SHOULD contain it.
            // Let's verify if we can load it.
            val saslClientClass = Class.forName("javax.security.sasl.SaslClient")
            Log.d("FoodNutritionApp", "SaslClient class found: ${saslClientClass.name}")
            
        } catch (e: ClassNotFoundException) {
            Log.e("FoodNutritionApp", "SaslClient class NOT found!", e)
        } catch (e: Exception) {
             Log.e("FoodNutritionApp", "Error checking SaslClient", e)
        }
    }
}
