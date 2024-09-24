package pt.isel

class TPCC {

    private val dbManager = DatabaseManager()

    fun initApi() : TpccApi {
        return TpccApi(dbManager, 21474836, true)
    }

    fun initAsyncApi() : TpccAsyncApi {
        return TpccAsyncApi(dbManager, 21474836, true)
    }
}
