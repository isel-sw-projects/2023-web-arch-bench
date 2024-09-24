package pt.isel.tpccStatement

import pt.isel.Dbms

object CreateStatements {
    fun createItemSQL(dbms: Dbms?): String {
        val sqlText =
            "create table item ( " +
                    "  i_id int not null," +
                    "  i_im_id int," +
                    "  i_name varchar(24)," +
                    "  i_price decimal(5,2), " +
                    "  i_data varchar(50), " +
                    "  constraint item_pk primary key(i_id)" +
                    ")"
        val mysql = "$sqlText Engine=InnoDB"
        val oracle =
            ("create table item ( " +
                    "  i_id int not null, " +
                    "  i_im_id int, " +
                    "  i_name varchar2(24), " +
                    "  i_price decimal(5,2), " +
                    "  i_data varchar2(50), " +
                    "  constraint item_pk primary key(i_id)" +
                    ")")
        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            else -> sqlText
        }
    }

    fun createWarehouseSQL(dbms: Dbms?): String {
        val sqlText =
            "create table warehouse ( " +
                    "  w_id smallint not null, " +
                    "  w_name varchar(10), " +
                    "  w_street_1 varchar(20), " +
                    "  w_street_2 varchar(20), " +
                    "  w_city varchar(20), " +
                    "  w_state char(2), " +
                    "  w_zip char(9), " +
                    "  w_tax decimal(4,4), " +
                    "  w_ytd decimal(12,2), " +
                    "  constraint warehouse_pk primary key(w_id)" +
                    ")"
        val mysql = "$sqlText Engine=InnoDB"
        val oracle =
            ("create table warehouse ( " +
                    "  w_id smallint not null, " +
                    "  w_name varchar2(10), " +
                    "  w_street_1 varchar2(20), " +
                    "  w_street_2 varchar2(20), " +
                    "  w_city varchar2(20), " +
                    "  w_state char(2), " +
                    "  w_zip char(9), " +
                    "  w_tax decimal(4,2), " +
                    "  w_ytd decimal(12,2), " +
                    "  constraint warehouse_pk primary key(w_id)" +
                    ")")

        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            else -> sqlText
        }
    }

    fun createStock(dbms: Dbms?): String {
        val sqlText =
            "create table stock ( " +
                    "  s_i_id int not null, " +
                    "  s_w_id smallint not null, " +
                    "  s_quantity smallint, " +
                    "  s_dist_01 char(24), " +
                    "  s_dist_02 char(24), " +
                    "  s_dist_03 char(24), " +
                    "  s_dist_04 char(24), " +
                    "  s_dist_05 char(24), " +
                    "  s_dist_06 char(24), " +
                    "  s_dist_07 char(24), " +
                    "  s_dist_08 char(24), " +
                    "  s_dist_09 char(24), " +
                    "  s_dist_10 char(24), " +
                    "  s_ytd decimal(8,0), " +
                    "  s_order_cnt smallint, " +
                    "  s_remote_cnt smallint, " +
                    "  s_data varchar(50), " +
                    "  constraint stock_pk primary key (s_w_id, s_i_id) " +
                    ")"
        val mysql = "$sqlText Engine=InnoDB"
        val oracle =
            ("create table stock( " +
                    "  s_i_id int not null, " +
                    "  s_w_id smallint not null, " +
                    "  s_quantity smallint, " +
                    "  s_dist_01 char(24), " +
                    "  s_dist_02 char(24), " +
                    "  s_dist_03 char(24), " +
                    "  s_dist_04 char(24), " +
                    "  s_dist_05 char(24), " +
                    "  s_dist_06 char(24), " +
                    "  s_dist_07 char(24), " +
                    "  s_dist_08 char(24), " +
                    "  s_dist_09 char(24), " +
                    "  s_dist_10 char(24), " +
                    "  s_ytd decimal(8,0), " +
                    "  s_order_cnt smallint, " +
                    "  s_remote_cnt smallint, " +
                    "  s_data varchar2(50), " +
                    "  constraint stock_pk primary key (s_w_id, s_i_id) " +
                    ")")

        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            else -> sqlText
        }
    }

    fun createDistrictSQL(dbms: Dbms?): String {
        val sqlText = "create table district( " +
                "  d_id tinyint not null, " +
                "  d_w_id smallint not null, " +
                "  d_name varchar(10), " +
                "  d_street_1 varchar(20), " +
                "  d_street_2 varchar(20), " +
                "  d_city varchar(20), " +
                "  d_state char(2), " +
                "  d_zip char(9), " +
                "  d_tax decimal(4,4), " +
                "  d_ytd decimal(12,2), " +
                "  d_next_o_id int, " +
                "  constraint district_pk primary key (d_w_id, d_id)" +
                ")"
        val mysql = "$sqlText Engine=InnoDB"
        val oracle =
            "create table district ( " +
                    "  d_id number(3,0) not null, " +
                    "  d_w_id smallint not null, " +
                    "  d_name varchar2(10), " +
                    "  d_street_1 varchar2(20), " +
                    "  d_street_2 varchar2(20), " +
                    "  d_city varchar2(20), " +
                    "  d_state char(2), " +
                    "  d_zip char(9), " +
                    "  d_tax decimal(4,2), " +
                    "  d_ytd decimal(12,2), " +
                    "  d_next_o_id int, " +
                    "  constraint district_pk primary key (d_w_id, d_id)" +
                    ")"

        val pgsql = ("create table district( " +
                "  d_id smallint not null, " +
                "  d_w_id smallint not null, " +
                "  d_name varchar(10), " +
                "  d_street_1 varchar(20), " +
                "  d_street_2 varchar(20), " +
                "  d_city varchar(20), " +
                "  d_state char(2), " +
                "  d_zip char(9), " +
                "  d_tax decimal(4,4), " +
                "  d_ytd decimal(12,2), " +
                "  d_next_o_id int, " +
                "  constraint district_pk primary key (d_w_id, d_id)" +
                ")")

        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            Dbms.PostgreSQL, Dbms.OpenGauss, Dbms.DB2, Dbms.H2, Dbms.Derby -> pgsql
            else -> sqlText
        }
    }

    fun createCustomerSQL(dbms: Dbms?): String {
        val sqlText =
            "create table customer ( " +
                    "  c_id int not null, " +
                    "  c_d_id tinyint not null, " +
                    "  c_w_id smallint not null, " +
                    "  c_first varchar(16), " +
                    "  c_middle char(2), " +
                    "  c_last varchar(16), " +
                    "  c_street_1 varchar(20), " +
                    "  c_street_2 varchar(20), " +
                    "  c_city varchar(20), " +
                    "  c_state char(2), " +
                    "  c_zip char(9), " +
                    "  c_phone char(16), " +
                    "  c_since datetime, " +
                    "  c_credit char(2), " +
                    "  c_credit_lim bigint, " +
                    "  c_discount decimal(4,4), " +
                    "  c_balance decimal(12,2), " +
                    "  c_ytd_payment decimal(12,2), " +
                    "  c_payment_cnt smallint, " +
                    "  c_delivery_cnt smallint, " +
                    "  c_data text, " +
                    "  constraint customer_pk primary key (c_w_id, c_d_id, c_id)" +
                    ")"
        val mysql = "$sqlText Engine=InnoDB"
        val oracle =
            ("create table customer ( " +
                    "  c_id number(10,0) not null,  " +
                    "  c_d_id number(3,0) not null, " +
                    "  c_w_id smallint not null, " +
                    "  c_first varchar2(16), " +
                    "  c_middle char(2), " +
                    "  c_last varchar2(16), " +
                    "  c_street_1 varchar2(20), " +
                    "  c_street_2 varchar2(20), " +
                    "  c_city varchar2(20), " +
                    "  c_state char(2), " +
                    "  c_zip char(9), " +
                    "  c_phone char(16), " +
                    "  c_since date, " +
                    "  c_credit char(2), " +
                    "  c_credit_lim number(24,0), " +
                    "  c_discount decimal(4,2), " +
                    "  c_balance decimal(12,2), " +
                    "  c_ytd_payment decimal(12,2), " +
                    "  c_payment_cnt smallint, " +
                    "  c_delivery_cnt smallint, " +
                    "  c_data clob, " +
                    "  constraint customer_pk primary key (c_w_id, c_d_id, c_id)" +
                    ")")
        val pgsql =
            ("create table customer ( " +
                    "  c_id int not null, " +
                    "  c_d_id smallint not null, " +
                    "  c_w_id smallint not null, " +
                    "  c_first varchar(16), " +
                    "  c_middle char(2), " +
                    "  c_last varchar(16), " +
                    "  c_street_1 varchar(20), " +
                    "  c_street_2 varchar(20), " +
                    "  c_city varchar(20), " +
                    "  c_state char(2), " +
                    "  c_zip char(9), " +
                    "  c_phone char(16), " +
                    "  c_since timestamp without time zone, " +
                    "  c_credit char(2), " +
                    "  c_credit_lim bigint, " +
                    "  c_discount decimal(4,4), " +
                    "  c_balance decimal(12,2), " +
                    "  c_ytd_payment decimal(12,2), " +
                    "  c_payment_cnt smallint, " +
                    "  c_delivery_cnt smallint, " +
                    "  c_data text, " +
                    "  constraint customer_pk primary key (c_w_id, c_d_id, c_id)" +
                    ")")
        val db2 =
            ("create table customer ( " +
                    "  c_id int not null, " +
                    "  c_d_id smallint not null, " +
                    "  c_w_id smallint not null, " +
                    "  c_first varchar(16), " +
                    "  c_middle char(2), " +
                    "  c_last varchar(16), " +
                    "  c_street_1 varchar(20), " +
                    "  c_street_2 varchar(20), " +
                    "  c_city varchar(20), " +
                    "  c_state char(2), " +
                    "  c_zip char(9), " +
                    "  c_phone char(16), " +
                    "  c_since timestamp, " +
                    "  c_credit char(2), " +
                    "  c_credit_lim bigint, " +
                    "  c_discount decimal(4,4), " +
                    "  c_balance decimal(12,2), " +
                    "  c_ytd_payment decimal(12,2), " +
                    "  c_payment_cnt smallint, " +
                    "  c_delivery_cnt smallint, " +
                    "  c_data clob, " +
                    "  constraint customer_pk primary key (c_w_id, c_d_id, c_id)" +
                    ")")

        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            Dbms.PostgreSQL, Dbms.OpenGauss, Dbms.H2 -> pgsql
            Dbms.DB2, Dbms.Derby -> db2
            else -> sqlText
        }
    }

    fun createHistorySQL(dbms: Dbms?): String {
        val sqlText =
            "create table history ( " +
                    "  h_c_id int, " +
                    "  h_c_d_id tinyint, " +
                    "  h_c_w_id smallint, " +
                    "  h_d_id tinyint, " +
                    "  h_w_id smallint, " +
                    "  h_date datetime, " +
                    "  h_amount decimal(6,2), " +
                    "  h_data varchar(24) " +
                    ")"
        val mysql = "$sqlText Engine=InnoDB"
        val oracle =
            ("create table history ( " +
                    "  h_c_id int, " +
                    "  h_c_d_id number(3,0), " +
                    "  h_c_w_id smallint, " +
                    "  h_d_id number(3,0), " +
                    "  h_w_id smallint, " +
                    "  h_date date, " +
                    "  h_amount decimal(6,2), " +
                    "  h_data varchar2(24) " +
                    ")")
        val pgsql =
            ("create table history ( " +
                    "  h_c_id int, " +
                    "  h_c_d_id smallint, " +
                    "  h_c_w_id smallint, " +
                    "  h_d_id smallint, " +
                    "  h_w_id smallint, " +
                    "  h_date timestamp without time zone, " +
                    "  h_amount decimal(6,2), " +
                    "  h_data varchar(24) " +
                    ")")
        val db2 =
            ("create table history ( " +
                    "  h_c_id int, " +
                    "  h_c_d_id smallint, " +
                    "  h_c_w_id smallint, " +
                    "  h_d_id smallint, " +
                    "  h_w_id smallint, " +
                    "  h_date timestamp, " +
                    "  h_amount decimal(6,2), " +
                    "  h_data varchar(24) " +
                    ")")

        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            Dbms.PostgreSQL, Dbms.OpenGauss, Dbms.H2 -> pgsql
            Dbms.DB2, Dbms.Derby -> db2
            else -> sqlText
        }
    }

    fun createOrdersSQL(dbms: Dbms?): String {
        val sqlText =
            "create table orders ( " +
                    "  o_id int not null, " +
                    "  o_d_id tinyint not null, " +
                    "  o_w_id smallint not null, " +
                    "  o_c_id int, " +
                    "  o_entry_d datetime, " +
                    "  o_carrier_id tinyint, " +
                    "  o_ol_cnt tinyint, " +
                    "  o_all_local tinyint, " +
                    "  constraint orders_pk primary key (o_w_id, o_d_id, o_id)" +
                    ")"
        val mysql = "$sqlText Engine=InnoDB"
        val oracle = ("create table orders ( " +
                "  o_id int not null, " +
                "  o_d_id number(3,0) not null, " +
                "  o_w_id smallint not null, " +
                "  o_c_id int, " +
                "  o_entry_d date, " +
                "  o_carrier_id number(3,0), " +
                "  o_ol_cnt number(3,0), " +
                "  o_all_local number(3,0), " +
                "  constraint orders_pk primary key (o_w_id, o_d_id, o_id)" +
                ")")
        val pgsql =
            ("create table orders ( " +
                    "  o_id int not null, " +
                    "  o_d_id smallint not null, " +
                    "  o_w_id smallint not null, " +
                    "  o_c_id int, " +
                    "  o_entry_d timestamp without time zone, " +
                    "  o_carrier_id smallint, " +
                    "  o_ol_cnt smallint, " +
                    "  o_all_local smallint, " +
                    "  constraint orders_pk primary key (o_w_id, o_d_id, o_id)" +
                    ")")
        val db2 =
            ("create table orders ( " +
                    "  o_id int not null, " +
                    "  o_d_id smallint not null, " +
                    "  o_w_id smallint not null, " +
                    "  o_c_id int, " +
                    "  o_entry_d timestamp, " +
                    "  o_carrier_id smallint, " +
                    "  o_ol_cnt smallint, " +
                    "  o_all_local smallint, " +
                    "  constraint orders_pk primary key (o_w_id, o_d_id, o_id)" +
                    ")")
        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            Dbms.PostgreSQL, Dbms.OpenGauss, Dbms.H2 -> pgsql
            Dbms.DB2, Dbms.Derby -> db2
            else -> sqlText
        }
    }

    fun createNewOrderSQL(dbms: Dbms?): String {
        val sqlText =
            ("create table new_order ( " +
                    "  no_o_id int not null, " +
                    "  no_d_id tinyint not null, " +
                    "  no_w_id smallint not null, " +
                    "  constraint new_order_pk primary key (no_w_id, no_d_id, no_o_id)" +
                    ")")
        val mysql = "$sqlText Engine=InnoDB"
        val oracle = ("create table new_order ( " +
                "  no_o_id int not null, " +
                "  no_d_id number(3,0) not null, " +
                "  no_w_id smallint not null, " +
                "  constraint new_order_pk primary key (no_w_id, no_d_id, no_o_id)" +
                ")")
        val pgsql =
            ("create table new_order ( " +
                    "  no_o_id int not null, " +
                    "  no_d_id smallint not null, " +
                    "  no_w_id smallint not null, " +
                    "  constraint new_order_pk primary key (no_w_id, no_d_id, no_o_id)" +
                    ")")
        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            Dbms.PostgreSQL, Dbms.OpenGauss, Dbms.DB2, Dbms.H2, Dbms.Derby -> pgsql
            else -> sqlText
        }
    }

    fun createOrderLineSQL(dbms: Dbms?): String {
        val sqlText =
            ("create table order_line ( " +
                    "  ol_o_id int not null, " +
                    "  ol_d_id tinyint not null, " +
                    "  ol_w_id smallint not null, " +
                    "  ol_number tinyint not null, " +
                    "  ol_i_id int, " +
                    "  ol_supply_w_id smallint, " +
                    "  ol_delivery_d datetime, " +
                    "  ol_quantity tinyint, " +
                    "  ol_amount decimal(6,2), " +
                    "  ol_dist_info char(24), " +
                    "  constraint order_line_pk primary key(ol_w_id, ol_d_id, ol_o_id, ol_number)" +
                    ")")
        val mysql = "$sqlText Engine=InnoDB"
        val oracle =
            ("create table order_line ( " +
                    "  ol_o_id int not null, " +
                    "  ol_d_id number(3,0) not null, " +
                    "  ol_w_id smallint not null, " +
                    "  ol_number  number(3,0) not null, " +
                    "  ol_i_id int, " +
                    "  ol_supply_w_id smallint, " +
                    "  ol_delivery_d date, " +
                    "  ol_quantity number(3,0), " +
                    "  ol_amount decimal(6,2), " +
                    "  ol_dist_info char(24), " +
                    "  constraint order_line_pk primary key(ol_w_id, ol_d_id, ol_o_id, ol_number)" +
                    ")")
        val pgsql =
            ("create table order_line ( " +
                    "  ol_o_id int not null, " +
                    "  ol_d_id smallint not null, " +
                    "  ol_w_id smallint not null, " +
                    "  ol_number smallint not null, " +
                    "  ol_i_id int, " +
                    "  ol_supply_w_id smallint, " +
                    "  ol_delivery_d timestamp without time zone, " +
                    "  ol_quantity smallint, " +
                    "  ol_amount decimal(6,2), " +
                    "  ol_dist_info char(24), " +
                    "  constraint order_line_pk primary key(ol_w_id, ol_d_id, ol_o_id, ol_number)" +
                    ")")
        val db2 =
            ("create table order_line ( " +
                    "  ol_o_id int not null, " +
                    "  ol_d_id smallint not null, " +
                    "  ol_w_id smallint not null, " +
                    "  ol_number smallint not null, " +
                    "  ol_i_id int, " +
                    "  ol_supply_w_id smallint, " +
                    "  ol_delivery_d timestamp, " +
                    "  ol_quantity smallint, " +
                    "  ol_amount decimal(6,2), " +
                    "  ol_dist_info char(24), " +
                    "  constraint order_line_pk primary key(ol_w_id, ol_d_id, ol_o_id, ol_number)" +
                    ")")
        return when (dbms) {
            Dbms.MySQL -> mysql
            Dbms.Oracle, Dbms.OB_Oracle -> oracle
            Dbms.PostgreSQL, Dbms.OpenGauss -> pgsql
            Dbms.DB2, Dbms.Derby -> db2
            else -> sqlText
        }
    }
}