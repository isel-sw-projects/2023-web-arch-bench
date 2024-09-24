package pt.isel.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController;
import pt.isel.ApiService
import pt.isel.model.NewPaymentRequest

@RestController
@RequestMapping("/api/payment")
class PaymentController (private val apiService: ApiService) {

    @PostMapping
    suspend fun newPayment(@RequestBody req: NewPaymentRequest) : Int {
        return apiService.async.payment(req.warehouseId, req.districtId, req.byName,
            req.customerWarehouseId, req.customerDistrictId, req.customerId,
            req.customerLastName, req.amount)
    }

}
