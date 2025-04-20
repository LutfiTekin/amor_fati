package tekin.luetfi.amorfati.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tekin.luetfi.amorfati.domain.model.TarotCard

@Composable
fun TarotCardItem(card: TarotCard){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { /* no-op */ }
    ) {
        AsyncImage(
            card.imageUrl,
            contentDescription = card.name,
            modifier = Modifier
                .width(80.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Text(
            text = card.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}