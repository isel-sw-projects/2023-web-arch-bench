package pt.isel.controller

import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import pt.isel.ApiService

@RestController
@RequestMapping("/api/async/stock")
class StockController (private val apiService: ApiService) {

    @GetMapping
    fun stockByLevel(@RequestParam wid: Int, @RequestParam did: Int,
                     @RequestParam stockLevel: Int) : Int {
        var resp : Int
        runBlocking {
            resp = apiService.async.stockLevel(wid,did,stockLevel)
        }
        return resp
    }

}

@RestController
@RequestMapping("/api/stock")
class StockSyncController (private val apiService: ApiService) {

    @GetMapping
    fun stockByLevel(@RequestParam wid: Int, @RequestParam did: Int,
                     @RequestParam stockLevel: Int) : Int {
        return apiService.sync.stockLevel(wid,did,stockLevel)
    }

}
