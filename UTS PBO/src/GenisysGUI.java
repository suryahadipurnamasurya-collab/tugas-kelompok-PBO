import java.awt.*;
import java.net.URL; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel; 

interface DeteksiAnomali {
    boolean cekMutasiBerbahaya();
    String getRekomendasiTindakan();
}

abstract class SampelGenetik implements DeteksiAnomali {
    protected String idSampel;
    protected String sequenceDNA;
    protected String tanggalAnalisis;

    public SampelGenetik(String idSampel, String sequenceDNA, String tanggalAnalisis) {
        this.idSampel = idSampel;
        this.sequenceDNA = sequenceDNA;
        this.tanggalAnalisis = tanggalAnalisis;
    }

    public abstract double hitungPotensiMutasi();
    public abstract String getTipe();
    public abstract String getDetail();

    public String getIdSampel() { return idSampel; }
    public String getSequenceDNA() { return sequenceDNA; }
}

class DNAManusia extends SampelGenetik {
    private String golonganDarah;
    public DNAManusia(String id, String seq, String tgl, String goldar) { super(id, seq, tgl); this.golonganDarah = goldar; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 0.15; }
    @Override public String getTipe() { return "Human DNA"; }
    @Override public String getDetail() { return "Gol. Darah: " + golonganDarah; }
    @Override public boolean cekMutasiBerbahaya() { return hitungPotensiMutasi() > 50.0; }
    @Override public String getRekomendasiTindakan() { return "Observasi Rutin"; }
}

class BakteriPathogen extends SampelGenetik {
    private double tingkatResistensiAntibiotik;
    public BakteriPathogen(String id, String seq, String tgl, double res) { super(id, seq, tgl); this.tingkatResistensiAntibiotik = res; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 1.5 + tingkatResistensiAntibiotik; }
    @Override public String getTipe() { return "Bacterial Pathogen"; }
    @Override public String getDetail() { return "Resistensi: " + tingkatResistensiAntibiotik + "%"; }
    @Override public boolean cekMutasiBerbahaya() { return tingkatResistensiAntibiotik > 80.0; }
    @Override public String getRekomendasiTindakan() { return "Gunakan Antibiotik Eksperimental"; }
}

class VirusDsDNA extends SampelGenetik {
    private String namaVirus;
    public VirusDsDNA(String id, String seq, String tgl, String nama) { super(id, seq, tgl); this.namaVirus = nama; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 0.8; }
    @Override public String getTipe() { return "Virus dsDNA (I)"; }
    @Override public String getDetail() { return "Strain: " + namaVirus; }
    @Override public boolean cekMutasiBerbahaya() { return sequenceDNA.contains("TATA"); }
    @Override public String getRekomendasiTindakan() { return "Antiviral Standar"; }
}

class VirusSsRNAPlus extends SampelGenetik {
    private String namaVirus;
    public VirusSsRNAPlus(String id, String seq, String tgl, String nama) { super(id, seq, tgl); this.namaVirus = nama; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 3.5; }
    @Override public String getTipe() { return "Virus (+)ssRNA (IV)"; }
    @Override public String getDetail() { return "Strain: " + namaVirus; }
    @Override public boolean cekMutasiBerbahaya() { return sequenceDNA.contains("UAG") || hitungPotensiMutasi() > 70; }
    @Override public String getRekomendasiTindakan() { return "Karantina Terbatas"; }
}

class VirusRetrovirus extends SampelGenetik {
    private String namaVirus;
    public VirusRetrovirus(String id, String seq, String tgl, String nama) { super(id, seq, tgl); this.namaVirus = nama; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 8.0; }
    @Override public String getTipe() { return "Retrovirus (Group VI)"; }
    @Override public String getDetail() { return "Strain: " + namaVirus; }
    @Override public boolean cekMutasiBerbahaya() { return true; } 
    @Override public String getRekomendasiTindakan() { return "ISOLASI TOTAL (Biohazard 4)!"; }
}

class DatabaseLab {
    private SampelGenetik[] arrayDatabase;
    private int jumlahData;

    public DatabaseLab(int kapasitas) {
        arrayDatabase = new SampelGenetik[kapasitas];
        jumlahData = 0;
    }

    public void tambahData(SampelGenetik sampel) throws Exception {
        if (jumlahData >= arrayDatabase.length) throw new Exception("Database Penuh!");
        arrayDatabase[jumlahData++] = sampel;
    }

    public SampelGenetik[] getAllData() {
        SampelGenetik[] dataAktif = new SampelGenetik[jumlahData];
        System.arraycopy(arrayDatabase, 0, dataAktif, 0, jumlahData);
        return dataAktif;
    }

    public void urutkanDataBahaya() {
        for (int i = 0; i < jumlahData - 1; i++) {
            int indexMax = i;
            for (int j = i + 1; j < jumlahData; j++) {
                if (arrayDatabase[j].hitungPotensiMutasi() > arrayDatabase[indexMax].hitungPotensiMutasi()) {
                    indexMax = j;
                }
            }
            SampelGenetik temp = arrayDatabase[indexMax];
            arrayDatabase[indexMax] = arrayDatabase[i];
            arrayDatabase[i] = temp;
        }
    }

    public SampelGenetik cariBerdasarkanID(String idCari) {
        for (int i = 0; i < jumlahData; i++) {
            if (arrayDatabase[i].getIdSampel().equalsIgnoreCase(idCari)) return arrayDatabase[i];
        }
        return null;
    }
}

public class GenisysGUI extends JFrame {
    private DatabaseLab databaseLab = new DatabaseLab(100);
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea terminalLog;
    private JTextField txtId, txtSequence, txtTanggal, txtDetail;
    private JComboBox<String> comboTipe;

    public GenisysGUI() {
        setTitle("GENISYS COMMAND CENTER - UNESA Division of Genetics");
        setSize(1050, 780); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(5, 5));

        buatPanelHeader(); 

        inisialisasiDataAwal();
        buatPanelInput(); 
        buatPanelTabelDanLog(); 
        
        cetakTerminal("GENISYS SYSTEM READY.");
        refreshTable();
    }

    private void buatPanelHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(20, 20, 20)); 
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 

        JTextArea identityLog = new JTextArea();
        identityLog.setEditable(false);
        identityLog.setBackground(new Color(20, 20, 20)); 
        identityLog.setForeground(new Color(0, 255, 0)); 
        identityLog.setText("SYSTEM BOOTING...\n" +
                          "Lab Pusat UNESA - Divisi Genetik\n" +
                          "Nama Pembuat : Surya, Ray, Muti, Fauzan ray ganteng\n" +
                          "NIM          : 25051204402\n" +
                          "NIM          : 25051204388\n" +
                          "Status       : DATABASE ONLINE. READY FOR INPUT.");
        panelHeader.add(identityLog, BorderLayout.WEST);

        JLabel logoLabel = new JLabel();
        
        String imageFileName = "/LogoUnesa.png";
        URL imageURL = GenisysGUI.class.getResource(imageFileName);
        
        if (imageURL != null) {
            ImageIcon unesaIcon = new ImageIcon(imageURL);
            if (unesaIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image img = unesaIcon.getImage();
                Image scaledImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                gagalLoadGambar(logoLabel);
            }
        } else {
            gagalLoadGambar(logoLabel);
            System.err.println("Gagal memuat logo: Pastikan 'LogoUnesa.webp' berada di dalam folder 'src'.");
        }
        
        logoLabel.setPreferredSize(new Dimension(80, 80)); 
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelHeader.add(logoLabel, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);
    }

    private void gagalLoadGambar(JLabel label) {
        label.setText("<html><center>LOGO<br>MISSING</center></html>");
        label.setForeground(Color.RED);
        label.setFont(new Font("Arial", Font.ITALIC, 12));
        label.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
    }

    private void buatPanelInput() {
        JPanel panelInput = new JPanel(new GridLayout(6, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Spesimen (Baltimore System)"));
        panelInput.setPreferredSize(new Dimension(300, 0));

        panelInput.add(new JLabel("ID Sampel (Unik):"));
        txtId = new JTextField(); panelInput.add(txtId);

        panelInput.add(new JLabel("Sequence DNA/RNA:"));
        txtSequence = new JTextField(); panelInput.add(txtSequence);

        panelInput.add(new JLabel("Tgl Analisis (DD-MM-YYYY):"));
        txtTanggal = new JTextField(); panelInput.add(txtTanggal);

        panelInput.add(new JLabel("Klasifikasi Biologi:"));
        String[] tipe = {"Human DNA", "Bacterial Pathogen", "Virus dsDNA (I)", "Virus (+)ssRNA (IV)", "Retrovirus (VI)"};
        comboTipe = new JComboBox<>(tipe); panelInput.add(comboTipe);

        panelInput.add(new JLabel("Gol. Darah/Strain Virus:"));
        txtDetail = new JTextField(); panelInput.add(txtDetail);

        JButton btnSimpan = new JButton("<html><center>💾<br>ENCODE DATA</center></html>"); 
        btnSimpan.setBackground(new Color(41, 128, 185));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 15));
        btnSimpan.addActionListener(e -> simpanData());
        panelInput.add(btnSimpan);

        add(panelInput, BorderLayout.WEST);
    }

    private void buatPanelTabelDanLog() {
        String[] kolom = {"ID", "Klasifikasi", "Detail Khusus", "Potensi Mutasi (%)", "Status Bahaya", "Tindakan Karantina"};
        tableModel = new DefaultTableModel(kolom, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Database Genetik"));

        terminalLog = new JTextArea();
        terminalLog.setEditable(false);
        terminalLog.setBackground(Color.BLACK);
        terminalLog.setForeground(Color.GREEN);
        terminalLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scrollLog = new JScrollPane(terminalLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("System Event Log"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, scrollLog);
        splitPane.setDividerLocation(300);

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton btnSort = new JButton("<html><center>📶<br>SORT</center></html>"); 
        btnSort.setToolTipText("Urutkan dari yang paling berbahaya");
        btnSort.setBackground(new Color(230, 126, 34));
        btnSort.setForeground(Color.WHITE);
        btnSort.setFont(new Font("Arial", Font.BOLD, 13));
        btnSort.addActionListener(e -> {
            databaseLab.urutkanDataBahaya();
            refreshTable();
            cetakTerminal(">> ALGORITMA SORTING DIJALANKAN (Selection Sort berdasarkan Laju Mutasi).");
        });

        // Tombol Searching dengan Ikon Kaca Pembesar Unicode (🔍)
        JButton btnSearch = new JButton("<html><center>🔍<br>SEARCH</center></html>"); 
        btnSearch.setToolTipText("Lacak ID Sampel spesifik");
        btnSearch.setBackground(new Color(155, 89, 182)); 
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Arial", Font.BOLD, 13));
        btnSearch.addActionListener(e -> {
            String target = JOptionPane.showInputDialog(this, "Masukkan ID Sampel yang dicari:");
            if (target != null && !target.trim().isEmpty()) {
                SampelGenetik hasil = databaseLab.cariBerdasarkanID(target);
                if (hasil != null) {
                    tableModel.setRowCount(0); 
                    tambahBarisKeTabel(hasil);
                    cetakTerminal(">> SEARCHING SUKSES: Sampel " + target + " ditemukan!");
                } else {
                    cetakTerminal(">> SEARCHING GAGAL: Sampel " + target + " tidak terdaftar.");
                    JOptionPane.showMessageDialog(this, "ID Tidak Ditemukan!", "Error 404", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnReset = new JButton("<html><center>🗑<br>RESET</center></html>"); 
        btnReset.setToolTipText("Reset tampilan tabel");
        btnReset.setBackground(new Color(149, 165, 166)); 
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Arial", Font.BOLD, 13));
        btnReset.addActionListener(e -> { refreshTable(); cetakTerminal(">> Tampilan tabel direset ke data penuh."); });

        panelAksi.add(btnSort); panelAksi.add(btnSearch); panelAksi.add(btnReset);

        JPanel panelKanan = new JPanel(new BorderLayout());
        panelKanan.add(splitPane, BorderLayout.CENTER);
        panelKanan.add(panelAksi, BorderLayout.SOUTH);

        add(panelKanan, BorderLayout.CENTER);
    }

    private void inisialisasiDataAwal() {
        try {
            databaseLab.tambahData(new DNAManusia("H-001", "ATCGGCTAGCTA", "30-03-2026", "O+"));
            databaseLab.tambahData(new VirusDsDNA("V-HSV1", "ATCGTATA", "30-03-2026", "Herpes Simplex Virus"));
            databaseLab.tambahData(new VirusSsRNAPlus("V-COV2", "AUCGUAGCUUA", "30-03-2026", "SARS-CoV-2 (COVID-19)"));
            databaseLab.tambahData(new VirusRetrovirus("V-HIV1", "AUCGGCA", "29-03-2026", "Human Immunodeficiency Virus"));
            databaseLab.tambahData(new BakteriPathogen("B-ECOLI", "GGCCTTTAAA", "29-03-2026", 85.5));
        } catch (Exception e) { System.out.println("Error init: " + e.getMessage()); }
    }

    private void simpanData() {
        try {
            if (txtId.getText().isEmpty() || txtSequence.getText().isEmpty() || txtDetail.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = txtId.getText(); String seq = txtSequence.getText(); String tgl = txtTanggal.getText(); String detail = txtDetail.getText(); int pilihan = comboTipe.getSelectedIndex();
            SampelGenetik sampelBaru;
            switch (pilihan) {
                case 0: sampelBaru = new DNAManusia(id, seq, tgl, detail); break;
                case 1: try { double res = Double.parseDouble(detail); sampelBaru = new BakteriPathogen(id, seq, tgl, res); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Kolom detail Bakteri harus berupa ANGKA (Persentase)!", "Error", JOptionPane.ERROR_MESSAGE); return; } break;
                case 2: sampelBaru = new VirusDsDNA(id, seq, tgl, detail); break;
                case 3: sampelBaru = new VirusSsRNAPlus(id, seq, tgl, detail); break;
                case 4: sampelBaru = new VirusRetrovirus(id, seq, tgl, detail); break;
                default: throw new Exception("Tipe spesimen tidak valid.");
            }
            databaseLab.tambahData(sampelBaru); refreshTable();
            cetakTerminal(">> BERHASIL ENCODE: " + id + " ditambahkan ke database.");
            txtId.setText(""); txtSequence.setText(""); txtDetail.setText("");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); SampelGenetik[] semuaData = databaseLab.getAllData();
        for (SampelGenetik s : semuaData) tambahBarisKeTabel(s);
    }

    private void tambahBarisKeTabel(SampelGenetik s) {
        String statusMutasi = s.cekMutasiBerbahaya() ? "⚠ KRITIS" : "AMAN";
        Object[] baris = { s.getIdSampel(), s.getTipe(), s.getDetail(), String.format("%.2f", s.hitungPotensiMutasi()), statusMutasi, s.getRekomendasiTindakan() };
        tableModel.addRow(baris);
    }

    private void cetakTerminal(String pesan) {
        terminalLog.append(pesan + "\n");
        terminalLog.setCaretPosition(terminalLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GenisysGUI().setVisible(true));
    }
}