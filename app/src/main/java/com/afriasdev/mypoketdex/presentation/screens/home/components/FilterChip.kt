package com.afriasdev.mypoketdex.presentation.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.afriasdev.mypoketdex.ui.theme.getTypeColor

@Composable
fun PokemonTypeFilterChip(
    modifier: Modifier = Modifier,
    type: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val typeColor = getTypeColor(type)

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) typeColor else typeColor.copy(alpha = 0.2f),
        label = "background_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else typeColor,
        label = "content_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = type.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}