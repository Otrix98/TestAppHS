package com.example.testapp.data

import android.content.Context

object AccessToken {
   var value: String? = null

   const val TOKEN_PREFERENCES = "token_preferences"
   const val TOKEN_STRING = "token string"

   fun saveToken(context: Context) {
      val editor = context.getSharedPreferences(TOKEN_PREFERENCES, Context.MODE_PRIVATE).edit()
      editor.putString(TOKEN_STRING, AccessToken.value)
      editor.apply()
   }
}