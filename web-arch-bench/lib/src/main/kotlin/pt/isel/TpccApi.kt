package pt.isel

import syncApi.*

class TpccApi(val dataSource: DatabaseManager, val fetchSize:Int, val joins: Boolean) {

    fun ping(): String {
        return "Pong"
    }

    fun newOrder(
        wId: Int,
        dId: Int,
        cId: Int,
        itemCnt: Int,
        allLinesLocal: Int,
        itemId: IntArray?,
        supWare: IntArray?,
        qty: IntArray?
    ): Int {
        dataSource.getConnection().use { conn ->
            val pStmts = TpccStatements(conn, fetchSize)
            val newOrder = NewOrder(pStmts, joins)
            return newOrder.neword(0,wId,dId,cId,itemCnt,allLinesLocal,itemId,supWare,qty)
        }
    }

    fun payment(
        wId: Int,
        dId: Int,
        byName: Int,
        cWId: Int,
        cDId: Int,
        cId: Int,
        cLastName: String?,
        amount: Float
    ): Int {
        dataSource.getConnection().use { conn ->
            val pStmts = TpccStatements(conn, fetchSize)
            val payment = Payment(pStmts)
            return payment.payment(0,wId,dId,cId,cWId,cDId,cId,cLastName,amount)
        }
    }

    fun orderStat(wId: Int, dId: Int, byName: Int, cId: Int, cLastName: String?): Int {
        dataSource.getConnection().use { conn ->
            val pStmts = TpccStatements(conn, fetchSize)
            val orderStat = OrderStat(pStmts)
            return orderStat.ordStat(0,wId,dId,byName,cId,cLastName)
        }
    }

    fun delivery(wId: Int, oCarrierId: Int): Int {
        dataSource.getConnection().use { conn ->
            val pStmts = TpccStatements(conn, fetchSize)
            val delivery = Delivery(pStmts)
            return delivery.delivery(wId,oCarrierId)
        }
    }

    fun stockLevel(wId: Int, dId: Int, stockLevel: Int): Int {
        dataSource.getConnection().use { conn ->
            val pStmts = TpccStatements(conn, fetchSize)
            val stockLevelStore = Slev(pStmts)
            return stockLevelStore.slev(0,wId,dId,stockLevel)
        }
    }
}
