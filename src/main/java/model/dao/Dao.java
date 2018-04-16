package model.dao;

public abstract class Dao {

    //Used for readability, not to call everytime DbManager.getInstance()
    protected static DbManager dbManager;

    protected Dao() {
        dbManager = DbManager.getInstance();
    }

}