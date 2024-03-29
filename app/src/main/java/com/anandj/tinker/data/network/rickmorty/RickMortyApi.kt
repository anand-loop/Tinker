package com.anandj.tinker.data.network.rickmorty

import retrofit2.http.GET
import retrofit2.http.Query

interface RickMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int? = null,
    ): PagedList<Character>
}
