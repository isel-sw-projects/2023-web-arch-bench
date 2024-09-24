package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Db (
     var port            : String? = null,
     var jdbcUrl         : String? = null,
     var username        : String? = null,
     var password        : String? = null,
     var databaseDriver  : String = "H2",
     var driverClassName : String = "org.h2.Driver",
     var warehouseNumber : Int     = 1,
     @SerialName("max_connections_pool") var maxTotal: Int = -1,
     @SerialName("max_connections_idle") var maxIdle: Int = -1,
     @SerialName("min_connections_idle") var minIdle: Int = -1,
     @SerialName("max_wait_millis_conn") var maxWaitMillis: Long = -1
)