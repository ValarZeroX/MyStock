package com.banshus.mystock

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StockReportScreen(modifier: Modifier = Modifier) {
    // Tab 1 specific content here
    Text("This is Tab Report", modifier = modifier.padding(16.dp))
}