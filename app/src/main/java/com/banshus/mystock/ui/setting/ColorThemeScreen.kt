package com.banshus.mystock.ui.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.banshus.mystock.ui.theme.Blue1
import com.banshus.mystock.ui.theme.primaryDark
import com.banshus.mystock.ui.theme.surfaceContainerDark
import com.banshus.mystock.ui.theme.surfaceDark

@Composable
fun ColorThemeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(top = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.clickable { navController.popBackStack() }){
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "返回",
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "主題顏色",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "確定",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(36.dp),
            )
        }
        Column(
            modifier = Modifier.padding(top = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .size(100.dp, 150.dp),
                    border = BorderStroke(2.dp, Blue1),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f) // 占据相等的空间
                                .fillMaxWidth()
                                .background(surfaceDark) // 第一块颜色
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f) // 占据相等的空间
                                .fillMaxWidth()
                                .background(surfaceContainerDark) // 第二块颜色
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f) // 占据相等的空间
                                .fillMaxWidth()
                                .background(primaryDark) // 第三块颜色
                        )
                    }
                }
            }
        }
    }
}