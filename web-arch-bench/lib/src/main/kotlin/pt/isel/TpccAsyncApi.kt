package pt.isel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import syncApi.*

class TpccAsyncApi(private val dataSource: DatabaseManager, val fetchSize:Int, val joins: Boolean)  {

    private val jsonContent = ConfigUtils.readConfig()
    val config = ConfigUtils.parseConfig(jsonContent)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = Dispatchers.IO.limitedParallelism(config.asyncParallelism)

    fun ping(): String {
        return "Pong"
    }

    suspend fun newOrder(
        wId: Int,
        dId: Int,
        cId: Int,
        itemCnt: Int,
        allLinesLocal: Int,
        itemId: IntArray?,
        supWare: IntArray?,
        qty: IntArray?
    ): Int {
        val conn = dataSource.getConnection()
        return withContext(dispatcher) {
            conn.use { conn ->
                val pStmts = TpccStatements(conn, fetchSize)
                val newOrder = NewOrder(pStmts, joins)
                newOrder.neword(0, wId, dId, cId, itemCnt, allLinesLocal, itemId, supWare, qty)
            }

        }
    }

    suspend fun payment(
        wId: Int,
        dId: Int,
        byName: Int,
        cWId: Int,
        cDId: Int,
        cId: Int,
        cLastName: String?,
        amount: Float
    ): Int {
        val conn = dataSource.getConnection()
        return withContext(dispatcher) {
            conn.use { conn ->
                val pStmts = TpccStatements(conn, fetchSize)
                val payment = Payment(pStmts)
                payment.payment(0,wId,dId,cId,cWId,cDId,cId,cLastName,amount)
            }
        }
    }

    suspend fun orderStat(wId: Int, dId: Int, byName: Int, cId: Int, cLastName: String?): Int {
        val conn = dataSource.getConnection()
        return withContext(dispatcher) {
            conn.use { conn ->
                val pStmts = TpccStatements(conn, fetchSize)
                val orderStat = OrderStat(pStmts)
                orderStat.ordStat(0, wId, dId, byName, cId, cLastName)
            }
        }
    }


    suspend fun delivery(wId: Int, oCarrierId: Int): Int {
        val conn = dataSource.getConnection()
        return withContext(dispatcher) {
            conn.use { conn ->
                val pStmts = TpccStatements(conn, fetchSize)
                val delivery = Delivery(pStmts)
                delivery.delivery(wId,oCarrierId)
            }
        }
    }

    suspend fun stockLevel(wId: Int, dId: Int, stockLevel: Int): Int {
        val conn = dataSource.getConnection()
        return withContext(dispatcher) {
            conn.use { conn ->
                val pStmts = TpccStatements(conn, fetchSize)
                val stockLevelStore = Slev(pStmts)
                stockLevelStore.slev(0,wId,dId,stockLevel)
            }
        }
    }
}