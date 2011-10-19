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

    public static void createDb(String strName) throws SQLException {
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
            con = DriverManager.getConnection(KGConfig.getConfiguration().db_url.replace("postgresql_postGIS", "postgresql") + "/postgres", user, password);
            if (con != null) {
                PreparedStatement pst = con.prepareStatement("CREATE DATABASE " + strName.toLowerCase() + " template=postgis;");
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
            con = DriverManager.getConnection(KGConfig.getConfiguration().db_url.replace("postgresql_postGIS", "postgresql") + "/postgres", user, password);
            if (con != null) {
                //drop all connection to the db
                String dropCon = "select pg_terminate_backend(procpid) from pg_stat_activity where datname='" + strName.toLowerCase() + "';";
                PreparedStatement pst = con.prepareStatement(dropCon);              
                pst.execute();
                pst.close();
                //drop the database itself
                String dropDb = "DROP DATABASE " + strName + " ;";              
                pst = con.prepareStatement(dropDb);  
                pst.execute();
                pst.close();
                con.close();
            } else {
                throw new SQLException("Connection to database " + strName.toLowerCase() + " is impossible");
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