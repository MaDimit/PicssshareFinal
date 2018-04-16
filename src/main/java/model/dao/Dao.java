package model.dao;

import java.sql.Connection;

abstract class Dao {

    //Used for readability, not to call everytime DbManager.getInstance()
    protected static DbManager dbManager;
    protected static Connection conn;

    protected Dao() {
        dbManager = DbManager.getInstance();
        conn = dbManager.getConnection();
    }

}