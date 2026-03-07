package com.scenariosimulator.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scenariosimulator.domain.model.Message
import com.scenariosimulator.domain.model.MessageType
import com.scenariosimulator.domain.model.Persona

@Composable
fun ChatMessageBubble(
    message: Message,
    persona: Persona?,
    modifier: Modifier = Modifier
) {
    val isSystem = message.messageType == MessageType.SYSTEM
    val isSummary = message.messageType == MessageType.SUMMARY

    val backgroundColor = when {
        isSystem -> MaterialTheme.colorScheme.secondaryContainer
        isSummary -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        isSystem -> MaterialTheme.colorScheme.onSecondaryContainer
        isSummary -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (persona != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(persona.avatarColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column {
            if (persona != null) {
                Text(
                    text = persona.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = persona.avatarColor
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }
}
