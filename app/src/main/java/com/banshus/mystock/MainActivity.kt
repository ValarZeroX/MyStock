package com.banshus.mystock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import com.banshus.mystock.ui.theme.MyStockTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<StockViewModel>()
    //同意實驗API
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyStockTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box {
                        Column(modifier = Modifier.systemBarsPadding()){
                            when (viewModel.selectedTab) {
                                0 -> StockAddScreen()
                                1 -> StockAddScreen()
                                2 -> StockAddScreen()
                                3 -> StockReportScreen(modifier = Modifier.weight(1f))
                                4 -> StockAddScreen()
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter) // Align NavBar to the bottom
                                .background(Color.Transparent)
                        ) {
                            NavBar(
                                viewModel.selectedTab,
                                onTabSelected = { index ->
                                    viewModel.selectedTab = index
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
