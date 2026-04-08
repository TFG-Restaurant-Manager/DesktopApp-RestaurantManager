package com.tfg_rm.desktopapp_restaurantmanager.util

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.AuthRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.EmployeesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.OrdersRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SessionManager
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.TokenProvider
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.EmployeesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.ScheduleRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.DishesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.IngredientsService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.DishesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ScheduleRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ScheduleService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.LoginService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrderHistoryService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EconomyService
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EconomyViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.InventoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.DishesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.LoginViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.NewOrderViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrderHistoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrdersViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ScheduleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.TablesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf

val appModule = module {
    // ── Network ─────────────────────────────────────────────────────────────
    single { TokenProvider() }

    single {
        val tokenProvider = get<TokenProvider>()
        HttpClient(OkHttp) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            defaultRequest {
                url("https://investigation-expects-formula-criterion.trycloudflare.com/")
                tokenProvider.getToken()?.let {
                    header("Authorization", "Bearer $it")
                }
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val status = clientException.response.status.value
                    // Only treat as session expiry when there is already a token
                    if ((status == 401 || status == 403) && tokenProvider.getToken() != null) {
                        runBlocking {
                            tokenProvider.clearToken()
                            SessionManager.notifySessionExpired()
                        }
                    }
                }
            }
        }
    }

    singleOf(::AuthRemoteDataSource)
    singleOf(::AuthRepository)
    singleOf(::OrdersRemoteDataSource)
    singleOf(::OrdersRepository)
    singleOf(::OrdersService)
    singleOf(::EmployeesRemoteDataSource)
    singleOf(::EmployeesRepository)
    singleOf(::EmployeesService)
    singleOf(::ScheduleRemoteDataSource)
    singleOf(::DishesRemoteDataSource)
    singleOf(::TablesRemoteDataSource)
    singleOf(::IngredientsRepository)
    singleOf(::IngredientsService)
    singleOf(::DishesRepository)
    singleOf(::DishesService)
    singleOf(::ScheduleRepository)
    singleOf(::ScheduleService)
    singleOf(::TablesRepository)
    singleOf(::TablesService)
    singleOf(::LoginService)
    singleOf(::OrderHistoryService)
    singleOf(::EconomyService)

    viewModelOf(::LoginViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::TablesViewModel)
    viewModelOf(::EmployeesViewModel)
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::InventoryViewModel)
    viewModelOf(::DishesViewModel)
    viewModelOf(::EconomyViewModel)
    viewModelOf(::OrderHistoryViewModel)
    viewModelOf(::NewOrderViewModel)
}
