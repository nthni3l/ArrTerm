package com.arrterm.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.arrterm.data.settings.ServerConfig
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.AppCard
import com.arrterm.ui.common.PillButton
import com.arrterm.ui.common.TabTopBar
import com.arrterm.ui.common.ToastBus
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.CardSurface
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.StatusError
import com.arrterm.ui.theme.StatusSuccess
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextPrimary
import com.arrterm.ui.theme.TextSecondary

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val configs by viewModel.configs.collectAsState()
    val testStates by viewModel.testStates.collectAsState()
    val testMessages by viewModel.testMessages.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TabTopBar(title = "Settings", count = "")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(ServiceType.entries) { service ->
                ServiceConfigSection(
                    service = service,
                    config = configs[service] ?: ServerConfig(),
                    testState = testStates[service] ?: TestState.IDLE,
                    testMessage = testMessages[service],
                    onSave = { viewModel.save(service, it); ToastBus.show("Saved") },
                    onTest = { viewModel.testConnection(service, it) },
                )
            }
        }
    }
}

@Composable
private fun ServiceConfigSection(
    service: ServiceType,
    config: ServerConfig,
    testState: TestState,
    testMessage: String?,
    onSave: (ServerConfig) -> Unit,
    onTest: (ServerConfig) -> Unit,
) {
    var url by remember(service, config) { mutableStateOf(config.baseUrl) }
    var apiKey by remember(service, config) { mutableStateOf(config.apiKey) }
    var keyVisible by remember { mutableStateOf(false) }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = service.displayName,
                color = AccentGreen,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            FieldLabel("Server URL")
            MonoTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )

            FieldLabel("API Key")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
            ) {
                MonoTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation('•'),
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                        .clickable { keyVisible = !keyVisible },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(if (keyVisible) AccentGreen else Color.Transparent, CircleShape)
                            .border(2.dp, TextSecondary, CircleShape),
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PillButton(text = "Save", onClick = { onSave(ServerConfig(url, apiKey)) }, verticalPadding = 9.dp, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                PillButton(text = "Test Connection", onClick = { onTest(ServerConfig(url, apiKey)) }, filled = false, verticalPadding = 9.dp, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                if (testState == TestState.TESTING) {
                    Text("…", color = TextMuted, fontFamily = JetBrainsMono, style = MaterialTheme.typography.labelMedium)
                }
            }

            if (testMessage != null && testState != TestState.TESTING) {
                val color = if (testState == TestState.SUCCESS) StatusSuccess else StatusError
                Text(
                    text = testMessage,
                    color = color,
                    fontFamily = JetBrainsMono,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = TextMuted,
        fontFamily = JetBrainsMono,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun MonoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontFamily = JetBrainsMono),
        visualTransformation = visualTransformation,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentGreen),
        modifier = modifier
            .background(CardSurface, RoundedCornerShape(8.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    )
}
