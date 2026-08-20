package com.arrterm.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.arrterm.data.settings.ServerConfig
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val configs by viewModel.configs.collectAsState()
    val testStates by viewModel.testStates.collectAsState()
    val testMessages by viewModel.testMessages.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("SETTINGS", fontFamily = FontFamily.Monospace) }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
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

    Column {
        SectionHeader(service.displayName)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onSave(ServerConfig(url, apiKey)) }) {
                    Text("SAVE")
                }
                Button(
                    onClick = { onTest(ServerConfig(url, apiKey)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text("TEST CONNECTION")
                }
                if (testState == TestState.TESTING) {
                    CircularProgressIndicator(modifier = Modifier.padding(start = 4.dp))
                }
            }

            if (testMessage != null) {
                val color = if (testState == TestState.SUCCESS) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
                Text(text = testMessage, color = color, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
