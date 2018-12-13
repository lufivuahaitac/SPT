/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author YTB
 */
public class Config implements Runnable {

    private static Logger logger = LogManager.getLogger(Config.class);
    private static final long serialVersionUID = 1L;
    private static PropertiesConfiguration databaseConfig;
    private static PropertiesConfiguration appConfig;
    
    public final static String DIR = System.getProperty("user.dir");
    

    /**
     * initConfig
     */
    public static void init() {

        try {

            String databseConfigPath = "./config/DatabaseConfig.properties";
            databaseConfig = new PropertiesConfiguration(databseConfigPath);
            databaseConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            
            String appConfigPath = "./config/AppConfig.properties";
            appConfig = new PropertiesConfiguration(appConfigPath);
            appConfig.setReloadingStrategy(new FileChangedReloadingStrategy());

            
        } catch (ConfigurationException e) {
            logger.error("Load file config failed ex: {}.", e);
        }
    }
    /**
     *
     * @return
     */
    public static PropertiesConfiguration getDatabaseConfig() {
        return databaseConfig;
    } 
    
    /**
     *
     * @return
     */
    public static PropertiesConfiguration getAppConfig() {
        return appConfig;
    } 


    @Override
    public void run() {
        init();
    }

}
