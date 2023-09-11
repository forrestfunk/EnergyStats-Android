package com.alpriest.energystats.ui.login

import com.alpriest.energystats.models.*
import com.alpriest.energystats.services.Networking
import com.alpriest.energystats.stores.ConfigManaging
import com.alpriest.energystats.ui.settings.RefreshFrequency
import com.alpriest.energystats.ui.settings.SelfSufficiencyEstimateMode
import com.alpriest.energystats.ui.theme.AppTheme
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class ConfigManager(var config: ConfigInterface, val networking: Networking, override var appVersion: String) : ConfigManaging {
    override val themeStream: MutableStateFlow<AppTheme> = MutableStateFlow(
        AppTheme(
            useLargeDisplay = config.useLargeDisplay,
            useColouredLines = config.useColouredFlowLines,
            showBatteryTemperature = config.showBatteryTemperature,
            decimalPlaces = config.decimalPlaces,
            showSunnyBackground = config.showSunnyBackground,
            showBatteryEstimate = config.showBatteryEstimate,
            showUsableBatteryOnly = config.showUsableBatteryOnly,
            showTotalYield = config.showTotalYield,
            selfSufficiencyEstimateMode = SelfSufficiencyEstimateMode.fromInt(config.selfSufficiencyEstimateMode),
            showEstimatedEarnings = config.showEstimatedEarnings,
            showValuesInWatts = config.showValuesInWatts,
            showInverterTemperatures = config.showInverterTemperatures,
            showInverterIcon = config.showInverterIcon,
            showHomeTotal = config.showHomeTotal,
            shouldInvertCT2 = config.shouldInvertCT2,
            showGridTotals = config.showGridTotals,
            showInverterTypeNameOnPowerflow = config.showInverterTypeNameOnPowerflow,
            showInverterPlantNameOnPowerflow = config.showInverterPlantNameOnPowerflow,
            showLastUpdateTimestamp = config.showLastUpdateTimestamp
        )
    )

    override var decimalPlaces: Int
        get() = config.decimalPlaces
        set(value) {
            config.decimalPlaces = value
            themeStream.value = themeStream.value.copy(decimalPlaces = decimalPlaces)
        }

    override var showSunnyBackground: Boolean
        get() = config.showSunnyBackground
        set(value) {
            config.showSunnyBackground = value
            themeStream.value = themeStream.value.copy(showSunnyBackground = showSunnyBackground)
        }

    override var selfSufficiencyEstimateMode: SelfSufficiencyEstimateMode
        get() = SelfSufficiencyEstimateMode.fromInt(config.selfSufficiencyEstimateMode)
        set(value) {
            config.selfSufficiencyEstimateMode = value.value
            themeStream.value = themeStream.value.copy(selfSufficiencyEstimateMode = selfSufficiencyEstimateMode)
        }

    override var showTotalYield: Boolean
        get() = config.showTotalYield
        set(value) {
            config.showTotalYield = value
            themeStream.value = themeStream.value.copy(showTotalYield = showTotalYield)
        }

    override var showEstimatedEarnings: Boolean
        get() = config.showEstimatedEarnings
        set(value) {
            config.showEstimatedEarnings = value
            themeStream.value = themeStream.value.copy(showEstimatedEarnings = showEstimatedEarnings)
        }

    override var showBatteryEstimate: Boolean
        get() = config.showBatteryEstimate
        set(value) {
            config.showBatteryEstimate = value
            themeStream.value = themeStream.value.copy(showBatteryEstimate = showBatteryEstimate)
        }

    override var showValuesInWatts: Boolean
        get() = config.showValuesInWatts
        set(value) {
            config.showValuesInWatts = value
            themeStream.value = themeStream.value.copy(showValuesInWatts = showValuesInWatts)
        }

    override val minSOC: MutableStateFlow<Double?> = MutableStateFlow(null)

    override var batteryCapacity: Int
        get() {
            return currentDevice.value?.let {
                val override = config.deviceBatteryOverrides[it.deviceID]
                return (override ?: it.battery?.capacity ?: "0").toDouble().toInt()
            } ?: run {
                2600
            }
        }
        set(value) {
            currentDevice.value?.let {
                val map = config.deviceBatteryOverrides.toMutableMap()
                map[it.deviceID] = value.toString()
                config.deviceBatteryOverrides = map
            }

            devices = devices?.map { it }
        }

    override var isDemoUser: Boolean
        get() = config.isDemoUser
        set(value) {
            config.isDemoUser = value
        }

    override var useColouredFlowLines: Boolean
        get() = config.useColouredFlowLines
        set(value) {
            config.useColouredFlowLines = value
            themeStream.value = themeStream.value.copy(useColouredLines = useColouredFlowLines)
        }

    override var refreshFrequency: RefreshFrequency
        get() = RefreshFrequency.fromInt(config.refreshFrequency)
        set(value) {
            config.refreshFrequency = value.value
        }

    override var showBatteryTemperature: Boolean
        get() = config.showBatteryTemperature
        set(value) {
            config.showBatteryTemperature = value
            themeStream.value = themeStream.value.copy(showBatteryTemperature = showBatteryTemperature)
        }

    override var showInverterTemperatures: Boolean
        get() = config.showInverterTemperatures
        set(value) {
            config.showInverterTemperatures = value
            themeStream.value = themeStream.value.copy(showInverterTemperatures = showInverterTemperatures)
        }

    override var useLargeDisplay: Boolean
        get() = config.useLargeDisplay
        set(value) {
            config.useLargeDisplay = value
            themeStream.value = themeStream.value.copy(useLargeDisplay = useLargeDisplay)
        }

    override fun logout() {
        config.devices = null
        config.isDemoUser = false
    }

    override var showUsableBatteryOnly: Boolean
        get() = config.showUsableBatteryOnly
        set(value) {
            config.showUsableBatteryOnly = value
            themeStream.value = themeStream.value.copy(showUsableBatteryOnly = showUsableBatteryOnly)
        }

    override var showInverterIcon: Boolean
        get() = config.showInverterIcon
        set(value) {
            config.showInverterIcon = value
            themeStream.value = themeStream.value.copy(showInverterIcon = showInverterIcon)
        }

    override var showHomeTotal: Boolean
        get() = config.showHomeTotal
        set(value) {
            config.showHomeTotal = value
            themeStream.value = themeStream.value.copy(showHomeTotal = showHomeTotal)
        }

    override var shouldInvertCT2: Boolean
        get() = config.shouldInvertCT2
        set(value) {
            config.shouldInvertCT2 = value
            themeStream.value = themeStream.value.copy(shouldInvertCT2 = shouldInvertCT2)
        }

    override var showGridTotals: Boolean
        get() = config.showGridTotals
        set(value) {
            config.showGridTotals = value
            themeStream.value = themeStream.value.copy(showGridTotals = showGridTotals)
        }

    override var showInverterTypeNameOnPowerflow: Boolean
        get() = config.showInverterTypeNameOnPowerflow
        set(value) {
            config.showInverterTypeNameOnPowerflow = value
            themeStream.value = themeStream.value.copy(showInverterTypeNameOnPowerflow = showInverterTypeNameOnPowerflow)
        }

    override var showInverterPlantNameOnPowerflow: Boolean
        get() = config.showInverterPlantNameOnPowerflow
        set(value) {
            config.showInverterPlantNameOnPowerflow = value
            themeStream.value = themeStream.value.copy(showInverterPlantNameOnPowerflow = showInverterPlantNameOnPowerflow)
        }

    override var showLastUpdateTimestamp: Boolean
        get() = config.showLastUpdateTimestamp
        set(value) {
            config.showLastUpdateTimestamp = value
            themeStream.value = themeStream.value.copy(showLastUpdateTimestamp = showLastUpdateTimestamp)
        }

    final override var devices: List<Device>?
        get() {
            config.devices?.let {
                return Gson().fromJson(it, Array<Device>::class.java).toList()
            }

            return null
        }
        set(value) {
            if (value != null) {
                config.devices = Gson().toJson(value)
            } else {
                config.devices = null
            }

            currentDevice.value = devices?.firstOrNull { it.deviceID == selectedDeviceID }
        }

    final override var currentDevice: MutableStateFlow<Device?> = MutableStateFlow(null)

    override var selectedDeviceID: String?
        get() = config.selectedDeviceID
        set(value) {
            config.selectedDeviceID = value
        }

    override fun select(device: Device) {
        selectedDeviceID = device.deviceID
        currentDevice.value = devices?.firstOrNull { it.deviceID == selectedDeviceID }
    }

    override val variables: List<RawVariable>
        get() {
            return currentDevice.value?.variables ?: listOf()
        }

    override val hasBattery: Boolean
        get() {
            return currentDevice.value?.let { it.battery == null } ?: false
        }

    override suspend fun fetchDevices() {
        val deviceList = networking.fetchDeviceList()
        var currentAction = ""

        try {
            val mappedDevices = ArrayList<Device>()
            deviceList.devices.asFlow().map {
                currentAction = "fetch variables"
                val variables = networking.fetchVariables(it.deviceID)
                currentAction = "fetch firmware versions"
                val firmware = fetchFirmwareVersions(it.deviceID)
                var deviceBattery: Battery?

                if (it.hasBattery) {
                    currentAction = "fetch battery"
                    val battery = networking.fetchBattery(it.deviceID)
                    currentAction = "fetch battery settings"
                    val batterySettings = networking.fetchBatterySettings(it.deviceSN)
                    try {
                        val batteryCapacity = (battery.residual / (battery.soc.toDouble() / 100.0)).toString()
                        val minSOC = (batterySettings.minGridSoc.toDouble() / 100.0).toString()
                        deviceBattery = Battery(batteryCapacity, minSOC)
                    } catch (_: Exception) {
                        deviceBattery = null
                    }
                } else {
                    deviceBattery = null
                }

                mappedDevices.add(
                    Device(
                        plantName = it.plantName,
                        deviceID = it.deviceID,
                        deviceSN = it.deviceSN,
                        hasPV = it.hasPV,
                        hasBattery = it.hasBattery,
                        battery = deviceBattery,
                        deviceType = it.deviceType,
                        firmware = firmware,
                        variables = variables,
                        moduleSN = it.moduleSN
                    )
                )
            }.collect()

            devices = mappedDevices

            if (selectedDeviceID == null) {
                selectedDeviceID = devices?.firstOrNull()?.deviceID
            }
        } catch (ex: NoSuchElementException) {
            throw NoDeviceFoundException()
        } catch (ex: Exception) {
            throw CouldNotFetchDeviceList(currentAction, ex)
        }
    }

    override suspend fun refreshFirmwareVersions() {
        try {
            devices = devices?.map {
                val firmware = fetchFirmwareVersions(it.deviceID)
                if (it.firmware != firmware) {
                    return@map it.copy(firmware = firmware)
                } else {
                    return@map it
                }
            }
        } catch (ex: Exception) {
            // Ignore
        }
    }

    private suspend fun fetchFirmwareVersions(deviceID: String): DeviceFirmwareVersion {
        val firmware = networking.fetchAddressBook(deviceID)

        return DeviceFirmwareVersion(
            master = firmware.softVersion.master,
            slave = firmware.softVersion.slave,
            manager = firmware.softVersion.manager
        )
    }

    override var selectedParameterGraphVariables: List<String>
        get() = config.selectedParameterGraphVariables
        set(value) {
            config.selectedParameterGraphVariables = value
        }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        currentDevice = MutableStateFlow(devices?.firstOrNull { it.deviceID == selectedDeviceID })
        coroutineScope.launch {
            currentDevice.collect {
                minSOC.value = it?.battery?.minSOC?.toDouble()
            }
        }
    }
}

class CouldNotFetchDeviceList(message: String, ex: Exception) : Exception("Could not fetch device list (${message}) (#${ex.localizedMessage})")
class NoDeviceFoundException : Exception("No device found")
