import java.awt.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
<<<<<<< HEAD
import javax.swing.table.DefaultTableCellRenderer;
=======
>>>>>>> 45c0715641bfd669511442c2b1dd63965ca89350
import javax.swing.table.DefaultTableModel;

interface DeteksiAnomali extends Serializable {
    boolean cekMutasiBerbahaya();
    String getRekomendasiTindakan();
}

abstract class SampelGenetik implements DeteksiAnomali {
    protected String idSampel;
    protected String sequenceDNA;
    protected String tanggalAnalisis;
    protected String operator;

    public SampelGenetik(String idSampel, String sequenceDNA, String tanggalAnalisis, String operator) {
        this.idSampel = idSampel;
        this.sequenceDNA = sequenceDNA;
        this.tanggalAnalisis = tanggalAnalisis;
        this.operator = operator;
    }

    public abstract double hitungPotensiMutasi();
    public abstract String getTipe();
    public abstract String getDetail();

    public String getIdSampel() { return idSampel; }
    public String getSequenceDNA() { return sequenceDNA; }
    public String getOperator() { return operator; }
}

class DNAManusia extends SampelGenetik {
    private String golonganDarah;
    public DNAManusia(String id, String seq, String tgl, String goldar, String op) { super(id, seq, tgl, op); this.golonganDarah = goldar; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 0.15; }
    @Override public String getTipe() { return "Human DNA"; }
    @Override public String getDetail() { return "Gol. Darah: " + golonganDarah; }
    @Override public boolean cekMutasiBerbahaya() { return hitungPotensiMutasi() > 50.0; }
    @Override public String getRekomendasiTindakan() { return "Observasi Rutin"; }
}

class BakteriPathogen extends SampelGenetik {
    private double tingkatResistensiAntibiotik;
    public BakteriPathogen(String id, String seq, String tgl, double res, String op) { super(id, seq, tgl, op); this.tingkatResistensiAntibiotik = res; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 1.5 + tingkatResistensiAntibiotik; }
    @Override public String getTipe() { return "Bacterial Pathogen"; }
    @Override public String getDetail() { return "Resistensi: " + tingkatResistensiAntibiotik + "%"; }
    @Override public boolean cekMutasiBerbahaya() { return tingkatResistensiAntibiotik > 80.0; }
    @Override public String getRekomendasiTindakan() { return "Gunakan Antibiotik Eksperimental"; }
}

class VirusDsDNA extends SampelGenetik {
    private String namaVirus;
    public VirusDsDNA(String id, String seq, String tgl, String nama, String op) { super(id, seq, tgl, op); this.namaVirus = nama; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 0.8; }
    @Override public String getTipe() { return "Virus dsDNA (I)"; }
    @Override public String getDetail() { return "Strain: " + namaVirus; }
    @Override public boolean cekMutasiBerbahaya() { return sequenceDNA.contains("TATA"); }
    @Override public String getRekomendasiTindakan() { return "Antiviral Standar"; }
}

class VirusSsRNAPlus extends SampelGenetik {
    private String namaVirus;
    public VirusSsRNAPlus(String id, String seq, String tgl, String nama, String op) { super(id, seq, tgl, op); this.namaVirus = nama; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 3.5; }
    @Override public String getTipe() { return "Virus (+)ssRNA (IV)"; }
    @Override public String getDetail() { return "Strain: " + namaVirus; }
    @Override public boolean cekMutasiBerbahaya() { return sequenceDNA.contains("UAG") || hitungPotensiMutasi() > 70; }
    @Override public String getRekomendasiTindakan() { return "Karantina Terbatas"; }
}

class VirusRetrovirus extends SampelGenetik {
    private String namaVirus;
    public VirusRetrovirus(String id, String seq, String tgl, String nama, String op) { super(id, seq, tgl, op); this.namaVirus = nama; }
    @Override public double hitungPotensiMutasi() { return sequenceDNA.length() * 8.0; }
    @Override public String getTipe() { return "Retrovirus (Group VI)"; }
    @Override public String getDetail() { return "Strain: " + namaVirus; }
    @Override public boolean cekMutasiBerbahaya() { return true; } 
    @Override public String getRekomendasiTindakan() { return "ISOLASI TOTAL (Biohazard 4)!"; }
}

class DatabaseLab {
    private SampelGenetik[] arrayDatabase;
    private int jumlahData;
    private final String FILE_NAME = "database_genisys.dat";

    public DatabaseLab(int kapasitas) {
        arrayDatabase = new SampelGenetik[kapasitas];
        jumlahData = 0;
        muatDataDariFile();
    }

    public void tambahData(SampelGenetik sampel) throws Exception {
        if (jumlahData >= arrayDatabase.length) throw new Exception("Database Penuh!");
        arrayDatabase[jumlahData++] = sampel;
        simpanDataKeFile();
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

    private void simpanDataKeFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(arrayDatabase);
            oos.writeInt(jumlahData);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan data: " + e.getMessage());
        }
    }

    private void muatDataDariFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                arrayDatabase = (SampelGenetik[]) ois.readObject();
                jumlahData = ois.readInt();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Gagal memuat data: " + e.getMessage());
            }
        }
    }
}
class LoginGUI extends JFrame {
    private final String[][] AKUN_KELOMPOK = {
        {"SURYAHADI PURNAMA", "4402"},
        {"LEEVI QUSHAI RAY IFTIKHAR", "4390"}, 
        {"MANDRIVA RADITHYA CAHYADI", "4406"}, 
        {"MUHAMMAD NADHIF FAIZURRAHMAN", "4388"},
        {"SALSABILLA OKTAVIA RAMADHANI", "4393"},
        {"FAUZAN RAMDHANI FAJRI", "4400"},
        {"SAKARINA HARERA", "4407"},
        {"MUTIARA NUR HIDAYAH", "4412"}
    };

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginGUI() {
        setTitle("GENISYS - Security Authentication");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelUtama = new JPanel(new GridLayout(3, 1, 10, 10));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("<html><center><b>GENISYS SECURE LOGIN</b><br>Gunakan Nama & 4 Digit Akhir NIM</center></html>", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 5, 5));
        panelForm.add(new JLabel("Username (Nama (CAPS)):"));
        txtUsername = new JTextField();
        panelForm.add(txtUsername);

        panelForm.add(new JLabel("Password (4 Digit NIM):"));
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword);
        
        panelUtama.add(panelForm);

        JButton btnLogin = new JButton("LOGIN TO SYSTEM");
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogin.addActionListener(e -> prosesLogin());
        
        JPanel panelBtn = new JPanel();
        panelBtn.add(btnLogin);
        panelUtama.add(panelBtn);

        add(panelUtama, BorderLayout.CENTER);
    }

    private void prosesLogin() {
        String inputUser = txtUsername.getText();
        String inputPass = new String(txtPassword.getPassword());
        boolean loginSukses = false;

        for (String[] akun : AKUN_KELOMPOK) {
            if (akun[0].equalsIgnoreCase(inputUser) && akun[1].equals(inputPass)) {
                loginSukses = true;
                break;
            }
        }

        if (loginSukses) {
            JOptionPane.showMessageDialog(this, "Akses Diberikan. Selamat datang, " + inputUser + "!", "Login Sukses", JOptionPane.INFORMATION_MESSAGE);
            new GenisysGUI(inputUser).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Akses Ditolak! Username atau NIM salah.", "Security Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
}

public class GenisysGUI extends JFrame {
    private DatabaseLab databaseLab = new DatabaseLab(100);
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea terminalLog;
    private JTextField txtId, txtSequence, txtTanggal, txtDetail;
    private JComboBox<String> comboTipe;
    private String namaOperatorAktif;

    public GenisysGUI(String namaOperator) {
        this.namaOperatorAktif = namaOperator;

        setTitle("GENISYS COMMAND CENTER - Logged in as: " + namaOperatorAktif);
        setSize(1100, 780); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        buatPanelHeader(); 
        buatPanelInput(); 
        buatPanelTabelDanLog(); 
        
        cetakTerminal("GENISYS SYSTEM READY. Operator Activating: " + namaOperatorAktif);
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
        identityLog.setFont(new Font("Consolas", Font.BOLD, 14)); 
        identityLog.setText("SYSTEM BOOTING...\n" +
                          "Lab Pusat UNESA - Divisi Genetik\n" +
                          "Sistem Penyimpanan : PERMANENT STORAGE (I/O Active)\n" +
                          "Operator Saat Ini  : " + namaOperatorAktif.toUpperCase() + "\n" +
                          "Status             : DATABASE ONLINE. READY FOR INPUT.");
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

        JButton btnSimpan = new JButton("<html><center>沈<br>ENCODE DATA</center></html>"); 
        btnSimpan.setBackground(new Color(41, 128, 185)); 
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 15)); 
        btnSimpan.addActionListener(e -> simpanData());
        panelInput.add(btnSimpan);

        add(panelInput, BorderLayout.WEST);
    }

    private void buatPanelTabelDanLog() {
        String[] kolom = {"ID", "Diinput Oleh", "Klasifikasi", "Detail Khusus", "Mutasi (%)", "Status", "Tindakan"};
        
        // Modifikasi DefaultTableModel agar kolom Mutasi (%) diurutkan sebagai angka (Double)
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Double.class;
                }
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        // AKTIFKAN FITUR AUTO SORTING DI HEADER TABEL
        table.setAutoCreateRowSorter(true); 

        // Tambahkan format desimal untuk kolom Mutasi (%) agar tetap rapi saat ditampilkan
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = String.format("%.2f", (Double) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Database Genetik (Tersimpan Permanen)"));

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

        JButton btnSearch = new JButton("<html><center>剥<br>SEARCH</center></html>"); 
        btnSearch.setPreferredSize(new Dimension(110, 50)); 
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

        JButton btnReset = new JButton("<html><center>卵<br>RESET</center></html>"); 
        btnReset.setPreferredSize(new Dimension(100, 50)); 
        btnReset.setBackground(new Color(149, 165, 166)); 
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Arial", Font.BOLD, 13));
        btnReset.addActionListener(e -> { refreshTable(); cetakTerminal(">> Tampilan tabel direset ke data penuh."); });

        panelAksi.add(btnSearch); 
        panelAksi.add(btnReset);

        JPanel panelKanan = new JPanel(new BorderLayout());
        panelKanan.add(splitPane, BorderLayout.CENTER);
        panelKanan.add(panelAksi, BorderLayout.SOUTH);

        add(panelKanan, BorderLayout.CENTER);
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
                case 0: sampelBaru = new DNAManusia(id, seq, tgl, detail, namaOperatorAktif); break;
                case 1: try { double res = Double.parseDouble(detail); sampelBaru = new BakteriPathogen(id, seq, tgl, res, namaOperatorAktif); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Kolom detail Bakteri harus berupa ANGKA (Persentase)!", "Error", JOptionPane.ERROR_MESSAGE); return; } break;
                case 2: sampelBaru = new VirusDsDNA(id, seq, tgl, detail, namaOperatorAktif); break;
                case 3: sampelBaru = new VirusSsRNAPlus(id, seq, tgl, detail, namaOperatorAktif); break;
                case 4: sampelBaru = new VirusRetrovirus(id, seq, tgl, detail, namaOperatorAktif); break;
                default: throw new Exception("Tipe spesimen tidak valid.");
            }
            
            databaseLab.tambahData(sampelBaru);
            refreshTable();
            cetakTerminal(">> BERHASIL ENCODE: " + id + " ditambahkan ke database oleh " + namaOperatorAktif);
            txtId.setText(""); txtSequence.setText(""); txtDetail.setText("");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); SampelGenetik[] semuaData = databaseLab.getAllData();
        for (SampelGenetik s : semuaData) tambahBarisKeTabel(s);
    }

    private void tambahBarisKeTabel(SampelGenetik s) {
        String statusMutasi = s.cekMutasiBerbahaya() ? "笞 KRITIS" : "AMAN";
        // Nilai s.hitungPotensiMutasi() kini dipassing sebagai angka (Double) asli, bukan format String
        Object[] baris = { s.getIdSampel(), s.getOperator(), s.getTipe(), s.getDetail(), s.hitungPotensiMutasi(), statusMutasi, s.getRekomendasiTindakan() };
        tableModel.addRow(baris);
    }

    private void cetakTerminal(String pesan) {
        terminalLog.append(pesan + "\n");
        terminalLog.setCaretPosition(terminalLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}