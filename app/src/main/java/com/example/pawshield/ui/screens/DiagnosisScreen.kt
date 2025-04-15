package com.example.pawshield.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pawshield.viewmodel.MainViewModel
import com.example.pawshield.ui.Screen // Import Screen class
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val capturedImageUri by viewModel.capturedImageUri.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val selectedAnimalType by viewModel.selectedAnimalType.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() } // For showing errors

    // Image picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setCapturedImageUri(it) }
    }

    // Camera Launcher (Example - requires camera permission and setup)
    /*
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Handle the captured image URI (you need to provide a URI for the camera to save to)
            // viewModel.setCapturedImageUri(uriProvidedToCamera)
        }
    }
    */

    // Show error messages in a Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorMessage() // Clear the error after showing
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Add SnackbarHost
        topBar = {
            TopAppBar(
                title = { Text("New Diagnosis") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Take or select a clear photo of your pet's skin condition",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Image preview/placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (capturedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(capturedImageUri),
                        contentDescription = "Selected pet image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Image Placeholder",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image selection buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Gallery")
                }

                Button(
                    onClick = {
                        // TODO: Implement Camera Logic
                        // 1. Create a file URI using FileProvider
                        // 2. Launch cameraLauncher.launch(uri)
                        // 3. In cameraLauncher callback, use the URI: viewModel.setCapturedImageUri(uri)
                        // For now, it just opens gallery again
                        galleryLauncher.launch("image/*")
                        // Log.w("DiagnosisScreen", "Camera button clicked - functionality not fully implemented")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Take Photo")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pet type selector (Using Toggle Buttons for better UX)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center // Center the buttons
            ) {
                FilterChip(
                    selected = selectedAnimalType == "dog",
                    onClick = { viewModel.setAnimalType("dog") },
                    label = { Text("Dog") },
                    modifier = Modifier.padding(horizontal = 8.dp) // Add padding
                )

                FilterChip(
                    selected = selectedAnimalType == "cat",
                    onClick = { viewModel.setAnimalType("cat") },
                    label = { Text("Cat") },
                    modifier = Modifier.padding(horizontal = 8.dp) // Add padding
                )
            }


            Spacer(modifier = Modifier.weight(1f)) // Pushes elements below to the bottom

            // Tips section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) // Use secondary container
                ),
                shape = RoundedCornerShape(8.dp) // Adjust shape
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Tips for a Clear Photo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold // Make title bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• Ensure good lighting (avoid shadows)\n" +
                                "• Focus directly on the affected skin area\n" +
                                "• Hold the camera steady\n" +
                                "• Include some surrounding healthy skin if possible",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Analyze button
            Button(
                onClick = {
                    capturedImageUri?.let { uri ->
                        viewModel.analyzeImage(uri) // Pass the URI
                    } ?: run {
                        // Optionally show message if no image selected
                        viewModel.setErrorMessage("Please select or take a photo first.")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp), // Standard button height
                enabled = capturedImageUri != null && !isAnalyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary, // White on primary
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyzing...")
                } else {
                    Text("Analyze")
                }
            }
        }
    }
}

// Helper function to manage error state (example)
fun MainViewModel.setErrorMessage(message: String) {
    // This assumes you have a private mutable state flow _errorMessage
    // and a public state flow errorMessage in your ViewModel
    (this.javaClass.getDeclaredField("_errorMessage").apply { isAccessible = true }
        .get(this) as MutableStateFlow<String?>).value = message
}