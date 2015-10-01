/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classpdam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author Engkid
 */
public class class_pegawai {

    private int count;
    private final int[] pegawaiID = new int[9999],
            jabatanID = new int[9999],
            kantorID = new int[9999];
    private String[] pegawaiName, alamat, nohp;

    public class_pegawai() {
        loadData();
    }

    public void loadData(int kdKantor) {
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT pegawai.NIP,"
                            + "pegawai.nama_pegawai,pegawai.jeniskelamin,"
                            + "pegawai.alamat,pegawai.nohp,jabatan.jabatan,"
                            + "kantor.namakantor from pegawai,jabatan,"
                            + "kantor where pegawai.kdkantor = kantor.kdkantor "
                            + "and pegawai.kdjabatan = jabatan.kdjabatan"
                            + "and kdkantor = '"+ kdKantor +"'")) {
                int i = 0;
                count = 0;
                while (r.next()) {
                    //lakukan penelusuran baris
                    pegawaiID[i] = r.getInt("NIP");
                    pegawaiName[i] = r.getString("namapegawai");
                    alamat[i] = r.getString("alamat");
                    nohp[i] = r.getString("nohp");
                    jabatanID[i] = r.getInt("kdjabatan");
                    kantorID[i] = r.getInt("kdkantor");
                    i++;
                    count = i;
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Tabel Order) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }

    public static int getKodeKantor(String namaKantor) {
        int val = 0;
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT * FROM `absenpdam`.`kantor` "
                            + "WHERE `namakantor` = '" + namaKantor + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    val = r.getInt("kdkantor");
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Tabel Kantor) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        return val;
    }
    
    private void loadData() {
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT * FROM `absenpdam`.`pegawai` ")) {
                int i = 0;
                count = 0;
                while (r.next()) {
                    //lakukan penelusuran baris
                    pegawaiID[i] = r.getInt("NIP");
                    pegawaiName[i] = r.getString("namapegawai");
                    alamat[i] = r.getString("alamat");
                    nohp[i] = r.getString("nohp");
                    jabatanID[i] = r.getInt("kdjabatan");
                    kantorID[i] = r.getInt("kdkantor");
                    i++;
                    count = i;
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Tabel Pegawai) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
}
