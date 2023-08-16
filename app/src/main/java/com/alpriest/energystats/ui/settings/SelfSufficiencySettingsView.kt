package com.alpriest.energystats.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alpriest.energystats.R
import com.alpriest.energystats.preview.FakeConfigManager
import com.alpriest.energystats.stores.ConfigManaging
import com.alpriest.energystats.ui.SegmentedControl
import com.alpriest.energystats.ui.theme.EnergyStatsTheme

@Composable
fun SelfSufficiencySettingsView(config: ConfigManaging, modifier: Modifier = Modifier) {
    val selfSufficiencyEstimateModeState = rememberSaveable { mutableStateOf(config.selfSufficiencyEstimateMode) }
    val description = when (selfSufficiencyEstimateModeState.value) {
        SelfSufficiencyEstimateMode.Absolute -> stringResource(R.string.absolute_self_sufficiency)
        SelfSufficiencyEstimateMode.Net -> stringResource(R.string.net_self_sufficiency)
        else -> ""
    }

    SettingsColumnWithChild(
        modifier = modifier
    ) {
        Column {
            SettingsTitleView(stringResource(R.string.self_sufficiency_estimates))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val items = listOf(SelfSufficiencyEstimateMode.Off, SelfSufficiencyEstimateMode.Net, SelfSufficiencyEstimateMode.Absolute)
                SegmentedControl(
                    items = items.map { it.title() },
                    useFixedWidth = true,
                    defaultSelectedItemIndex = items.indexOf(selfSufficiencyEstimateModeState.value),
                    color = colors.primary
                ) {
                    selfSufficiencyEstimateModeState.value = items[it]
                    config.selfSufficiencyEstimateMode = items[it]
                }
            }

            if (description.isNotEmpty()) {
                Text(
                    description,
                    color = colors.onSecondary,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 640
)
@Composable
fun SelfSufficiencySettingsViewPreview() {
    EnergyStatsTheme {
        SelfSufficiencySettingsView(config = FakeConfigManager())
    }
}