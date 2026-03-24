package com.example.insatlkotlinv1.network

import com.example.insatlkotlinv1.models.Appartement
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiService {
    // Utilisez la nouvelle URL localtunnel
    private val BASE_URL = "http://192.168.2.134:4040/api"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = GsonBuilder().create()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    
    interface ApiCallback<T> {
        fun onSuccess(result: T)
        fun onError(error: String)
    }
    
    fun getAllAppartements(callback: ApiCallback<List<Appartement>>) {
        val request = Request.Builder()
            .url("$BASE_URL/appartement/")
            .addHeader("User-Agent", "MyApp/1.0")  // Pour éviter la page d'avertissement
            .get()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Erreur réseau: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback.onError("Erreur serveur: ${response.code}")
                        return
                    }
                    
                    val json = response.body?.string()
                    if (json != null) {
                        try {
                            val appartements = gson.fromJson(json, Array<Appartement>::class.java).toList()
                            callback.onSuccess(appartements)
                        } catch (e: Exception) {
                            callback.onError("Erreur parsing: ${e.message}")
                        }
                    } else {
                        callback.onError("Réponse vide")
                    }
                }
            }
        })
    }
    
    fun addAppartement(appartement: Appartement, callback: ApiCallback<Map<String, Any>>) {
        val json = gson.toJson(appartement)
        val body = json.toRequestBody(JSON)
        
        val request = Request.Builder()
            .url("$BASE_URL/appartement/")
            .post(body)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Erreur réseau: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val result = gson.fromJson(json, Map::class.java) as Map<String, Any>
                            callback.onSuccess(result)
                        } catch (e: Exception) {
                            callback.onError("Erreur parsing: ${e.message}")
                        }
                    } else {
                        callback.onError("Erreur serveur: ${response.code}")
                    }
                }
            }
        })
    }
    
    fun updateAppartement(appartement: Appartement, callback: ApiCallback<Map<String, Any>>) {
        val json = gson.toJson(appartement)
        val body = json.toRequestBody(JSON)
        
        val request = Request.Builder()
            .url("$BASE_URL/appartement/${appartement.numApp}")
            .put(body)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Erreur réseau: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val result = gson.fromJson(json, Map::class.java) as Map<String, Any>
                            callback.onSuccess(result)
                        } catch (e: Exception) {
                            callback.onError("Erreur parsing: ${e.message}")
                        }
                    } else {
                        callback.onError("Erreur serveur: ${response.code}")
                    }
                }
            }
        })
    }
    
    fun deleteAppartement(numApp: Int, callback: ApiCallback<Map<String, Any>>) {
        val request = Request.Builder()
            .url("$BASE_URL/appartement/$numApp")
            .delete()
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Erreur réseau: ${e.message}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val result = gson.fromJson(json, Map::class.java) as Map<String, Any>
                            callback.onSuccess(result)
                        } catch (e: Exception) {
                            callback.onError("Erreur parsing: ${e.message}")
                        }
                    } else {
                        callback.onError("Erreur serveur: ${response.code}")
                    }
                }
            }
        })
    }
}