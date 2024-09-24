package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkSettings (
    var db                  : Db       = Db(),
    var api                 : Api      = Api(),
    var benchmark           : Benchmark= Benchmark(),
    var url                 : String?  = null,
    var threadMax           : Int?     = null,
    var threadMaxIdle       : Int?     = null,
    var threadMinIdle       : Int?     = null,
    var threadMaxWaitMillis : Int?     = null,
    var asyncParallelism    : Int      = 128,
    @SerialName("ngnix_path") var ngnixPath         : String = "",
    @SerialName("ngnix_conf") var ngnixConf         : String = ""
)
