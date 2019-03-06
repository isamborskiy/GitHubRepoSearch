package io.samborskii.githubreposearch.api.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    @JsonProperty("id") val id: Long,
    @JsonProperty("full_name") val fullName: String,
    @JsonProperty("html_url") val htmlUrl: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("language") val language: String?
)
