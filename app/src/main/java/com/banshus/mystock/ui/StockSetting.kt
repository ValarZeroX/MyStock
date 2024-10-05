package com.banshus.mystock.ui

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banshus.mystock.MainActivity
import com.banshus.mystock.R
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.csv.exportCSV
import com.banshus.mystock.ui.setting.ColorThemeScreen
import com.banshus.mystock.ui.theme.Gray1
import com.banshus.mystock.viewmodels.BillingViewModel
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun StockSettingScreen(
    navController: NavHostController,
    csvImportLauncher: ActivityResultLauncher<Intent>,
    billingViewModel: BillingViewModel
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    Scaffold(
        topBar = {
            SettingHeader()
        },
        bottomBar = {
            AdBanner(billingViewModel) // 将广告放在底部栏
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            LazyColumn{
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.my_account),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.subscription)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "方案",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("billingScreen")
                            }
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.settings_app),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_stock_account)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "股票帳戶",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("accountScreen")
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_stock_market)) },
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
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_stock_symbol)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "股票代碼",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("stockSymbolScreen")
                            }
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.settings_preferences),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_color_theme)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "主題顏色",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("colorThemeScreen")
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_report)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "報表",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("reportSettingScreen")
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_currency)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "幣別匯率",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("currencyScreen")
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_auto_update)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "自動更新",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("autoUpdateScreen")
                            }
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.settings_import_export),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_export_csv),) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "匯出CSV",
                                )
                            },
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    val csvFile = exportCSV(navController.context)
                                    if (csvFile.exists()) {
                                        shareCSVFile(navController.context, csvFile)
                                    }
                                }
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.settings_import_csv)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "匯入CSV",
                                )
                            },
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "text/csv"
                                    }
                                    csvImportLauncher.launch(intent)
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.about),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        ListItem(
                            headlineContent = { Text(stringResource(id = R.string.disclaimer_title)) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Disclaimer",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("disclaimerScreen")
                            }
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Version: $versionName",
                            color = Gray1
                        )
                    }
                }
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
                stringResource(id = R.string.settings_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
    )
}

fun shareCSVFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share CSV via"))
}
