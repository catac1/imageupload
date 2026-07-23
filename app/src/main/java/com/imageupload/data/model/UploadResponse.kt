package com.imageupload.data.model

data class UploadResponse(
    val result: Int,
    val code: String,
    val filename: String
)