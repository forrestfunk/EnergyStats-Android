package com.alpriest.energystats.ui.flow.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alpriest.energystats.ui.flow.PowerFlowView

@Composable
fun SolarPowerFlow(amount: Double, modifier: Modifier, iconHeight: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            Icons.Rounded.WbSunny,
            contentDescription = "Sun",
            modifier = Modifier
                .size(iconHeight)
                .padding(4.dp)
        )

        PowerFlowView(
            amount = amount,
            modifier = Modifier.height(200.dp)
        )
    }
}