package com.alpriest.energystats.ui.statsgraph

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alpriest.energystats.R
import com.alpriest.energystats.models.power
import com.alpriest.energystats.preview.FakeConfigManager
import com.alpriest.energystats.services.DemoNetworking
import com.alpriest.energystats.ui.flow.home.preview
import com.alpriest.energystats.ui.settings.SelfSufficiencyEstimateMode
import com.alpriest.energystats.ui.theme.AppTheme
import com.alpriest.energystats.ui.theme.ApproximationHeaderText
import com.alpriest.energystats.ui.theme.DarkApproximationBackground
import com.alpriest.energystats.ui.theme.DarkApproximationHeader
import com.alpriest.energystats.ui.theme.EnergyStatsTheme
import com.alpriest.energystats.ui.theme.LightApproximationBackground
import com.alpriest.energystats.ui.theme.LightApproximationHeader
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun StatsApproximationView(themeStream: MutableStateFlow<AppTheme>, modifier: Modifier = Modifier, viewModel: StatsTabViewModel) {
    val appTheme = themeStream.collectAsState().value
    val fontSize = appTheme.fontSize()
    val selfSufficiency = when (appTheme.selfSufficiencyEstimateMode) {
        SelfSufficiencyEstimateMode.Off -> null
        SelfSufficiencyEstimateMode.Net -> viewModel.netSelfSufficiencyEstimationStream.collectAsState().value
        SelfSufficiencyEstimateMode.Absolute -> viewModel.absoluteSelfSufficiencyEstimationStream.collectAsState().value
    }
    val homeUsage = viewModel.homeUsageStream.collectAsState().value
    val totalSolarGenerated = viewModel.totalSolarGeneratedStream.collectAsState().value
    val financialModel = viewModel.energyStatsFinancialModelStream.collectAsState().value

    Box(modifier) {
        Column(
            Modifier
                .background(
                    colors.ApproximationBackground,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .border(
                    width = 1.dp,
                    color = colors.ApproximationHeader,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                if (appTheme.selfSufficiencyEstimateMode != SelfSufficiencyEstimateMode.Off && selfSufficiency != null) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.self_sufficiency),
                            fontSize = fontSize
                        )
                        Text(
                            selfSufficiency,
                            fontSize = fontSize
                        )
                    }
                }

                homeUsage?.let {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.home_usage),
                            fontSize = fontSize
                        )
                        Text(
                            it.power(appTheme.displayUnit, appTheme.decimalPlaces),
                            fontSize = fontSize
                        )
                    }
                }

                totalSolarGenerated?.let {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Solar generated",
                            fontSize = fontSize
                        )
                        Text(
                            it.power(appTheme.displayUnit, appTheme.decimalPlaces),
                            fontSize = fontSize
                        )
                    }
                }

                financialModel?.let {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Export income",
                            fontSize = fontSize
                        )
                        Text(
                            it.exportIncome.formattedAmount(),
                            fontSize = fontSize
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Grid import avoided",
                            fontSize = fontSize
                        )
                        Text(
                            it.solarSaving.formattedAmount(),
                            fontSize = fontSize
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total benefit",
                            fontSize = fontSize
                        )
                        Text(
                            it.total.formattedAmount(),
                            fontSize = fontSize
                        )
                    }
                }
            }
        }

        Text(
            stringResource(R.string.approximations),
            Modifier
                .offset(x = 8.dp, y = (-11).dp)
                .background(
                    colors.ApproximationHeader,
                    shape = RoundedCornerShape(size = 4.dp)
                )
                .padding(horizontal = 2.dp, vertical = 1.dp),
            color = ApproximationHeaderText,
        )
    }
}

@Preview
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StatsApproximationViewPreview() {
    EnergyStatsTheme() {
        StatsApproximationView(themeStream = MutableStateFlow(AppTheme.preview()), viewModel = StatsTabViewModel(FakeConfigManager(), DemoNetworking()) { _, _ -> null })
    }
}

val Colors.ApproximationHeader: Color
    @Composable
    get() = if (isSystemInDarkTheme()) DarkApproximationHeader else LightApproximationHeader

val Colors.ApproximationBackground: Color
    @Composable
    get() = if (isSystemInDarkTheme()) DarkApproximationBackground else LightApproximationBackground
