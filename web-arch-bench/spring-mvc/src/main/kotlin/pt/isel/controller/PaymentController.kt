package pt.isel.controller

import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController;
import pt.isel.ApiService
import pt.isel.model.NewOrderRequest
import pt.isel.model.NewPaymentRequest

@RestController
@RequestMapping("/api/async/payment")
class PaymentController (private val apiService: ApiService) {

    @PostMapping
    fun newPayment(@RequestBody req: NewPaymentRequest) : Int {
        var resp : Int
        runBlocking {
            resp = apiService.async.payment(req.warehouseId, req.districtId, req.byName,
                req.customerWarehouseId, req.customerDistrictId, req.customerId,
                req.customerLastName, req.amount)
        }
        return resp
    }

}

@RestController
@RequestMapping("/api/payment")
class PaymentSyncController (private val apiService: ApiService) {

    @PostMapping
    fun newPayment(@RequestBody req: NewPaymentRequest) : Int {
        return apiService.sync.payment(req.warehouseId, req.districtId, req.byName,
            req.customerWarehouseId, req.customerDistrictId, req.customerId,
            req.customerLastName, req.amount)
    }

}

