/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.daos;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;

/**
 *
 * @author lufiv
 */
public class ConnectionManager {
    
    private final HikariDataSource ds;
    
    private static final class SingletonHolder {

        private static final ConnectionManager INSTANCE = new ConnectionManager();
    }

    public static ConnectionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    public ConnectionManager(){
        HikariConfig config = new HikariConfig();
        config.setPoolName("AuthMeSQLitePool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:data.db");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        ds = new HikariDataSource(config);
    }
    
    public Connection getConnection(){
        try{
            return ds.getConnection();
        } catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }
    
    public boolean shutdown(){
        try {
            ds.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}
