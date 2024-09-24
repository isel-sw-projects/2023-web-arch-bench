package pt.isel

import org.apache.http.entity.ContentType
import org.apache.jmeter.protocol.http.util.HTTPConstants
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import us.abstracta.jmeter.javadsl.core.controllers.PercentController
import us.abstracta.jmeter.javadsl.core.threadgroups.*

private val createOrderBody = "{\"warehouseId\": 1,\"districtId\": 1,\"customerId\": 1,\"orderLinesCount\": 1,\"allOrderLinesLocal\": 1,\"itemsId\": [1],\"supplyWarehouseIds\": [1],\"itensQuantity\": [3]}"
private val createPaymentBody = "{\"warehouseId\":1,\"districtId\":1,\"byName\":1,\"customerWarehouseId\":1,\"customerDistrictId\":1,\"customerId\":1,\"customerLastName\":\"fehguy\",\"amount\":23.95}"
private val createDeliveryBody = "{\"warehouseId\":10,\"carrierId\":3}"

fun getThreadGroup(url: String): DslDefaultThreadGroup {
    return  threadGroup().children(
        getPercentControllerCreateOrder(url),
        getPercentControllerGetOrder(url),
        getPercentControllerCreatePayment(url),
        getPercentControllerCreateDelivery(url),
        getPercentControllerGetByStock(url)
    )
}

fun getPercentControllerCreateOrder(url: String): PercentController? {
    val name = "Create Order Request"
    return percentController("45",
        httpSampler(name,"$url/order")
            .post(createOrderBody, ContentType.APPLICATION_JSON)
            .header("Accept", "application/json")
    )
}

fun getPercentControllerGetOrder(url: String): PercentController? {
    val name = "Get Order Request"
    return percentController("4",
        httpSampler(name,"$url/order/status")
            .method(HTTPConstants.GET)
            .header("Accept", "application/json")
            .param("wid", "1")
            .param("did", "2")
            .param("byname", "0")
            .param("cid", "2")
            .param("lastname", "xpto"),
    )
}
fun getPercentControllerCreatePayment(url: String): PercentController? {
    val name = "Create Payment Request"
    return percentController("43",
        httpSampler(name,"$url/payment")
            .post(createPaymentBody, ContentType.APPLICATION_JSON)
            .header("Accept", "application/json")
    )
}
fun getPercentControllerCreateDelivery(url: String): PercentController? {
    val name = "Create Delivery Request"
    return percentController("4",
        httpSampler(name,"$url/delivery")
            .post(createDeliveryBody, ContentType.APPLICATION_JSON)
            .header("Accept", "application/json")
    )
}
fun getPercentControllerGetByStock(url: String): PercentController? {
    val name = "Create by Stock Request"
    return percentController("4",
        httpSampler(name,"$url/stock")
            .method(HTTPConstants.GET)
            .header("Accept", "application/json")
            .param("wid", "1")
            .param("did", "2")
            .param("stockLevel", "2")
    )
}