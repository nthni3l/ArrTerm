package com.arrterm.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.arrterm.data.settings.ServerConfig
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.GlassButton
import com.arrterm.ui.common.GlassCard
import com.arrterm.ui.theme.BubbleError
import com.arrterm.ui.theme.BubbleSuccess
import com.arrterm.ui.theme.SkyBlueDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val configs by viewModel.configs.collectAsState()
    val testStates by viewModel.testStates.collectAsState()
    val testMessages by viewModel.testMessages.collectAsState()

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(ServiceType.entries) { service ->
                ServiceConfigSection(
                    service = service,
                    config = configs[service] ?: ServerConfig(),
                    testState = testStates[service] ?: TestState.IDLE,
                    testMessage = testMessages[service],
                    onSave = { viewModel.save(service, it) },
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

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(service.displayName, style = MaterialTheme.typography.titleMedium, color = SkyBlueDeep)

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Server URL (e.g. http://192.168.1.50:7878)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                singleLine = true,
                visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { keyVisible = !keyVisible }) {
                        Icon(
                            imageVector = if (keyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle API key visibility",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassButton(text = "Save", onClick = { onSave(ServerConfig(url, apiKey)) })
                GlassButton(
                    text = "Test Connection",
                    onClick = { onTest(ServerConfig(url, apiKey)) },
                    tint = SkyBlueDeep,
                )
                if (testState == TestState.TESTING) {
                    CircularProgressIndicator(modifier = Modifier.padding(start = 4.dp))
                }
            }

            if (testMessage != null) {
                val color = if (testState == TestState.SUCCESS) BubbleSuccess else BubbleError
                Text(text = testMessage, color = color, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
