package com.savvasdalkitsis.librephotos.photos.model

data class Photo(
    val id: String?,
    val url: String?,
    val fallbackColor: String?,
    val ratio: Float,
    val isVideo: Boolean,
)
