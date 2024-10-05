package com.banshus.mystock.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.banshus.mystock.R
import com.banshus.mystock.ads.AdBanner
import com.banshus.mystock.viewmodels.BillingViewModel

@Composable
fun DisclaimerScreen(
    navController: NavHostController,
    billingViewModel: BillingViewModel
) {
    Scaffold(
        topBar = {
            DisclaimerScreenHeader(navController)
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
            val disclaimerTexts = listOf(
                R.string.disclaimer_text_2,
                R.string.disclaimer_text_3,
                R.string.disclaimer_text_4,
                R.string.disclaimer_text_5,
                R.string.disclaimer_text_6,
                R.string.disclaimer_text_7,
                R.string.disclaimer_text_8,
                R.string.disclaimer_text_9,
                R.string.disclaimer_text_10,
                R.string.disclaimer_text_11,
                R.string.disclaimer_text_12,
                R.string.disclaimer_text_13,
                R.string.disclaimer_text_14,
                R.string.disclaimer_text_15,
            )

            LazyColumn {
                items(disclaimerTexts) { textId ->
                    Text(
                        text = stringResource(id = textId),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp) // 適當的間距
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisclaimerScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(id = R.string.disclaimer_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "back"
                )
            }
        },
    )
}