package org.hyperskill.phrases.data

import android.app.Application
import org.hyperskill.phrases.data.room.AppDatabase
import org.hyperskill.phrases.data.room.PhraseDao
import org.hyperskill.phrases.data.room.entity.Phrase

class PhrasesRepository(application: Application) {
    val appDatabase: AppDatabase = AppDatabase.getInstance(application)
    val dao: PhraseDao = appDatabase.phraseDao()

    fun insert(phrase: Phrase) {
        dao.insert(phrase)
    }

    fun update(phrase: Phrase) {
        dao.update(phrase)
    }

    fun delete(phrase: Phrase) {
        dao.delete(phrase)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    fun getById(id: Long): Phrase {
        return dao.getById(id)
    }

    fun getAll(): List<Phrase> {
        return dao.getAll()
    }

}