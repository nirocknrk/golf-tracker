package com.rapsodo.golftracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Generic paged envelope returned by every list endpoint. */
@JsonClass(generateAdapter = true)
data class PagedResponse<T>(
    @Json(name = "data")        val data: List<T>,
    @Json(name = "total")       val total: Int,
    @Json(name = "page")        val page: Int,
    @Json(name = "per_page")    val perPage: Int,
    @Json(name = "total_pages") val totalPages: Int,
)
