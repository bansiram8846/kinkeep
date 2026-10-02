package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExpirationUtils
import com.example.data.local.VaultDatabase
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import com.example.data.repository.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class BottomTab {
    VAULT,
    FAMILY,
    EXPIRING,
    SETTINGS
}

enum class ActiveScreen {
    TABS,
    MEMBER_VAULT,
    UPLOAD_DOCUMENT
}

data class VaultUiState(
    val currentTab: BottomTab = BottomTab.VAULT,
    val activeScreen: ActiveScreen = ActiveScreen.TABS,
    val selectedMemberId: String = "organizer",
    val memberCategoryFilter: String = "All",
    val dashboardCategoryFilter: String = "All",
    val viewerDocument: VaultDocumentEntity? = null,
    val isBiometricModalOpen: Boolean = false,
    val isBiometricAuthenticated: Boolean = false,
    val toastMessage: String? = null,
    val shareDialogDoc: VaultDocumentEntity? = null,
    val isAlertsModalOpen: Boolean = false
)

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VaultRepository

    val allMembers: StateFlow<List<FamilyMemberEntity>>
    val allDocuments: StateFlow<List<VaultDocumentEntity>>
    val expiringDocuments: StateFlow<List<VaultDocumentEntity>>

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    init {
        val database = VaultDatabase.getDatabase(application, viewModelScope)
        repository = VaultRepository(database.vaultDao())

        viewModelScope.launch {
            repository.ensureInitialData()
        }

        allMembers = repository.allMembers
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allDocuments = repository.allDocuments
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        expiringDocuments = repository.expiringDocuments
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun selectTab(tab: BottomTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            activeScreen = ActiveScreen.TABS
        )
    }

    fun openMemberVault(memberId: String) {
        _uiState.value = _uiState.value.copy(
            selectedMemberId = memberId,
            memberCategoryFilter = "All",
            activeScreen = ActiveScreen.MEMBER_VAULT
        )
    }

    fun openUpload(memberId: String = _uiState.value.selectedMemberId) {
        _uiState.value = _uiState.value.copy(
            selectedMemberId = memberId,
            activeScreen = ActiveScreen.UPLOAD_DOCUMENT
        )
    }

    fun navigateBack(): Boolean {
        return if (_uiState.value.activeScreen != ActiveScreen.TABS) {
            _uiState.value = _uiState.value.copy(activeScreen = ActiveScreen.TABS)
            true
        } else {
            false
        }
    }

    fun setMemberCategoryFilter(filter: String) {
        _uiState.value = _uiState.value.copy(memberCategoryFilter = filter)
    }

    fun setDashboardCategoryFilter(category: String) {
        _uiState.value = _uiState.value.copy(dashboardCategoryFilter = category)
    }

    fun openAlertsModal() {
        _uiState.value = _uiState.value.copy(isAlertsModalOpen = true)
    }

    fun closeAlertsModal() {
        _uiState.value = _uiState.value.copy(isAlertsModalOpen = false)
    }

    fun viewDocument(doc: VaultDocumentEntity) {
        if (doc.requireBiometric) {
            _uiState.value = _uiState.value.copy(
                viewerDocument = doc,
                isBiometricModalOpen = true,
                isBiometricAuthenticated = false
            )
        } else {
            _uiState.value = _uiState.value.copy(
                viewerDocument = doc,
                isBiometricModalOpen = false,
                isBiometricAuthenticated = true
            )
        }
    }

    fun authenticateBiometric() {
        _uiState.value = _uiState.value.copy(
            isBiometricAuthenticated = true,
            isBiometricModalOpen = false
        )
    }

    fun dismissViewer() {
        _uiState.value = _uiState.value.copy(
            viewerDocument = null,
            isBiometricModalOpen = false,
            isBiometricAuthenticated = false
        )
    }

    fun openShare(doc: VaultDocumentEntity) {
        _uiState.value = _uiState.value.copy(shareDialogDoc = doc)
    }

    fun dismissShare() {
        _uiState.value = _uiState.value.copy(shareDialogDoc = null)
    }

    fun renewPolicy(doc: VaultDocumentEntity) {
        viewModelScope.launch {
            val newTimestamp = ExpirationUtils.getTimestampAfterDays(365)
            val newDateStr = ExpirationUtils.formatDate(newTimestamp)
            val renewed = doc.copy(
                expiryDate = newDateStr,
                expiryTimestamp = newTimestamp,
                daysRemaining = 365,
                status = "Verified",
                isActionNeeded = false
            )
            repository.updateDocument(renewed)
            showToast("✓ Policy renewed! Valid until $newDateStr")
        }
    }

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            repository.deleteDocument(docId)
            dismissViewer()
            showToast("Document removed from vault.")
        }
    }

    fun saveDocument(
        name: String,
        category: String,
        provider: String,
        docNumber: String,
        memberId: String,
        expiryDate: String,
        remindExpiry: Boolean,
        requireBiometric: Boolean,
        tags: List<String>,
        fileUri: String? = null,
        fileSizeText: String? = null,
        expiryTimestamp: Long? = null,
        reminderDaysBefore: Int = 30
    ) {
        viewModelScope.launch {
            val member = allMembers.value.find { it.id == memberId }
            val memberName = member?.name ?: "Family Member"

            // Compute exact days remaining
            val parsedTimestamp = expiryTimestamp ?: ExpirationUtils.parseDate(expiryDate)
            val calculatedDays = if (parsedTimestamp != null) {
                ExpirationUtils.calculateDaysRemaining(parsedTimestamp)
            } else null

            val isUrgent = calculatedDays != null && calculatedDays <= reminderDaysBefore
            val status = when {
                calculatedDays != null && calculatedDays < 0 -> "Expired"
                calculatedDays != null && calculatedDays <= reminderDaysBefore -> "Expiring"
                else -> "Verified"
            }

            val newDoc = VaultDocumentEntity(
                id = "doc_" + UUID.randomUUID().toString().take(8),
                name = name.ifBlank { "Untitled Document" },
                category = category,
                provider = provider.ifBlank { "Government Authority" },
                policyOrIdNumber = docNumber.ifBlank { "REF-" + System.currentTimeMillis().toString().takeLast(6) },
                memberId = memberId,
                memberName = memberName,
                expiryDate = expiryDate.ifBlank { "Valid till 2030" },
                expiryTimestamp = parsedTimestamp,
                daysRemaining = calculatedDays,
                status = status,
                metricText = fileSizeText ?: "End-to-End Encrypted",
                isActionNeeded = isUrgent && remindExpiry,
                remindBeforeExpiry = remindExpiry,
                reminderDaysBefore = reminderDaysBefore,
                requireBiometric = requireBiometric,
                tagsCsv = tags.joinToString(","),
                sharedNotes = if (member?.role?.contains("Organizer") == true) "Shared with Co-Organizer" else null,
                isPinned = false,
                fileUri = fileUri,
                fileType = if (fileUri?.endsWith(".pdf", ignoreCase = true) == true) "application/pdf" else "image/jpeg",
                fileSizeText = fileSizeText ?: "2.8 MB PDF"
            )
            repository.insertDocument(newDoc)
            _uiState.value = _uiState.value.copy(activeScreen = ActiveScreen.MEMBER_VAULT, selectedMemberId = memberId)

            if (isUrgent) {
                showToast("⚠️ 30-Day Alert Active: ${name} expires in ${calculatedDays} days!")
            } else {
                showToast("Saved to ${memberName.split(" ").first()}'s Vault")
            }
        }
    }

    fun addSampleExpiringDocument() {
        showToast("Vault is operating in clean mode with zero test documents")
    }

    fun addFamilyMember(
        name: String,
        relationship: String,
        accessPermission: String,
        avatarUrl: String? = null
    ) {
        viewModelScope.launch {
            val id = name.lowercase().replace(" ", "_").take(10) + "_" + UUID.randomUUID().toString().take(4)
            val initials = name.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()
                .ifBlank { "FM" }

            val isEmergency = accessPermission == "Emergency Contact"
            val role = if (accessPermission == "Full Access") "Co-Organizer" else "Member"

            val newMember = FamilyMemberEntity(
                id = id,
                name = name.trim().ifBlank { "New Member" },
                relationship = relationship,
                role = role,
                accessPermission = accessPermission,
                avatarUrl = avatarUrl,
                initials = initials,
                badgeColorHex = 0xFF5352ED,
                isEmergencyContact = isEmergency
            )
            repository.insertMember(newMember)
            showToast("Added $name to Family Vault")
        }
    }

    fun showToast(msg: String) {
        _uiState.value = _uiState.value.copy(toastMessage = msg)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun clearAllDocuments() {
        viewModelScope.launch {
            repository.clearAllDocuments()
            showToast("All documents cleared. Vault is now clean.")
        }
    }

    fun saveScannedDocument(
        name: String,
        memberId: String,
        category: String = "Identity & IDs",
        provider: String = "National Issuing Authority",
        docNumber: String? = null,
        photoUri: String? = null
    ) {
        val calculatedExpiry = ExpirationUtils.getTimestampAfterDays(365)
        val expiryDateStr = ExpirationUtils.formatDate(calculatedExpiry)
        val generatedDocNumber = docNumber ?: ("SCAN-" + System.currentTimeMillis().toString().takeLast(6))

        saveDocument(
            name = name,
            category = category,
            provider = provider,
            docNumber = generatedDocNumber,
            memberId = memberId,
            expiryDate = expiryDateStr,
            remindExpiry = true,
            requireBiometric = false,
            tags = listOf("#scanned", "#camera", "#encrypted"),
            fileUri = photoUri,
            fileSizeText = "1.8 MB (Encrypted Scan)",
            expiryTimestamp = calculatedExpiry,
            reminderDaysBefore = 30
        )
        showToast("📷 Document captured & encrypted! 30-day early alert active (Expires: $expiryDateStr)")
    }
}
