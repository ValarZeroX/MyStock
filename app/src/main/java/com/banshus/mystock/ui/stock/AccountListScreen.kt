package com.banshus.mystock.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController

@Composable
fun AccountListScreen(navController: NavHostController){
    Scaffold(
        topBar = {
            AccountListHeader(navController)
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "帳戶列表",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "關閉"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("AddAccountScreen")
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "新增"
                )
            }
        }
    )
}