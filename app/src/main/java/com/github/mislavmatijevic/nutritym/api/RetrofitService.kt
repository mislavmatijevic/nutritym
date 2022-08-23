package com.github.mislavmatijevic.nutritym.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {
    companion object {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ApiRoutes.DEVELOPER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}