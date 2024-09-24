package pt.isel.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ApiService

@RestController
@RequestMapping("/api/stock")
class StockController (private val apiService: ApiService) {

    @GetMapping
    suspend fun stockByLevel(@RequestParam wid: Int, @RequestParam did: Int,
                     @RequestParam stockLevel: Int) : Int {
        return apiService.async.stockLevel(wid,did,stockLevel)
    }

}