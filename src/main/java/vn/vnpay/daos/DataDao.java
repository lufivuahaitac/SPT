/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.vnpay.config.Config;

/**
 *
 * @author truongnq
 */
public class DataDao {

    private static final Logger LOGGER = LogManager.getLogger(DataDao.class);

    private static final class SingletonHolder {

        private static final DataDao INSTANCE = new DataDao();
    }

    public static DataDao getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final String demo = "{\n"
            + "  \"results\": [\n"
            + "    {\n"
            + "      \"alternatives\": [\n"
            + "        {\n"
            + "          \"transcript\": \"We'll add a while loop which takes the user's input from the console and write it to the server.\",\n"
            + "          \"confidence\": 0.876408\n"
            + "        }\n"
            + "      ],\n"
            + "      \"languageCode\": \"en-us\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"alternatives\": [\n"
            + "        {\n"
            + "          \"transcript\": \" And finally when we exit the loop for whatever reason will need to shut down the event Loop.\",\n"
            + "          \"confidence\": 0.88041484\n"
            + "        }\n"
            + "      ],\n"
            + "      \"languageCode\": \"en-us\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    public boolean insertText(final int id, final long uid, final String data, final int type) {
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            if (con == null) {
                LOGGER.info("ID: [{}] - UID: [{}] - Get Connect to Database insertText...failed", id, uid);
                LOGGER.debug("ID: [{}] - UID: [{}] - Data: {}", id, uid, data);
                return false;
            }
            st = con.prepareStatement("INSERT INTO TEXT_STORE (TEXT_ID, TEXT_UID, TEXT, TYPE) VALUES (?,?,?,?)");
            st.setInt(1, id);
            st.setLong(2, uid);
            st.setString(3, data);
            st.setInt(4, type);
            return st.execute();
        } catch (Exception ex) {
            LOGGER.error("ID: [{}] - UID: [{}] - insertText...failed. Error: {}", id, uid, ex);
            LOGGER.debug("ID: [{}] - UID: [{}] - Data: {}", id, uid, data);
        } finally {
            DBUtils.closeQuietly(st, con);
        }
        return false;
    }
    
    public String getFullText(final long uid) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            if (con == null) {
                LOGGER.info("UID: [{}] - Get Connect to Database insertText...failed", uid);
                return "";
            }
            st = con.prepareStatement("SELECT * FROM TEXT_STORE WHERE TEXT_UID = ? AND TYPE = 1 ORDER BY TEXT_ID");
            st.setLong(1, uid);
            rs = st.executeQuery();
            String fullText = "";
            while(rs.next()){
                fullText += rs.getString("TEXT");
            }
            return fullText;
        } catch (Exception ex) {
            LOGGER.error("ID: [{}] - UID: [{}] - insertText...failed. Error: {}", uid, ex);
        } finally {
            DBUtils.closeQuietly(st, con);
        }
        return "";
    }

    public boolean initTable() {
        Connection con = null;
        Statement st = null;
        try {
            con = ConnectionManager.getInstance().getConnection();
            if (con == null) {
                LOGGER.info("Get Connect to Database initTable...failed");
                return false;
            }
            st = con.createStatement();
            boolean result = st.execute(Config.getDatabaseConfig().getString("CREATE_TABLE"));
            return result;
        } catch (Exception ex) {
            LOGGER.error("InitTable...failed. Error: {}", ex);
        } finally {
            DBUtils.closeQuietly(st, con);
        }
        return false;
    }

}
