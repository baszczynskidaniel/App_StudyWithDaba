package com.example.studywithdaba.core.design_system.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.studywithdaba.core.design_system.icon.SWDIcons

@Composable
fun ErrorRowMessage(
    error: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(error, color = MaterialTheme.colorScheme.error)
        Icon(imageVector = SWDIcons.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
    }
}

