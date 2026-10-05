package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.ValueStreamWasteComponent
import com.example.ui.viewmodel.HospitalViewModel

/**
 * Value Stream Mapping (VSM) & Operational Waste (Muda) Tracking Screen
 * Visualizes the hospital patient journey lead time and enables clinical staff
 * to log and categorize operational waste (D.O.W.N.T.I.M.E.) encountered across hospital workflows.
 */
@Composable
fun ValueStreamMappingScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    ValueStreamWasteComponent(
        viewModel = viewModel,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("value_stream_mapping_screen")
    )
}
