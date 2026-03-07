package com.scenariosimulator.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scenariosimulator.domain.model.SummaryResult

@Composable
fun SummaryCard(
    summary: SummaryResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Key Takeaways", style = MaterialTheme.typography.titleMedium)
            summary.keyTakeaways.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Major Disagreements", style = MaterialTheme.typography.titleMedium)
            summary.majorDisagreements.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Strongest Ideas", style = MaterialTheme.typography.titleMedium)
            summary.strongestIdeas.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Action Plan", style = MaterialTheme.typography.titleMedium)
            summary.actionPlan.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Consensus", style = MaterialTheme.typography.titleMedium)
            Text(summary.consensus, style = MaterialTheme.typography.bodyMedium)
            Text("Open Questions", style = MaterialTheme.typography.titleMedium)
            summary.openQuestions.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
