package com.example.data.repository

import com.example.data.DefaultVaultData
import com.example.data.local.VaultDao
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VaultRepository(private val vaultDao: VaultDao) {

    val allMembers: Flow<List<FamilyMemberEntity>> = vaultDao.getAllMembers()
    val allDocuments: Flow<List<VaultDocumentEntity>> = vaultDao.getAllDocuments()
    val expiringDocuments: Flow<List<VaultDocumentEntity>> = vaultDao.getExpiringDocuments()

    fun getDocumentsForMember(memberId: String): Flow<List<VaultDocumentEntity>> {
        return vaultDao.getDocumentsForMember(memberId)
    }

    suspend fun insertDocument(document: VaultDocumentEntity) = withContext(Dispatchers.IO) {
        vaultDao.insertDocument(document)
    }

    suspend fun insertMember(member: FamilyMemberEntity) = withContext(Dispatchers.IO) {
        vaultDao.insertMember(member)
    }

    suspend fun updateDocument(document: VaultDocumentEntity) = withContext(Dispatchers.IO) {
        vaultDao.updateDocument(document)
    }

    suspend fun deleteDocument(docId: String) = withContext(Dispatchers.IO) {
        vaultDao.deleteDocumentById(docId)
    }

    suspend fun renewDocument(doc: VaultDocumentEntity, newExpiryText: String = "Valid till 2027") = withContext(Dispatchers.IO) {
        val renewed = doc.copy(
            expiryDate = newExpiryText,
            daysRemaining = null,
            status = "Verified",
            isActionNeeded = false
        )
        vaultDao.updateDocument(renewed)
    }

    suspend fun ensureInitialData() = withContext(Dispatchers.IO) {
        val memberCount = vaultDao.getMemberCount()
        if (memberCount == 0) {
            vaultDao.insertMembers(DefaultVaultData.members)
        }
        val docCount = vaultDao.getDocumentCount()
        if (docCount == 0 && DefaultVaultData.documents.isNotEmpty()) {
            vaultDao.insertDocuments(DefaultVaultData.documents)
        }
    }

    suspend fun clearAllDocuments() = withContext(Dispatchers.IO) {
        vaultDao.clearAllDocuments()
    }
}
