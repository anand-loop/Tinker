package com.anandj.tinker.module.rickmorty.data.api

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
        @Path("id") id: Int,
    ): Character

    @GET("character/{ids}")
    suspend fun getMultipleCharacters(
        @Path("ids") ids: List<Int>,
    ): List<Character>

    @GET("episode")
    suspend fun getEpisodes(
        @Query("page") page: Int? = null,
    ): PagedList<Episode>
}
