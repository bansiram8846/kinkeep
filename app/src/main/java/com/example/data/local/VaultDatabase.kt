package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.DefaultVaultData
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [FamilyMemberEntity::class, VaultDocumentEntity::class],
    version = 4,
    exportSchema = false
)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "kinkeep_vault.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(VaultDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class VaultDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.vaultDao())
                    }
                }
            }

            suspend fun populateDatabase(vaultDao: VaultDao) {
                vaultDao.insertMembers(DefaultVaultData.members)
                vaultDao.insertDocuments(DefaultVaultData.documents)
            }
        }
    }
}
