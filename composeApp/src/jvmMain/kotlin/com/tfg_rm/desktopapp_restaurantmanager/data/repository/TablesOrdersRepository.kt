package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesOrdersDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toTablesOrders
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.TablesOrders

/**
 * Repository in charge of managing the combined data of tables and their respective orders.
 *
 * This class implements a simple in-memory caching strategy to minimize redundant network
 * requests. It serves as a shared data source for other repositories that need to
 * extract specific information about tables or orders.
 *
 * @property remote The remote data source used to fetch the unified table and order information.
 */
class TablesOrdersRepository (
    private val remote: TablesOrdersDataSource
) {

    /** * In-memory cache to store the last fetched list of [TablesOrders].
     */
    private var cache: List<TablesOrders>? = null

    /**
     * Retrieves the list of tables and their orders, using the cache if available.
     *
     * If the [cache] is null, it performs a network request, maps the DTOs to domain
     * models, and populates the cache before returning the data.
     *
     * @return A list of [TablesOrders] domain objects.
     * @throws Exception If the remote data source fails.
     */
    suspend fun getTablesAndOrders(): List<TablesOrders> {
        return cache ?: remote.getTablesOrders().map { it.toTablesOrders() }.also {
            cache = it
        }
    }

    /**
     * Invalidates the current cache, forcing the next call to [getTablesAndOrders]
     * to fetch fresh data from the remote source.
     */
    fun clearCache() {
        cache = null
    }
}