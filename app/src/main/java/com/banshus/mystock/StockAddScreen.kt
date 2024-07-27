package com.banshus.mystock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banshus.mystock.ui.theme.Black1
import com.banshus.mystock.ui.theme.White1
import androidx.compose.runtime.*
import com.banshus.mystock.ui.theme.Black2

@Composable
fun StockAddScreen() {
    Column (
        modifier = Modifier.background(Black2)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Black1)
                .padding(top = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "關閉",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(36.dp),
                tint = White1
            )
            Spacer(modifier = Modifier.weight(1f))
            Text("新增記錄", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = White1, fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "確定",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(36.dp),
                tint = White1
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            var stockSymbol by remember {
                mutableStateOf("")
            }
            TextField(
                value = stockSymbol,
                onValueChange = {stockSymbol = it},
                label = {Text("股票代碼")}
            )
        }
    }
    // Tab 1 specific content here

}

@Preview
@Composable
fun StockAddScreenPreview(){
    StockAddScreen()
}