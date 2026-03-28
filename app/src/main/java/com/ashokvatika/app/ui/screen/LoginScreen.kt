package com.ashokvatika.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashokvatika.app.ui.theme.AshokvatikaPalette
import com.ashokvatika.app.ui.theme.AshokvatikaTheme
import com.ashokvatika.app.ui.theme.OrangeLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ValidUsername = "admin"
private const val ValidPassword = "123"

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val adaptive = rememberAdaptiveLayoutInfo()
    val scope = rememberCoroutineScope()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var usernameVisible by rememberSaveable { mutableStateOf(true) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }
    var showForgotDialog by rememberSaveable { mutableStateOf(false) }
    var helperMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AshokvatikaPalette.colors.background)
            .padding(horizontal = adaptive.horizontalPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            LogoCard(isCompact = adaptive.isCompact)
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Ashokvatika",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (adaptive.isCompact) 30.sp else MaterialTheme.typography.headlineLarge.fontSize,
                    letterSpacing = (-0.7).sp
                ),
                color = AshokvatikaPalette.colors.title
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = AshokvatikaPalette.colors.text
            )
            Spacer(modifier = Modifier.height(48.dp))

            FieldLabel("Username")
            Spacer(modifier = Modifier.height(14.dp))
            DesignTextField(
                value = username,
                onValueChange = {
                    username = it
                    helperMessage = null
                },
                placeholder = "Enter your username",
                visible = usernameVisible,
                onToggleVisibility = { usernameVisible = !usernameVisible },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(26.dp))
            FieldLabel("Password")
            Spacer(modifier = Modifier.height(14.dp))
            DesignTextField(
                value = password,
                onValueChange = {
                    password = it
                    helperMessage = null
                },
                placeholder = "Enter your password",
                visible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            AnimatedVisibility(
                visible = helperMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = helperMessage.orEmpty(),
                    color = AshokvatikaPalette.colors.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(38.dp))
            PrimaryActionButton(
                text = if (isSubmitting) "Signing In..." else "Sign In",
                onClick = {
                    when {
                        username.isBlank() && password.isBlank() -> {
                            helperMessage = "Enter username and password."
                        }

                        username.isBlank() -> {
                            helperMessage = "Username is required."
                        }

                        password.isBlank() -> {
                            helperMessage = "Password is required."
                        }

                        username != ValidUsername || password != ValidPassword -> {
                            helperMessage = "Use username admin and password 123."
                        }

                        else -> {
                            helperMessage = "Credentials accepted. Opening inventory..."
                            isSubmitting = true
                            scope.launch {
                                delay(500)
                                isSubmitting = false
                                onLoginSuccess()
                            }
                        }
                    }
                },
                enabled = !isSubmitting,
                isCompact = adaptive.isCompact
            )
            Spacer(modifier = Modifier.height(18.dp))
            SecondaryActionButton(
                text = "Clear",
                onClick = {
                    username = ""
                    password = ""
                    helperMessage = null
                    usernameVisible = true
                    passwordVisible = false
                },
                isCompact = adaptive.isCompact
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Forgot password?",
                color = AshokvatikaPalette.colors.link,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showForgotDialog = true
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            confirmButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Login credentials") },
            text = {
                Text("Username: admin\nPassword: 123")
            }
        )
    }
}

@Composable
private fun LogoCard(isCompact: Boolean) {
    Box(
        modifier = Modifier
            .size(if (isCompact) 132.dp else 158.dp)
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(34.dp),
                ambientColor = AshokvatikaPalette.colors.shadow,
                spotColor = AshokvatikaPalette.colors.shadow
            )
            .clip(RoundedCornerShape(34.dp))
            .background(AshokvatikaPalette.colors.card),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = AshokvatikaPalette.colors.accent,
            modifier = Modifier.size(if (isCompact) 48.dp else 58.dp)
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
        color = AshokvatikaPalette.colors.text,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DesignTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    keyboardOptions: KeyboardOptions
) {
    val transformation = if (visible) VisualTransformation.None else PasswordVisualTransformation()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = AshokvatikaPalette.colors.shadow,
                spotColor = AshokvatikaPalette.colors.shadow
            ),
        placeholder = {
            Text(
                text = placeholder,
                color = AshokvatikaPalette.colors.hint,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
        visualTransformation = transformation,
        keyboardOptions = keyboardOptions,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = AshokvatikaPalette.colors.text,
            fontWeight = FontWeight.Bold
        ),
        trailingIcon = {
            Icon(
                imageVector = if (visible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                contentDescription = "Toggle visibility",
                tint = AshokvatikaPalette.colors.hint,
                modifier = Modifier.clickable { onToggleVisibility() }
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = AshokvatikaPalette.colors.accent,
            focusedTextColor = AshokvatikaPalette.colors.text,
            unfocusedTextColor = AshokvatikaPalette.colors.text
        )
    )
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isCompact: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 68.dp else 102.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = AshokvatikaPalette.colors.shadow,
                spotColor = AshokvatikaPalette.colors.shadow
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(OrangeLight, AshokvatikaPalette.colors.accent)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    isCompact: Boolean
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = AshokvatikaPalette.colors.text
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 62.dp else 94.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = AshokvatikaPalette.colors.shadow,
                spotColor = AshokvatikaPalette.colors.shadow
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    AshokvatikaTheme {
        LoginScreen(onLoginSuccess = {})
    }
}


