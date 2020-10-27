package com.laioffer.job.db;

public class MySQLDBUtil {
    // (under RDS dashboard: connectivity -> endpoint) ↓Instance address
    private static final String INSTANCE = "laiproject-instance.c7dc7no8ktso.us-east-2.rds.amazonaws.com";
    private static final String PORT_NUM = "3306";
    //  (under instance configuration page) ↓database name
    public static final String DB_NAME = "laiproject";
    private static final String USERNAME = "admin";
    // 显示pwd不是很合理。一般production的database都是通过公钥&密钥去access
    private static final String PASSWORD = "yy12345678";
    public static final String URL = "jdbc:mysql://"
            + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
            + "?user=" + USERNAME + "&password=" + PASSWORD
            + "&autoReconnect=true&serverTimezone=UTC";
}
