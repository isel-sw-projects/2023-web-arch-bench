package pt.isel

import java.util.*


enum class Dbms {
    MySQL, Oracle, Derby, SQLite, MSSQL, PostgreSQL, DB2, H2, OB_Oracle, OB_MySQL, DM, OpenGauss, Unknown;

    companion object {
        fun parse(name: String?): Dbms {
            if (Objects.isNull(name)) {
                return Unknown
            }
            val dbmsArray: Array<Dbms> = Dbms.values()
            for (dbms in dbmsArray) {
                if (dbms.name.equals(name, ignoreCase = true)) {
                    return dbms
                }
            }
            return Unknown
        }
    }
}