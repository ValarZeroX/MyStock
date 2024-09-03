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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.ui.theme.Blue1
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.ui.theme.primaryDark
import com.banshus.mystock.ui.theme.primaryDarkGreen
import com.banshus.mystock.ui.theme.primaryLight
import com.banshus.mystock.ui.theme.primaryLightGreen
import com.banshus.mystock.ui.theme.surfaceContainerDark
import com.banshus.mystock.ui.theme.surfaceContainerDarkGreen
import com.banshus.mystock.ui.theme.surfaceContainerLight
import com.banshus.mystock.ui.theme.surfaceContainerLightGreen
import com.banshus.mystock.ui.theme.surfaceDark
import com.banshus.mystock.ui.theme.surfaceDarkGreen
import com.banshus.mystock.ui.theme.surfaceLight
import com.banshus.mystock.ui.theme.surfaceLightGreen
import com.banshus.mystock.viewmodels.UserSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorThemeScreen(navController: NavHostController, userSettingsViewModel: UserSettingsViewModel) {
    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    var themeIndex by remember { mutableIntStateOf(0) }
    var darkTheme by remember { mutableStateOf(true) }
    LaunchedEffect(userSettings) {
        themeIndex = userSettings!!.themeIndex
        darkTheme = userSettings!!.darkTheme
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.theme_color),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            AdBanner() // 将广告放在底部栏
        }
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
//                Row(
//                    modifier = Modifier.padding(5.dp, bottom = 20.dp)
//                ) {
//                    Text(
//                        text = stringResource(id = R.string.dark_mode),
//                        modifier = Modifier
//                            .align(Alignment.CenterVertically)
//                            .width(150.dp)
//                            .padding(start = 10.dp, end = 20.dp),
//                    )
//                    Switch(
//                        checked = darkTheme,
//                        onCheckedChange = {
//                            darkTheme = it
//                            userSettingsViewModel.updateDarkTheme(
//                                darkTheme,
//                            )
//                        }
//                    )
//                }
//                HorizontalDivider()
                Row(
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.color),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(150.dp)
                            .padding(start = 10.dp, end = 20.dp, bottom = 20.dp),
                    )
                }
                if (darkTheme) {
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
                    }
                }else{
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
                                        .background(surfaceLight)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .background(surfaceContainerLight)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .background(primaryLight)
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
                                        .background(surfaceLightGreen)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .background(surfaceContainerLightGreen)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .background(primaryLightGreen)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}