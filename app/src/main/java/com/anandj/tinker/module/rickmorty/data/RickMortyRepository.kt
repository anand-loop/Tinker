package com.anandj.tinker.module.rickmorty.data

import com.anandj.tinker.module.rickmorty.data.api.Character
import com.anandj.tinker.module.rickmorty.data.api.Episode
import com.anandj.tinker.module.rickmorty.data.api.PagedList
import com.anandj.tinker.module.rickmorty.data.api.RickMortyApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Singleton
class RickMortyRepository
    @Inject
    constructor(
        private val api: RickMortyApi,
    ) {
        private val characterCache = mutableMapOf<Int, Character>()

        suspend fun getCharacters(page: Int? = null): Result<PagedList<Character>> =
            kotlin.runCatching {
                delay(3000)
                api.getCharacters(page).apply {
                    results.forEach { character ->
                        characterCache[character.id] = character
                    }
                }
            }

        suspend fun getMultipleCharacters(ids: List<Int>): List<Character> {
            val cachedCharacters = mutableListOf<Character>()
            val nonCachedIds = mutableListOf<Int>()

            ids.forEach { id ->
                val cached = characterCache[id]
                if (cached != null) {
                    cachedCharacters.add(cached)
                } else {
                    nonCachedIds.add(id)
                }
            }

            val newCharacters =
                api.getMultipleCharacters(nonCachedIds).apply {
                    forEach { character ->
                        characterCache[character.id] = character
                    }
                }

            return (cachedCharacters + newCharacters).sortedBy { it.id }
        }

        suspend fun getEpisodes(page: Int? = null): PagedList<Episode> {
            val list = api.getEpisodes(page)

            list.results.forEach { episode ->
                val characterIds =
                    episode.characters
                        .map { it.substringAfterLast("/").toInt() }
                // val chars = getMultipleCharacters(characterIds)
                // println(chars.toString())
            }

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
