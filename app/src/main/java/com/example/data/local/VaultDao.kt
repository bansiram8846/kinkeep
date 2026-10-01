package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM family_members ORDER BY id ASC")
    fun getAllMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: FamilyMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<FamilyMemberEntity>)

    @Query("SELECT * FROM vault_documents ORDER BY isActionNeeded DESC, isPinned DESC, timestamp DESC")
    fun getAllDocuments(): Flow<List<VaultDocumentEntity>>

    @Query("SELECT * FROM vault_documents WHERE memberId = :memberId ORDER BY isActionNeeded DESC, timestamp DESC")
    fun getDocumentsForMember(memberId: String): Flow<List<VaultDocumentEntity>>

    @Query("SELECT * FROM vault_documents WHERE isActionNeeded = 1 OR (daysRemaining IS NOT NULL AND daysRemaining <= 30) OR status = 'Expiring' ORDER BY daysRemaining ASC")
    fun getExpiringDocuments(): Flow<List<VaultDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: VaultDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<VaultDocumentEntity>)

    @Update
    suspend fun updateDocument(document: VaultDocumentEntity)

    @Query("DELETE FROM vault_documents WHERE id = :docId")
    suspend fun deleteDocumentById(docId: String)

    @Query("DELETE FROM vault_documents")
    suspend fun clearAllDocuments()

    @Query("SELECT COUNT(*) FROM vault_documents")
    suspend fun getDocumentCount(): Int

    @Query("SELECT COUNT(*) FROM family_members")
    suspend fun getMemberCount(): Int
}
