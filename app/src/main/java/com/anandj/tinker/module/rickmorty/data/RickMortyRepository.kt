package com.anandj.tinker.module.rickmorty.data

import com.anandj.tinker.module.rickmorty.data.remote.Character
import com.anandj.tinker.module.rickmorty.data.remote.Episode
import com.anandj.tinker.module.rickmorty.data.remote.PagedList
import com.anandj.tinker.module.rickmorty.data.remote.RickMortyApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RickMortyRepository
    @Inject
    constructor(
        private val api: RickMortyApi,
    ) {
        private val characterCache = mutableMapOf<Int, Character>()

        suspend fun getCharacters(page: Int? = null): PagedList<Character> {
            val list = api.getCharacters(page)

            list.results.forEach { character ->
                characterCache[character.id] = character
            }
            // delay(2000)
            return list
        }

        suspend fun getEpisodes(page: Int? = null): PagedList<Episode> {
            val list = api.getEpisodes(page)

            return list
        }

        suspend fun getCharacter(
            id: Int,
            invalidateCache: Boolean = false,
        ): Character {
            if (invalidateCache) characterCache.remove(id)

            val character = characterCache[id]
            if (character != null) return character

            val remoteCharacter = api.getCharacter(id)
            characterCache[id] = remoteCharacter
            return remoteCharacter
        }
    }
