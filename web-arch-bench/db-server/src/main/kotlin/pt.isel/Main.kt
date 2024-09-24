package pt.isel

import ConfigUtils
import org.apache.commons.dbcp2.BasicDataSource
import org.h2.tools.Server

fun main() {
    val jsonContent = ConfigUtils.readConfig()
    val config = ConfigUtils.parseConfig(jsonContent)

    val ds = BasicDataSource()
    ds.driverClassName = config.db.driverClassName
    ds.url = config.db.jdbcUrl
    ds.username = config.db.username
    ds.password = config.db.password
    ds.maxTotal = config.db.maxTotal
    ds.maxIdle = config.db.maxIdle
    ds.minIdle = config.db.minIdle
    ds.maxWaitMillis = config.db.maxWaitMillis

    val loader = TpccLoader(ds, Dbms.parse(config.db.databaseDriver), config.db.warehouseNumber, 10)
    loader.createTpccTables()
    loader.doLoad()
    loader.doAddFksAndIndexes(true)
    loader.doGatherStatistics()
    loader.checkTableRows()

    Server.createTcpServer("-tcpAllowOthers", "-tcpPort", config.db.port).start()
}

