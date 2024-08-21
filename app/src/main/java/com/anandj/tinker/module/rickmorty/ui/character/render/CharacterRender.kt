package com.anandj.tinker.module.rickmorty.ui.character.render

import com.anandj.tinker.module.rickmorty.data.api.Character

data class CharacterRender(
    val id: Int,
    val name: String,
    val image: String,
    val status: String,
    val species: String,
)

fun Character.toRender(): CharacterRender {
    return CharacterRender(
        id = this.id,
        name = this.name,
        image = this.image,
        status = this.status,
        species = this.species,
    )
}
