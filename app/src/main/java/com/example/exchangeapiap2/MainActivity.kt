package com.example.exchangeapiap2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                    Greeting("Android")
                }
            }
        }
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
    fun getCoins(): Flow<Resource<List<ExchangeDto>>> = flow {
        try {
            emit(Resource.Loading()) //indicar que estamos cargando

            val coins = api.getCoins() //descarga las monedas de internet, se supone quedemora algo

            emit(Resource.Success(coins)) //indicar que se cargo correctamente y pasarle las monedas
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
        exchangeRepository.getCoins().onEach { result ->
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