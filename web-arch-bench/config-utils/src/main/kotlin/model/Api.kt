package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Api (
    @SerialName("ports") var ports             : List<Int>? = null,
    @SerialName("project") var project         : String? = null,
    )