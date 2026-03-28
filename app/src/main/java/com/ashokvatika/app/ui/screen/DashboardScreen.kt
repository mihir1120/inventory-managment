
package com.ashokvatika.app.ui.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.ashokvatika.app.model.InventoryRecord
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DashboardBackground = Color(0xFFFFFFFF)
private val DashboardSurface = Color(0xFFF8F8F6)
private val DashboardWhite = Color(0xFFFFFFFF)
private val DashboardOrange = Color(0xFFE38A47)
private val DashboardOrangeDark = Color(0xFFD57834)
private val DashboardText = Color(0xFF1E1E1B)
private val DashboardHint = Color(0xFF9C9A93)
private val DashboardStroke = Color(0xFFEDE8E0)
private val DashboardFont = FontFamily.SansSerif

private enum class InputTarget {
    Title,
    Batch,
    Sale,
    Rent
}

data class DashboardDraft(
    val id: Int = 0,
    val productTitle: String = "Product Name",
    val batch: Int = 0,
    val sale: Int = 0,
    val rent: Int = 0,
    val vendorName: String = "Vendor",
    val vendorPhone: String = "",
    val vendorEmail: String = "",
    val vendorAddress: String = "",
    val vendorWebsite: String = "",
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val photoUri: String? = null,
    val saveStamp: String = "save",
    val saveResultLabel: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onSaveInventory: (InventoryRecord) -> Unit,
    draft: DashboardDraft,
    onDraftChange: (DashboardDraft) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val adaptive = rememberAdaptiveLayoutInfo()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val productTitle = draft.productTitle
    val batch = draft.batch
    val sale = draft.sale
    val rent = draft.rent
    val vendorName = draft.vendorName
    val vendorPhone = draft.vendorPhone
    val vendorEmail = draft.vendorEmail
    val vendorAddress = draft.vendorAddress
    val vendorWebsite = draft.vendorWebsite
    val selectedDateMillis = draft.selectedDateMillis
    val photoUri = draft.photoUri
    val saveStamp = draft.saveStamp
    val saveResultLabel = draft.saveResultLabel

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var inputTarget by remember { mutableStateOf<InputTarget?>(null) }
    var inputBuffer by rememberSaveable { mutableStateOf("") }
    var inputInitialValue by rememberSaveable { mutableStateOf("") }
    var showVendorDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var cameraDenied by remember { mutableStateOf(false) }
    var showImageActions by rememberSaveable(photoUri) { mutableStateOf(photoUri == null) }

    val inStock = (batch - sale - rent).coerceAtLeast(0)
    val hasData = batch != 0 || sale != 0 || rent != 0
    val compactHeight = screenHeightDp < 760
    val stockScale by animateFloatAsState(
        targetValue = if (hasData) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 430f),
        label = "stock-scale"
    )
    val tileHeight by animateDpAsState(
        targetValue = when {
            adaptive.isCompact && compactHeight && hasData -> 66.dp
            adaptive.isCompact && compactHeight -> 76.dp
            adaptive.isCompact && hasData -> 76.dp
            adaptive.isCompact -> 88.dp
            hasData -> 92.dp
            else -> 108.dp
        },
        animationSpec = spring(dampingRatio = 0.88f, stiffness = 420f),
        label = "tile-height"
    )

    val imageWeight = when {
        adaptive.isCompact && compactHeight -> 0.9f
        adaptive.isCompact -> 1.15f
        adaptive.isMedium -> 1.25f
        else -> 1.4f
    }
    val contentWeight = when {
        adaptive.isCompact && compactHeight -> 1.35f
        adaptive.isCompact -> 1.2f
        else -> 1.05f
    }

    val tileTitleSize = if (adaptive.isCompact) 13.sp else 16.sp
    val tileValueSize = if (adaptive.isCompact && compactHeight) 24.sp else if (adaptive.isCompact) 28.sp else 34.sp
    val titleSize = if (adaptive.isCompact && compactHeight) 26.sp else if (adaptive.isCompact) 30.sp else 40.sp
    val stockSize = if (adaptive.isCompact && compactHeight) 48.sp else if (adaptive.isCompact) 58.sp else 82.sp
    val supportingSize = if (adaptive.isCompact) 14.sp else 18.sp
    val spacing = if (adaptive.isCompact && compactHeight) 8.dp else if (adaptive.isCompact) 10.dp else 14.dp

    val displayDate = formatShortDate(selectedDateMillis)
    val fullDate = formatFullDate(selectedDateMillis)
    val shareSummary = """
        $productTitle
        In Stock: $inStock
        Batch: $batch
        Sale: $sale
        Rent: $rent
        Date: $fullDate
        Vendor: $vendorName
        Phone: $vendorPhone
        Email: $vendorEmail
        Address: $vendorAddress
        Website: $vendorWebsite
    """.trimIndent()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onDraftChange(draft.copy(photoUri = uri.toString()))
            showImageActions = false
            persistUriPermission(context, uri)
        }
    }
    val driveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            onDraftChange(draft.copy(photoUri = uri.toString()))
            showImageActions = false
            persistUriPermission(context, uri)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            onDraftChange(draft.copy(photoUri = pendingCameraUri?.toString()))
            showImageActions = false
        }
        pendingCameraUri = null
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val output = createPersistentImageUri(context)
            pendingCameraUri = output
            cameraLauncher.launch(output)
        } else {
            cameraDenied = true
        }
    }

    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DashboardBackground, Color(0xFFF8F5F0))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = adaptive.horizontalPadding,
                    end = adaptive.horizontalPadding,
                    top = topInset + 12.dp,
                    bottom = bottomInset + 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = if (adaptive.isExpanded) 760.dp else adaptive.contentMaxWidth)
            ) {
                MinimalTopBar(
                    onBack = onBack,
                    onClose = onLogout,
                    isCompact = adaptive.isCompact
                )

                Spacer(modifier = Modifier.height(if (compactHeight) 8.dp else 12.dp))

                ImageSelectorCard(
                    photoUri = photoUri,
                    showActions = showImageActions,
                    modifier = Modifier.fillMaxWidth()
                        .weight(imageWeight),
                    onImageClick = {
                        if (photoUri != null) {
                            showImageActions = !showImageActions
                        }
                    },
                    onCameraClick = {
                        cameraDenied = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val output = createPersistentImageUri(context)
                            pendingCameraUri = output
                            cameraLauncher.launch(output)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onGalleryClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onDriveClick = {
                        driveLauncher.launch(arrayOf("image/*"))
                    }
                )

                Spacer(modifier = Modifier.height(if (compactHeight) 8.dp else 12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .weight(contentWeight),
                    verticalArrangement = Arrangement.spacedBy(if (compactHeight) 12.dp else 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(if (compactHeight) 4.dp else 6.dp)
                    ) {
                        Text(
                            text = productTitle,
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    inputTarget = InputTarget.Title
                                    inputInitialValue = if (productTitle == "Product Name") "" else productTitle
                                inputBuffer = inputInitialValue
                                },
                            textAlign = TextAlign.Center,
                            style = dashboardTextStyle(titleSize, DashboardText, (-1.2).sp, FontWeight.Normal)
                        )


                        Spacer(modifier = Modifier.height(if (compactHeight) 2.dp else 4.dp))

                        Text(
                            text = "in stock",
                            modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
                            style = dashboardTextStyle(supportingSize, DashboardHint, (-0.4).sp, FontWeight.Normal)
                        )

                        Text(
                            text = inStock.toString(),
                            modifier = Modifier.fillMaxWidth()
                                .scale(stockScale),
                            textAlign = TextAlign.Center,
                            style = dashboardTextStyle(stockSize, DashboardText, (-1.6).sp, FontWeight.Normal)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
                        ) {
                            DashboardMetricTile("batch", batch.toString(), tileHeight, tileTitleSize, tileValueSize, Modifier.weight(1f)) {
                                inputTarget = InputTarget.Batch
                                inputInitialValue = if (batch == 0) "" else batch.toString()
                                inputBuffer = inputInitialValue
                            }
                            DashboardMetricTile("sale", sale.toString(), tileHeight, tileTitleSize, tileValueSize, Modifier.weight(1f)) {
                                inputTarget = InputTarget.Sale
                                inputInitialValue = if (sale == 0) "" else sale.toString()
                                inputBuffer = inputInitialValue
                            }
                            DashboardMetricTile("rent", rent.toString(), tileHeight, tileTitleSize, tileValueSize, Modifier.weight(1f)) {
                                inputTarget = InputTarget.Rent
                                inputInitialValue = if (rent == 0) "" else rent.toString()
                                inputBuffer = inputInitialValue
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
                        ) {
                            DashboardMetricTile("date", displayDate, tileHeight, tileTitleSize, if (adaptive.isCompact && compactHeight) 18.sp else if (adaptive.isCompact) 22.sp else 28.sp, Modifier.weight(1f)) {
                                showDatePicker = true
                            }
                            DashboardMetricTile("vendor", vendorName, tileHeight, tileTitleSize, if (adaptive.isCompact && compactHeight) 18.sp else if (adaptive.isCompact) 20.sp else 26.sp, Modifier.weight(1f)) {
                                showVendorDialog = true
                            }
                            DashboardMetricTile("share", "send", tileHeight, tileTitleSize, if (adaptive.isCompact && compactHeight) 18.sp else if (adaptive.isCompact) 20.sp else 26.sp, Modifier.weight(1f)) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareSummary)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Inventory"))
                            }
                        }

                        SaveButton(
                            value = saveResultLabel ?: saveStamp,
                            isCompact = adaptive.isCompact || compactHeight
                        ) {
                            val trimmedTitle = productTitle.trim().ifBlank { "Product Name" }
                            val savedAt = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                            onSaveInventory(
                                InventoryRecord(
                                    id = draft.id,
                                    title = trimmedTitle,
                                    vendor = vendorName.trim(),
                                    vendorPhone = vendorPhone.trim(),
                                    vendorEmail = vendorEmail.trim(),
                                    vendorAddress = vendorAddress.trim(),
                                    vendorWebsite = vendorWebsite.trim(),
                                    date = formatStorageDate(selectedDateMillis),
                                    photoUri = photoUri,
                                    batch = batch,
                                    sale = sale,
                                    rent = rent,
                                    qtn = 0
                                )
                            )
                            onDraftChange(
                                draft.copy(
                                    productTitle = trimmedTitle,
                                    saveStamp = savedAt,
                                    saveResultLabel = "saved $savedAt"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    if (inputTarget != null) {
        DashboardInputDialog(
            title = when (inputTarget) {
                InputTarget.Title -> "Product Name"
                InputTarget.Batch -> "Batch Quantity"
                InputTarget.Sale -> "Sale Quantity"
                InputTarget.Rent -> "Rent Quantity"
                null -> ""
            },
            value = inputBuffer,
            numericOnly = inputTarget != InputTarget.Title,
            showUndo = inputTarget == InputTarget.Sale || inputTarget == InputTarget.Rent,
            onValueChange = { inputBuffer = it },
            onUndo = { inputBuffer = inputInitialValue },
            onDismiss = { inputTarget = null },
            onConfirm = {
                when (inputTarget) {
                    InputTarget.Title -> onDraftChange(draft.copy(productTitle = inputBuffer.trim().ifBlank { productTitle }))
                    InputTarget.Batch -> onDraftChange(draft.copy(batch = inputBuffer.toIntOrNull() ?: 0))
                    InputTarget.Sale -> onDraftChange(draft.copy(sale = inputBuffer.toIntOrNull() ?: 0))
                    InputTarget.Rent -> onDraftChange(draft.copy(rent = inputBuffer.toIntOrNull() ?: 0))
                    null -> Unit
                }
                inputTarget = null
            }
        )
    }

    if (showVendorDialog) {
        var vendorNameInput by remember { mutableStateOf(vendorName.takeUnless { it == "Vendor" }.orEmpty()) }
        var vendorPhoneInput by remember { mutableStateOf(vendorPhone) }
        var vendorEmailInput by remember { mutableStateOf(vendorEmail) }
        var vendorAddressInput by remember { mutableStateOf(vendorAddress) }
        var vendorWebsiteInput by remember { mutableStateOf(vendorWebsite) }

        AlertDialog(
            onDismissRequest = { showVendorDialog = false },
            title = {
                Text(
                    text = "Vendor Details",
                    style = dashboardTextStyle(24.sp, DashboardText, (-0.8).sp)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    LargeInputField(vendorNameInput, { vendorNameInput = it }, "Vendor name")
                    LargeInputField(vendorPhoneInput, { vendorPhoneInput = it.filter { ch -> ch.isDigit() || ch == '+' || ch == ' ' } }, "Phone", keyboardType = KeyboardType.Phone)
                    LargeInputField(vendorEmailInput, { vendorEmailInput = it }, "Email")
                    LargeInputField(vendorAddressInput, { vendorAddressInput = it }, "Address")
                    LargeInputField(vendorWebsiteInput, { vendorWebsiteInput = it }, "Website")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDraftChange(
                            draft.copy(
                                vendorName = vendorNameInput.trim().ifBlank { "Vendor" },
                                vendorPhone = vendorPhoneInput.trim(),
                                vendorEmail = vendorEmailInput.trim(),
                                vendorAddress = vendorAddressInput.trim(),
                                vendorWebsite = vendorWebsiteInput.trim()
                            )
                        )
                        showVendorDialog = false
                    }
                ) {
                    Text("Apply", style = dashboardTextStyle(18.sp, DashboardOrangeDark, (-0.5).sp))
                }
            },
            dismissButton = {
                TextButton(onClick = { showVendorDialog = false }) {
                    Text("Cancel", style = dashboardTextStyle(18.sp, DashboardHint, (-0.5).sp))
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = DashboardWhite
        )
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onDraftChange(draft.copy(selectedDateMillis = it))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirm Date", style = dashboardTextStyle(18.sp, DashboardOrangeDark, (-0.4).sp))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", style = dashboardTextStyle(18.sp, DashboardHint, (-0.4).sp))
                }
            },
            shape = RoundedCornerShape(34.dp)
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(horizontal = 8.dp),
                title = null,
                headline = null,
                showModeToggle = false
            )
        }
    }

    if (cameraDenied) {
        AlertDialog(
            onDismissRequest = { cameraDenied = false },
            title = {
                Text("Camera Permission", style = dashboardTextStyle(22.sp, DashboardText, (-0.7).sp))
            },
            text = {
                Text(
                    text = "Camera access is required to capture a product image.",
                    style = dashboardTextStyle(18.sp, DashboardHint, (-0.4).sp)
                )
            },
            confirmButton = {
                TextButton(onClick = { cameraDenied = false }) {
                    Text("Okay", style = dashboardTextStyle(18.sp, DashboardOrangeDark, (-0.4).sp))
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = DashboardWhite
        )
    }
}

@Composable
private fun MinimalTopBar(
    onBack: (() -> Unit)?,
    onClose: () -> Unit,
    isCompact: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (onBack != null) "Back" else "",
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable(enabled = onBack != null) { onBack?.invoke() }
                .padding(horizontal = 4.dp, vertical = 8.dp),
            style = dashboardTextStyle(if (isCompact) 16.sp else 18.sp, DashboardHint, (-0.5).sp)
        )
        Text(
            text = "Ashokvatika",
            style = dashboardTextStyle(if (isCompact) 32.sp else 36.sp, DashboardOrangeDark, (-1.2).sp, FontWeight.Bold)
        )
        Text(
            text = "Close",
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable { onClose() }
                .padding(horizontal = 4.dp, vertical = 8.dp),
            style = dashboardTextStyle(if (isCompact) 16.sp else 18.sp, DashboardHint, (-0.5).sp)
        )
    }
}

@Composable
private fun ImageSelectorCard(
    photoUri: String?,
    showActions: Boolean,
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDriveClick: () -> Unit
) {
    Column(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(34.dp), ambientColor = Color(0x10C66B2D), spotColor = Color(0x10C66B2D))
            .clip(RoundedCornerShape(34.dp))
            .background(DashboardSurface)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .clickable(enabled = photoUri != null) { onImageClick() }
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF7F5F0), Color(0xFFEFE7DB))
                    )
                )
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Product image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Product Image",
                        textAlign = TextAlign.Center,
                        style = dashboardTextStyle(24.sp, DashboardText, (-1.0).sp, FontWeight.Normal)
                    )
                    Text(
                        text = "Open camera, gallery or Google Drive directly below",
                        textAlign = TextAlign.Center,
                        style = dashboardTextStyle(14.sp, DashboardHint, (-0.2).sp)
                    )
                }
            }
        }

        if (showActions) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(DashboardWhite)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageActionButton("camera", Modifier.weight(1f), onCameraClick)
                ImageActionButton("gallery", Modifier.weight(1f), onGalleryClick)
                ImageActionButton("google drive", Modifier.weight(1f), onDriveClick)
            }
        }
    }
}

@Composable
private fun ImageActionButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(DashboardWhite)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = dashboardTextStyle(14.sp, DashboardText, (-0.4).sp)
        )
    }
}
@Composable
private fun DashboardMetricTile(
    title: String,
    value: String,
    tileHeight: Dp,
    titleSize: TextUnit,
    valueSize: TextUnit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val glowElevation by animateDpAsState(
        targetValue = if (isHovered) 22.dp else 10.dp,
        animationSpec = spring(dampingRatio = 0.88f, stiffness = 460f),
        label = "tile-glow"
    )
    val tileScale by animateFloatAsState(
        targetValue = if (isHovered) 1.015f else 1f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 520f),
        label = "tile-scale"
    )

    Column(
        modifier = modifier
            .scale(tileScale)
            .height(tileHeight)
            .shadow(
                elevation = glowElevation,
                shape = RoundedCornerShape(28.dp),
                ambientColor = DashboardOrange.copy(alpha = if (isHovered) 0.45f else 0.18f),
                spotColor = DashboardOrange.copy(alpha = if (isHovered) 0.45f else 0.18f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(colors = listOf(DashboardOrange, DashboardOrangeDark)))
            .hoverable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
            style = dashboardTextStyle(titleSize, DashboardWhite, (-0.5).sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value.ifBlank { "0" },
            modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
            style = dashboardTextStyle(valueSize, DashboardWhite, (-1.4).sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SaveButton(
    value: String,
    isCompact: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val glowElevation by animateDpAsState(
        targetValue = if (isHovered) 22.dp else 10.dp,
        animationSpec = spring(dampingRatio = 0.88f, stiffness = 460f),
        label = "save-glow"
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .shadow(
                elevation = glowElevation,
                shape = RoundedCornerShape(28.dp),
                ambientColor = DashboardOrange.copy(alpha = if (isHovered) 0.45f else 0.2f),
                spotColor = DashboardOrange.copy(alpha = if (isHovered) 0.45f else 0.2f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.horizontalGradient(listOf(DashboardOrange, DashboardOrangeDark)))
            .hoverable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 22.dp, vertical = if (isCompact) 16.dp else 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "save",
            style = dashboardTextStyle(if (isCompact) 15.sp else 18.sp, DashboardWhite, (-0.5).sp)
        )
        Text(
            text = value,
            style = dashboardTextStyle(if (isCompact) 22.sp else 28.sp, DashboardWhite, (-1.1).sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DashboardInputDialog(
    title: String,
    value: String,
    numericOnly: Boolean,
    showUndo: Boolean = false,
    onValueChange: (String) -> Unit,
    onUndo: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = dashboardTextStyle(24.sp, DashboardText, (-0.8).sp))
        },
        text = {
            LargeInputField(
                value = value,
                onValueChange = {
                    onValueChange(
                        if (numericOnly) it.filter(Char::isDigit) else it
                    )
                },
                label = title,
                keyboardType = if (numericOnly) KeyboardType.Number else KeyboardType.Text,
                showUndo = showUndo,
                onUndo = onUndo,
                onDone = {
                    onConfirm()
                    keyboardController?.hide()
                }
            )
        },
        confirmButton = if (numericOnly) {
            {}
        } else {
            {
                TextButton(onClick = onConfirm) {
                    Text("Apply", style = dashboardTextStyle(18.sp, DashboardOrangeDark, (-0.5).sp))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = dashboardTextStyle(18.sp, DashboardHint, (-0.5).sp))
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = DashboardWhite
    )
}

@Composable
private fun LargeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    showUndo: Boolean = false,
    onUndo: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        label = {
            Text(text = label, style = dashboardTextStyle(17.sp, DashboardHint, (-0.2).sp))
        },
        trailingIcon = if (showUndo && onUndo != null) {
            {
                IconButton(onClick = onUndo) {
                    Icon(
                        imageVector = Icons.Outlined.Undo,
                        contentDescription = "Undo",
                        tint = DashboardHint
                    )
                }
            }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone?.invoke() }
        ),
        textStyle = dashboardTextStyle(28.sp, DashboardText, (-1.0).sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DashboardOrangeDark,
            unfocusedBorderColor = DashboardStroke,
            focusedContainerColor = DashboardWhite,
            unfocusedContainerColor = DashboardWhite
        ),
        shape = RoundedCornerShape(22.dp)
    )
}

private fun dashboardTextStyle(
    size: TextUnit,
    color: Color,
    letterSpacing: TextUnit,
    fontWeight: FontWeight = FontWeight.Thin
) = TextStyle(
    fontFamily = DashboardFont,
    fontWeight = fontWeight,
    fontSize = size,
    letterSpacing = letterSpacing,
    color = color
)

private fun formatShortDate(timeMillis: Long): String {
    return SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timeMillis))
}

private fun formatFullDate(timeMillis: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timeMillis))
}

private fun formatStorageDate(timeMillis: Long): String {
    return SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Date(timeMillis))
}

private fun persistUriPermission(context: Context, uri: Uri) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

private fun createPersistentImageUri(context: Context): Uri {
    val imageDirectory = File(context.filesDir, "plant_images").apply {
        if (!exists()) mkdirs()
    }
    val imageFile = File.createTempFile("dashboard_", ".jpg", imageDirectory)
    return androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}


















