package org.hyperskill.phrases.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.hyperskill.phrases.data.room.entity.Phrase

@Database(entities = [Phrase::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPhraseDao(): PhraseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private var LOCK = Any()
        fun getInstance(context: Context) : AppDatabase {
            synchronized(LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                        AppDatabase::class.java,
                        "phrases.db")
                        .allowMainThreadQueries()
                        .build()
                }
                return INSTANCE!!
            }
        }

    }
}