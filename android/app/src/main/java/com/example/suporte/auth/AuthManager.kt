package com.example.suporte.auth

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("suporte_prefs", Context.MODE_PRIVATE)

    fun saveUser(id: Int, nome: String, email: String, role: String = "usuario") {
        prefs.edit()
            .putInt("user_id", id)
            .putString("user_name", nome)
            .putString("user_email", email)
            .putString("user_role", role)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun isLogged(): Boolean = getUserId() != -1
    fun getUserName(): String? = prefs.getString("user_name", null)
    fun getUserRole(): String? = prefs.getString("user_role", "usuario")
    fun isTecnico(): Boolean = getUserRole() == "tecnico"
}
