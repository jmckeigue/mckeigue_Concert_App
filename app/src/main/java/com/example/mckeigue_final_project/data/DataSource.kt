package com.example.mckeigue_final_project.data

import androidx.compose.ui.res.painterResource
import com.example.mckeigue_final_project.R

object DataSource {
    val concerts = listOf(
        R.string.Tame_Impala,
        R.string.hippo_campus,
        R.string.dayglow,
        R.string.m83,
    )

    val quantityOptions = listOf(
        Pair(R.string.one_ticket, 1),
        Pair(R.string.two_ticket, 2),
        Pair(R.string.four_ticket, 4)
    )

    val concertImages = listOf(
        R.drawable.tame_impala,
        R.drawable.hippo_campus,
        R.drawable.dayglow,
        R.drawable.m83
    )

}