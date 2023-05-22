package com.alpriest.energystats.ui.graph

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.core.DefaultColors
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes

@Composable
internal fun chartStyle(columnChartColors: List<Color>, lineChartColors: List<Color> = listOf()): ChartStyle {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val defaultColors = if (isSystemInDarkTheme) DefaultColors.Dark else DefaultColors.Light

    return ChartStyle(
        ChartStyle.Axis(
            axisLabelColor = Color(defaultColors.axisLabelColor),
            axisGuidelineColor = Color(defaultColors.axisGuidelineColor),
            axisLineColor = Color(defaultColors.axisLineColor),
            axisLabelTextAlign = Paint.Align.RIGHT,
            axisLabelTypeface = Typeface.DEFAULT,
            axisLabelTextSize = 8.sp
        ),
        ChartStyle.ColumnChart(
            columnChartColors.map { columnChartColor ->
                LineComponent(
                    columnChartColor.toArgb(),
                    DefaultDimens.COLUMN_WIDTH,
                    Shapes.rectShape,
                )
            },
        ),
        ChartStyle.LineChart(
            lineChartColors.map { lineChartColor ->
                LineChart.LineSpec(
                    lineColor = lineChartColor.toArgb(),
                )
            },
        ),
        ChartStyle.Marker(),
        Color(defaultColors.elevationOverlayColor),
    )
}
