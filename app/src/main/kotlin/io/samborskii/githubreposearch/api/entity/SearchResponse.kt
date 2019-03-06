package io.samborskii.githubreposearch.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchResponse(
    @JsonProperty("total_count") val totalCount: Int,
    @JsonProperty("incomplete_results") val incompleteResults: Boolean,
    @JsonProperty("items") val items: List<Repository>
)
