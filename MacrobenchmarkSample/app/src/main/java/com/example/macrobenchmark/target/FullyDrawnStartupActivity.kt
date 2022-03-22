package com.example.macrobenchmark.target

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.doOnPreDraw

class FullyDrawnStartupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextBlock("Compose Macrobenchmark Target")
        }
    }

    @Composable
    fun ReportFullyDrawn(
        text: String
    ) {
        val localView: View = LocalView.current
        LaunchedEffect(text) {
            val activity = localView.context as? Activity
            if (activity != null) {
                localView.doOnPreDraw {
                    activity.reportFullyDrawn()
                }
            }
        }
    }

    @Composable
    fun TextBlock(
        text: String,
    ) {
        Text(text)
        ReportFullyDrawn(text)
    }
}
