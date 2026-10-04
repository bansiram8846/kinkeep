package com.example.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultVaultData
import com.example.data.ExpirationUtils
import com.example.data.local.VaultDatabase
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class BottomTab {
    DASHBOARD,
    FAMILY,
    EXPIRING,
    SETTINGS
}

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VaultDatabase.getDatabase(application, viewModelScope)
    private val dao = database.vaultDao()

    val allDocuments: StateFlow<List<VaultDocumentEntity>> = dao.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMembers: StateFlow<List<FamilyMemberEntity>> = dao.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(BottomTab.DASHBOARD)
    val currentTab: StateFlow<BottomTab> = _currentTab.asStateFlow()

    private val _selectedDocumentForView = MutableStateFlow<VaultDocumentEntity?>(null)
    val selectedDocumentForView: StateFlow<VaultDocumentEntity?> = _selectedDocumentForView.asStateFlow()

    private val _selectedDocumentForShare = MutableStateFlow<VaultDocumentEntity?>(null)
    val selectedDocumentForShare: StateFlow<VaultDocumentEntity?> = _selectedDocumentForShare.asStateFlow()

    private val _isUploadScreenOpen = MutableStateFlow(false)
    val isUploadScreenOpen: StateFlow<Boolean> = _isUploadScreenOpen.asStateFlow()

    private val _uploadPreselectedMemberId = MutableStateFlow<String?>(null)
    val uploadPreselectedMemberId: StateFlow<String?> = _uploadPreselectedMemberId.asStateFlow()

    private val _uploadPreselectedCategory = MutableStateFlow<String?>(null)
    val uploadPreselectedCategory: StateFlow<String?> = _uploadPreselectedCategory.asStateFlow()

    private val _memberCategoryFilter = MutableStateFlow("All")
    val memberCategoryFilter: StateFlow<String> = _memberCategoryFilter.asStateFlow()

    fun selectTab(tab: BottomTab) {
        _currentTab.value = tab
    }

    fun openUploadScreen(memberId: String? = null, category: String? = null) {
        _uploadPreselectedMemberId.value = memberId
        _uploadPreselectedCategory.value = category
        _isUploadScreenOpen.value = true
    }

    fun closeUploadScreen() {
        _isUploadScreenOpen.value = false
        _uploadPreselectedMemberId.value = null
        _uploadPreselectedCategory.value = null
    }

    fun viewDocument(doc: VaultDocumentEntity) {
        _selectedDocumentForView.value = doc
    }

    fun closeDocumentViewer() {
        _selectedDocumentForView.value = null
    }

    fun openShare(doc: VaultDocumentEntity) {
        _selectedDocumentForShare.value = doc
    }

    fun closeShare() {
        _selectedDocumentForShare.value = null
    }

    fun setMemberCategoryFilter(category: String) {
        _memberCategoryFilter.value = category
    }

    fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    fun saveDocument(
        name: String,
        category: String,
        provider: String,
        docNumber: String,
        memberId: String,
        expiryDate: String,
        remindExpiry: Boolean = true,
        requireBiometric: Boolean = false,
        tags: List<String> = emptyList(),
        fileUri: String? = null,
        fileSizeText: String? = null,
        metricText: String? = null,
        sharedNotes: String? = null,
        expiryTimestamp: Long? = null,
        reminderDaysBefore: Int = 30
    ) {
        viewModelScope.launch {
            val member = dao.getMemberById(memberId)
            val memberName = member?.name ?: "Family Member"
            val calculatedTimestamp = expiryTimestamp ?: ExpirationUtils.parseDate(expiryDate)
            val daysRemaining = calculatedTimestamp?.let { ExpirationUtils.calculateDaysRemaining(it) }

            val status = when {
                daysRemaining != null && daysRemaining <= 0 -> "EXPIRED"
                daysRemaining != null && daysRemaining <= 30 -> "EXPIRING"
                else -> "ACTIVE"
            }

            val doc = VaultDocumentEntity(
                id = UUID.randomUUID().toString(),
                name = name.ifBlank { "Untitled Document" },
                category = category,
                provider = provider.ifBlank { "Unspecified Provider" },
                policyOrIdNumber = docNumber.ifBlank { "N/A" },
                memberId = memberId,
                memberName = memberName,
                expiryDate = expiryDate,
                expiryTimestamp = calculatedTimestamp,
                remindExpiry = remindExpiry,
                requireBiometric = requireBiometric,
                tags = tags,
                fileUri = fileUri,
                fileSizeText = fileSizeText,
                metricText = metricText,
                sharedNotes = sharedNotes,
                status = status,
                daysRemaining = daysRemaining,
                lastUpdated = System.currentTimeMillis()
            )

            dao.insertDocument(doc)

            // Update member document count
            member?.let {
                dao.updateMember(it.copy(documentCount = it.documentCount + 1))
            }
        }
    }

    fun addFamilyMember(
        name: String,
        role: String,
        relationship: String,
        accessLevel: String = "FULL",
        avatarUrl: String? = null,
        isEmergencyContact: Boolean = true
    ) {
        viewModelScope.launch {
            val member = FamilyMemberEntity(
                id = UUID.randomUUID().toString(),
                name = name.ifBlank { "Family Member" },
                role = role.ifBlank { "Member" },
                relationship = relationship.ifBlank { "Relative" },
                accessLevel = accessLevel,
                avatarUrl = avatarUrl,
                isEmergencyContact = isEmergencyContact,
                documentCount = 0
            )
            dao.insertMember(member)
            showToast("Added ${member.name} to Family Vault")
        }
    }

    fun renewPolicy(doc: VaultDocumentEntity) {
        viewModelScope.launch {
            // Extend policy by 1 year
            val currentTs = doc.expiryTimestamp ?: System.currentTimeMillis()
            val newTs = currentTs + (365L * 24 * 60 * 60 * 1000)
            val newDateStr = ExpirationUtils.formatDate(newTs)
            val newDays = ExpirationUtils.calculateDaysRemaining(newTs)

            val updatedDoc = doc.copy(
                expiryDate = newDateStr,
                expiryTimestamp = newTs,
                daysRemaining = newDays,
                status = "ACTIVE",
                lastUpdated = System.currentTimeMillis()
            )
            dao.updateDocument(updatedDoc)
            showToast("Policy \"${doc.name}\" renewed until $newDateStr")
        }
    }

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            val doc = dao.getDocumentById(docId)
            dao.deleteDocumentById(docId)
            doc?.memberId?.let { memberId ->
                val member = dao.getMemberById(memberId)
                if (member != null && member.documentCount > 0) {
                    dao.updateMember(member.copy(documentCount = member.documentCount - 1))
                }
            }
            closeDocumentViewer()
            showToast("Document deleted from secure storage")
        }
    }

    fun purgeTestData() {
        viewModelScope.launch {
            dao.clearAllDocuments()
            val members = dao.getMemberById("organizer")
            if (members != null) {
                dao.updateMember(members.copy(documentCount = 0))
            }
            showToast("All sample data cleared. Vault is now empty and pristine.")
        }
    }
}
