package com.github.mislavmatijevic.nutritym.api

import com.github.mislavmatijevic.nutritym.dto.PhotoDto
import com.mislavmatijevic.nutritym.backend.responses.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PhotoService {
    @POST(ApiRoutes.ROUTE_PHOTOS)
    suspend fun uploadPhoto(@Body photoDto: PhotoDto): Response<ApiResponse>
}