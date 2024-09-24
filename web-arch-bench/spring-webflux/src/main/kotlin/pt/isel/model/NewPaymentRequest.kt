package pt.isel.model

data class NewPaymentRequest(
    val warehouseId: Int,
    val districtId: Int,
    val byName: Int,
    val customerWarehouseId: Int,
    val customerDistrictId: Int,
    val customerId: Int,
    val customerLastName: String?,
    val amount: Float
)
