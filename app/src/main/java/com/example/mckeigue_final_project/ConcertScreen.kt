@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.mckeigue_final_project

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mckeigue_final_project.data.DataSource
import com.example.mckeigue_final_project.ui.OrderSummaryScreen
import com.example.mckeigue_final_project.ui.OrderViewModel
import com.example.mckeigue_final_project.ui.SelectConcertScreen
import com.example.mckeigue_final_project.ui.StartScreen
import com.example.mckeigue_final_project.ui.SelectOptionScreen

enum class ConcertScreen(@StringRes val title: Int) {
    FirstScreen(title = R.string.start_screen),
    AppName(title = R.string.app_name),
    ConcertChoice(title = R.string.ticket_order),
    Date(title = R.string.choose_date),
    Summary(title = R.string.order_summary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertAppBar(
    currentScreen: ConcertScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = ConcertScreen.valueOf(
            backStackEntry?.destination?.route ?: ConcertScreen.FirstScreen.name
        )
   Scaffold(
       topBar = {
           ConcertAppBar(
               currentScreen = currentScreen,
               canNavigateBack = navController.previousBackStackEntry != null,
               navigateUp = { navController.navigateUp() }
           )
       }
   ) { innerPadding ->
       val uiState by viewModel.uiState.collectAsState()

       NavHost(
           navController = navController,
           startDestination = ConcertScreen.FirstScreen.name,
           modifier = Modifier.padding(innerPadding)
       ) {
           composable(route = ConcertScreen.FirstScreen.name) {
               StartScreen(
                   quantityOptions = DataSource.quantityOptions,
                   onNextButtonClicked = {
                       navController.navigate(ConcertScreen.AppName.name)
                   },
                   modifier = Modifier
                       .fillMaxSize()
                       .padding(dimensionResource(R.dimen.padding_small))
               )
           }
           composable(route = ConcertScreen.AppName.name) {
               SelectConcertScreen(
                   quantityOptions = DataSource.quantityOptions,
                   onNextButtonClicked = {
                       viewModel.setQuantity(it)
                       navController.navigate(ConcertScreen.ConcertChoice.name)
                   },
                   modifier = Modifier
                       .fillMaxSize()
                       .padding(dimensionResource(R.dimen.padding_small))
               )
           }
           composable(route = ConcertScreen.ConcertChoice.name) {
               val context = LocalContext.current
               SelectOptionScreen(
                   subtotal = uiState.price,
                   onNextButtonClicked = { navController.navigate(ConcertScreen.Date.name) },
                   onCancelButtonClicked = {
                       cancelOrderAndNavtoStart(viewModel, navController)
                   },
                   options = DataSource.concerts.map { id -> context.resources.getString(id) } ,
                   imageOptions = listOf(R.drawable.tame_impala, R.drawable.hippo_campus, R.drawable.dayglow, R.drawable.m83),
                   onSelectionChanged = { viewModel.setConcert(it) },
                   modifier = Modifier.fillMaxHeight()
               )
           }
           composable(route = ConcertScreen.Date.name) {
               SelectOptionScreen(
                   subtotal = uiState.price,
                   onNextButtonClicked = { navController.navigate(ConcertScreen.Summary.name) },
                   onCancelButtonClicked = {
                       cancelOrderAndNavtoStart(viewModel, navController)
                   },
                   options = uiState.concertOptions,
                   imageOptions = listOf(R.drawable.white, R.drawable.white, R.drawable.white, R.drawable.white,),
                   onSelectionChanged = { viewModel.setDate(it) },
                   modifier = Modifier.fillMaxHeight()
               )
           }
           composable(route = ConcertScreen.Summary.name) {
               val context = LocalContext.current
               OrderSummaryScreen(
                   orderUiState = uiState,
                   onCancelButtonClicked = {
                       cancelOrderAndNavtoStart(viewModel, navController)
                   },
                   onSendButtonClicked = { subject: String, summary: String ->
                       shareOrder(context, subject = subject, summary = summary)
                   },
                   modifier = Modifier.fillMaxHeight()
               )
           }
       }

   }
}

private fun cancelOrderAndNavtoStart(
    viewModel: OrderViewModel,
    navController: NavHostController
){
    viewModel.resetOrder()
    navController.popBackStack(ConcertScreen.FirstScreen.name, inclusive = false)
}

private fun shareOrder(context: Context, subject: String, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_ticket_order)
        )
    )
}