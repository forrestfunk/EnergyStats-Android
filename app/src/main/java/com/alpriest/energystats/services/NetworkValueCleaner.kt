package com.alpriest.energystats.services

import com.alpriest.energystats.models.AddressBookResponse
import com.alpriest.energystats.models.BatteryResponse
import com.alpriest.energystats.models.BatterySettingsResponse
import com.alpriest.energystats.models.BatteryTimesResponse
import com.alpriest.energystats.models.ChargeTime
import com.alpriest.energystats.models.DeviceSettingsGetResponse
import com.alpriest.energystats.models.EarningsResponse
import com.alpriest.energystats.models.PagedDataLoggerListResponse
import com.alpriest.energystats.models.PagedDeviceListResponse
import com.alpriest.energystats.models.QueryDate
import com.alpriest.energystats.models.RawData
import com.alpriest.energystats.models.RawResponse
import com.alpriest.energystats.models.RawVariable
import com.alpriest.energystats.models.ReportData
import com.alpriest.energystats.models.ReportResponse
import com.alpriest.energystats.models.ReportVariable
import com.alpriest.energystats.ui.statsgraph.ReportType

class NetworkValueCleaner(private val network: Networking) : Networking {
    override suspend fun fetchDeviceList(): PagedDeviceListResponse {
        return network.fetchDeviceList()
    }

    override suspend fun ensureHasToken() {
        network.ensureHasToken()
    }

    override suspend fun verifyCredentials(username: String, password: String) {
        network.verifyCredentials(username, password)
    }

    override suspend fun fetchBattery(deviceID: String): BatteryResponse {
        return network.fetchBattery(deviceID)
    }

    override suspend fun fetchBatterySettings(deviceSN: String): BatterySettingsResponse {
        return network.fetchBatterySettings(deviceSN)
    }

    override suspend fun fetchRaw(deviceID: String, variables: List<RawVariable>, queryDate: QueryDate): ArrayList<RawResponse> {
        val rawList = network.fetchRaw(deviceID, variables, queryDate)
        return ArrayList(rawList.map { original ->
            RawResponse(
                variable = original.variable,
                data = original.data.map { originalData ->
                    RawData(
                        time = originalData.time,
                        value = originalData.value.capped()
                    )
                }.toTypedArray()
            )
        })
    }

    override suspend fun fetchReport(deviceID: String, variables: List<ReportVariable>, queryDate: QueryDate, reportType: ReportType): ArrayList<ReportResponse> {
        val reportList = network.fetchReport(deviceID = deviceID, variables = variables, queryDate = queryDate, reportType = reportType)
        return ArrayList(reportList.map { original ->
            ReportResponse(
                variable = original.variable,
                data = original.data.map { originalData ->
                    ReportData(
                        index = originalData.index,
                        value = originalData.value.capped()
                    )
                }.toTypedArray()
            )
        })
    }

    override suspend fun fetchAddressBook(deviceID: String): AddressBookResponse {
        return network.fetchAddressBook(deviceID)
    }

    override suspend fun fetchVariables(deviceID: String): List<RawVariable> {
        return network.fetchVariables(deviceID)
    }

    override suspend fun fetchEarnings(deviceID: String): EarningsResponse {
        return network.fetchEarnings(deviceID)
    }

    override suspend fun setSoc(minGridSOC: Int, minSOC: Int, deviceSN: String) {
        network.setSoc(minGridSOC, minSOC, deviceSN)
    }

    override suspend fun fetchBatteryTimes(deviceSN: String): BatteryTimesResponse {
        return network.fetchBatteryTimes(deviceSN)
    }

    override suspend fun setBatteryTimes(deviceSN: String, times: List<ChargeTime>) {
        network.setBatteryTimes(deviceSN, times)
    }

    override suspend fun fetchWorkMode(deviceID: String): DeviceSettingsGetResponse {
        return network.fetchWorkMode(deviceID)
    }

    override suspend fun setWorkMode(deviceID: String, workMode: String) {
        network.setWorkMode(deviceID, workMode)
    }

    override suspend fun fetchDataLoggers(): PagedDataLoggerListResponse {
        return network.fetchDataLoggers()
    }

    override suspend fun fetchErrorMessages() {
        network.fetchErrorMessages()
    }
}

private fun Double.capped(): Double {
    val mask = 0x0FFFF
    return ((this * 10).toInt() and mask).toDouble() / 10.0
}
