package io.samborskii.githubreposearch.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

val EMPTY_REPOSITORY = Repository(-1, "", "", null, null)
val ERROR_REPOSITORY = Repository(-2, "", "", null, null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    @JsonProperty("id") val id: Long,
    @JsonProperty("full_name") val fullName: String,
    @JsonProperty("html_url") val htmlUrl: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("language") val language: String?
)
