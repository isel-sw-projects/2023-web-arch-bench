package pt.isel.controller

import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import pt.isel.ApiService
import pt.isel.model.NewOrderRequest

@RestController
@RequestMapping("/api/async/order")
class OrderController (private val apiService: ApiService) {

    @PostMapping
    fun newOrder(@RequestBody req: NewOrderRequest) : Int {
        var resp : Int
        runBlocking {
            resp = apiService.async.newOrder(req.warehouseId, req.districtId, req.customerId,
                req.orderLinesCount, req.allOrderLinesLocal, req.itemsId,
                req.supplyWarehouseIds, req.itensQuantity)
        }
        return resp
    }

    @GetMapping("/status")
    fun orderStatus(@RequestParam wid: Int, @RequestParam did: Int,
                    @RequestParam byname: Int, @RequestParam cid: Int,
                    @RequestParam lastname: String?): Int {
        var resp : Int
        runBlocking {
            resp = apiService.async.orderStat(wid,did,byname,cid,lastname)
        }
        return resp
    }

}

@RestController
@RequestMapping("/api/order")
class OrderSyncController (private val apiService: ApiService) {

    @PostMapping
    fun newOrder(@RequestBody req: NewOrderRequest) : Int {
        return apiService.sync.newOrder(req.warehouseId, req.districtId, req.customerId,
            req.orderLinesCount, req.allOrderLinesLocal, req.itemsId,
            req.supplyWarehouseIds, req.itensQuantity)
    }

    @GetMapping("/status")
    fun orderStatus(@RequestParam wid: Int, @RequestParam did: Int,
                    @RequestParam byname: Int, @RequestParam cid: Int,
                    @RequestParam lastname: String?): Int {
        return apiService.sync.orderStat(wid,did,byname,cid,lastname)

    }

}
