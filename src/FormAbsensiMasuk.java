
import classpdam.KoneksiDatabase;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Engkid
 */
public class FormAbsensiMasuk extends javax.swing.JFrame {

    private final String defaultSQL;
    private String usedSQL = "", commandSQL = "";
    private final int[] kodeKantor = new int[9999];
    private DefaultTableModel model2 = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "NIP", "Nama", "Kantor", "Tanggal Absen", "Jam Masuk"
            }
    ) {
        Class[] types = new Class[]{
            java.lang.String.class, java.lang.String.class, java.lang.String.class, java.sql.Date.class, java.sql.Time.class
        };

        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }
    };

    private DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "NIP", "Nama", "Jabatan", "Kantor", "Tanggal Masuk", "Jam Masuk", "Status Masuk",
                "Keterangan"
            }
    ) {
        Class[] types = new Class[]{
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
            java.lang.String.class, java.sql.Date.class, java.sql.Time.class,
            java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean[]{
            false, false, false, false, false, false, false, false
        };

    };

    /**
     * Creates new form FormAbsensi
     */
    public FormAbsensiMasuk() {
        initComponents();
        this.setTitle("Absen Masuk");
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            BufferedImage image = ImageIO.read(cl.getResource("image/marketing 40x.png"));
            this.setIconImage(image);
        } catch (IOException ex) {
            Logger.getLogger(Absensi.class.getName()).log(Level.SEVERE, null, ex);
        }
        defaultSQL = "SELECT pegawai.NIP, pegawai.namapegawai, jabatan.jabatan,"
                + "                kantor.namakantor, absenmsk.tglmsk, absenmsk.jammsk, absenmsk.statusmsk, absenmsk.ket"
                + "                FROM pegawai, kantor, absenmsk, jabatan"
                + "               where pegawai.kdkantor = kantor.kdkantor"
                + "                and absenmsk.NIP = pegawai.NIP and absenmsk.kdjabatan = jabatan.kdjabatan and absenmsk.tglmsk = curdate()";
        setLocationRelativeTo(null);
        tabelAbsensi.setModel(model);
        loadData2();
        cbTgl.setDate(new Date());
        //mun tabelna kosong
        if (getCount() == 0) {
            generateData();
        }
        loadData(defaultSQL);
        runClock();
    }

    public void loadData(String sql) {
        //mun sql kosong
        if (sql == null || sql.equals("")) {
            sql = defaultSQL;
        }
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
            if (model.getDataVector().isEmpty()) {
                generateData();
            }
        }
    }

    private int getCount() {
        int count = 0;
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `absenmsk` ");

            while (r.next()) {
                //lakukan penelusuran baris
                count = r.getInt(1);
            }
            r.close();
            s.close();
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 0:
                    JOptionPane.showMessageDialog(this, "get Count" + e.getErrorCode() + e.getMessage());
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "get Count" + e.getErrorCode() + e.getMessage());
                    break;
            }
        }
        return count;
    }

    private void generateData() {
        Vector NIP = new Vector(1, 1), kdJabatan = new Vector(1, 1), kdKantor = new Vector(1, 1);
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM `pegawai` ");

            while (r.next()) {
                //lakukan penelusuran baris
                NIP.add(r.getString("NIP"));
                kdJabatan.add(r.getString("kdjabatan"));
                kdKantor.add(r.getString("kdkantor"));
            }
            r.close();
            s.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, " generate Data" + e.getErrorCode() + "asdasdas");
        }
        //nelusurin berapa pegawai yg ad d kantor itu trs dimasukin ke tabel
        for (int i = 0; i < NIP.size(); i++) {
            addData(NIP.get(i).toString(), kdJabatan.get(i).toString(), kdKantor.get(i).toString());
        }
        loadData(commandSQL);
    }

    public void addData(String NIP, String kdJabatan, String kdKantor) {
        try {
            Connection c = KoneksiDatabase.getKoneksi();
            String sql = "INSERT INTO `absenmsk` (`index`, `NIP`, `kdjabatan`, "
                    + "`kdkantor`, `tglmsk`, `jammsk`, `statusmsk`, `ket`) "
                    + "VALUES (NULL, ?, ?, ?, ?, NULL, NULL, NULL)";
            try (PreparedStatement p = c.prepareStatement(sql)) {
                p.setString(1, (String) NIP);
                p.setString(2, (String) kdJabatan);
                p.setString(3, (String) kdKantor);
                p.setDate(4, new java.sql.Date(cbTgl.getDate().getTime()));
                p.executeUpdate();
            }
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                default:
                    JOptionPane.showMessageDialog(null,
                            e.getErrorCode() + " : " + e.getMessage(),
                            "(Data Eselon)Insert record error!",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }

    public void loadData1() {
        int value = cbKantor.getSelectedIndex();
        try {
            this.cbKantor.removeAllItems();
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            String sql = "SELECT * from pegawai where kd_kantor = '" + value + "'";
            ResultSet r = s.executeQuery(sql);

            while (r.next()) {
                cbKantor.addItem(r.getString("kdkantor"));
            }
        } catch (Exception e) {

        }
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tabelAbsensi = new javax.swing.JTable();
        cbKantor = new javax.swing.JComboBox();
        txNamaKantor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        cbTgl = new org.jdesktop.swingx.JXDatePicker();
        cbStatusmsk = new javax.swing.JComboBox();
        LClock = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabelAbsensi.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabelAbsensi);

        cbKantor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbKantor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbKantorActionPerformed(evt);
            }
        });

        jLabel1.setText("Pilih Kantor :");

        jButton1.setText("Tampilkan Absensi");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cbStatusmsk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ONTIME", "LATE", "EARLY", "OTHER" }));
        cbStatusmsk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbStatusmskActionPerformed(evt);
            }
        });

        LClock.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        LClock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LClock.setText("17:52:33");
        LClock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setText("FORM ABSEN MASUK KARYAWAN");

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jButton2.setText("Lain-lain");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jMenu1.setText("Home");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txNamaKantor, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cbStatusmsk, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cbKantor, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(cbTgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(467, 467, 467))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LClock)
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbTgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbKantor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txNamaKantor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbStatusmsk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(LClock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void TikTok() {
        LClock.setText(DateFormat.getTimeInstance().format(new Date()));
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String value = cbKantor.getSelectedItem().toString();
        commandSQL = "select pegawai.NIP, pegawai.namapegawai, jabatan.jabatan, "
                + "kantor.namakantor, absenmsk.tglmsk, absenmsk.jammsk, "
                + "absenmsk.statusmsk, absenmsk.ket "
                + "from pegawai, kantor, absenmsk, jabatan where pegawai.kdkantor = '" + value + "' and "
                + "absenmsk.tglmsk = '" + new java.sql.Date(cbTgl.getDate().getTime()) + "' and "
                + "pegawai.NIP = absenmsk.NIP and pegawai.kdkantor = kantor.kdkantor and absenmsk.kdjabatan = jabatan.kdjabatan ";
        loadData(commandSQL);
        //tabelAbsensi.setModel(model2);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cbStatusmskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStatusmskActionPerformed
        String value = cbStatusmsk.getSelectedItem().toString();
    }//GEN-LAST:event_cbStatusmskActionPerformed

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        Absensi absen = new Absensi();
        absen.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jMenu1MouseClicked

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        int i = tabelAbsensi.getSelectedRow();
        JPanel panel = new JPanel();
        JTextField nama = new JTextField();
        panel.setLayout(new GridLayout(1, 2));
        panel.add(new JLabel("Pilih NIP : "));
        panel.add(nama);
        int n;
        Object[] options = {"Tambah",
            "Batal"};
        n = JOptionPane.showOptionDialog(null,
                panel,
                "Absen Masuk ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title
        {
            String selectedNIP = nama.getText();
            java.sql.Date tglMsk = new java.sql.Date(new Date().getTime());            
            if (selectedNIP != null) {
                try {
                    Connection c = KoneksiDatabase.getKoneksi();
                    String sql = "UPDATE `absenmsk` SET `jammsk` = CURTIME(), "
                            + "`statusmsk` = ? "
                            + "WHERE `absenmsk`.`nip` = ? AND `tglmsk` = ? ";
                    try (PreparedStatement p = c.prepareStatement(sql)) {
                        String status = null;
                        if (new Date().getHours() < 8) {
                            status = "EARLY";
                        } else if (new Date().getHours() == 8) {
                            if (new Date().getMinutes() < 11) {
                                status = "ONTIME";
                            }
                        } else {
                            status = "LATE";
                        }
                        p.setString(1, status);
                        p.setString(2, selectedNIP);
                        p.setDate(3, tglMsk);                        
                        p.executeUpdate();
                        
                    }
                } catch (SQLException e) {
                    switch (e.getErrorCode()) {
                        default:
                            JOptionPane.showMessageDialog(null,
                                    e.getErrorCode() + " : " + e.getMessage(),
                                    "(Data Eselon)Update record error!",
                                    JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Pilih Data Di Tabel Bos!",
                        "Load data error!",
                        JOptionPane.ERROR_MESSAGE);
            }
            loadData(commandSQL);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int i = tabelAbsensi.getSelectedRow();
        JPanel panel = new JPanel();
        JComboBox list = new JComboBox();
        JTextField nama = new JTextField();
        panel.add(new JLabel("Isi Keterangan : "));
        try {
            //this.list.removeAllItems();
            Connection c = KoneksiDatabase.getKoneksi();
            Statement s = c.createStatement();
            String sql = "SELECT * from pegawai";
            ResultSet r = s.executeQuery(sql);

            while (r.next()) {
                list.addItem(r.getString("NIP"));
            }
        } catch (Exception e) {

        }
        panel.setLayout(new GridLayout(2, 4));
        panel.add(new JLabel("Pilih NIP : "));
        panel.add(nama);
        panel.add(list);
        //String selectedNIP = nama.getText();
        int n;
        Object[] options = {"Tambah",
            "Batal"};
        n = JOptionPane.showOptionDialog(null,
                panel,
                "Keterangan ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title
        {
            String SelectedNIP = list.getSelectedItem().toString();
            java.sql.Date tglMsk = new java.sql.Date(new Date().getTime());            
            String ket = nama.getText();
            if (SelectedNIP != null) {
                try {
                    Connection c = KoneksiDatabase.getKoneksi();
                    String sql = "UPDATE `absenmsk` "
                            + "SET `ket` = ? "
                            + "WHERE `absenmsk`.`NIP` =? "
                            + "AND `absenmsk`.`tglmsk` = ?";
                    try (PreparedStatement p = c.prepareStatement(sql)) {
                        p.setString(1, (String) ket);
                        p.setString(2, (String) SelectedNIP);
                        p.setDate(3, tglMsk);
                        p.executeUpdate();
                    }
                } catch (SQLException e) {
                    switch (e.getErrorCode()) {
                        default:
                            JOptionPane.showMessageDialog(null,
                                    e.getErrorCode() + " : " + e.getMessage(),
                                    "(Data Eselon)Update record error!",
                                    JOptionPane.ERROR_MESSAGE);
                            break;

                    }
                } 
            loadData(commandSQL);
            }
        }


    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(FormAbsensiMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormAbsensiMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormAbsensiMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormAbsensiMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormAbsensiMasuk().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LClock;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cbKantor;
    private javax.swing.JComboBox cbStatusmsk;
    private org.jdesktop.swingx.JXDatePicker cbTgl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelAbsensi;
    private javax.swing.JTextField txNamaKantor;
    // End of variables declaration//GEN-END:variables
}
