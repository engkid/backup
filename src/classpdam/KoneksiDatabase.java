package classpdam;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Engkid
 */


import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class KoneksiDatabase {
    private static Connection koneksi;
    public static String user = "root", password = "";

    public static Connection getKoneksi() {
        //cek apakah koneksi null
        if (koneksi == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/absenpdam";
                DriverManager.registerDriver(
                        new com.mysql.jdbc.Driver());
                koneksi = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {

            }
        }
        return koneksi;
    }
    
}
