package pt.isel

import ConfigUtils
import org.apache.commons.dbcp2.BasicDataSource
import java.sql.Connection
import javax.sql.DataSource

class DatabaseManager () {
    companion object {

        val dataSource = initDatabase()
        private fun initDatabase(): DataSource {
            val jsonContent = ConfigUtils.readConfig()
            val config = ConfigUtils.parseConfig(jsonContent)

            val ds = BasicDataSource()
            ds.driverClassName = config.db.driverClassName
            ds.url = config.url
            ds.username = config.db.username
            ds.password = config.db.password
            return ds
        }

    }

    fun getConnection(): Connection {
        return dataSource.connection
    }

}