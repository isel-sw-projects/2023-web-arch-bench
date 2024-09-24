package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Benchmark (
    @SerialName("result_path") var resultPath            : String? = null,
    @SerialName("simultaneous_requests") var simultaneousRequests  : List<Int>? = null,
    @SerialName("duration_seconds") var durationSeconds       : Long = 50,
    @SerialName("ramp_up_seconds") var rampUpSeconds         : Long = 10,
)