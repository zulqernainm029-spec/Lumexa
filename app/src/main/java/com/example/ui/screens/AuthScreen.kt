package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.AuthUiState
import com.example.ui.ConnectLiveViewModel
import com.example.ui.components.CosmosBackgroundBrush
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PremiumGradientBrush

@Composable
fun GoogleGLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.White)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = Color(0xFF4285F4)
        )
    }
}

@Composable
fun OtpDigitInputRow(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    digitCount: Int = 6
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 0 until digitCount) {
            val char = if (i < otpValue.length) otpValue[i].toString() else ""
            val isFocused = i == otpValue.length || (i == digitCount - 1 && otpValue.length == digitCount)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (char.isNotEmpty()) Color(0xFF8E2DE2).copy(0.2f) else Color.White.copy(0.06f))
                    .border(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = if (isFocused) Color(0xFF00E5FF) else if (char.isNotEmpty()) Color(0xFF8E2DE2) else Color.White.copy(0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: ConnectLiveViewModel) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val isGoogleInProg by viewModel.isGoogleSignInInProgress.collectAsState()
    val googleError by viewModel.googleSignInError.collectAsState()
    val authError by viewModel.authErrorMessage.collectAsState()
    val verificationBanner by viewModel.verificationNoticeBanner.collectAsState()
    val resendTimer by viewModel.resendTimerSeconds.collectAsState()
    val activeCode by viewModel.activeVerificationCode.collectAsState()

    var selectedMethod by remember { mutableStateOf("google") } // google, email, phone, guest
    var showGooglePickerSheet by remember { mutableStateOf(false) }
    var isEmailSignUp by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Inputs
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+1") }
    var otpInput by remember { mutableStateOf("") }
    var customGoogleEmail by remember { mutableStateOf("") }

    // Forgot Password Inputs
    var forgotEmailInput by remember { mutableStateOf("") }
    var resetCodeInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // App Identity Logo
            Image(
                painter = painterResource(id = R.drawable.connectlive_logo_1784430926307),
                contentDescription = "Lumexa Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Lumexa",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Premium Verified Social Video Chat",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Notice Banner (Showing Sent OTP / Verification Code)
            if (verificationBanner != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF00E676).copy(0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("verification_notice_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = verificationBanner ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Error Alert Banner
            if (authError != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFF1744).copy(0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF1744).copy(0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("auth_error_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = authError ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearAuthError() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(0.7f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Form container
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (authState) {
                        is AuthUiState.Loading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF00E5FF),
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isGoogleInProg) "Verifying Google OAuth 2.0 Identity..." else "Securing session & validating credentials...",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        is AuthUiState.EmailVerificationRequired -> {
                            val state = authState as AuthUiState.EmailVerificationRequired
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.MarkEmailRead,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Verify Email Address",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Verification code sent to ${state.email}",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Code Hint Card
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF8E2DE2).copy(0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8E2DE2).copy(0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Verification Code: ${state.generatedCode}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = { otpInput = state.generatedCode },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Text("Auto-fill", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                OtpDigitInputRow(
                                    otpValue = otpInput,
                                    onOtpChange = { if (it.length <= 6) otpInput = it }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = { if (it.length <= 6) otpInput = it },
                                    label = { Text("Or Type 6-Digit Code", color = Color.White.copy(0.7f)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(0.7f)) },
                                    trailingIcon = {
                                        if (otpInput.length == 6) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676))
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF00E5FF),
                                        unfocusedBorderColor = Color.White.copy(0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("email_otp_input")
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        if (otpInput.isBlank()) {
                                            viewModel.setAuthError("Please enter the 6-digit verification code.")
                                        } else {
                                            viewModel.verifyEmailCode(state.email, state.displayName, otpInput.trim())
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(PremiumGradientBrush, RoundedCornerShape(25.dp))
                                        .testTag("verify_email_code_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify & Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            if (resendTimer <= 0) {
                                                viewModel.resendVerificationCode()
                                            }
                                        },
                                        enabled = resendTimer <= 0,
                                        modifier = Modifier.testTag("resend_email_code_button")
                                    ) {
                                        Text(
                                            text = if (resendTimer > 0) "Resend code in ${resendTimer}s" else "Resend Verification Code",
                                            color = if (resendTimer <= 0) Color(0xFF00E5FF) else Color.White.copy(0.5f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    TextButton(
                                        onClick = { viewModel.clearAuthError() }
                                    ) {
                                        Text("Change Email", color = Color.White.copy(0.6f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        is AuthUiState.OtpVerificationRequired -> {
                            val state = authState as AuthUiState.OtpVerificationRequired
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Verify Phone SMS",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Enter 6-digit SMS code sent to ${state.phoneNumber}",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF8E2DE2).copy(0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8E2DE2).copy(0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "SMS Code: ${state.generatedCode}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = { otpInput = state.generatedCode },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Text("Auto-fill", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                OtpDigitInputRow(
                                    otpValue = otpInput,
                                    onOtpChange = { if (it.length <= 6) otpInput = it }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = { if (it.length <= 6) otpInput = it },
                                    label = { Text("Verification Code", color = Color.White.copy(0.7f)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(0.7f)) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF8E2DE2),
                                        unfocusedBorderColor = Color.White.copy(0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("otp_input")
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        if (otpInput.isBlank()) {
                                            viewModel.setAuthError("Please enter the 6-digit SMS verification code.")
                                        } else {
                                            viewModel.verifyOtp(otpInput.trim())
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(PremiumGradientBrush, RoundedCornerShape(25.dp))
                                        .testTag("verify_otp_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify & Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                TextButton(
                                    onClick = {
                                        if (resendTimer <= 0) {
                                            viewModel.resendVerificationCode()
                                        }
                                    },
                                    enabled = resendTimer <= 0
                                ) {
                                    Text(
                                        text = if (resendTimer > 0) "Resend SMS in ${resendTimer}s" else "Resend SMS Code",
                                        color = if (resendTimer <= 0) Color(0xFF00E5FF) else Color.White.copy(0.5f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        is AuthUiState.PasswordResetVerification -> {
                            val state = authState as AuthUiState.PasswordResetVerification
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Reset Password", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Enter reset code sent to ${state.email}", color = Color.White.copy(0.6f), fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(12.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFB300).copy(0.15f)
                                ) {
                                    Text(
                                        text = "Reset Code: ${state.generatedCode}",
                                        color = Color(0xFFFFB300),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = resetCodeInput,
                                    onValueChange = { resetCodeInput = it },
                                    label = { Text("6-Digit Reset Code", color = Color.White.copy(0.7f)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFB300),
                                        unfocusedBorderColor = Color.White.copy(0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = newPasswordInput,
                                    onValueChange = { newPasswordInput = it },
                                    label = { Text("New Password (min 6 chars)", color = Color.White.copy(0.7f)) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFB300),
                                        unfocusedBorderColor = Color.White.copy(0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        if (resetCodeInput.isBlank()) {
                                            viewModel.setAuthError("Please enter the 6-digit reset code.")
                                        } else if (newPasswordInput.isBlank() || newPasswordInput.length < 6) {
                                            viewModel.setAuthError("Please enter a new password (minimum 6 characters).")
                                        } else {
                                            viewModel.resetPasswordWithCode(state.email, resetCodeInput.trim(), newPasswordInput.trim())
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text("Set New Password", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        else -> {
                            // Selector tabs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(
                                    "google" to "Google",
                                    "email" to "Email",
                                    "phone" to "Phone",
                                    "guest" to "Device"
                                ).forEach { (id, label) ->
                                    val isSelected = selectedMethod == id
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                if (isSelected) {
                                                    if (id == "google") Color(0xFF4285F4) else Color(0xFF8E2DE2)
                                                } else Color.Transparent
                                            )
                                            .clickable {
                                                selectedMethod = id
                                                viewModel.clearAuthError()
                                            }
                                            .testTag("auth_tab_$id"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (id == "google" && !isSelected) {
                                                GoogleGLogo(modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = label,
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            when (selectedMethod) {
                                "google" -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF4285F4).copy(0.12f))
                                                .border(1.dp, Color(0xFF4285F4).copy(0.3f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VerifiedUser,
                                                contentDescription = null,
                                                tint = Color(0xFF4285F4),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Google Identity One-Tap OAuth 2.0 System",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Official Google Sign-In Button Component
                                        Button(
                                            onClick = {
                                                showGooglePickerSheet = true
                                            },
                                            shape = RoundedCornerShape(24.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.White,
                                                contentColor = Color(0xFF1F1F1F)
                                            ),
                                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                                .testTag("google_signin_primary")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                GoogleGLogo(modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "Sign in with Google",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1F1F1F)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "Verified 1-tap sign in. Auto-populates profile & security badge.",
                                            color = Color.White.copy(0.5f),
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        HorizontalDivider(color = Color.White.copy(0.08f))

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "Select Google Account:",
                                            color = Color.White.copy(0.7f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.align(Alignment.Start)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            listOf(
                                                Triple("zulqernainm029@gmail.com", "Zulqernain (User)", "https://i.pravatar.cc/150?img=12"),
                                                Triple("danishla7860786@gmail.com", "Danish (Platform Owner)", "https://i.pravatar.cc/150?img=33")
                                            ).forEach { (gEmail, gName, gPic) ->
                                                Surface(
                                                    onClick = {
                                                        viewModel.loginWithGoogle(gEmail, gName, gPic)
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color.White.copy(0.06f),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.12f)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .testTag("select_google_${gEmail.substringBefore("@")}")
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(10.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        AsyncImage(
                                                            model = gPic,
                                                            contentDescription = null,
                                                            modifier = Modifier
                                                                .size(36.dp)
                                                                .clip(CircleShape),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Text(gName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF4285F4), modifier = Modifier.size(14.dp))
                                                            }
                                                            Text(gEmail, color = Color.White.copy(0.6f), fontSize = 11.sp)
                                                        }
                                                        Icon(
                                                            Icons.Default.ChevronRight,
                                                            contentDescription = null,
                                                            tint = Color(0xFF4285F4)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                "email" -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        // Mode switch: Sign In vs Sign Up
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(Color.White.copy(0.06f)),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .clip(RoundedCornerShape(18.dp))
                                                    .background(if (!isEmailSignUp) Color(0xFF8E2DE2) else Color.Transparent)
                                                    .clickable { isEmailSignUp = false }
                                                    .testTag("email_mode_signin"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .clip(RoundedCornerShape(18.dp))
                                                    .background(if (isEmailSignUp) Color(0xFF8E2DE2) else Color.Transparent)
                                                    .clickable { isEmailSignUp = true }
                                                    .testTag("email_mode_signup"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("Create Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        if (isEmailSignUp) {
                                            OutlinedTextField(
                                                value = nameInput,
                                                onValueChange = { nameInput = it },
                                                label = { Text("Display Name", color = Color.White.copy(0.7f)) },
                                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(0.7f)) },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF8E2DE2),
                                                    unfocusedBorderColor = Color.White.copy(0.2f),
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("name_input")
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))
                                        }

                                        OutlinedTextField(
                                            value = emailInput,
                                            onValueChange = {
                                                emailInput = it
                                                viewModel.clearAuthError()
                                            },
                                            label = { Text("Email Address", color = Color.White.copy(0.7f)) },
                                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White.copy(0.7f)) },
                                            trailingIcon = {
                                                if (emailInput.contains("@") && emailInput.contains(".")) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "Valid Email", tint = Color(0xFF00E676))
                                                }
                                            },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF8E2DE2),
                                                unfocusedBorderColor = Color.White.copy(0.2f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("email_input")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                            value = passwordInput,
                                            onValueChange = {
                                                passwordInput = it
                                                viewModel.clearAuthError()
                                            },
                                            label = { Text("Password", color = Color.White.copy(0.7f)) },
                                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(0.7f)) },
                                            visualTransformation = PasswordVisualTransformation(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF8E2DE2),
                                                unfocusedBorderColor = Color.White.copy(0.2f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("password_input")
                                        )

                                        if (isEmailSignUp && passwordInput.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            val passLength = passwordInput.length
                                            val strengthText = when {
                                                passLength >= 10 -> "Strong Password"
                                                passLength >= 6 -> "Medium Strength"
                                                else -> "Weak (min 6 chars)"
                                            }
                                            val strengthColor = when {
                                                passLength >= 10 -> Color(0xFF00E676)
                                                passLength >= 6 -> Color(0xFFFFB300)
                                                else -> Color(0xFFFF5252)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                LinearProgressIndicator(
                                                    progress = { (passLength / 12f).coerceIn(0.1f, 1f) },
                                                    color = strengthColor,
                                                    trackColor = Color.White.copy(0.1f),
                                                    modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(strengthText, color = strengthColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        if (!isEmailSignUp) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(
                                                    onClick = {
                                                        forgotEmailInput = emailInput.ifBlank { "alex.mercer@lumexa.net" }
                                                        showForgotPasswordDialog = true
                                                    }
                                                ) {
                                                    Text("Forgot Password?", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }

                                        Button(
                                            onClick = {
                                                if (isEmailSignUp && nameInput.isBlank()) {
                                                    viewModel.setAuthError("Please enter your display name.")
                                                } else if (emailInput.isBlank() || !emailInput.contains("@") || !emailInput.contains(".")) {
                                                    viewModel.setAuthError("Please enter a valid email address.")
                                                } else if (passwordInput.isBlank() || passwordInput.length < 6) {
                                                    viewModel.setAuthError("Please enter a password with at least 6 characters.")
                                                } else {
                                                    viewModel.loginWithEmail(
                                                        email = emailInput.trim(),
                                                        name = nameInput.trim(),
                                                        password = passwordInput,
                                                        isSignUp = isEmailSignUp,
                                                        requireVerification = true
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .background(PremiumGradientBrush, RoundedCornerShape(25.dp))
                                                .testTag("email_login_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                        ) {
                                            Icon(
                                                if (isEmailSignUp) Icons.Default.Send else Icons.Default.Login,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isEmailSignUp) "Register & Send Verification OTP" else "Sign In & Send OTP",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }

                                "phone" -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Select Country Code:",
                                            color = Color.White.copy(0.7f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            listOf("+1", "+92", "+44", "+91", "+971").forEach { code ->
                                                val isSel = selectedCountryCode == code
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(36.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(if (isSel) Color(0xFF8E2DE2) else Color.White.copy(0.06f))
                                                        .clickable { selectedCountryCode = code },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(code, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                            value = phoneInput,
                                            onValueChange = {
                                                phoneInput = it
                                                viewModel.clearAuthError()
                                            },
                                            label = { Text("Phone Number", color = Color.White.copy(0.7f)) },
                                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White.copy(0.7f)) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF8E2DE2),
                                                unfocusedBorderColor = Color.White.copy(0.2f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            placeholder = { Text("555 019 2834", color = Color.White.copy(0.3f)) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("phone_input")
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Button(
                                            onClick = {
                                                if (phoneInput.isBlank() || phoneInput.trim().length < 7) {
                                                    viewModel.setAuthError("Please enter a valid phone number.")
                                                } else {
                                                    viewModel.loginWithPhone("$selectedCountryCode ${phoneInput.trim()}")
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .background(PremiumGradientBrush, RoundedCornerShape(25.dp))
                                                .testTag("send_otp_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                        ) {
                                            Icon(Icons.Default.Sms, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Send SMS Verification Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }
                                }

                                "guest" -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "One-Tap Encrypted Device Login",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Instant hardware enclave guest login. You can link your Google or Email account anytime in settings for full verification.",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))

                                        Button(
                                            onClick = { viewModel.guestLogin() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .background(PremiumGradientBrush, RoundedCornerShape(25.dp))
                                                .testTag("device_login_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                        ) {
                                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Authenticate & Enter", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Direct Google Sign In Prominent Bar
            Button(
                onClick = {
                    viewModel.launchCredentialManagerGoogleSignIn(context)
                },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_login")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GoogleGLogo(modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sign in with Google",
                        color = Color(0xFF1F1F1F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(16.dp))
                }
            }
        }
    }

    // Google Account Picker Dialog
    if (showGooglePickerSheet) {
        AlertDialog(
            onDismissRequest = { showGooglePickerSheet = false },
            icon = { GoogleGLogo(modifier = Modifier.size(36.dp)) },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Choose a Google Account", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Text("to continue to Lumexa", color = Color.White.copy(0.6f), fontSize = 13.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    listOf(
                        Triple("zulqernainm029@gmail.com", "Zulqernain", "https://i.pravatar.cc/150?img=12"),
                        Triple("danishla7860786@gmail.com", "Danish (Platform Owner)", "https://i.pravatar.cc/150?img=33"),
                        Triple("alex.mercer.google@gmail.com", "Alex Mercer", "https://i.pravatar.cc/150?img=60")
                    ).forEach { (accEmail, accName, accPic) ->
                        Surface(
                            onClick = {
                                showGooglePickerSheet = false
                                viewModel.loginWithGoogle(accEmail, accName, accPic)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(0.08f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("dialog_google_$accEmail")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = accPic,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(accName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(accEmail, color = Color.White.copy(0.6f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Or sign in with custom Google Email:", color = Color.White.copy(0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customGoogleEmail,
                        onValueChange = { customGoogleEmail = it },
                        placeholder = { Text("your.email@gmail.com", color = Color.White.copy(0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4285F4),
                            unfocusedBorderColor = Color.White.copy(0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_google_email_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val e = customGoogleEmail.ifBlank { "zulqernainm029@gmail.com" }
                        val name = e.substringBefore("@").replaceFirstChar { it.uppercase() }
                        showGooglePickerSheet = false
                        viewModel.loginWithGoogle(e, name)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                ) {
                    Text("Sign in with Google", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGooglePickerSheet = false }) {
                    Text("Cancel", color = Color.White.copy(0.6f))
                }
            },
            containerColor = Color(0xFF18102B),
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Forgot Password Trigger Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            icon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(36.dp)) },
            title = { Text("Forgot Password", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp) },
            text = {
                Column {
                    Text("Enter your email address below to receive a 6-digit password reset verification code.", color = Color.White.copy(0.7f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        label = { Text("Email Address", color = Color.White.copy(0.7f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color.White.copy(0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("forgot_email_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotPasswordDialog = false
                        viewModel.forgotPassword(forgotEmailInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Send Reset Code", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = Color.White.copy(0.6f))
                }
            },
            containerColor = Color(0xFF18102B),
            shape = RoundedCornerShape(20.dp)
        )
    }
}
