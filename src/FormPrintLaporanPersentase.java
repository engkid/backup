
import classpdam.Class_Printer;
import classpdam.KoneksiDatabase;
import classpdam.class_pegawai;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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
                "NIP", "Nama", "Kantor", "Early", "Late", "On-Time", "Lain-lain", "Kehadiran(%)", "Reward"
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
                    if (cbStat.getSelectedItem().equals("Masuk")) {
                        o[3] = countEarlyMasuk(r.getString("NIP"));
                        o[4] = countLateMasuk(r.getString("NIP"));
                        o[5] = countOnTimeMasuk(r.getString("NIP"));
                    } else {
                        o[3] = countEarlyKeluar(r.getString("NIP"));
                        o[4] = countLateKeluar(r.getString("NIP"));
                        o[5] = countOnTimeKeluar(r.getString("NIP"));
                    }                               
                    o[6] = countOther(r.getString("NIP"));;
                    int total = Integer.parseInt(o[3].toString())
                            + Integer.parseInt(o[4].toString())
                            + Integer.parseInt(o[5].toString())
                            + Integer.parseInt(o[6].toString());
                    float persentase = (Integer.parseInt(o[3].toString())
                            + Integer.parseInt(o[5].toString()))
                            * 100 / total;
                    o[7] = persentase;
                    if (cbStat.getSelectedItem().equals("Masuk")) {
                       o[8] = Integer.parseInt(o[3].toString()) * 5000;
                    } else {
                       o[8] = Integer.parseInt(o[4].toString()) * 5000; 
                    }
                    
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

    private int countEarlyMasuk(String nip) {
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
        return hasil;
    }

    private int countEarlyKeluar(String nip) {
        int hasil = 0;
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

    private int countLateMasuk(String nip) {
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
        return hasil;
    }

    private int countLateKeluar(String nip) {
        int hasil = 0;
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

    private int countOnTimeMasuk(String nip) {
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
        return hasil;
    }

    private int countOnTimeKeluar(String nip) {
        int hasil = 0;
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

        cbKantor = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txNamaKantor = new javax.swing.JTextField();
        tglawal = new org.jdesktop.swingx.JXDatePicker();
        tglakhir = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cbStat = new javax.swing.JComboBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

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

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("PERSENTASE ABSENSI");

        jLabel4.setText("Kasubbag");

        jLabel5.setText("Admin");

        jLabel6.setText("Kepala");

        jLabel7.setText("Mengetahui");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jLabel4)
                .addGap(177, 177, 177)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(74, 74, 74))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        cbStat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Masuk", "Keluar" }));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txNamaKantor)
                    .addComponent(cbKantor, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tglawal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tglakhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cbStat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(99, 99, 99))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txNamaKantor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbStat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        /*try {
         MessageFormat header = new MessageFormat("Data Rekap Detail Absen Karyawan PDAM");
         MessageFormat footer = new MessageFormat("Halaman{0,number,integer}");
         tabel.print(JTable.PrintMode.FIT_WIDTH, header, footer);
         } catch (java.awt.print.PrinterException e) {
         System.err.format("Cannot Print %s%n", e.getMessage());
         }*/
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
            PageFormat pf = pj.defaultPage();
            Paper paper = pf.getPaper();
            //ukuran lebar dalam inci
            double width = 8.3d * 72d;
            //ukuran panjang dalam inci
            double height = 11.7d * 72d;
            double margin = 4;
            paper.setSize(width, height);
            paper.setImageableArea(
                    margin,
                    margin,
                    width - (margin * 2),
                    height - (margin * 2));
            pf.setOrientation(PageFormat.PORTRAIT);
            pf.setPaper(paper);

            pj.setPrintable(new Class_Printer(jPanel1), pf);

            try {
                pj.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
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
    private javax.swing.JComboBox cbStat;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel;
    private org.jdesktop.swingx.JXDatePicker tglakhir;
    private org.jdesktop.swingx.JXDatePicker tglawal;
    private javax.swing.JTextField txNamaKantor;
    // End of variables declaration//GEN-END:variables
}
