package com.savvasdalkitsis.librephotos.search.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResults(
    val results: List<SearchResult>
)
