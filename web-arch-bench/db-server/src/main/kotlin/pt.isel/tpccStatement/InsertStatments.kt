package pt.isel.tpccStatement

object InsertStatments {
    fun insertItemSQL(): String {
        return "insert into item values (?,?,?,?,?)"
    }
    fun insertWarehouseSQL(): String {
        return "insert into warehouse values (?,?,?,?,?,?,?,?,?)"
    }
    fun insertStockSQL(): String {
        return "insert into stock values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    }
    fun insertDistrictSQL(): String {
        return "insert into district values (?,?,?,?,?,?,?,?,?,?,?)"
    }
    fun insertCustomerSQL(): String {
        return "insert into customer values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    }
    fun insertHistorySQL(): String {
        return "insert into history values (?,?,?,?,?,?,?,?)"
    }
    fun insertOrdersSQL(): String {
        return "insert into orders values (?,?,?,?,?,?,?,?)"
    }
    fun insertNewOrdersSQL(): String {
        return "insert into new_order values (?,?,?)"
    }
    fun insertOrderLineSQL(): String {
        return "insert into order_line values (?,?,?,?,?,?,?,?,?,?)"
    }
}