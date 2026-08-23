package nl.petervanmanen.minimalauncher.ui.dock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import nl.petervanmanen.minimalauncher.data.model.DockApp
import nl.petervanmanen.minimalauncher.data.model.DockPage
import nl.petervanmanen.minimalauncher.ui.components.PageIndicatorDots
import nl.petervanmanen.minimalauncher.ui.settings.SettingsScreen
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureBlack
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

@Composable
fun DockEditOverlay(viewModel: DockViewModel, currentPageIndex: Int) {
    val dockConfig by viewModel.dockConfig.collectAsState()
    val showPicker by viewModel.showAddAppPicker.collectAsState()
    val showSettings by viewModel.showSettings.collectAsState()
    val allowRotation by viewModel.allowRotation.collectAsState()
    val showWeather by viewModel.showWeather.collectAsState()
    val showMap by viewModel.showMap.collectAsState()
    val showDate by viewModel.showDate.collectAsState()
    val hideStatusBar by viewModel.hideStatusBar.collectAsState()
    val notificationBubbleEnabled by viewModel.notificationBubbleEnabled.collectAsState()
    val notificationBubbleColor by viewModel.notificationBubbleColor.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val mapsAppPackage by viewModel.mapsAppPackage.collectAsState()
    val weatherAppPackage by viewModel.weatherAppPackage.collectAsState()
    val dateAppPackage by viewModel.dateAppPackage.collectAsState()
    val page = dockConfig.pages.getOrElse(currentPageIndex) { DockPage() }

    Dialog(
        onDismissRequest = { viewModel.exitEditMode() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        if (showSettings) {
            Surface(color = PureBlack, modifier = Modifier.fillMaxSize()) {
                SettingsScreen(
                    allowRotation = allowRotation,
                    onAllowRotationChange = { viewModel.setAllowRotation(it) },
                    showWeather = showWeather,
                    onShowWeatherChange = { viewModel.setShowWeather(it) },
                    showMap = showMap,
                    onShowMapChange = { viewModel.setShowMap(it) },
                    showDate = showDate,
                    onShowDateChange = { viewModel.setShowDate(it) },
                    hideStatusBar = hideStatusBar,
                    onHideStatusBarChange = { viewModel.setHideStatusBar(it) },
                    notificationBubbleEnabled = notificationBubbleEnabled,
                    onNotificationBubbleEnabledChange = { viewModel.setNotificationBubbleEnabled(it) },
                    notificationBubbleColor = notificationBubbleColor,
                    onNotificationBubbleColorChange = { viewModel.setNotificationBubbleColor(it) },
                    installedApps = installedApps,
                    mapsAppPackage = mapsAppPackage,
                    onMapsAppChange = { app -> viewModel.setMapsApp(app.packageName) },
                    weatherAppPackage = weatherAppPackage,
                    onWeatherAppChange = { app -> viewModel.setWeatherApp(app.packageName) },
                    dateAppPackage = dateAppPackage,
                    onDateAppChange = { app -> viewModel.setDateApp(app.packageName) },
                    onBack = { viewModel.closeSettings() },
                )
            }
            return@Dialog
        }

        Surface(color = PureBlack, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Edit dock", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Settings",
                            color = DimWhite,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.clickable { viewModel.openSettings() },
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        Text(
                            text = "×",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.clickable { viewModel.exitEditMode() },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                EditableDockAppList(
                    apps = page.apps,
                    onRemove = { packageName -> viewModel.removeApp(packageName) },
                    onRename = { packageName, newName -> viewModel.renameApp(packageName, newName) },
                    onReorder = { from, to -> viewModel.reorderWithinPage(currentPageIndex, from, to) },
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = "+ Add app",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { viewModel.openAddAppPicker() },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "+ New page",
                        color = DimWhite,
                        modifier = Modifier.clickable { viewModel.addPage() },
                    )
                    if (page.apps.isEmpty() && dockConfig.pages.size > 1) {
                        Text(
                            text = "Remove this page",
                            color = DimWhite,
                            modifier = Modifier.clickable { viewModel.removePage(currentPageIndex) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PageIndicatorDots(
                    pageCount = dockConfig.pages.size,
                    currentPage = currentPageIndex,
                    modifier = Modifier.align(Alignment.End),
                )
            }
        }

        if (showPicker) {
            AddAppToDockScreen(
                installedApps = viewModel.installedApps.collectAsState().value,
                alreadyInDock = dockConfig.pages.flatMap { it.apps }.map { it.packageName }.toSet(),
                onAppSelected = { app -> viewModel.addApp(currentPageIndex, app) },
                onBack = { viewModel.closeAddAppPicker() },
            )
        }
    }
}

@Composable
private fun EditableDockAppList(
    apps: List<DockApp>,
    onRemove: (String) -> Unit,
    onRename: (String, String) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rowHeightDp = 48.dp
    val rowHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { rowHeightDp.toPx() }
    var draggedIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(0f) }

    Column(modifier = modifier) {
        apps.forEachIndexed { index, app ->
            val isDragged = index == draggedIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeightDp)
                    .zIndex(if (isDragged) 1f else 0f)
                    .then(
                        if (isDragged) {
                            Modifier.offset { IntOffset(0, dragOffset.roundToInt()) }
                        } else {
                            Modifier
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "−",
                    color = PureWhite,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onRemove(app.packageName) },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )

                Spacer(modifier = Modifier.width(8.dp))

                RenamableAppLabel(
                    displayName = app.displayName,
                    onRename = { newName -> onRename(app.packageName, newName) },
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = "≡",
                    color = DimWhite,
                    modifier = Modifier
                        .size(32.dp)
                        .pointerInput(app.packageName, apps.size) {
                            detectDragGestures(
                                onDragStart = {
                                    draggedIndex = index
                                    dragOffset = 0f
                                },
                                onDragEnd = {
                                    val target = (index + (dragOffset / rowHeightPx).roundToInt())
                                        .coerceIn(0, apps.lastIndex)
                                    if (target != index) onReorder(index, target)
                                    draggedIndex = -1
                                    dragOffset = 0f
                                },
                                onDragCancel = {
                                    draggedIndex = -1
                                    dragOffset = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffset += dragAmount.y
                                },
                            )
                        },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun RenamableAppLabel(
    displayName: String,
    onRename: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRenaming by remember { mutableStateOf(false) }
    var text by remember(displayName) { mutableStateOf(displayName) }
    val focusRequester = remember { FocusRequester() }

    if (isRenaming) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged { if (!it.isFocused) commitRename(text, displayName, onRename).also { isRenaming = false } },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = PureWhite),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = {
                    commitRename(text, displayName, onRename)
                    isRenaming = false
                },
            ),
        )
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    } else {
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.clickable { isRenaming = true },
        )
    }
}

private fun commitRename(newText: String, oldText: String, onRename: (String) -> Unit) {
    val trimmed = newText.trim()
    if (trimmed.isNotEmpty() && trimmed != oldText) onRename(trimmed)
}
