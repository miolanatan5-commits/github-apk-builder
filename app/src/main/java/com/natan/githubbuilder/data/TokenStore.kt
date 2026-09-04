package com.natan.githubbuilder.data
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
class TokenStore(context:Context){ private val prefs=EncryptedSharedPreferences.create("github",MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),context,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM); fun get()=prefs.getString("token","").orEmpty(); fun save(v:String)=prefs.edit().putString("token",v).apply(); fun clear()=prefs.edit().remove("token").apply() }
