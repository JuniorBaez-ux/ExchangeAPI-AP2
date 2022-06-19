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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangeapiap2.ui.theme.ExchangeApiAp2Theme
import com.example.exchangeapiap2.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException
import javax.inject.Inject


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
            items( state.coins){ exchange ->
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
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(exchange) }
        .padding(16.dp)
    ) {
        Text(
            text = exchange.name,
            style = MaterialTheme.typography.subtitle1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = exchange.description,
            style = MaterialTheme.typography.body1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = if(exchange.is_active) "Activa" else "Inactiva",
            color = if(exchange.is_active) Color.Green else Color.Red ,
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Text(
            text = exchange.last_updated,
            style = MaterialTheme.typography.overline,
            overflow = TextOverflow.Ellipsis
        )
    }

}

//RUTA: data/remote/dto
data class ExchangeDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val is_active: Boolean = false,
    val last_updated: String = ""
)

//RUTA: data/remote
interface ExchangeApi {
    @GET("/v1/coins")
    suspend fun getCoins(): List<ExchangeDto>

    @GET("/v1/coins/{coinId}")
    suspend fun getCoin(@Path("coinId") coinId: String): ExchangeDto
}

class ExchangeRepository @Inject constructor(
    private val api: ExchangeApi
) {
    fun getExchanges(): Flow<Resource<List<ExchangeDto>>> = flow {
        try {
            emit(Resource.Loading()) //indicar que estamos cargando

            val exchanges = api.getCoins() //descarga las monedas de internet, se supone quedemora algo

            emit(Resource.Success(exchanges)) //indicar que se cargo correctamente y pasarle las monedas
        } catch (e: HttpException) {
            //error general HTTP
            emit(Resource.Error(e.message ?: "Error HTTP GENERAL"))
        } catch (e: IOException) {
            //debe verificar tu conexion a internet
            emit(Resource.Error(e.message ?: "verificar tu conexion a internet"))
        }
    }
}

data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<ExchangeDto> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private var _state = mutableStateOf(CoinListState())
    val state: State<CoinListState> = _state

    init {
        exchangeRepository.getExchanges().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = CoinListState(isLoading = true)
                }

                is Resource.Success -> {
                    _state.value = CoinListState(coins = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = CoinListState(error = result.message ?: "Error desconocido")
                }
            }
        }.launchIn(viewModelScope)
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ExchangeApiAp2Theme {
        Greeting("Android")
    }
}