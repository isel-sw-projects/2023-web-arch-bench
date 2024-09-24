package pt.isel

import pt.isel.RandomHelper.getNow
import pt.isel.RandomHelper.lastName
import pt.isel.RandomHelper.nuRand
import pt.isel.RandomHelper.randomBitMap
import pt.isel.RandomHelper.randomBoolean
import pt.isel.RandomHelper.randomDecimal
import pt.isel.RandomHelper.randomInt
import pt.isel.RandomHelper.randomNumberString
import pt.isel.RandomHelper.randomPermutation
import pt.isel.RandomHelper.randomString
import java.sql.Connection
import java.sql.Timestamp
import java.sql.Types
import java.util.*
import pt.isel.TpccConstants.*
import pt.isel.tpccStatement.CreateStatements
import pt.isel.tpccStatement.InsertStatments

object TpccLoad {
    @Throws(Exception::class)
    fun createItem(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createItemSQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun loadItem(connection: Connection): Long {
        var i_id: Int  // Item ID
        var i_im_id: Int  // Image ID associated to Item
        var i_name: String?  // Item Name, varchar(24)
        var i_price: Double  // Item price, decimal(5,2)
        var i_data: String?  // Brand information, varchar(50)

        val sqlText: String = InsertStatments.insertItemSQL()
        var rows = 0L
        val origBitmap: BitSet = randomBitMap(MAX_ITEMS / 10, MAX_ITEMS)
        try {
            val stmt = connection.prepareStatement(sqlText)
            i_id = 1
            while (i_id <= MAX_ITEMS) {
                i_im_id = randomInt(1, 10000)
                i_name = randomString(14, 24)
                i_price = randomDecimal(2, 1.00, 100.00).toDouble()
                i_data = randomString(26, 50)
                if (origBitmap[i_id - 1]) {
                    val pos: Int = randomInt(0, i_data.length - 8)
                    i_data = replaceString(i_data, pos, "original")
                }
                stmt.setInt(1, i_id)
                stmt.setInt(2, i_im_id)
                stmt.setString(3, i_name)
                stmt.setDouble(4, i_price)
                stmt.setString(5, i_data)
                stmt.addBatch()
                rows++
                if (i_id % SQL_BATCH_SIZE == 0) {
                    stmt.executeBatch()
                }
                i_id++
            }
            stmt.executeBatch()
            stmt.close()
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    private fun replaceString(target: String?, startPos: Int, replacement: String): String {
        val originalLength = target!!.length
        if ((startPos < 0) or (startPos > originalLength)) {
            return target
        }
        var newString = target.substring(0, startPos)
        newString += replacement

        var newStringPos = newString.length
        if (newStringPos == originalLength) {
            return newString
        } else if (newStringPos > originalLength) {
            return newString.substring(0, originalLength)
        } else {
            newString += target.substring(newStringPos, originalLength)
        }
        return newString
    }

    @Throws(Exception::class)
    fun createWarehouse(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createWarehouseSQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun loadWarehouse(connection: Connection, dbms: Dbms?, wareCount: Int): Long {
        var w_id = 1
        var w_name: String? = null
        var w_street_1: String? = null
        var w_street_2: String? = null
        var w_city: String? = null
        var w_state: String? = null
        var w_zip: String? = null
        var w_tax = 0.0
        var w_ytd = 0.0

        val sqlText: String = InsertStatments.insertWarehouseSQL()
        var rows = 0L
        try {
            val stmt = connection.prepareStatement(sqlText)
            while (w_id <= wareCount) {
                w_name = randomString(6, 10)
                w_street_1 = randomString(10, 20)
                w_street_2 = randomString(10, 20)
                w_city = randomString(10, 20)
                w_state = randomString(2)
                w_zip = randomNumberString(9)
                w_tax = randomDecimal(4, 0.0000, 0.2001).toDouble()
                w_ytd = 300000.00
                stmt.setInt(1, w_id)
                stmt.setString(2, w_name)
                stmt.setString(3, w_street_1)
                stmt.setString(4, w_street_2)
                stmt.setString(5, w_city)
                stmt.setString(6, w_state)
                stmt.setString(7, w_zip)
                stmt.setDouble(8, w_tax)
                stmt.setDouble(9, w_ytd)
                stmt.executeUpdate()
                rows++
                w_id++
            }
            stmt.close()
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun createStock(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createStock(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun loadStock(connection: Connection, dbms: Dbms?, w_id: Int): Long {
        var s_i_id = 1
        val s_w_id = w_id
        var s_quantity: Int
        var s_dist_01: String?
        var s_dist_02: String?
        var s_dist_03: String?
        var s_dist_04: String?
        var s_dist_05: String?
        var s_dist_06: String?
        var s_dist_07: String?
        var s_dist_08: String?
        var s_dist_09: String?
        var s_dist_10: String?
        val s_ytd = 0
        val s_order_cnt = 0
        val s_remote_cnt = 0
        var s_data: String?
        val origBitmap: BitSet = randomBitMap(MAX_ITEMS / 10, MAX_ITEMS)
        val sqlText: String = InsertStatments.insertStockSQL()
        var rows = 0L
        try {
            val stmt = connection.prepareStatement(sqlText)
            s_i_id = 1
            while (s_i_id <= MAX_ITEMS) {
                s_quantity = randomInt(10, 100)
                s_dist_01 = randomString(24)
                s_dist_02 = randomString(24)
                s_dist_03 = randomString(24)
                s_dist_04 = randomString(24)
                s_dist_05 = randomString(24)
                s_dist_06 = randomString(24)
                s_dist_07 = randomString(24)
                s_dist_08 = randomString(24)
                s_dist_09 = randomString(24)
                s_dist_10 = randomString(24)
                s_data = randomString(26, 50)
                if (origBitmap[s_i_id - 1]) {
                    val pos: Int = randomInt(0, s_data!!.length - 8)
                    s_data = replaceString(s_data, pos, "original")
                }
                stmt.setInt(1, s_i_id)
                stmt.setInt(2, s_w_id)
                stmt.setInt(3, s_quantity)
                stmt.setString(4, s_dist_01)
                stmt.setString(5, s_dist_02)
                stmt.setString(6, s_dist_03)
                stmt.setString(7, s_dist_04)
                stmt.setString(8, s_dist_05)
                stmt.setString(9, s_dist_06)
                stmt.setString(10, s_dist_07)
                stmt.setString(11, s_dist_08)
                stmt.setString(12, s_dist_09)
                stmt.setString(13, s_dist_10)
                stmt.setInt(14, s_ytd)
                stmt.setInt(15, s_order_cnt)
                stmt.setInt(16, s_remote_cnt)
                stmt.setString(17, s_data)
                stmt.addBatch()
                rows++
                if (s_i_id % SQL_BATCH_SIZE == 0) {
                    stmt.executeBatch()
                }
                s_i_id++
            }
            stmt.executeBatch()
            stmt.close()
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun createDistrict(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createDistrictSQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun loadDistrict(connection: Connection, dbms: Dbms?, w_id: Int): Long {
        var d_id = 1
        val d_w_id = w_id
        var d_name: String?
        var d_street_1: String?
        var d_street_2: String?
        var d_city: String?
        var d_state: String?
        var d_zip: String?
        var d_tax: Double
        val d_ytd = 30000.0
        val d_next_o_id = 3001

        val sqlText: String = InsertStatments.insertDistrictSQL()
        var rows = 0L
        try {
            val stmt = connection.prepareStatement(sqlText)
            d_id = 1
            while (d_id <= DIST_PER_WARE) {
                d_name = randomString(6, 10)
                d_street_1 = randomString(10, 20)
                d_street_2 = randomString(10, 20)
                d_city = randomString(10, 20)
                d_state = randomString(2)
                d_zip = randomNumberString(9)
                d_tax = randomDecimal(4, 0.0000, 0.2001).toDouble()
                stmt.setInt(1, d_id)
                stmt.setInt(2, d_w_id)
                stmt.setString(3, d_name)
                stmt.setString(4, d_street_1)
                stmt.setString(5, d_street_2)
                stmt.setString(6, d_city)
                stmt.setString(7, d_state)
                stmt.setString(8, d_zip)
                stmt.setDouble(9, d_tax)
                stmt.setDouble(10, d_ytd)
                stmt.setInt(11, d_next_o_id)
                val ret = stmt.executeUpdate()
                rows += ret.toLong()
                d_id++
            }
            stmt.close()
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun createCustomer(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createCustomerSQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun createHistory(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createHistorySQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun loadCustomer(connection: Connection, dbms: Dbms?, d_id: Int, w_id: Int): Long {
        var c_id = 1
        val c_d_id = d_id
        val c_w_id = w_id
        var c_first: String?
        val c_middle = "OE"
        var c_last: String?
        var c_street_1: String?
        var c_street_2: String?
        var c_city: String?
        var c_state: String?
        var c_zip: String?
        var c_phone: String?
        var c_since: Timestamp?
        var c_credit: String
        val c_credit_lim: Long = 50000
        var c_discount: Double
        val c_balance = -10.0
        val c_ytd_payment = 10.0
        val c_payment_cnt = 1
        val c_delivery_cnt = 0
        var c_data: String? = null

        var h_c_id: Int
        var h_c_d_id: Int
        var h_c_w_id: Int
        var h_d_id: Int
        var h_w_id: Int
        var h_date: Timestamp?
        var h_amount: Double
        var h_data: String?

        val sqlText1: String = InsertStatments.insertCustomerSQL()
        val sqlText2: String = InsertStatments.insertHistorySQL()

        var rows = 0L
        try {
            val stmt1 = connection.prepareStatement(sqlText1)
            val stmt2 = connection.prepareStatement(sqlText2)
            while (c_id <= CUST_PER_DIST) {
                c_first = randomString(8, 16)
                c_last = if (c_id <= 1000) {
                    lastName(c_id - 1)
                } else {
                    lastName(nuRand(255, 0, 999))
                }

                c_street_1 = randomString(10, 20)
                c_street_2 = randomString(10, 20)
                c_city = randomString(10, 20)
                c_state = randomString(2)
                c_zip = randomNumberString(9)
                c_phone = randomNumberString(16)
                c_since = getNow()


                c_credit = if (randomBoolean()) {
                    "BC"
                } else {
                    "GC"
                }

                c_discount = randomDecimal(4, 0.0000, 0.5001).toDouble()
                c_data = randomString(300, 500)

                stmt1.setInt(1, c_id)
                stmt1.setInt(2, c_d_id)
                stmt1.setInt(3, c_w_id)
                stmt1.setString(4, c_first)
                stmt1.setString(5, c_middle)
                stmt1.setString(6, c_last)
                stmt1.setString(7, c_street_1)
                stmt1.setString(8, c_street_2)
                stmt1.setString(9, c_city)
                stmt1.setString(10, c_state)
                stmt1.setString(11, c_zip)
                stmt1.setString(12, c_phone)
                stmt1.setTimestamp(13, c_since)
                stmt1.setString(14, c_credit)
                stmt1.setLong(15, c_credit_lim)
                stmt1.setDouble(16, c_discount)
                stmt1.setDouble(17, c_balance)
                stmt1.setDouble(18, c_ytd_payment)
                stmt1.setInt(19, c_payment_cnt)
                stmt1.setInt(20, c_delivery_cnt)
                stmt1.setString(21, c_data)
                stmt1.addBatch()
                rows++

                h_c_id = c_id
                h_c_d_id = d_id
                h_c_w_id = w_id
                h_d_id = d_id
                h_w_id = w_id
                h_date = getNow()
                h_amount = 10.00
                h_data = randomString(12, 24)
                stmt2.setInt(1, h_c_id)
                stmt2.setInt(2, h_c_d_id)
                stmt2.setInt(3, h_c_w_id)
                stmt2.setInt(4, h_d_id)
                stmt2.setInt(5, h_w_id)
                stmt2.setTimestamp(6, h_date)
                stmt2.setDouble(7, h_amount)
                stmt2.setString(8, h_data)
                stmt2.addBatch()
                rows++
                if (c_id % SQL_BATCH_SIZE == 0) {
                    stmt1.executeBatch()
                    stmt2.executeBatch()
                }
                c_id++
            }
            stmt1.executeBatch()
            stmt2.executeBatch()
            stmt1.close()
            stmt2.close()
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    fun createOrders(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createOrdersSQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun createNewOrders(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createNewOrderSQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun createOrderLine(connection: Connection, dbms: Dbms?): Int {
        val sqlText: String = CreateStatements.createOrderLineSQL(dbms)
        return executeUpdate(connection, sqlText)
    }

    @Throws(Exception::class)
    fun loadOrders(connection: Connection, dbms: Dbms?, d_id: Int, w_id: Int): Long {
        var o_id: Int
        val o_d_id = d_id
        val o_w_id = w_id
        var o_c_id: Int
        var o_entry_d: Timestamp?
        var o_carrier_id: Int?
        var o_ol_cnt: Int
        var o_all_local = 1
        val nums: IntArray = randomPermutation(ORD_PER_DIST)

        var no_o_id: Int
        val no_d_id = d_id
        val no_w_id = w_id

        val sqlText1: String = InsertStatments.insertOrdersSQL()
        val sqlText2: String = InsertStatments.insertNewOrdersSQL()
        val sqlText3: String = InsertStatments.insertOrderLineSQL()

        var rows = 0L
        try {
            val stmt1 = connection.prepareStatement(sqlText1)
            val stmt2 = connection.prepareStatement(sqlText2)
            val stmt3 = connection.prepareStatement(sqlText3)
            o_id = 1
            while (o_id <= ORD_PER_DIST) {
                o_c_id = nums[o_id - 1] // array 's index start with 0
                o_entry_d = getNow()
                o_ol_cnt = randomInt(5, 15)
                o_all_local = 1
                if (o_id > 2100) { // the last 900 orders have not been delivered, o_carrier_id = null;
                    no_o_id = o_id
                    stmt1.setNull(6, Types.INTEGER)
                    stmt2.setInt(1, no_o_id)
                    stmt2.setInt(2, no_d_id)
                    stmt2.setInt(3, no_w_id)
                    stmt2.addBatch()
                    rows++
                } else {
                    o_carrier_id = randomInt(1, 10)
                    stmt1.setInt(6, o_carrier_id)
                }

                stmt1.setInt(1, o_id)
                stmt1.setInt(2, o_d_id)
                stmt1.setInt(3, o_w_id)
                stmt1.setInt(4, o_c_id)
                stmt1.setTimestamp(5, o_entry_d)
                //stmt.setInt(6, o_carrier_id); see above ...
                stmt1.setInt(7, o_ol_cnt)
                stmt1.setInt(8, o_all_local)
                stmt1.addBatch()
                rows++


                //-> order_line
                val ol_o_id = o_id
                val ol_d_id = d_id
                val ol_w_id = w_id
                var ol_number = 1
                var ol_i_id: Int
                val ol_supply_w_id = o_w_id
                var ol_delivery_d: Timestamp? = null
                val ol_quantity = 5
                var ol_amount = 0.0
                var ol_dist_info: String?

                ol_number = 1
                while (ol_number <= o_ol_cnt) {
                    ol_i_id = randomInt(1, MAX_ITEMS)
                    ol_dist_info = randomString(24)
                    // OL_DELIVERY_D = O_ENTRY_D if OL_O_ID < 2,101,  null otherwise;
                    // OL_AMOUNT = 0.00 if OL_O_ID < 2,101, random within [0.01 .. 9,999.99] otherwise;
                    if (ol_o_id > 2100) {
                        stmt3.setNull(7, Types.TIMESTAMP)
                        ol_amount = randomDecimal(2, 0.01, 100.00).toDouble()
                    } else {
                        ol_delivery_d = getNow()
                        stmt3.setTimestamp(7, ol_delivery_d)
                        ol_amount = 0.0
                    }
                    stmt3.setInt(1, ol_o_id)
                    stmt3.setInt(2, ol_d_id)
                    stmt3.setInt(3, ol_w_id)
                    stmt3.setInt(4, ol_number)
                    stmt3.setInt(5, ol_i_id)
                    stmt3.setInt(6, ol_supply_w_id)
                    stmt3.setInt(8, ol_quantity)
                    stmt3.setDouble(9, ol_amount)
                    stmt3.setString(10, ol_dist_info)
                    stmt3.addBatch()
                    rows++
                    ol_number++
                }

                if (o_id % SQL_BATCH_SIZE == 0) {
                    stmt1.executeBatch()
                    stmt2.executeBatch()
                    stmt3.executeBatch()
                }
                o_id++
            }
            stmt1.executeBatch()
            stmt2.executeBatch()
            stmt3.executeBatch()
            stmt1.close()
            stmt2.close()
            stmt3.close()
        } catch (e: Exception) {
            throw e
        }
        return rows
    }

    @Throws(Exception::class)
    private fun executeUpdate(connection: Connection, sqlText: String): Int {
        val stmt = connection.createStatement()
        val rows = stmt.executeUpdate(sqlText)
        stmt.close()
        return rows
    }
}