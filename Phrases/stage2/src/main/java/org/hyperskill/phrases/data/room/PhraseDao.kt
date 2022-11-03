package org.hyperskill.phrases.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.hyperskill.phrases.data.room.entity.Phrase

@Dao
interface PhraseDao {
    @Insert
    fun insert(vararg phrase: Phrase)

    @Update
    fun update(vararg phrase: Phrase)

    @Delete
    fun delete(phrase: Phrase)

    @Query("DELETE FROM phrases")
    fun deleteAll()

    @Query("Select * FROM phrases WHERE id = :id")
    fun getById(id: Long): Phrase

    @Query("Select * FROM phrases")
    fun getAll(): List<Phrase>
}