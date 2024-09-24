package pt.isel

import org.springframework.stereotype.Component

@Component
class ApiService  {
    private final val tpcc = TPCC()
    val async = tpcc.initAsyncApi()
}