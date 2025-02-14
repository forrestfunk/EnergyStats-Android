package com.alpriest.energystats.ui.flow

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpriest.energystats.R
import com.alpriest.energystats.models.Earning
import com.alpriest.energystats.models.EarningsResponse
import com.alpriest.energystats.models.ReportResponse
import com.alpriest.energystats.preview.FakeConfigManager
import com.alpriest.energystats.stores.ConfigManaging
import com.alpriest.energystats.ui.settings.FinancialModel
import com.alpriest.energystats.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SubLabelledView(value: String, label: String, alignment: Alignment.Horizontal) {
    Column(
        horizontalAlignment = alignment,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            value,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            label.uppercase(),
            fontSize = 8.sp
        )
    }
}

@Composable
fun EarningsView(themeStream: MutableStateFlow<AppTheme>, viewModel: EarningsViewModel) {
    val context = LocalContext.current
    val theme by themeStream.collectAsState()

    Row {
        viewModel.amounts(theme.financialModel, context).forEach {
            SubLabelledView(
                value = it.formattedAmount(),
                label = it.title(context),
                alignment = Alignment.CenterHorizontally
            )
        }
    }
}

enum class FinanceAmountType {
    TODAY,
    MONTH,
    YEAR,
    EXPORTED,
    AVOIDED,
    TOTAL
}

class FinanceAmount(val type: FinanceAmountType, val amount: Double, private val currencyCode: String, val currencySymbol: String) {
    fun formattedAmount(): String {
        return amount.roundedToString(2, currencyCode, currencySymbol)
    }

    fun title(context: Context): String {
        return when (type) {
            FinanceAmountType.EXPORTED -> context.getString(R.string.exported_income_short_title)
            FinanceAmountType.AVOIDED -> context.getString(R.string.grid_import_avoided_short_title)
            FinanceAmountType.TOTAL -> context.getString(R.string.total)
            FinanceAmountType.TODAY -> context.getString(R.string.today)
            FinanceAmountType.MONTH -> context.getString(R.string.month)
            FinanceAmountType.YEAR -> context.getString(R.string.year)
        }
    }
}

class EnergyStatsFinancialModel(totalsViewModel: TotalsViewModel, configManager: ConfigManaging) {
    val exportIncome: FinanceAmount
    val solarSaving: FinanceAmount
    val total: FinanceAmount

    init {
        exportIncome = FinanceAmount(
            type = FinanceAmountType.EXPORTED,
            amount = totalsViewModel.feedIn * configManager.feedInUnitPrice,
            currencySymbol = configManager.currencySymbol,
            currencyCode = configManager.currencyCode
        )

        solarSaving = FinanceAmount(
            type = FinanceAmountType.AVOIDED,
            amount = (totalsViewModel.solar - totalsViewModel.feedIn) * configManager.gridImportUnitPrice,
            currencySymbol = configManager.currencySymbol,
            currencyCode = configManager.currencyCode
        )

        total = FinanceAmount(
            type = FinanceAmountType.TOTAL,
            amount = exportIncome.amount + solarSaving.amount,
            currencySymbol = configManager.currencySymbol,
            currencyCode = configManager.currencyCode
        )
    }

    fun amounts(): List<FinanceAmount> {
        return listOf(exportIncome, solarSaving, total)
    }
}

class EarningsViewModel(val response: EarningsResponse, val energyStatsFinancialModel: EnergyStatsFinancialModel) {
    fun amounts(model: FinancialModel, context: Context): List<FinanceAmount> {
        return when (model) {
            FinancialModel.FoxESS ->
                listOf(
                    FinanceAmount(FinanceAmountType.TODAY, response.today.earnings, response.currencyCode(), response.currencySymbol()),
                    FinanceAmount(FinanceAmountType.MONTH, response.month.earnings, response.currencyCode(), response.currencySymbol()),
                    FinanceAmount(FinanceAmountType.YEAR, response.year.earnings, response.currencyCode(), response.currencySymbol()),
                    FinanceAmount(FinanceAmountType.TOTAL, response.cumulate.earnings, response.currencyCode(), response.currencySymbol())
                )

            FinancialModel.EnergyStats ->
                energyStatsFinancialModel.amounts()
        }
    }

    companion object {
        fun preview(): EarningsViewModel {
            return EarningsViewModel(
                response = EarningsResponse(
                    today = Earning(1.0, 1.0),
                    month = Earning(5.0, 5.0),
                    year = Earning(50.0, 50.0),
                    cumulate = Earning(500.0, 500.0),
                    currency = "GBP(£)"
                ),
                energyStatsFinancialModel = EnergyStatsFinancialModel(
                    totalsViewModel = TotalsViewModel(listOf(ReportResponse("raw", arrayOf()))),
                    configManager = FakeConfigManager()
                )
            )
        }
    }
}