package com.banshus.mystock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StockSettingScreen(){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceBright)
    ){
        item{
            SettingHeader()
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
                            fontSize = 24.sp,
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
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "主題顏色",
                            fontSize = 18.sp,
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

@Composable
fun SettingHeader() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically){
        Text(
            "功能列表",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
fun StockSettingScreenPreview(){
    StockSettingScreen()
}