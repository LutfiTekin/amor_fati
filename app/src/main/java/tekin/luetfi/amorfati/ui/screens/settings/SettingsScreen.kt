package tekin.luetfi.amorfati.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tekin.luetfi.amorfati.utils.Defaults


@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onDone: () -> Unit) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Session Settings", style = MaterialTheme.typography.titleLarge)

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
