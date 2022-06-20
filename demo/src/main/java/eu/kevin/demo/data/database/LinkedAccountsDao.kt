package eu.kevin.demo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eu.kevin.demo.data.database.entities.LinkedAccount
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LinkedAccountsDao {

    @Query("SELECT * FROM LinkedAccount")
    fun getLinkedAccountsFlow(): Flow<List<LinkedAccount>>

    @Query("SELECT * FROM LinkedAccount")
    suspend fun getLinkedAccounts(): List<LinkedAccount>

    @Query("SELECT * FROM LinkedAccount WHERE bankId = :bankId")
    suspend fun getLinkedAccounts(bankId: String): List<LinkedAccount>

    @Query("SELECT * FROM LinkedAccount WHERE id = :id")
    suspend fun getById(id: Long): LinkedAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(linkedAccount: LinkedAccount)

    @Query("DELETE FROM LinkedAccount WHERE bankId = :bankId")
    suspend fun delete(bankId: String)
}