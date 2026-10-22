package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.PremiumGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(viewModel: ConnectLiveViewModel) {
    val meProfile by viewModel.meProfile.collectAsState()
    val profile = meProfile ?: return

    var nameInput by remember { mutableStateOf(profile.name) }
    var bioInput by remember { mutableStateOf(profile.bio) }
    var countryInput by remember { mutableStateOf(profile.country) }
    var cityInput by remember { mutableStateOf(profile.city) }
    var interestsInput by remember { mutableStateOf(profile.interests) }
    var languagesInput by remember { mutableStateOf(profile.languages) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080512))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo("main") },
                        modifier = Modifier.testTag("edit_profile_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Modify Profile Details", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }

                IconButton(
                    onClick = {
                        viewModel.updateMyProfile(
                            name = nameInput,
                            bio = bioInput,
                            country = countryInput,
                            city = cityInput,
                            interests = interestsInput,
                            languages = languagesInput
                        )
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(PremiumGradientBrush, RoundedCornerShape(20.dp))
                        .testTag("edit_profile_save_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White)
                }
            }

            // Input Form listing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name", color = Color.White.copy(0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input")
                )

                OutlinedTextField(
                    value = bioInput,
                    onValueChange = { bioInput = it },
                    label = { Text("Short Biography", color = Color.White.copy(0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("edit_bio_input")
                )

                val aiLoading by viewModel.aiOperationLoading.collectAsState()
                val aiResult by viewModel.aiOperationResult.collectAsState()

                // When AI bio generation succeeds, update the text field
                LaunchedEffect(aiResult) {
                    if (aiResult != null && aiResult!!.length > 10 && aiResult!!.length < 180) {
                        bioInput = aiResult!!
                        viewModel.clearAiOperationResult()
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { viewModel.aiOptimizeProfileBio() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2).copy(0.2f)),
                        modifier = Modifier.testTag("ai_optimize_bio_btn")
                    ) {
                        if (aiLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Optimizing...", color = Color.White, fontSize = 11.sp)
                        } else {
                            Text("✨ Optimize Bio with AI", color = Color(0xFFD0BCFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = cityInput,
                        onValueChange = { cityInput = it },
                        label = { Text("City", color = Color.White.copy(0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8E2DE2)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_city_input")
                    )

                    OutlinedTextField(
                        value = countryInput,
                        onValueChange = { countryInput = it },
                        label = { Text("Country", color = Color.White.copy(0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF8E2DE2)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_country_input")
                    )
                }

                OutlinedTextField(
                    value = interestsInput,
                    onValueChange = { interestsInput = it },
                    label = { Text("Interests (comma separated)", color = Color.White.copy(0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_interests_input")
                )

                OutlinedTextField(
                    value = languagesInput,
                    onValueChange = { languagesInput = it },
                    label = { Text("Spoken Languages", color = Color.White.copy(0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8E2DE2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_languages_input")
                )
            }
        }
    }
}
