/*
 * @author corneliu
 */
package org.kloudgis.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.kloudgis.KGConfig;

public class DatabaseFactory {

    public static void createDB(String strName) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        Connection con = null;
        Map<String, String> map = PersistenceManager.getInstance().getDefaultProperties();
        String user = map.get("user");
        String password = map.get("password");
        try {
            con = DriverManager.getConnection("jdbc:postgresql://" + KGConfig.getConfiguration().db_url + "/postgres", user, password);
            if (con != null) {
                PreparedStatement pst = con.prepareStatement("CREATE DATABASE " + strName + " template=postgis;");
                pst.execute();
                pst.close();
                con.close();
            } else {
                throw new SQLException("Connection to database postgres is impossible");
            }
        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex1) {
                }
            }
            throw ex;
        }
    }

    public static void dropDb(String strName) throws SQLException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        Map<String, String> map = PersistenceManager.getInstance().getDefaultProperties();
        String user = map.get("user");
        String password = map.get("password");
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:postgresql://" + KGConfig.getConfiguration().db_url + "/postgres", user, password);
            if (con != null) {
                PreparedStatement pst = con.prepareStatement("DROP DATABASE " + strName + " ;");
                pst.execute();
                pst.close();
                con.close();
            } else {
                throw new SQLException("Connection to database " + strName + " is impossible");
            }
        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex1) {
                }
            }
            throw ex;
        }
    }
}