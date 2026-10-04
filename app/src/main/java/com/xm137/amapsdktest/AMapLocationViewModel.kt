package com.xm137.amapsdktest

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.IReGeoLocationCallback
import com.amap.apis.utils.core.api.AMapUtilCoreApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LocationDataModel(
    val adcode: String? = "0",
    val location: String? = "empty"
)
sealed interface AMapLocationUiState {
    data class LocationData(val info: LocationDataModel) : AMapLocationUiState
    data class Error(val info: String): AMapLocationUiState
    object Loading: AMapLocationUiState
    object Nothing: AMapLocationUiState
}

class AMapLocationViewModel(
    application: Application
) : AndroidViewModel(application) {
    var aMapLocationUiState: AMapLocationUiState by mutableStateOf(AMapLocationUiState.Nothing)
        private set
    private val _locationState = MutableStateFlow<AMapLocationUiState>(AMapLocationUiState.Nothing)
    val locationState: StateFlow<AMapLocationUiState> = _locationState
    private val mClient = AMapLocationClient(application)

    private val mAMapLocationListener = AMapLocationListener { amapLocation ->
        if (amapLocation != null) {
            if (amapLocation.errorCode == 0) {
                Log.i("Location: ", "${amapLocation.address}")
                Log.i("This ACode: ","${amapLocation.adCode}")
                viewModelScope.launch {
                    AMapLocationUiState.LocationData(
                        info = LocationDataModel(
                            adcode = amapLocation.adCode,
                            location = amapLocation.address
                        )
                    )
                    _locationState.value = AMapLocationUiState.LocationData(
                        info = LocationDataModel(
                            adcode = amapLocation.adCode,
                            location = amapLocation.address
                        )
                    )
                }

                Log.i("aMapLocationUiState", aMapLocationUiState.toString())
            } else {
                AMapLocationUiState.Error(
                    info = "ErrorCode: " + amapLocation.errorInfo +
                    "ErrorInfo: " + amapLocation.errorInfo
                )
                _locationState.value = AMapLocationUiState.Error(
                    info = "ErrorCode: " + amapLocation.errorInfo +
                    "ErrorInfo: " + amapLocation.errorInfo
                )
                Log.e("AMapError", "ErrCode " + amapLocation.errorCode + "errInfo" + amapLocation.errorInfo)
            }
            stopLocation().also {
                Log.i("Stop Location","Location Data Got")
            }
        }
    }

    init {
        try {
            val mLocationOption = AMapLocationClientOption()
            AMapUtilCoreApi.setCollectInfoEnable(false) // 取消收集信息
            try {
                mClient.setLocationListener(mAMapLocationListener) //设置监听器
            } catch (e: Exception) {
                Log.e("setLocationListenerError", e.toString())
            }

            val iReGeoLocationCallback = IReGeoLocationCallback { reGeoLocation ->
                Log.i("reGeoLocation", reGeoLocation.toString())
            }
            mClient.setReGeoLocationCallback(iReGeoLocationCallback) // 设置逆地理回调

            if (mClient != null) {
                mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn //签到模式
                mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //高精度定位
                mLocationOption.isNeedAddress = true // 显示地址
                mLocationOption.isMockEnable = true // 允许模拟位置
                mLocationOption.isOnceLocation = true //  单次定位
                mLocationOption.isOnceLocationLatest = true // 3秒内最精确位置
                mClient.setLocationOption(mLocationOption) // 设置定位选项
                mClient.stopLocation() // 停止定位
//                mClient.startLocation()
            }
        } catch (e: Exception) {
            Log.e("AMapLocationClient", e.toString())
        }
    }

    fun getLocation() {
        aMapLocationUiState = AMapLocationUiState.Loading
        _locationState.value = AMapLocationUiState.Loading
        mClient.stopLocation()
        mClient.startLocation()
    }

    fun stopLocation() {
        mClient.stopLocation()
        Log.i("stopLocation", "stopLocation is called")
    }

    fun destroy() {
        aMapLocationUiState = AMapLocationUiState.Nothing
        mClient.stopLocation()
        mClient.onDestroy()
        aMapLocationUiState = AMapLocationUiState.Nothing
        _locationState.value = AMapLocationUiState.Nothing
        Log.i("LocationDestroy", "LocationDestroyClicked")
    }

    override fun onCleared() {
//        super.onCleared()
        mClient.onDestroy()
    }
}