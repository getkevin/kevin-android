package eu.kevin.demo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import eu.kevin.demo.data.database.entities.LinkedAccount

@Database(entities = [LinkedAccount::class], version = 1)
internal abstract class DemoDatabase : RoomDatabase() {
    abstract fun linkedAccountsDao(): LinkedAccountsDao
}