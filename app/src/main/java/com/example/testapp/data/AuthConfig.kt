package com.example.testapp.data

import net.openid.appauth.ResponseTypeValues

object AuthConfig {

    const val AUTH_URI = "https://github.com/login/oauth/authorize"
    const val TOKEN_URI = "https://github.com/login/oauth/access_token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE = "user,repo"

    const val CLIENT_ID = "5c426de66c29d42bf78c"
    const val CLIENT_SECRET = "66dc5f08de61e89e6ba09a721bed5265fb03ed7f"
    const val CALLBACK_URL = "github://github.ru/callback"
}