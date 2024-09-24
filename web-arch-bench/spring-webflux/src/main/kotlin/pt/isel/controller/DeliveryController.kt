package pt.isel.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ApiService
import pt.isel.model.NewDeliveryRequest

@RestController
@RequestMapping("/api/delivery")
class DeliveryController ( private val apiService: ApiService) {
    @PostMapping
    suspend fun newDelivery(@RequestBody req: NewDeliveryRequest) :Int  {
        return apiService.async.delivery(req.warehouseId, req.carrierId)
    }

}
