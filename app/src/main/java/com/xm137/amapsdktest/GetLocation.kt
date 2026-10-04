package com.xm137.amapsdktest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GetLocation (
    viewModel: AMapLocationViewModel = viewModel(),
    onLocationButtonClick : () -> Unit,
    onLocationStopButtonClick : () -> Unit,
    onLocationDestroyButtonClick : () -> Unit,
) {
    val state by viewModel.locationState.collectAsState()
    Column {
        Row (
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { onLocationButtonClick() },
                shape = AssistChipDefaults.shape
            ) {
                Text("GET Location")
            }
            Button(
                shape = AssistChipDefaults.shape,
                onClick = { onLocationStopButtonClick() }
            ) {
                Text("StopLocation")
            }

        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
            shape = AssistChipDefaults.shape,
            onClick = { onLocationDestroyButtonClick() }
            ) {
                Text("DestroyLocationClient")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column {
            when (state) {
                is AMapLocationUiState.LocationData -> {
                    val data = state as AMapLocationUiState.LocationData
                    if (data.info.adcode?.isEmpty() ?: true) {
                        Text("区域编号为空，请查看日志或更换设备")
                    } else {
                        Text(
                            text = "区域编号: " + data.info.adcode + "\n" +
                                    "位置: " + data.info.location
                        )
                    }
                }
                is AMapLocationUiState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text("Loading...")
                }

                is AMapLocationUiState.Nothing ->
                    Text("可开始定位")

                is AMapLocationUiState.Error -> {
                    val data = state as AMapLocationUiState.Error
                    Text(data.info)
                }
            }
        }
    }
}