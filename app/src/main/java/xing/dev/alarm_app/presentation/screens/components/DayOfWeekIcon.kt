package xing.dev.alarm_app.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DayOfWeekIcon(
    dayInitial: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
    val contentColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = modifier
            .size(24.dp) // Slightly smaller for fitting in a row
            .background(color = backgroundColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayInitial,
            color = contentColor,
            fontSize = 8.sp, // Adjusted for smaller circle
            fontWeight = FontWeight.Bold, // Make it pop a bit
            textAlign = TextAlign.Center
        )
    }
}