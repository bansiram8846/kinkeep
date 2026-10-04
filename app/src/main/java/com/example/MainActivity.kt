package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DocumentViewerDialog
import com.example.ui.components.KinKeepBottomNavigation
import com.example.ui.components.ShareDocumentDialog
import com.example.ui.screens.ExpiringScreen
import com.example.ui.screens.FamilyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UploadDocumentScreen
import com.example.ui.screens.VaultOverviewScreen
import com.example.ui.theme.KinKeepTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.VaultViewModel
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: VaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)

        setContent {
            KinKeepTheme {
                MainVaultApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainVaultApp(viewModel: VaultViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isUploadOpen by viewModel.isUploadScreenOpen.collectAsStateWithLifecycle()
    val uploadPreselectedMemberId by viewModel.uploadPreselectedMemberId.collectAsStateWithLifecycle()
    val uploadPreselectedCategory by viewModel.uploadPreselectedCategory.collectAsStateWithLifecycle()
    val selectedDocForView by viewModel.selectedDocumentForView.collectAsStateWithLifecycle()
    val selectedDocForShare by viewModel.selectedDocumentForShare.collectAsStateWithLifecycle()
    val allDocs by viewModel.allDocuments.collectAsStateWithLifecycle()

    val expiringCount = allDocs.count { it.status == "EXPIRED" || (it.daysRemaining != null && it.daysRemaining <= 30) }

    // Dialog: In-App Document Viewer (No download required)
    selectedDocForView?.let { doc ->
        DocumentViewerDialog(
            document = doc,
            onDismiss = { viewModel.closeDocumentViewer() },
            onDelete = { id -> viewModel.deleteDocument(id) },
            onShare = { document -> viewModel.openShare(document) }
        )
    }

    // Dialog: Encrypted Share
    selectedDocForShare?.let { doc ->
        ShareDocumentDialog(
            document = doc,
            onDismiss = { viewModel.closeShare() }
        )
    }

    if (isUploadOpen) {
        UploadDocumentScreen(
            viewModel = viewModel,
            preselectedMemberId = uploadPreselectedMemberId,
            preselectedCategory = uploadPreselectedCategory,
            onClose = { viewModel.closeUploadScreen() }
        )
    } else {
        Scaffold(
            bottomBar = {
                KinKeepBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    expiringCount = expiringCount
                )
            },
            containerColor = ObsidianBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianBackground)
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        BottomTab.DASHBOARD -> VaultOverviewScreen(viewModel = viewModel)
                        BottomTab.FAMILY -> FamilyScreen(viewModel = viewModel)
                        BottomTab.EXPIRING -> ExpiringScreen(viewModel = viewModel)
                        BottomTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
