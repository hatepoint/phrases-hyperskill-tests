package org.hyperskill.phrases.data.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "phrases", indices = [Index(value = ["phrase"], unique = true)])
data class Phrase(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val phrase: String
) {

}
