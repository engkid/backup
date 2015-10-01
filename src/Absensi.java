/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Engkid
 */
import classpdam.KoneksiDatabase;
import classpdam.class_pegawai;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class Absensi extends javax.swing.JFrame {

    private boolean subShown = false;
    private int countKantor, countPegawai, countAbsen, kodeKantor;
    private final String[] KodeKantor = new String[9999], NIP = new String[9999], index = new String[9999], JamKerja = new String[9999];
    private final String[] NamaKantor = new String[9999], NamaPegawai = new String[9999],
            StatusMasuk = new String[9999], StatusKeluar = new String[9999], Ket = new String[9999],
            Terlambat = new String[9999], PulangCepat = new String[9999];
    private String[] SubKantor = new String[9999];
    private DefaultComboBoxModel VarKantor, VarNIP, VarAbsen;
    private final boolean[] JenisKelamin = new boolean[9999];
    ;
    private final java.sql.Time[] JamMasuk = new java.sql.Time[9999], JamKeluar = new java.sql.Time[9999];
    private final java.sql.Date[] Tanggal = new java.sql.Date[9999];
    private DefaultTableModel model2 = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "NIP", "Tanggal Absen", "Jam Masuk", "Jam Keluar", "Jam Kerja", "Status Masuk", "Status Keluar", "Keterangan", "Terlambat", "Pulang Cepat"
            }
    ) {
        Class[] types = new Class[]{
            java.lang.Integer.class, java.sql.Date.class, java.sql.Time.class, java.sql.Time.class, java.lang.Integer.class,
            java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
        };

        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }
    };
    private DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "NIP", "Nama", "Jenis Kelamin"
            }
    ) {
        Class[] types = new Class[]{
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean[]{
            false, false, false
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

    /**
     * Creates new form Absensi
     */
    public Absensi() {
        initComponents();
        this.setTitle("Aplikasi Absensi PDAM Garut 1.0");
         ClassLoader cl = this.getClass().getClassLoader();
        try {
            BufferedImage image = ImageIO.read(cl.getResource("image/marketing 40x.png"));
            this.setIconImage(image);
        } catch (IOException ex) {
            Logger.getLogger(Absensi.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadDataKantor();
        Tabel.setModel(model);
        runClock();        
    }

    private void TikTok() {
        jLabel1.setText(DateFormat.getTimeInstance().format(new java.util.Date()));
        if (new java.util.Date().getHours() >= 5 && new java.util.Date().getHours() <= 9) {
            jButton2.setEnabled(true);
            jButton5.setEnabled(false);
        } else {
            jButton2.setEnabled(false);
            jButton5.setEnabled(true);
        }
    }

    private void runClock() {
        Timer timer = new Timer(500, (ActionEvent e) -> {
            TikTok();
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.setInitialDelay(0);
        timer.start();
    }

    private void loadDataKantor() {
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT * FROM `kantor` "
                            + "WHERE `parent` IS NULL")) {
                int i = 0;
                while (r.next()) {
                    //lakukan penelusuran baris
                    KodeKantor[i] = r.getString("kdkantor");
                    NamaKantor[i] = r.getString("namakantor");
                    i++;
                    countKantor = i;
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
            String list[] = new String[countKantor];
            for (int i = 0; i < countKantor; i++) {
                list[i] = NamaKantor[i];
            }
            VarKantor = new javax.swing.DefaultComboBoxModel(list);
            listKantor.setModel(VarKantor);
        }
    }

    private void loadDataPegawai() {
        Tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT * FROM `pegawai` "
                            + "WHERE `kdkantor` = " + kodeKantor)) {
                int i = 0;
                while (r.next()) {
                    //lakukan penelusuran baris
                    NIP[i] = r.getString("NIP");
                    NamaPegawai[i] = r.getString("namapegawai");
                    JenisKelamin[i] = r.getBoolean("jeniskelamin");
                    //KodeKantor[i] = r.getString("kdkantor");
                    i++;
                    countPegawai = i;
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
            String list[] = new String[countPegawai];
            for (int i = 0; i < countPegawai; i++) {
                list[i] = String.valueOf(NIP[i]);
            }
            //VarNIP = new javax.swing.DefaultComboBoxModel(list);
            loadTableJenisKelamin();
        }
    }

    public final void loadTableJenisKelamin() {
        if (model.getDataVector() != null) {
            model.getDataVector().removeAllElements();
        }
        // memberi tahu bahwa data telah kosong
        model.fireTableDataChanged();
        try {
            for (int i = 0; i < countPegawai; i++) {
                Object[] o = new Object[4];
                o[0] = NIP[i];
                o[1] = NamaPegawai[i];
                if (JenisKelamin[i] == true) {
                    o[2] = "Laki-laki";
                } else {
                    o[2] = "Perempuan";
                }
                o[3] = getNamaKantor(KodeKantor[i]);
                model.addRow(o);
            }
        } finally {
            Tabel.setModel(model);
        }
    }

    public String getNamaKantor(String kodeKantor) {
        String namaKantor = null;
        for (int i = 0; i < countKantor; i++) {
            if (kodeKantor == this.KodeKantor[i]) {
                namaKantor = NamaKantor[i];
            }
        }
        return namaKantor;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listKantor = new javax.swing.JList();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        listKantor.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listKantor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listKantorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(listKantor);

        jToolBar1.setRollover(true);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/pegawai.png"))); // NOI18N
        jButton1.setText("Data Karyawan");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/absensi.png"))); // NOI18N
        jButton2.setText("Absen Masuk");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/absensi.png"))); // NOI18N
        jButton5.setText("Absen Keluar");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/print35.png"))); // NOI18N
        jButton3.setText("Print");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/print35.png"))); // NOI18N
        jButton6.setText("Print Akumulasi");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/about.png"))); // NOI18N
        jButton4.setText("Tentang Aplikasi");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jTabbedPane1.addTab("Home", jToolBar1);

        Tabel.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(Tabel);

        jLabel1.setFont(new java.awt.Font("Arial Black", 3, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("19 : 59 :09");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/pdamlogo.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(89, 89, 89))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)))
                        .addGap(0, 26, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (listKantor.getSelectedIndex() > -1) {
            String namaKantor = listKantor.getSelectedValue().toString();
            kodeKantor = class_pegawai.getKodeKantor(namaKantor);
            if (listKantor.getSelectedValue().toString() != (null)) {
                loadDataPegawai();
            } else {
                System.out.println("error");
                JOptionPane.showMessageDialog(null, "Pilih Kantor");
            }
            //System.out.println(namaKantor);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Pilih kantor terlebih dahulu!",
                    "Load data error!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        FormAbsensiMasuk absen = new FormAbsensiMasuk();
        absen.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        FormAbsensiKeluar keluar = new FormAbsensiKeluar();
        keluar.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        FormPrint print = new FormPrint();
        print.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        AboutForm tentang = new AboutForm();
        tentang.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void listKantorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listKantorMouseClicked
        int index = listKantor.getSelectedIndex();
        if (index > -1) {
            String namaKantor = listKantor.getSelectedValue().toString();
            if (subShown == false) {
                if (listKantor.getSelectedIndex() == 0) {
                    DefaultComboBoxModel model = (DefaultComboBoxModel) listKantor.getModel();
                    //tergantung jumlah sub
                    for (int i = 1; i <= getCount(namaKantor); i++) {
                        model.insertElementAt(SubKantor[i], index + i);
                    }
                    subShown = true;
                }
            } else {
                if (listKantor.getSelectedIndex() == 0) {
                    DefaultComboBoxModel model = (DefaultComboBoxModel) listKantor.getModel();
                    //tergantung jumlah sub
                    for (int i = 1; i <= getCount(namaKantor); i++) {
                        model.removeElementAt(index + 1);
                    }
                    subShown = false;
                }
            }
        }
    }//GEN-LAST:event_listKantorMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        FormPrintLaporanPersentase print = new FormPrintLaporanPersentase();
        print.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton6ActionPerformed

    private int getCount(String namaKantor) {
        int hasil = 0;
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            try (Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("SELECT * FROM `kantor` "
                            + "WHERE `parent` = '" + namaKantor + "' ")) {
                int i = 1;
                while (r.next()) {
                    //lakukan penelusuran baris
                    SubKantor[i] = r.getString("namakantor");
                    i++;
                    hasil = i - 1;
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
            java.util.logging.Logger.getLogger(Absensi.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Absensi.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Absensi.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Absensi.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Absensi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Tabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JList listKantor;
    // End of variables declaration//GEN-END:variables
}
