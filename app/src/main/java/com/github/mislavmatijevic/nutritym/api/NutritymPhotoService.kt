package com.github.mislavmatijevic.nutritym.api

import com.github.mislavmatijevic.nutritym.dto.PhotoDto
import com.mislavmatijevic.nutritym.backend.responses.ApiResponse
import retrofit2.Call
import retrofit2.http.POST

interface NutritymPhotoService {
    @POST(ApiRoutes.ROUTE_PHOTOS)
    suspend fun uploadPhoto(photoDto: PhotoDto): Call<ApiResponse>
}