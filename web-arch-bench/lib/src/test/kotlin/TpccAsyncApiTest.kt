/*
import pt.isel.TpccStatements
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pt.isel.DatabaseManager
import pt.isel.TpccAsyncApi
import java.sql.Connection
import java.sql.SQLException


class TpccAsyncApiTest {

    private lateinit var conn: Connection
    private lateinit var api: TpccAsyncApi
    private lateinit var pStmts: pt.isel.TpccStatements
    private lateinit var dbManager:DatabaseManager

    private fun connectDB() {
        try {
            Class.forName("org.h2.Driver")
            dbManager = DatabaseManager()
            conn = dbManager.getConnection()
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }
    private fun initApi() {
        api = TpccAsyncApi(dbManager, 21474836, true)
        pStmts = pt.isel.TpccStatements(conn, 21474836)
    }

    @BeforeEach
    fun setUp() {
        connectDB()
        initApi()
    }

    @AfterEach
    fun tearDown() {
        conn.close()
    }

    @Test
    fun newOrder() = runBlocking {
        val got = api.newOrder(1,1,1,1,1,intArrayOf(2),intArrayOf(2),intArrayOf(3))
        val expected = 1
        assertEquals(expected, got)

        val rs = pStmts.getStatement(37).executeQuery() //newOrder
        while (rs.next()) {
            assertEquals(4, rs.getInt(1))   //o_id
            assertEquals(1, rs.getInt(2))   //d_id
            assertEquals(1, rs.getInt(3))   //w_id
        }
    }

    @Test
    fun payment()  = runBlocking {
        val got = api.payment(1,1,3,1,1,3,"testLastName",1.2f)
        val expected = 1
        assertEquals(expected, got)

        val rs = pStmts.getStatement(38).executeQuery() //history
        while (rs.next()) {
            assertEquals(3,rs.getInt(1)) //h_c_id
            assertEquals(1,rs.getInt(2)) //h_c_d_id
            assertEquals(1,rs.getInt(3)) //h_c_w_id
            assertEquals(1,rs.getInt(4)) //h_d_id
            assertEquals(1,rs.getInt(5)) //h_w_id
            assertEquals(10f,rs.getFloat(7)) //h_amount
            assertEquals("OWSscWh1MMM7BNQP2",rs.getString(8)) //h_data
        }
    }

    @Test
    fun orderStat() = runBlocking {
        //This method only do reads so we don't have too much to test
        val got = api.orderStat(1,2,3,4,"testLastName")
        val expected = 1
        assertEquals(expected, got)
    }

    @Test
    fun delivery() = runBlocking {

        val got = api.delivery(1,1)
        val expected = 1
        assertEquals(expected, got)

        val rs = pStmts.getStatement(37).executeQuery() //newOrder
        while (rs.next()) {
            assertEquals(1,2)   //All FALSE
        }
    }

    @Test
    fun stockLevel() = runBlocking {
        //This method only do reads so we don't have too much to test
        val got = api.stockLevel(1,2,3)
        val expected = 1
        assertEquals(expected, got)
    }
}
*/