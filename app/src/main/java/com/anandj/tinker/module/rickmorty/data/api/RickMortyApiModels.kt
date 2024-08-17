package com.anandj.tinker.module.rickmorty.data.api

import com.squareup.moshi.Json
import java.time.LocalDateTime

data class PagedList<T>(
    val info: Info,
    val results: List<T>,
)

data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?,
)

data class Origin(
    val name: String,
    val url: String,
)

data class Location(
    val name: String,
    val url: String,
)

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String?,
    val gender: String,
    val origin: Origin,
    val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: LocalDateTime,
)

data class Episode(
    val id: Int,
    val name: String,
    @Json(name = "air_date") val airDate: String,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: LocalDateTime,
)
