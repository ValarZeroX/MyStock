package com.banshus.mystock.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import com.banshus.mystock.viewmodels.UserSettingsViewModel

@Composable
fun ReportSettingScreen(navController: NavHostController, userSettingsViewModel: UserSettingsViewModel) {
    Scaffold(
        topBar = {
            ReportSettingScreenHeader(navController)
        },
    ) { innerPadding ->
        Box(
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
fun ReportSettingScreenHeader(navController: NavHostController)
{
    CenterAlignedTopAppBar(
        title = {
            Text(
                "報表設定",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "返回"
                )
            }
        },
    )
}