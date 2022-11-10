package org.hyperskill.phrases

data class Phrase(
    val id: Long,
    val phrase: String
) {
    override fun toString(): String {
        return phrase
    }
}
