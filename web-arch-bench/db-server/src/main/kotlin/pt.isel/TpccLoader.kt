package pt.isel

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.LoggerFactory
import pt.isel.TpccConstants.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy
import kotlin.system.exitProcess

class TpccLoader(ds: BasicDataSource, dbm: Dbms, wareCount: Int, threads: Int) {
    private val dataSource: BasicDataSource = ds
    private val dbms: Dbms = dbm
    private val threads: Int = if (threads > 0) threads else 1
    private val wareCount: Int = if (wareCount > 0) wareCount else 1

    fun createTpccTables() {
        val beginTime = LocalDateTime.now()
        LOGGER.info(">>>> Creating TPC-C's tables:")
        try {
            dataSource.connection.use { connection ->
                TpccLoad.createItem(connection, dbms)
                LOGGER.info("....   item done.")

                TpccLoad.createWarehouse(connection, dbms)
                LOGGER.info("....   warehouse done.")

                TpccLoad.createDistrict(connection, dbms)
                LOGGER.info("....   district done.")

                TpccLoad.createStock(connection, dbms)
                LOGGER.info("....   stock done.")

                TpccLoad.createCustomer(connection, dbms)
                LOGGER.info("....   customer done.")

                TpccLoad.createHistory(connection, dbms)
                LOGGER.info("....   history done.")

                TpccLoad.createOrders(connection, dbms)
                LOGGER.info("....   orders done.")

                TpccLoad.createNewOrders(connection, dbms)
                LOGGER.info("....   new_order done.")

                TpccLoad.createOrderLine(connection, dbms)
                LOGGER.info("....   order_line done.")
                val endTime: LocalDateTime = LocalDateTime.now()
                val duration: Duration = Duration.between(beginTime, endTime)
                val runTime: Long = duration.toMillis()
                LOGGER.info(">>>> Tables created, elapsed $runTime ms.")
            }
        } catch (e: Exception) {
            LOGGER.error("Create TPC-C tables failed.", e)
        }
    }

    fun doLoad() {
        LOGGER.info(">>>> TPC-C Data Loading...")
        val loadBeginTime = LocalDateTime.now()
        val queueSize = 1 + 1 + wareCount + wareCount + wareCount * 10 + wareCount * 10
        val factory: ThreadFactory = ThreadFactoryBuilder().setNameFormat("tpcc-loader-pool-%d").build()
        val executor = ThreadPoolExecutor(
            threads, threads, 0L, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(queueSize), factory, AbortPolicy()
        )
        val futures = LinkedList<Future<Long>>()
        //// item
        val itemFuture = executor.submit(ItemLoader())
        futures.add(itemFuture)
        //// warehouse
        val warehousesFuture = executor.submit(WarehouseLoader())
        futures.add(warehousesFuture)

        for (w in 1..wareCount) {
            val w_id = w
            // stock threads (wareCount)
            val stockFuture = executor.submit(StockLoader(w_id))
            futures.add(stockFuture)

            // district threads (wareCount)
            val districtFuture = executor.submit(DistrictLoader(w_id))
            futures.add(districtFuture)
            for (d in 1..DIST_PER_WARE) {
                val d_id = d
                ///// customer & history threads (wareCount*10)
                val customerFuture = executor.submit<Long>(CustomerLoader(w_id, d_id))
                futures.add(customerFuture)


                ///// orders & new-order & order-line threads (wareCount*10).
                val ordersFuture = executor.submit<Long>(OrdersLoader(w_id, d_id))
                futures.add(ordersFuture)
            }
        }
        executor.shutdown()

        var waitCount: Long = 1
        var rows = 0.0
        while (true) {
            val threadCount = futures.size
            var doneCount = 0
            for (f: Future<*> in futures) {
                if (f.isDone) {
                    doneCount++
                }
            }

            if (waitCount % 6000L == 0L) {
                LOGGER.info("$doneCount of $threadCount tasks done.")
            }

            if (doneCount == threadCount) {
                for (f: Future<Long> in futures) {
                    try {
                        rows += f.get().toDouble()
                    } catch (_: Exception) {
                    }
                }
                break
            }
            try {
                TimeUnit.MICROSECONDS.sleep(100)
            } catch (_: InterruptedException) {
            }
            waitCount++
        }
        val loadEndTime = LocalDateTime.now()
        val duration = Duration.between(loadBeginTime, loadEndTime)
        val loadRunTime = duration.seconds
        val rps = if ((loadRunTime > 0)) (rows / loadRunTime) else 0.0
        LOGGER.info(">>>> TPC-C Data Load completed, elapsed " + loadRunTime + " secs. ( " + rps.toLong() + " rows/sec ).")
    }

    fun doAddFksAndIndexes(enableFk: Boolean) {
        LOGGER.info(">>>> Creating TPC-C table's indexes and constraints:")
        try {
            dataSource.connection.use { connection ->
                addIndexes(connection)
                if (enableFk) {
                    addForeignKeys(connection)
                }
                LOGGER.info(">>>>   indexes and constraints done.")
            }
        } catch (e: Exception) {
            LOGGER.error("Create table's indexes and constraints failed.", e)
        }
    }

    fun doAddForeignKeys() {
        LOGGER.info(">>>> Creating TPC-C table's foreign keys:")
        try {
            dataSource.connection.use { connection ->
                addForeignKeys(connection)
                LOGGER.info(">>>>   foreign keys created.")
            }
        } catch (e: Exception) {
            LOGGER.error("Drop TPC-C table's foreign key failed.", e)
        }
    }

    @Throws(Exception::class)
    private fun addIndexes(connection: Connection) {
        val index1 = "create index idx_customer on customer (c_w_id,c_d_id,c_last,c_first)"
        executeUpdate(connection, index1)

        val index2 = "create index idx_orders on orders (o_w_id,o_d_id,o_c_id,o_id)"
        executeUpdate(connection, index2)

        if (dbms != Dbms.DM) {
            val index3 = "create index fkey_stock_2 on stock (s_i_id)"
            executeUpdate(connection, index3)

            val index4 = "create index fkey_order_line_2 on order_line (ol_supply_w_id,ol_i_id)"
            executeUpdate(connection, index4)
        }

        val index5 = "create index idx_stock_1 on stock (s_i_id, s_w_id)"
        executeUpdate(connection, index5)

        val index6 = "create index idx_district_1 on district (d_id, d_w_id)"
        executeUpdate(connection, index6)


        //因为不同数据库系统的语法有差异, PK在创建表的时候指定, 不再通过alter table ... add constraint方式添加方式。
    }

    @Throws(Exception::class)
    private fun addForeignKeys(connection: Connection) {
        if (dbms == Dbms.SQLite) {
            // SQLite does not support ADD CONSTRAINT in the ALTER TABLE statement.
            return
        }
        val fk1 = "alter table district add constraint fkey_district_1 foreign key(d_w_id) references warehouse(w_id)"
        executeUpdate(connection, fk1)
        val fk2 =
            "alter table customer add constraint fkey_customer_1 foreign key(c_w_id,c_d_id) references district(d_w_id,d_id)"
        executeUpdate(connection, fk2)
        val fk3 =
            "alter table history add constraint fkey_history_1 foreign key(h_c_w_id,h_c_d_id,h_c_id) references customer(c_w_id,c_d_id,c_id)"
        executeUpdate(connection, fk3)
        val fk4 =
            "alter table history add constraint fkey_history_2 foreign key(h_w_id,h_d_id) references district(d_w_id,d_id)"
        executeUpdate(connection, fk4)
        val fk5 =
            "alter table new_order add constraint fkey_new_orders_1 foreign key(no_w_id,no_d_id,no_o_id) references orders(o_w_id,o_d_id,o_id)"
        executeUpdate(connection, fk5)
        val fk6 =
            "alter table orders add constraint fkey_orders_1 foreign key(o_w_id,o_d_id,o_c_id) references customer(c_w_id,c_d_id,c_id)"
        executeUpdate(connection, fk6)
        val fk7 =
            "alter table order_line add constraint fkey_order_line_1 foreign key(ol_w_id,ol_d_id,ol_o_id) references orders(o_w_id,o_d_id,o_id)"
        executeUpdate(connection, fk7)
        val fk8 =
            "alter table order_line add constraint fkey_order_line_2 foreign key(ol_supply_w_id,ol_i_id) references stock(s_w_id,s_i_id)"
        executeUpdate(connection, fk8)
        val fk9 = "alter table stock add constraint fkey_stock_1 foreign key(s_w_id) references warehouse(w_id)"
        executeUpdate(connection, fk9)
        val fk10 = "alter table stock add constraint fkey_stock_2 foreign key(s_i_id) references item(i_id)"
        executeUpdate(connection, fk10)
    }

    @Throws(Exception::class)
    fun doGatherStatistics() {
        LOGGER.info("Gather statistics...")
        if (dbms == Dbms.DM || dbms == Dbms.OB_Oracle) {
            val estimatePct = 85
            val blockSample = true
            val methodOpt = "FOR ALL COLUMNS SIZE AUTO"
            val degree = threads
            callDbmsStats(estimatePct, blockSample, methodOpt, degree)
        } else if ((dbms == Dbms.PostgreSQL) || dbms == Dbms.OpenGauss) {
            val tables = arrayOf(
                "item",
                "warehouse",
                "stock",
                "district",
                "customer",
                "history",
                "orders",
                "new_order",
                "order_line"
            )
            for (table: String in tables) {
                dataSource.connection.use { connection ->
                    connection.createStatement().use { stmt ->
                        val sqlText = "analyze $table"
                        LOGGER.info("  $sqlText")
                        stmt.execute(sqlText)
                    }
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun callDbmsStats(estimatePct: Int, blockSample: Boolean, methodOpt: String, degree: Int) {
        LOGGER.info("  call DBMS_STATS.GATHER_SCHEMA_STATS ...")
        dataSource.connection.use { connection ->
            connection
                .prepareStatement("SELECT SYS_CONTEXT ('userenv', 'current_schema') current_schema FROM DUAL;")
                .use { queryStmt ->
                    connection
                        .prepareCall("{call DBMS_STATS.GATHER_SCHEMA_STATS(?, ?, ?, ?, ?)}").use { callStmt ->
                            queryStmt.executeQuery().use { rs ->
                                if (rs.next()) {
                                    val owner: String = rs.getString(1)
                                    callStmt.setString(1, owner)
                                    callStmt.setInt(2, estimatePct)
                                    callStmt.setBoolean(3, blockSample)
                                    callStmt.setString(4, methodOpt)
                                    callStmt.setInt(5, degree)
                                    callStmt.execute()
                                    LOGGER.info(">>>> Gather statistics for '$owner' done.")
                                }
                            }
                        }
                }
        }
    }

    fun doDropForeignKeys() {
        LOGGER.info(">>>> dropping TPC-C table's foreign-keys:")
        try {
            dataSource.connection.use { connection ->
                dropForeignKeys(connection)
                LOGGER.info(">>>> Foreign keys droped.")
            }
        } catch (e: Exception) {
            LOGGER.error("Drop TPC-C tables foreign key failed.", e)
        }
    }

    @Throws(Exception::class)
    private fun dropForeignKeys(connection: Connection) {
        var fk1 = "alter table district drop constraint fkey_district_1"
        var fk2 = "alter table customer drop constraint fkey_customer_1"
        var fk3 = "alter table history drop constraint fkey_history_1"
        var fk4 = "alter table history drop constraint fkey_history_2"
        var fk5 = "alter table new_order drop constraint fkey_new_orders_1"
        var fk6 = "alter table orders drop constraint fkey_orders_1"
        var fk7 = "alter table order_line drop constraint fkey_order_line_1"
        var fk8 = "alter table order_line drop constraint fkey_order_line_2"
        var fk9 = "alter table stock drop constraint fkey_stock_1"
        var fk10 = "alter table stock drop constraint fkey_stock_2"

        if (dbms == Dbms.MySQL) {
            fk1 = "alter table district drop foreign key fkey_district_1"
            fk2 = "alter table customer drop foreign key fkey_customer_1"
            fk3 = "alter table history drop foreign key fkey_history_1"
            fk4 = "alter table history drop foreign key fkey_history_2"
            fk5 = "alter table new_order drop foreign key fkey_new_orders_1"
            fk6 = "alter table orders drop foreign key fkey_orders_1"
            fk7 = "alter table order_line drop foreign key fkey_order_line_1"
            fk8 = "alter table order_line drop foreign key fkey_order_line_2"
            fk9 = "alter table stock drop foreign key fkey_stock_1"
            fk10 = "alter table stock drop foreign key fkey_stock_2"
        }

        executeUpdate(connection, fk1)
        executeUpdate(connection, fk2)
        executeUpdate(connection, fk3)
        executeUpdate(connection, fk4)
        executeUpdate(connection, fk5)
        executeUpdate(connection, fk6)
        executeUpdate(connection, fk7)
        executeUpdate(connection, fk8)
        executeUpdate(connection, fk9)
        executeUpdate(connection, fk10)
    }

    private fun dropTables(connection: Connection) {
        val tab1 = "drop table item"
        executeUpdate(connection, tab1)
        val tab2 = "drop table district"
        executeUpdate(connection, tab2)
        val tab3 = "drop table customer"
        executeUpdate(connection, tab3)
        val tab4 = "drop table history"
        executeUpdate(connection, tab4)
        val tab5 = "drop table new_order"
        executeUpdate(connection, tab5)
        val tab6 = "drop table orders"
        executeUpdate(connection, tab6)
        val tab7 = "drop table order_line"
        executeUpdate(connection, tab7)
        val tab8 = "drop table stock"
        executeUpdate(connection, tab8)
        val tab9 = "drop table warehouse"
        executeUpdate(connection, tab9)
    }

    fun doDropTables() {
        LOGGER.info(">>>> Drop TPC-C tables:")
        try {
            dataSource.connection.use { connection ->
                dropForeignKeys(connection)
                dropTables(connection)
                LOGGER.info(">>>> Tables dropped.")
            }
        } catch (e: Exception) {
            LOGGER.error("Drop TPC-C tables failed.", e)
        }
    }

    internal inner class WarehouseLoader() : Callable<Long> {
        @Throws(Exception::class)
        override fun call(): Long {
            var rows: Long
            val beginTime = LocalDateTime.now()
            LOGGER.info("Loading Warehouse data...")
            try {
                dataSource.connection.use { connection ->
                    rows = TpccLoad.loadWarehouse(connection, dbms, wareCount)
                }
            } catch (e: Exception) {
                LOGGER.error("Load Warehouses data failed, abort.", e)
                exitProcess(202)
            }
            val endTime = LocalDateTime.now()
            val duration = Duration.between(beginTime, endTime)
            val runTime = duration.toMillis()
            val rps = if ((runTime > 0)) (rows * 1000L) / runTime else 0L
            LOGGER.info("Warehouse done, $rows rows, elapsed $runTime ms. ($rps rows/sec)")
            return rows
        }
    }

    internal inner class ItemLoader() : Callable<Long> {
        @Throws(Exception::class)
        override fun call(): Long {
            var rows = 0L
            val beginTime = LocalDateTime.now()
            LOGGER.info("Loading Item data... ")
            try {
                dataSource.connection.use { connection ->
                    rows = TpccLoad.loadItem(connection)
                }
            } catch (e: Exception) {
                LOGGER.error("Load Item failed, abort.", e)
                System.exit(201)
            }
            val endTime = LocalDateTime.now()
            val duration = Duration.between(beginTime, endTime)
            val runTime = duration.toMillis()
            val rps = if ((runTime > 0)) (rows * 1000L) / runTime else 0L
            LOGGER.info("Item done, $rows rows, elapsed $runTime ms. ($rps rows/sec)")
            return rows
        }
    }

    internal inner class StockLoader(var w_id: Int) : Callable<Long> {
        @Throws(Exception::class)
        override fun call(): Long {
            var rows: Long
            val beginTime = LocalDateTime.now()
            LOGGER.info("Loading Stock (w_id=$w_id of $wareCount) ...")
            try {
                dataSource.connection.use { connection ->
                    rows = TpccLoad.loadStock(connection, dbms, w_id)
                }
            } catch (e: Exception) {
                LOGGER.error("Load Stock (w_id=$w_id) failed, abort.", e)
                exitProcess(203)
            }
            val endTime = LocalDateTime.now()
            val duration = Duration.between(beginTime, endTime)
            val runTime = duration.toMillis()
            val rps = if ((runTime > 0)) (rows * 1000L) / runTime else 0L
            LOGGER.info("Stock ($w_id of $wareCount) done, $rows rows, elapsed $runTime ms. ($rps rows/sec)")
            return rows
        }
    }

    internal inner class DistrictLoader(var w_id: Int) : Callable<Long> {
        @Throws(Exception::class)
        override fun call(): Long {
            var rows = 0L
            val beginTime = LocalDateTime.now()
            LOGGER.info("Loading District Wid=$w_id ... ")
            try {
                dataSource.connection.use { connection ->
                    rows = TpccLoad.loadDistrict(connection, dbms, w_id)
                }
            } catch (e: Exception) {
                LOGGER.error("Loading District Wid=$w_id failed, abort.", e)
                exitProcess(204)
            }
            val endTime = LocalDateTime.now()
            val duration = Duration.between(beginTime, endTime)
            val runTime = duration.toMillis()
            val rps = if ((runTime > 0)) (rows * 1000L) / runTime else 0L
            LOGGER.info("District Wid=$w_id done, $rows rows, elapsed $runTime ms. ($rps rows/sec)")
            return rows
        }
    }

    internal inner class CustomerLoader(var w_id: Int, var d_id: Int) : Callable<Long> {
        @Throws(Exception::class)
        override fun call(): Long {
            var rows = 0L
            val beginTime = LocalDateTime.now()
            LOGGER.info("Loading Customer, History for Did=$d_id, Wid=$w_id ... ")
            try {
                dataSource.connection.use { connection ->
                    rows = TpccLoad.loadCustomer(connection, dbms, d_id, w_id)
                }
            } catch (e: Exception) {
                LOGGER.error("Loading Customer, History for Did=$d_id, Wid=$w_id failed, abort.", e)
                exitProcess(205)
            }
            val endTime = LocalDateTime.now()
            val duration = Duration.between(beginTime, endTime)
            val runTime = duration.toMillis()
            val rps = if ((runTime > 0)) (rows * 1000L) / runTime else 0L
            LOGGER.info(
                "Customer, History for Did=" + d_id + ", Wid=" + w_id + " done, " + rows + " rows, elapsed "
                        + runTime + " ms. (" + rps + " rows/sec)"
            )
            return rows
        }
    }

    ///// Orders & New-Orders & Order-Line Loader threads.
    internal inner class OrdersLoader(var w_id: Int, var d_id: Int) : Callable<Long> {
        @Throws(Exception::class)
        override fun call(): Long {
            var rows = 0L
            val beginTime = LocalDateTime.now()
            LOGGER.info("Loading Orders, New-Order, Order-Line for Did=$d_id, Wid=$w_id ... ")
            try {
                dataSource.connection.use { connection ->
                    rows = TpccLoad.loadOrders(connection, dbms, d_id, w_id)
                }
            } catch (e: Exception) {
                LOGGER.error("Loading Orders, New-Order, Order-Line for Did=$d_id, Wid=$w_id failed, abort.", e)
                exitProcess(206)
            }
            val endTime = LocalDateTime.now()
            val duration = Duration.between(beginTime, endTime)
            val runTime = duration.toMillis()
            val rps = if ((runTime > 0)) (rows * 1000L) / runTime else 0L
            LOGGER.info(
                ("Orders, New-Order, Order-Line for Did=" + d_id + ", Wid=" + w_id + " done, " + rows
                        + " rows, elapsed " + runTime + " ms. (" + rps + " rows/sec)")
            )
            return rows
        }
    }

    @Throws(Exception::class)
    fun checkTableRows() {
        LOGGER.info(">>>> Checking TPC-C Tables:")
        LOGGER.info("...... Warehouse (w)                     : " + warehouseRows() + " rows.")
        LOGGER.info("...... Item (100000)                     : " + itemRows() + " rows.")
        LOGGER.info("...... Stock (w*100000)                  : " + stockRows() + " rows.")
        LOGGER.info("...... District (w*10)                   : " + districtRows() + " rows.")
        LOGGER.info("...... Customer (w*10*3000)              : " + customerRows() + " rows.")
        LOGGER.info("...... Order (number of customers)       : " + ordersRows() + " rows.")
        LOGGER.info("...... New-Order (30% of the orders)     : " + newOrderRows() + " rows.")
        LOGGER.info("...... Order-Line (approx. 10 per order) : " + orderLineRows() + " rows.")
        LOGGER.info("...... History (number of customers)     : " + historyRows() + " rows.")
        LOGGER.info(">>>> done.")
    }

    @Throws(Exception::class)
    fun warehouseRows(): Long {
        var rows = -1
        val sqlText = "select count(*) as cnt from warehouse"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getInt(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows.toLong()
    }

    @Throws(Exception::class)
    fun itemRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from item"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun stockRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from stock"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun customerRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from customer"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun districtRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from district"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun newOrderRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from new_order"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun orderLineRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from order_line"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun ordersRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from orders"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun historyRows(): Long {
        var rows: Long = -1
        val sqlText = "select count(*) as cnt from history"
        try {
            dataSource.connection.use { connection ->
                val stmt: Statement = connection.createStatement()
                val rs: ResultSet = stmt.executeQuery(sqlText)
                if (rs.next()) {
                    rows = rs.getLong(1)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TpccLoader::class.java)
        private fun executeUpdate(connection: Connection, sqlText: String): Long {
            val beginTime = LocalDateTime.now()
            try {
                val stmt = connection.createStatement()
                stmt.executeUpdate(sqlText)
                stmt.close()
                val endTime = LocalDateTime.now()
                val duration = Duration.between(beginTime, endTime)
                val runTime = duration.toMillis()
                LOGGER.info(".... $sqlText, elapsed: $runTime ms.")
                return runTime
            } catch (e: Exception) {
                LOGGER.error("execute update failed.", e)
            }
            return -1
        }
    }
}
