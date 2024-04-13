package com.anandj.tinker.module.rickmorty.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int? = null,
    ): PagedList<Character>

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: String,
    ): Character
}
