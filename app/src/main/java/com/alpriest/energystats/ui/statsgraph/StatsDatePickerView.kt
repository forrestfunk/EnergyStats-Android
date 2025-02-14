package com.alpriest.energystats.ui.statsgraph

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.res.stringResource
import com.alpriest.energystats.R
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

enum class DatePickerRange {
    DAY,
    MONTH,
    YEAR
}

@Composable
fun StatsDatePickerView(viewModel: StatsDatePickerViewModel, graphShowingState: MutableStateFlow<Boolean>, modifier: Modifier = Modifier) {
    val range = viewModel.rangeStream.collectAsState().value

    Row(modifier = modifier) {
        DateRangePicker(viewModel, range, graphShowingState)

        when (range) {
            DatePickerRange.DAY -> CalendarView(viewModel.dateStream)
            DatePickerRange.MONTH -> {
                MonthPicker(viewModel = viewModel)
                YearPicker(viewModel = viewModel)
            }

            DatePickerRange.YEAR -> YearPicker(viewModel = viewModel)
        }

        Spacer(modifier = Modifier.weight(1.0f))

        Button(
            modifier = Modifier
                .padding(end = 14.dp)
                .padding(vertical = 6.dp)
                .size(36.dp),
            onClick = { viewModel.decrease() },
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Left")
        }

        Button(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .size(36.dp),
            onClick = { viewModel.increase() },
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Right")
        }
    }
}

@Composable
private fun MonthPicker(viewModel: StatsDatePickerViewModel) {
    var showing by remember { mutableStateOf(false) }
    val month = viewModel.monthStream.collectAsState().value
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .padding(end = 14.dp)
    ) {
        Button(
            onClick = { showing = true }
        ) {
            calendar.set(Calendar.MONTH, month)
            Text(monthFormat.format(calendar.time))
        }

        DropdownMenu(expanded = showing, onDismissRequest = { showing = false }) {
            for (monthIndex in 0 until 12) {
                calendar.set(Calendar.MONTH, monthIndex)
                val monthName = monthFormat.format(calendar.time)
                DropdownMenuItem(onClick = {
                    viewModel.monthStream.value = monthIndex
                    showing = false
                }) {
                    Text(monthName)
                    if (monthIndex == month) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(imageVector = Icons.Default.Done, contentDescription = "checked")
                    }
                }
                if (monthIndex < 11) {
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun YearPicker(viewModel: StatsDatePickerViewModel) {
    var showing by remember { mutableStateOf(false) }
    val year = viewModel.yearStream.collectAsState().value
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .padding(end = 14.dp)
    ) {
        Button(
            onClick = { showing = true }
        ) {
            Text(year.toString())
        }

        DropdownMenu(expanded = showing, onDismissRequest = { showing = false }) {
            for (yearIndex in 2015..currentYear) {
                DropdownMenuItem(onClick = {
                    viewModel.yearStream.value = yearIndex
                    showing = false
                }) {
                    Text(yearIndex.toString())
                    if (yearIndex == year) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(imageVector = Icons.Default.Done, contentDescription = "checked")
                    }
                }
                if (yearIndex < currentYear) {
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun DateRangePicker(
    viewModel: StatsDatePickerViewModel,
    range: DatePickerRange,
    graphShowingState: MutableStateFlow<Boolean>
) {
    var showing by remember { mutableStateOf(false) }
    val graphShowing = graphShowingState.collectAsState()

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .padding(end = 14.dp)
    ) {
        Button(
            onClick = { showing = true },
            modifier = Modifier
                .padding(vertical = 6.dp)
                .size(36.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = showing,
            onDismissRequest = { showing = false }
        )
        {
            DropdownMenuItem(onClick = {
                viewModel.rangeStream.value = DatePickerRange.DAY
                showing = false
            }) {
                Row {
                    Text(stringResource(R.string.day))
                    if (range == DatePickerRange.DAY) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(imageVector = Icons.Default.Done, contentDescription = "checked")
                    }
                }
            }
            Divider()
            DropdownMenuItem(onClick = {
                viewModel.rangeStream.value = DatePickerRange.MONTH
                showing = false
            }) {
                Text(stringResource(R.string.month))
                if (range == DatePickerRange.MONTH) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.Done, contentDescription = "checked")
                }
            }
            Divider()
            DropdownMenuItem(onClick = {
                viewModel.rangeStream.value = DatePickerRange.YEAR
                showing = false
            }) {
                Text(stringResource(R.string.year))
                if (range == DatePickerRange.YEAR) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.Done, contentDescription = "checked")
                }
            }

            Divider(thickness = 4.dp)

            DropdownMenuItem(onClick = {
                graphShowingState.value = !graphShowing.value
                showing = false
            }) {
                Text(if (graphShowing.value) stringResource(R.string.hide_graph) else stringResource(R.string.show_graph))
                Spacer(modifier = Modifier
                    .weight(1f)
                    .widthIn(min = 50.dp))
                Icon(imageVector = Icons.Default.BarChart, contentDescription = "graph")
            }
        }
    }
}

@Composable
fun CalendarView(dateStream: MutableStateFlow<LocalDate>) {
    var showingDatePicker by remember { mutableStateOf(false) }

    val dateState = dateStream.collectAsState().value
    val millis = localDateToMillis(dateState)

    Box(modifier = Modifier
        .wrapContentSize(Alignment.BottomCenter)
        .padding(end = 14.dp)) {
        Button(
            onClick = { showingDatePicker = true }
        ) {
            Text(dateState.toString())
        }
        if (showingDatePicker) {
            Dialog(
                onDismissRequest = { showingDatePicker = false },
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    AndroidView(
                        { CalendarView(it) },
                        modifier = Modifier.wrapContentWidth(),
                        update = { views ->
                            views.date = millis
                            views.setOnDateChangeListener { _, year, month, dayOfMonth ->
                                val cal = Calendar.getInstance()
                                cal.set(year, month, dayOfMonth)
                                dateStream.value = millisToLocalDate(cal.timeInMillis)
                                showingDatePicker = false
                            }
                        }
                    )
                }
            }
        }
    }
}

fun localDateToMillis(localDate: LocalDate): Long {
    val localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
    val zoneId = ZoneId.systemDefault()
    return localDateTime.atZone(zoneId).toInstant().toEpochMilli()
}

fun millisToLocalDate(millis: Long): LocalDate {
    val instant = Instant.ofEpochMilli(millis)
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
    return localDateTime.toLocalDate()
}

@Preview(widthDp = 500, heightDp = 500)
@Composable
fun StatsDatePickerViewPreview() {
    StatsDatePickerView(viewModel = StatsDatePickerViewModel(MutableStateFlow(StatsDisplayMode.Day(LocalDate.now()))),
        graphShowingState = MutableStateFlow(false))
}