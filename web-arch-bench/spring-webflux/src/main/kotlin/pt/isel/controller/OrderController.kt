package pt.isel.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ApiService
import pt.isel.model.NewOrderRequest

@RestController
@RequestMapping("/api/order")
class OrderController (private val apiService: ApiService) {

    @PostMapping
    suspend fun newOrder(@RequestBody req: NewOrderRequest) : Int {
        return  apiService.async.newOrder(req.warehouseId, req.districtId, req.customerId,
    req.orderLinesCount, req.allOrderLinesLocal, req.itemsId,
    req.supplyWarehouseIds, req.itensQuantity)
    }

    @GetMapping("/status")
    suspend fun orderStatus(@RequestParam wid: Int, @RequestParam did: Int,
                    @RequestParam byname: Int, @RequestParam cid: Int,
                    @RequestParam lastname: String?): Int {
        return apiService.async.orderStat(wid,did,byname,cid,lastname)
    }

}

