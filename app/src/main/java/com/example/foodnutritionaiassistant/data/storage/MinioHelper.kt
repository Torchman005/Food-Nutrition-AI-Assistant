package com.example.foodnutritionaiassistant.data.storage

import android.util.Log
import com.example.foodnutritionaiassistant.config.AppConfig
import io.minio.MinioClient
import io.minio.PutObjectArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

object MinioHelper {
    private const val TAG = "MinioHelper"
    private var minioClient: MinioClient? = null

    init {
        try {
            minioClient = MinioClient.builder()
                .endpoint(AppConfig.MINIO_ENDPOINT)
                .credentials(AppConfig.MINIO_ACCESS_KEY, AppConfig.MINIO_SECRET_KEY)
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MinIO client", e)
        }
    }

    suspend fun uploadImage(
        inputStream: InputStream,
        fileName: String,
        contentType: String = "image/jpeg"
    ): String? = withContext(Dispatchers.IO) {
        try {
            val client = minioClient ?: return@withContext null
            
            // Check if bucket exists, if not create it (this might fail if user permissions are restricted, but user said to create it)
            // Note: In a real app, bucket creation usually happens on backend. 
            // We assume bucket "nutriscan" exists as per instructions, or we try to create/ensure it exists.
            val bucketExists = client.bucketExists(io.minio.BucketExistsArgs.builder().bucket(AppConfig.MINIO_BUCKET_NAME).build())
            if (!bucketExists) {
                 client.makeBucket(io.minio.MakeBucketArgs.builder().bucket(AppConfig.MINIO_BUCKET_NAME).build())
            }

            client.putObject(
                PutObjectArgs.builder()
                    .bucket(AppConfig.MINIO_BUCKET_NAME)
                    .`object`(fileName)
                    .stream(inputStream, -1, 10485760) // 10MB part size
                    .contentType(contentType)
                    .build()
            )

            // Return the URL
            // Since we are inside the emulator, 10.0.2.2 works for the app to reach the server.
            // But if we store "http://10.0.2.2..." in DB, other clients might not be able to access it.
            // However, for this local setup, it's fine.
            "${AppConfig.MINIO_ENDPOINT}/${AppConfig.MINIO_BUCKET_NAME}/$fileName"
        } catch (e: Exception) {
            Log.e(TAG, "Upload failed", e)
            e.printStackTrace()
            null
        }
    }
}
