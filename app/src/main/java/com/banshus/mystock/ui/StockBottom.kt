package com.banshus.mystock.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.banshus.mystock.R

data class TabData(val icon: ImageVector, val description: String, val router: String)

@Composable
fun NavBar(selectedItemIndex: MutableState<Int>, navController: NavHostController) {
    val tabs = listOf(
        TabData(Icons.Filled.QueryStats, stringResource(id = R.string.tab_account), "stockAccountScreen"),
        TabData(Icons.Filled.CalendarMonth, stringResource(id = R.string.tab_record), "recordScreen"),
        TabData(Icons.Filled.Add, stringResource(id = R.string.tab_add), "addStockScreen"),
        TabData(Icons.Filled.BarChart, stringResource(id = R.string.tab_report), "reportScreen"),
        TabData(Icons.Filled.Menu, stringResource(id = R.string.tab_setting), "stockSettingScreen")
    )
    NavigationBar{
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = tab.description) },
                label = { Text(tab.description) },
                selected = selectedItemIndex.value == index,
                onClick = {
                    selectedItemIndex.value = index
                    navController.navigate(tab.router)
                }
            )
        }
    }
}
//fun NavBar(selected: Int, onTabSelected: (Int) -> Unit) {
//    val tabs = listOf(
//        TabData(Icons.Filled.QueryStats, "帳戶"),
//        TabData(Icons.Filled.CalendarMonth, "記錄"),
//        TabData(Icons.Filled.Add, "新增"),
//        TabData(Icons.Filled.BarChart, "報表"),
//        TabData(Icons.Filled.Menu, "設定")
//    )
//    NavigationBar{
//        tabs.forEachIndexed { index, tab ->
//            NavigationBarItem(
//                icon = { Icon(tab.icon, contentDescription = tab.description) },
//                label = { Text(tab.description) },
//                selected = selected == index,
//                onClick = { onTabSelected(index) }
//            )
//        }
//    }
//}



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

//@Preview(showBackground = false)
//@Composable
//fun NavBarPreview() {
//    NavBar(selected = 0, onTabSelected = {})
//}