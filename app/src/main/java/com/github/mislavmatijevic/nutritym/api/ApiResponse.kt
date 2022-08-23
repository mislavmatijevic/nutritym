package com.mislavmatijevic.nutritym.backend.responses

class ApiResponse(val isSuccess: Boolean, val message: String?) {
    constructor(message: String?) : this(true, message) {}
}