
import classpdam.KoneksiDatabase;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.text.*;
import java.awt.print.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Engkid
 */
public class FormPrint extends javax.swing.JFrame {

    /**
     * Creates new form FormPrint
     */
    private final String defaultSQL, usedSQL;
    private String commandSQL = "", commandSQL2 = "";
    private DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "NIP", "Nama", "Jabatan", "Kantor", "Tanggal", "Jam Masuk",
                "Status Masuk", "Keterangan"
            }
    ) {
        Class[] types = new Class[]{
            java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean[]{
            false, false, false, false, false, false, false, false
        };

        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }
    };

    private DefaultTableModel model2 = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "NIP", "Nama", "Jabatan", "Kantor", "Tanggal", "Jam Keluar",
                "Status Keluar", "Keterangan"
            }
    ) {
        Class[] types = new Class[]{
            java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean[]{
            false, false, false, false, false, false, false, false
        };

        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }
    };

    public FormPrint() {
        initComponents();
        this.setTitle("Laporan Absen");
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            BufferedImage image = ImageIO.read(cl.getResource("image/marketing 40x.png"));
            this.setIconImage(image);
        } catch (IOException ex) {
            Logger.getLogger(Absensi.class.getName()).log(Level.SEVERE, null, ex);
        }
        defaultSQL = "SELECT pegawai.NIP, "
                + "pegawai.namapegawai, "
                + "jabatan.jabatan, "
                + "kantor.namakantor, "
                + "absenmsk.tglmsk, "
                + "absenmsk.jammsk, "
                + "absenmsk.statusmsk, "
                + "absenmsk.ket "
                + "FROM pegawai,jabatan,absenmsk,kantor "
                + "WHERE pegawai.NIP = absenmsk.NIP "
                + "AND absenmsk.kdjabatan = jabatan.kdjabatan "
                + "AND kantor.kdkantor = absenmsk.kdkantor";
        panelprint.setBackground(Color.white);
        tableprint.setModel(model);
        loadData(defaultSQL);
        loadDataKantor();
        usedSQL = "SELECT pegawai.NIP, pegawai.namapegawai, jabatan.jabatan, kantor.namakantor, absenklr.tglklr, absenklr.jamklr,absenklr.statusklr, absenklr.ket               from pegawai,jabatan,absenklr,kantor where pegawai.NIP = absenklr.NIP\n"
                + "and absenklr.kdjabatan = jabatan.kdjabatan and\n"
                + "kantor.kdkantor = absenklr.kdkantor";
    }

    public void loadData(String sql) {
        //mun sql kosong
        if (sql == null || sql.equals("")) {
            sql = defaultSQL;
        }
        int late = 0, early = 0, ontime = 0, other = 0;
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);

            while (r.next()) {
                //lakukan penelusuran baris
                Object[] o = new Object[8];
                o[0] = r.getString("NIP");
                o[1] = r.getString("namapegawai");
                o[2] = r.getString("jabatan");
                o[3] = r.getString("namakantor");
                o[4] = r.getDate("tglmsk");
                o[5] = r.getTime("jammsk");
                o[6] = r.getString("statusmsk");
                if (o[6] != null) {
                    switch (o[6].toString()) {
                        case "LATE":
                            late++;
                            break;
                        case "EARLY":
                            early++;
                            break;
                        case "ONTIME":
                            ontime++;
                            break;
                        case "OTHER":
                            other++;
                            break;
                    }
                }
                o[7] = r.getString("ket");
                model.addRow(o);
            }
            r.close();
            s.close();
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    JOptionPane.showMessageDialog(this, "terjadi kesalahan saat pengambilan data" + e.getErrorCode() + e.getMessage());
                    System.exit(0);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "terjadi kesalahan saat pengambilan data" + e.getErrorCode() + e.getMessage());
                    break;
            }
        } finally {
            //mun data na kosong
            Object[] s = new Object[8];
            s[5] = "Jumlah";
            s[6] = late + early + ontime + other;
            model.addRow(s);
            if (model.getDataVector().isEmpty()) {
                //generateData();
            }
        }
    }

    public void loadData2(String sql) {
        //mun sql kosong
        if (sql == null || sql.equals("")) {
            sql = usedSQL;
        }
        int late = 0, early = 0, ontime = 0, other = 0;
        model2.getDataVector().removeAllElements();
        model2.fireTableDataChanged();
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);

            while (r.next()) {
                //lakukan penelusuran baris
                Object[] o = new Object[8];
                o[0] = r.getString("NIP");
                o[1] = r.getString("namapegawai");
                o[2] = r.getString("jabatan");
                o[3] = r.getString("namakantor");
                o[4] = r.getDate("tglklr");
                o[5] = r.getTime("jamklr");
                o[6] = r.getString("statusklr");
                if (o[6] != null) {
                    switch (o[6].toString()) {
                        case "LATE":
                            late++;
                            break;
                        case "EARLY":
                            early++;
                            break;
                        case "ONTIME":
                            ontime++;
                            break;
                        case "OTHER":
                            other++;
                            break;
                    }
                }
                o[7] = r.getString("ket");
                model2.addRow(o);
            }
            r.close();
            s.close();
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    JOptionPane.showMessageDialog(this, "terjadi kesalahan saat pengambilan data" + e.getErrorCode() + e.getMessage());
                    System.exit(0);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "terjadi kesalahan saat pengambilan data" + e.getErrorCode() + e.getMessage());
                    break;
            }
        } finally {
            //mun data na kosong
            Object[] s = new Object[8];
            s[5] = "Jumlah";
            /*switch (s[6].toString()) {
             case "ONTIME" :
             if(cbStatus.getSelectedItem().toString().equals("ONTIME")) {
             s[6] = ontime++;
             } else {
             break;
             }                   
                
             case "LATE":
             if(cbStatus.getSelectedItem().toString().equals("LATE")) {
             s[6] = late++;
             } else {
             break;
             }     
             }*/
            s[6] = late /*early + ontime + other*/;
            model2.addRow(s);
            if (model2.getDataVector().isEmpty()) {
                //generateData();
            }
        }
    }

    public void loadDataKantor() {
        try {
            this.cbNIP.removeAllItems();
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            String sql = "SELECT * from kantor";
            ResultSet r = s.executeQuery(sql);

            while (r.next()) {
                cbNIP.addItem(r.getString("kdkantor"));
            }
        } catch (Exception e) {

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelprint = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableprint = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cbNIP = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        tglawal = new org.jdesktop.swingx.JXDatePicker();
        tglakhir = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        checkKeluar = new javax.swing.JCheckBox();
        panel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelprint.setForeground(new java.awt.Color(240, 240, 240));
        panelprint.setPreferredSize(new java.awt.Dimension(657, 1008));

        tableprint.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tableprint);

        jLabel1.setText("Filter By Kantor :");

        cbNIP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Periode :");

        jLabel3.setText("s/d");

        checkKeluar.setText("AbsenKeluar");
        checkKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkKeluarActionPerformed(evt);
            }
        });

        panel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setText("Mengetahui");

        jLabel5.setText("KasubBag");

        jLabel6.setText("Admin");

        jLabel7.setText("Kepala");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel5)
                .addGap(145, 145, 145)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(23, 23, 23))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(0, 74, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelprintLayout = new javax.swing.GroupLayout(panelprint);
        panelprint.setLayout(panelprintLayout);
        panelprintLayout.setHorizontalGroup(
            panelprintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelprintLayout.createSequentialGroup()
                .addGroup(panelprintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelprintLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbNIP, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelprintLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(checkKeluar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tglawal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addComponent(tglakhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 29, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        panelprintLayout.setVerticalGroup(
            panelprintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelprintLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelprintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbNIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(tglawal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tglakhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkKeluar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("Home");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Print");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelprint, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelprint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        Absensi absen = new Absensi();
        absen.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jMenu1MouseClicked

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        try {
            if (checkKeluar.isSelected()) {
                MessageFormat header = new MessageFormat("Data Rekap Absen Keluar Karyawan PDAM");
                MessageFormat footer = new MessageFormat("Halaman{0,number,integer}");
               
                tableprint.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            } else {
                MessageFormat header = new MessageFormat("Data Rekap Absen Masuk Karyawan PDAM");
                MessageFormat footer = new MessageFormat("Halaman{0,number,integer}");
                tableprint.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            }

        } catch (java.awt.print.PrinterException e) {
            System.err.format("Cannot Print %s%n", e.getMessage());
        }
    }//GEN-LAST:event_jMenu2MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String value = cbNIP.getSelectedItem().toString();
        if (tglawal.getDate() != null && tglakhir.getDate() != null) {
            if (checkKeluar.isSelected()) {
                commandSQL2 = "select pegawai.NIP, "
                        + "pegawai.namapegawai, "
                        + "jabatan.jabatan, "
                        + "kantor.namakantor, "
                        + "absenklr.tglklr, "
                        + "absenklr.jamklr, "
                        + "absenklr.statusklr, "
                        + "absenklr.ket "
                        + "from pegawai, kantor, absenklr, jabatan "
                        + "where absenklr.tglklr between '" + new java.sql.Date(tglawal.getDate().getTime()) + "'"
                        + "and '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                        + "and pegawai.NIP = '" + value + "' "
                        + "and pegawai.NIP = absenklr.NIP "
                        + "and pegawai.kdkantor = kantor.kdkantor "
                        + "and absenklr.kdjabatan = jabatan.kdjabatan ";

                //loadData2(usedSQL);
                loadData2(commandSQL2);
            } else {
                commandSQL = "select pegawai.NIP, pegawai.namapegawai, jabatan.jabatan, "
                        + "kantor.namakantor, absenmsk.tglmsk, absenmsk.jammsk, "
                        + "absenmsk.statusmsk, absenmsk.ket "
                        + "from pegawai, kantor, absenmsk, jabatan where absenmsk.tglmsk between '" + new java.sql.Date(tglawal.getDate().getTime()) + "'"
                        + "and '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' and "
                        + "pegawai.NIP = '" + value + "' and "
                        + "pegawai.NIP = absenmsk.NIP and pegawai.kdkantor = kantor.kdkantor and absenmsk.kdjabatan = jabatan.kdjabatan ";
                loadData(commandSQL);
            }

        } else {
            JOptionPane.showMessageDialog(null,
                    "Tentukan periode terlebih dahulu!",
                    "Load data error!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void checkKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkKeluarActionPerformed
        if (checkKeluar.isSelected()) {
            loadData2(usedSQL);
            tableprint.setModel(model2);
        } else {
            loadData(commandSQL);
            tableprint.setModel(model);
        }
    }//GEN-LAST:event_checkKeluarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPrint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPrint().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbNIP;
    private javax.swing.JCheckBox checkKeluar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panelprint;
    private javax.swing.JTable tableprint;
    private org.jdesktop.swingx.JXDatePicker tglakhir;
    private org.jdesktop.swingx.JXDatePicker tglawal;
    // End of variables declaration//GEN-END:variables
}
