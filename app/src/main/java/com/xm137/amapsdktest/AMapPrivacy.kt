package com.xm137.amapsdktest

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amap.api.location.AMapLocationClient.updatePrivacyAgree
import com.amap.api.location.AMapLocationClient.updatePrivacyShow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AMapPrivacy(
    testViewModel: TestViewModel = viewModel(
        factory = TestViewModel.Factory
    ),
    aMapPrivacyUiState: IsAMapPrivacyUiState = testViewModel.isAMapPrivacyAgree.collectAsState().value,
    selectAMapPrivacy: (Boolean) -> Unit = testViewModel::selectAMapPrivacy
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        var checked by remember { mutableStateOf(aMapPrivacyUiState.isAMapPrivacy) }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                modifier = Modifier.semantics {
                    contentDescription = "Agree AMap Privacy Policy"
                },
                checked = checked,
                onCheckedChange = {
                    checked = it
                    updatePrivacyShow(context, it, it)
                    updatePrivacyAgree(context, it)
                    selectAMapPrivacy(it)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "隐私协议已显示已同意",
                fontSize = 20.sp
            )
        }
        Column {
            Text(
                buildAnnotatedString {
                    append("使用SDK名称: 高德开放平台定位SDK\n 第三方名称: 北京高德图强科技有限公司\n 使用目的: 为了向用户提供定位服务\n")
                    append("收集个人信息: 经纬度、设备信息【如IP 地址、GNSS信息、网络类型、WiFi状态、WiFi参数、WiFi列表、SSID、BSSID、基站信息、WiFi信号强度的信息传感器信息（矢量、加速度、压力）、设备信号强度信息】、设备标识信息（IDFA、OAID）、当前应用信息（应用名、应用版本号）、设备参数及系统信息（设备品牌及型号、操作系统、运营商信息、屏幕分辨率）\n")
                    append("权限使用: 位置、始终允许访问位置、网络访问\n 信息收集方式: SDK本机采集\n")
                    append("第三方隐私政策: ")
                    withLink(
                        LinkAnnotation.Url(
                            url = "https://lbs.amap.com/pages/privacy/",
                            styles = TextLinkStyles(style = SpanStyle(color = Color.Blue))
                        )
                    ) {
                        append("https://lbs.amap.com/pages/privacy/")
                    }
                }
            )
        }
        Column() {
            val testViewModel: TestViewModel = viewModel(factory = TestViewModel.Factory)

            if (testViewModel.isAMapPrivacyAgree.collectAsState().value.isAMapPrivacy) {
                val locationViewModel: AMapLocationViewModel = viewModel()
                if (ContextCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    GetLocation(
                        onLocationButtonClick = { locationViewModel.getLocation() },
                        onLocationStopButtonClick = { locationViewModel.stopLocation() },
                        onLocationDestroyButtonClick = { locationViewModel.destroy() },
                    )
                } else {
                    Row(modifier = Modifier.padding(16.dp)) { Text("请为定位授予位置权限") }
                    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
                    LaunchedEffect(permissionState.status) {
                        if (permissionState.status.isGranted) { Log.i("Permission", "${permissionState.status}") } else { permissionState.launchPermissionRequest() }
                    }
                }
            } else {
                Row(modifier = Modifier.padding(16.dp)) { Text("需要同意高德隐私协议方可使用定位SDK") }

            }


        }
    }
}

