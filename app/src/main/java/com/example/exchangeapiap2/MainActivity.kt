package com.example.exchangeapiap2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exchangeapiap2.data.remote.dto.ExchangeDto
import com.example.exchangeapiap2.model.ExchangeViewModel
import com.example.exchangeapiap2.ui.theme.ExchangeApiAp2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExchangeApiAp2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ExchangeListScreen()
                }
            }
        }
    }
}

@Composable
fun ExchangeListScreen(
    viewModel: ExchangeViewModel = hiltViewModel()
) {

    val state = viewModel.state.value

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()){
            items( state.exchange){ exchange ->
                ExchangeItem(exchange = exchange, {})
            }
        }

        if (state.isLoading)
            CircularProgressIndicator()

    }

}

@Composable
fun ExchangeItem(
    exchange:ExchangeDto,
    onClick : (ExchangeDto) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(exchange) }
        .padding(16.dp)
    ) {
        Text(
            text = exchange.name,
            style = MaterialTheme.typography.h5,
            overflow = TextOverflow.Ellipsis
        )

        Text(text =  "${exchange.description}" ,
            style = MaterialTheme.typography.body2,)

        Row(modifier = Modifier.fillMaxWidth()
            .height(30.dp).padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if(exchange.active) "Activa" else "Inactiva",
                color = if(exchange.active) Color.Green else Color.Red ,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.body2,
            )

            Text(
                text = exchange.last_updated,

                )
        }

    }

}