package com.banshus.mystock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banshus.mystock.ui.theme.Black1
import com.banshus.mystock.ui.theme.Blue1
import com.banshus.mystock.ui.theme.White1

data class TabData(val icon: ImageVector, val description: String)

@Composable
fun NavBar(selected: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        TabData(Icons.Filled.QueryStats, "QueryStats"),
        TabData(Icons.Filled.CalendarMonth, "CalendarMonth"),
        TabData(Icons.Filled.Add, "Add"),
        TabData(Icons.Filled.BarChart, "BarChart"),
        TabData(Icons.Filled.Menu, "Menu")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black1)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, tab ->
                NavItem(
                    imageVector = tab.icon,
                    contentDescription = tab.description,
                    modifier = Modifier.size(58.dp).padding(10.dp),
                    tint = if (selected == index) Blue1 else White1,
                    onClick = {
                        onTabSelected(index)
                    }
                )
            }
        }
    }
}



@Composable
fun NavItem(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}

@Preview(showBackground = false)
@Composable
fun NavBarPreview() {
    NavBar(selected = 0, onTabSelected = {})
}