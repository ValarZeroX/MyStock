//package com.banshus.mystock
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import com.banshus.mystock.ui.theme.MyStockTheme
//import androidx.compose.ui.Modifier
//import androidx.activity.viewModels
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import com.banshus.mystock.StockAddScreen
//
//
//@Composable
//fun MainScreen() {
////    val viewModel by viewModels<StockViewModel>()
//    val viewModel: StockViewModel = viewModels()
//    val selectedTab by remember { mutableStateOf(viewModel.selectedTab) }
//
//    MyStockTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//            Column {
//                // Display the current content based on the selected tab
//                when (viewModel.selectedTab) {
//                    0 -> StockAddScreen(modifier = Modifier.weight(1f))
//                    1 -> StockAddScreen(modifier = Modifier.weight(1f))
//                    2 -> StockAddScreen(modifier = Modifier.weight(1f))
//                    // Add more cases for other tabs
//                }
//
//                Spacer(modifier = Modifier.weight(1f)) // Pushes NavBar to the bottom
//                NavBar(viewModel.selectedTab, onTabSelected = { index ->
//                    viewModel.selectedTab = index
//                })
//            }
//        }
//    }
//}