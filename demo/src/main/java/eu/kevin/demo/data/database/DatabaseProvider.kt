package eu.kevin.demo.data.database

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
internal object DatabaseProvider {
    private lateinit var database: DemoDatabase

    fun getDatabase(context: Context): DemoDatabase {
        synchronized(this) {
            if (!this::database.isInitialized) {
                database = Room.databaseBuilder(
                    context,
                    DemoDatabase::class.java,
                    "demo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return database
        }
    }
}