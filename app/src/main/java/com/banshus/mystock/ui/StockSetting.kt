package com.banshus.mystock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banshus.mystock.ui.setting.ColorThemeScreen


@Composable
fun StockSettingScreen(navController: NavHostController){
    Scaffold(
        topBar = {
            SettingHeader()
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ){
            item{
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row{
                        Text(
                            text = "設定",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    ListItem(
                        headlineContent = { Text("股票市場") },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "股票市場",
                            )
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("stockMarketScreen")
                        }
                    )
                    HorizontalDivider()
                }
            }
            item{
                Column{
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ){
                        Row{
                            Text(
                                text = "偏好",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.primary,
                            thickness = 1.dp,  // 设置分割线的厚度
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                navController.navigate("colorThemeScreen")
                            }
                        ){
                            Text(
                                text = "主題顏色",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(10.dp),
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "主題顏色",
                                modifier = Modifier.padding(end = 12.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
            item{

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingHeader() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "功能列表",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
    )
}
