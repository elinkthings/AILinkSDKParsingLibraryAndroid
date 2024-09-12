package cn.net.aicare.modulelibrary.module.bm41

import com.pingwang.bluetoothlib.device.BaseBleDeviceData
import com.pingwang.bluetoothlib.device.BleDevice
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener

/**
 * bm41设备
 * @author xing
 * @date 2024/05/23
 * @constructor 创建[Bm41Device]
 * @param [bleDevice] ble设备
 */
class Bm41Device(val bleDevice: BleDevice) : BaseBleDeviceData(bleDevice), OnBleOtherDataListener {

    init {
        bleDevice.setOnBleOtherDataListener(this)
    }


    override fun onNotifyOtherData(uuid: String?, data: ByteArray?) {
        super.onNotifyOtherData(uuid, data)
        // TODO: 处理bm41设备的其他数据
    }




}