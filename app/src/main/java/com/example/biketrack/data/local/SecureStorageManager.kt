package com.example.biketrack.data.local

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SecureStorageManager(private val context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val gson = Gson()
    
    private fun getEncryptedFile(fileName: String): EncryptedFile {
        val file = File(context.filesDir, fileName)
        return EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }
    
    fun saveUserSession(userSession: UserSession): Boolean {
        return try {
            val encryptedFile = getEncryptedFile("user_session.json")
            val json = gson.toJson(userSession)
            
            val outputStream = encryptedFile.openFileOutput()
            outputStream.use { stream ->
                stream.write(json.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getUserSession(): UserSession? {
        return try {
            val encryptedFile = getEncryptedFile("user_session.json")
            val file = File(context.filesDir, "user_session.json")
            
            if (!file.exists()) {
                return null
            }
            
            val inputStream = encryptedFile.openFileInput()
            val json = inputStream.bufferedReader().use { it.readText() }
            
            gson.fromJson(json, UserSession::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun clearUserSession(): Boolean {
        return try {
            val file = File(context.filesDir, "user_session.json")
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun hasStoredSession(): Boolean {
        val file = File(context.filesDir, "user_session.json")
        return file.exists()
    }
} 