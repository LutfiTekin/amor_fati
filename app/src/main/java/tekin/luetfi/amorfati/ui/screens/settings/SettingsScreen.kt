package tekin.luetfi.amorfati.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tekin.luetfi.amorfati.utils.Defaults
import tekin.luetfi.amorfati.utils.Template


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    var templateDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Session Settings", style = MaterialTheme.typography.titleLarge)
        Text(
            "These settings will not persist. They will reset to default every start.",
            style = MaterialTheme.typography.bodySmall
        )


        // Toggle online image fetching
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Always get the images from the server")
            Switch(
                checked = Defaults.onlineOnly,
                onCheckedChange = { Defaults.onlineOnly = it }
            )
        }

        // Toggle test‑bucket
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Use Test Bucket")
            Switch(
                checked = Defaults.useTestBucket,
                onCheckedChange = { Defaults.useTestBucket = it }
            )
        }

        // Toggle sending email
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Email")
            Switch(
                checked = Defaults.sendEmail,
                onCheckedChange = { Defaults.sendEmail = it }
            )
        }

        //Toggle sending CC
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send CC")
            Switch(
                checked = Defaults.shouldSendCC,
                onCheckedChange = { Defaults.shouldSendCC = it },
                enabled = Defaults.sendEmail
            )
        }

        // Email Template selector
        ExposedDropdownMenuBox(
            expanded = templateDropdownExpanded,
            onExpandedChange = { templateDropdownExpanded = !templateDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = Defaults.selectedTemplate.name,
                onValueChange = { },
                readOnly = true,
                label = { Text("Email Template") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(templateDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true) // anchors the dropdown
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = templateDropdownExpanded,
                onDismissRequest = { templateDropdownExpanded = false }
            ) {
                Template.list.forEach { template ->
                    DropdownMenuItem(
                        text = { Text(template.name) },
                        onClick = {
                            Defaults.selectedTemplate = template
                            templateDropdownExpanded = false
                        }
                    )
                }
            }
        }

        //Download new cards button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download New Cards", style = MaterialTheme.typography.bodyLarge)
            Button(
                onClick = {
                    viewModel.downloadNewCards {
                        Toast
                            .makeText(context, "New cards downloaded!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "Download new cards"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download")
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onDone,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Done")
        }


    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(onDone = {})
}
