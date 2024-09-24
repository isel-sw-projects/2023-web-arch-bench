package pt.isel.model

data class NewOrderRequest (
    val warehouseId : Int,
    val districtId : Int,
    val customerId : Int,
    val orderLinesCount : Int,
    val allOrderLinesLocal : Int,
    val itemsId: IntArray?,
    val supplyWarehouseIds: IntArray?,
    val itensQuantity: IntArray?,
    )