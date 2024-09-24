package pt.isel.controller

import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ApiService
import pt.isel.model.NewDeliveryRequest

@RestController
@RequestMapping("/api/async/delivery")
class DeliveryController (private val apiService: ApiService) {

    @PostMapping
    fun newDelivery(@RequestBody req: NewDeliveryRequest) : Int {
        var resp : Int
        runBlocking {
            resp = apiService.async.delivery(req.warehouseId, req.carrierId)
        }
        return resp
    }

}

@RestController
@RequestMapping("/api/delivery")
class DeliverySyncController (private val apiService: ApiService) {

    @PostMapping
    fun newDelivery(@RequestBody req: NewDeliveryRequest) : Int {
        return apiService.sync.delivery(req.warehouseId, req.carrierId)
    }

}
