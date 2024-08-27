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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.banshus.mystock.data.database.AppDatabase
import com.banshus.mystock.repository.UserSettingsRepository
import com.banshus.mystock.ui.theme.Blue1
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.theme.primaryDark
import com.banshus.mystock.ui.theme.primaryDarkGreen
import com.banshus.mystock.ui.theme.surfaceContainerDark
import com.banshus.mystock.ui.theme.surfaceContainerDarkGreen
import com.banshus.mystock.ui.theme.surfaceDark
import com.banshus.mystock.ui.theme.surfaceDarkGreen
import com.banshus.mystock.viewmodels.UserSettingsViewModel
import com.banshus.mystock.viewmodels.UserSettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorThemeScreen(navController: NavHostController, userSettingsViewModel: UserSettingsViewModel) {
    // 获取 Context
//    val context = LocalContext.current
//    // 创建 Repository 和 Factory
//    val repository = UserSettingsRepository(AppDatabase.getDatabase(context).userSettingsDao())
//    val factory = UserSettingsViewModelFactory(repository)
//
//    // 获取 ViewModel 实例
//    val userSettingsViewModel: UserSettingsViewModel = viewModel(
//        factory = factory
//    )
    // 观察 LiveData<UserSettings>
    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    val themeIndex = userSettings?.themeIndex
    //選擇的主題
//    val selectedThemeIndex = remember { mutableIntStateOf(themeIndex ?: 0) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "主題顏色",
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
//                actions = {
//                    IconButton(onClick = { /* do something */ }) {
//                        Icon(
//                            imageVector = Icons.Filled.Check,
//                            contentDescription = "確定"
//                        )
//                    }
//                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .size(100.dp, 150.dp)
                            .weight(1f)
                            .clickable {
                                userSettingsViewModel.updateUserSettings(newThemeIndex = 0)
                            },
                        border = BorderStroke(
                            width = if (themeIndex == 0) 2.dp else 1.dp,
                            color = if (themeIndex == 0) Blue1 else Gray1
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(surfaceDark)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(surfaceContainerDark)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(primaryDark)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Card(
                        modifier = Modifier
                            .size(100.dp, 150.dp)
                            .weight(1f)
                            .clickable {
                                userSettingsViewModel.updateUserSettings(newThemeIndex = 1)
                            },
                        border = BorderStroke(
                            width = if (themeIndex == 1) 2.dp else 1.dp,
                            color = if (themeIndex == 1) Blue1 else Gray1
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(surfaceDarkGreen)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(surfaceContainerDarkGreen)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(primaryDarkGreen)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Card(
                        modifier = Modifier
                            .size(100.dp, 150.dp).weight(1f),
                        border = BorderStroke(1.dp, Gray1),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(surfaceDark)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(surfaceContainerDark)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(primaryDark)
                            )
                        }
                    }
                }
            }
        }
    }
}