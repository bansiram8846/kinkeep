package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BiometricAuthDialog
import com.example.ui.components.DocumentViewerDialog
import com.example.ui.components.ExpirationAlertsDialog
import com.example.ui.components.KinKeepBottomNav
import com.example.ui.components.ShareDocumentDialog
import com.example.ui.screens.ElenaVaultScreen
import com.example.ui.screens.ExpiringScreen
import com.example.ui.screens.FamilyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UploadDocumentScreen
import com.example.ui.screens.VaultOverviewScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.VaultViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KinKeepApp()
            }
        }
    }
}

@Composable
fun KinKeepApp(
    viewModel: VaultViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val expiringDocs by viewModel.expiringDocuments.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        when (uiState.activeScreen) {
            ActiveScreen.MEMBER_VAULT -> {
                ElenaVaultScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() },
                    onAddDocument = { memberId ->
                        viewModel.openUpload(memberId)
                    }
                )
            }
            ActiveScreen.UPLOAD_DOCUMENT -> {
                UploadDocumentScreen(
                    viewModel = viewModel,
                    onClose = { viewModel.navigateBack() }
                )
            }
            ActiveScreen.TABS -> {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        KinKeepBottomNav(
                            currentTab = uiState.currentTab,
                            onTabSelected = { tab ->
                                viewModel.selectTab(tab)
                            },
                            hasExpiringAlert = expiringDocs.isNotEmpty()
                        )
                    },
                    containerColor = ObsidianBackground
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        AnimatedContent(
                            targetState = uiState.currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                BottomTab.VAULT -> {
                                    VaultOverviewScreen(
                                        viewModel = viewModel,
                                        onOpenMemberVault = { memberId ->
                                            viewModel.openMemberVault(memberId)
                                        },
                                        onOpenUpload = {
                                            viewModel.openUpload()
                                        },
                                        onManageFamily = {
                                            viewModel.selectTab(BottomTab.FAMILY)
                                        }
                                    )
                                }
                                BottomTab.FAMILY -> {
                                    FamilyScreen(
                                        viewModel = viewModel,
                                        onOpenMemberVault = { memberId ->
                                            viewModel.openMemberVault(memberId)
                                        }
                                    )
                                }
                                BottomTab.EXPIRING -> {
                                    ExpiringScreen(viewModel = viewModel)
                                }
                                BottomTab.SETTINGS -> {
                                    SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Biometric Unlock Modal
        if (uiState.isBiometricModalOpen && uiState.viewerDocument != null) {
            BiometricAuthDialog(
                document = uiState.viewerDocument!!,
                onAuthenticate = {
                    viewModel.authenticateBiometric()
                },
                onDismiss = {
                    viewModel.dismissViewer()
                }
            )
        }

        // Decrypted Document Viewer Modal
        if (!uiState.isBiometricModalOpen && uiState.isBiometricAuthenticated && uiState.viewerDocument != null) {
            DocumentViewerDialog(
                document = uiState.viewerDocument!!,
                onDismiss = {
                    viewModel.dismissViewer()
                },
                onDelete = { docId ->
                    viewModel.deleteDocument(docId)
                },
                onShare = { doc ->
                    viewModel.openShare(doc)
                }
            )
        }

        // Share Dialog
        if (uiState.shareDialogDoc != null) {
            ShareDocumentDialog(
                document = uiState.shareDialogDoc!!,
                onDismiss = {
                    viewModel.dismissShare()
                }
            )
        }

        // 30-Day Proactive Expiration Alerts Dialog
        if (uiState.isAlertsModalOpen) {
            ExpirationAlertsDialog(
                expiringDocuments = expiringDocs,
                onDismiss = {
                    viewModel.closeAlertsModal()
                },
                onRenew = { doc ->
                    viewModel.renewPolicy(doc)
                },
                onViewDocument = { doc ->
                    viewModel.closeAlertsModal()
                    viewModel.viewDocument(doc)
                },
                onTriggerTestAlert = {
                    viewModel.addSampleExpiringDocument()
                },
                onOpenExpiringTab = {
                    viewModel.closeAlertsModal()
                    viewModel.selectTab(BottomTab.EXPIRING)
                }
            )
        }
    }
}
