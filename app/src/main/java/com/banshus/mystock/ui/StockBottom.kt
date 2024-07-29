package com.banshus.mystock.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class TabData(val icon: ImageVector, val description: String)

@Composable
fun NavBar(selected: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        TabData(Icons.Filled.QueryStats, "持股"),
        TabData(Icons.Filled.CalendarMonth, "記錄"),
        TabData(Icons.Filled.Add, "新增"),
        TabData(Icons.Filled.BarChart, "報表"),
        TabData(Icons.Filled.Menu, "設定")
    )
    NavigationBar{
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = tab.description) },
                label = { Text(tab.description) },
                selected = selected == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surfaceContainer)
//            .padding(8.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            tabs.forEachIndexed { index, tab ->
//                NavItem(
//                    imageVector = tab.icon,
//                    contentDescription = tab.description,
//                    modifier = Modifier
//                        .size(58.dp)
//                        .padding(10.dp),
//                    tint = if (selected == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
//                    onClick = {
//                        onTabSelected(index)
//                    }
//                )
//            }
//        }
//    }
}



//@Composable
//fun NavItem(
//    imageVector: ImageVector,
//    contentDescription: String,
//    modifier: Modifier,
//    tint: Color = MaterialTheme.colorScheme.onPrimary,
//    onClick: () -> Unit
//) {
//    Column(modifier = Modifier.clickable(onClick = onClick)) {
//        Icon(
//            imageVector = imageVector,
//            contentDescription = contentDescription,
//            modifier = modifier,
//            tint = tint
//        )
//    }
//}

@Preview(showBackground = false)
@Composable
fun NavBarPreview() {
    NavBar(selected = 0, onTabSelected = {})
}