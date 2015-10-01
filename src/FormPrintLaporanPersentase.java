
import classpdam.KoneksiDatabase;
import classpdam.class_pegawai;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DOC
 */
public class FormPrintLaporanPersentase extends javax.swing.JFrame {

    /**
     * Creates new form FormPrintLaporanPersentase
     */
    private int kodeKantor, countPegawai;
    private final String[] KodeKantor = new String[9999], NamaPegawai = new String[9999], NIP = new String[9999];
    private DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "NIP", "Nama", "Kantor", "Early", "Late", "On-time", "Other", "Kehadiran (%)", "Reward"
            }
    ) {
        Class[] types = new Class[]{
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
            java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class,
            java.lang.Integer.class
        };
        boolean[] canEdit = new boolean[]{
            false, false, false, false, false, false, false, false, false
        };

    };

    public FormPrintLaporanPersentase() {
        initComponents();
        this.setTitle("Persentase Kehadiran");
         ClassLoader cl = this.getClass().getClassLoader();
        try {
            BufferedImage image = ImageIO.read(cl.getResource("image/marketing 40x.png"));
            this.setIconImage(image);
        } catch (IOException ex) {
            Logger.getLogger(Absensi.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData2();
        tabel.setModel(model);
    }

    public void loadData2() {
        try {
            this.cbKantor.removeAllItems();
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            String sql = "SELECT * from kantor";
            ResultSet r = s.executeQuery(sql);

            while (r.next()) {
                cbKantor.addItem(r.getString("kdkantor"));
            }
        } catch (Exception e) {

        }
    }

    private void loadDataPegawai() {
        int late = 0, early = 0, ontime = 0, other = 0, reward = 0;
        String kodeKantor = cbKantor.getSelectedItem().toString();
        tabel.setModel(model);
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT * FROM `pegawai`, `kantor` "
                            + "WHERE `pegawai`.`kdkantor` = " + kodeKantor + " "
                            + "AND `kantor`.`kdkantor` = `pegawai`.`kdkantor` ")) {
                int i = 0;
                while (r.next()) {
                    //lakukan penelusuran baris
                    Object[] o = new Object[9];
                    o[0] = r.getString("NIP");
                    o[1] = r.getString("namapegawai");
                    o[2] = r.getString("namakantor");
                    o[3] = countEarly(r.getString("NIP"));
                    o[4] = countLate(r.getString("NIP"));;
                    o[5] = countOnTime(r.getString("NIP"));;
                    o[6] = countOther(r.getString("NIP"));;
                    int total = Integer.parseInt(o[3].toString())
                            + Integer.parseInt(o[4].toString())
                            + Integer.parseInt(o[5].toString())
                            + Integer.parseInt(o[6].toString());
                    float persentase = (Integer.parseInt(o[3].toString())
                            + Integer.parseInt(o[5].toString()))
                            * 100 / total;
                    o[7] = persentase;
                    o[8] = Integer.parseInt(o[3].toString()) * 5000;
                    i++;
                    countPegawai = i;
                    model.addRow(o);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } finally {

        }
    }

    private int countEarly(String nip) {
        int hasil = 0;
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenmsk` "
                            + "WHERE `tglmsk` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusmsk` = 'EARLY' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenklr` "
                            + "WHERE `tglklr` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusklr` = 'EARLY' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        return hasil;
    }

    private int countLate(String nip) {
        int hasil = 0;
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenmsk` "
                            + "WHERE `tglmsk` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusmsk` = 'LATE' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenklr` "
                            + "WHERE `tglklr` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusklr` = 'LATE' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        return hasil;
    }

    private int countOnTime(String nip) {
        int hasil = 0;
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenmsk` "
                            + "WHERE `tglmsk` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusmsk` = 'ONTIME' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenklr` "
                            + "WHERE `tglklr` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusklr` = 'ONTIME' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        return hasil;
    }

    private int countOther(String nip) {
        int hasil = 0;
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenmsk` "
                            + "WHERE `tglmsk` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusmsk` = 'OTHER' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenklr` "
                            + "WHERE `tglklr` "
                            + "BETWEEN '" + new java.sql.Date(tglawal.getDate().getTime()) + "' "
                            + "AND '" + new java.sql.Date(tglakhir.getDate().getTime()) + "' "
                            + "AND `statusklr` = 'OTHER' "
                            + "AND `NIP` = '" + nip + "'")) {
                while (r.next()) {
                    //lakukan penelusuran baris
                    hasil += r.getInt(1);
                }
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Variabel Unit) Load data error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        return hasil;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        cbKantor = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txNamaKantor = new javax.swing.JTextField();
        tglawal = new org.jdesktop.swingx.JXDatePicker();
        tglakhir = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabel.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabel);

        cbKantor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbKantor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbKantorActionPerformed(evt);
            }
        });

        jLabel1.setText("Pilih Kantor :");

        jLabel2.setText("s/d");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txNamaKantor)
                    .addComponent(cbKantor, 0, 118, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addComponent(tglawal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tglakhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbKantor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tglawal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tglakhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txNamaKantor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 916, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (tglawal.getDate() == null) {
            JOptionPane.showMessageDialog(null,
                    "Tentukan periode terlebih dahulu!",
                    "Load data error!",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            loadDataPegawai();
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void cbKantorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbKantorActionPerformed
        int i = cbKantor.getSelectedIndex();
        if (i == -1) {
            return;
        }
        try {

            String no = (String) cbKantor.getSelectedItem();
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            String sql = "select * from kantor where kdkantor = ?";
            com.mysql.jdbc.PreparedStatement p = (com.mysql.jdbc.PreparedStatement) c.prepareStatement(sql);
            p.setString(1, no);
            ResultSet result = p.executeQuery();
            result.next();

            //this.txNoSoal.setText(result.getString("no_soal"));
            this.txNamaKantor.setText(result.getString("namakantor"));
            //loadData2();

        } catch (SQLException e) {
        }
    }//GEN-LAST:event_cbKantorActionPerformed

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        Absensi absen = new Absensi();
        absen.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jMenu1MouseClicked

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        try {
            MessageFormat header = new MessageFormat("Data Rekap Detail Absen Karyawan PDAM");
            MessageFormat footer = new MessageFormat("Halaman{0,number,integer}");
            tabel.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (java.awt.print.PrinterException e) {
            System.err.format("Cannot Print %s%n", e.getMessage());
        }
    }//GEN-LAST:event_jMenu2MouseClicked

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
            java.util.logging.Logger.getLogger(FormPrintLaporanPersentase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPrintLaporanPersentase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPrintLaporanPersentase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPrintLaporanPersentase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPrintLaporanPersentase().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbKantor;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel;
    private org.jdesktop.swingx.JXDatePicker tglakhir;
    private org.jdesktop.swingx.JXDatePicker tglawal;
    private javax.swing.JTextField txNamaKantor;
    // End of variables declaration//GEN-END:variables
}
