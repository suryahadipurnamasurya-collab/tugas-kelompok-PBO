import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// ============================================================================
// 1. LOGIKA DOMAIN & STRUKTUR DATA
// ============================================================================

interface DeteksiAnomali extends Serializable {
    boolean cekMutasiBerbahaya();
    String getRekomendasiTindakan();
}

abstract class SampelGenetik implements DeteksiAnomali {
    protected String idSampel, sequenceDNA, tanggalAnalisis, operator;
    public SampelGenetik(String id, String seq, String tgl, String op) {
        this.idSampel = id; this.sequenceDNA = seq; this.tanggalAnalisis = tgl; this.operator = op;
    }
    public abstract double hitungPotensiMutasi();
    public abstract String getTipe();
    public abstract String getDetail();
    public String getIdSampel() { return idSampel; }
    public String getSequenceDNA() { return sequenceDNA; }
    public String getOperator() { return operator; }
    public String getTanggal() { return tanggalAnalisis; }
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
    private ArrayList<SampelGenetik> database;
    // Mengubah nama file agar membaca database yang 100% baru dan kosong
    private final String FILE_NAME = "database_genisys_v5.dat";

    public DatabaseLab() {
        database = new ArrayList<>();
        muatDataDariFile();
        // Tidak ada data dummy yang dimasukkan, database mulai murni kosong
    }

    public void tambahData(SampelGenetik sampel) {
        database.add(sampel);
        simpanDataKeFile();
    }

    public ArrayList<SampelGenetik> getAllData() { return database; }
    public int getJumlahData() { return database.size(); }

    private void simpanDataKeFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(database);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void muatDataDariFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                database = (ArrayList<SampelGenetik>) ois.readObject();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}

// ============================================================================
// 2. DESIGN SYSTEM & THEME
// ============================================================================

class AppTheme {
    public static final Color BG_MAIN = new Color(10, 25, 21);
    public static final Color BG_SIDEBAR = new Color(8, 20, 17);
    public static final Color BG_CARD = new Color(16, 33, 29);
    public static final Color BG_INPUT = new Color(12, 28, 24);
    
    public static final Color ACCENT_TEAL = new Color(27, 184, 145);
    public static final Color ACCENT_TEAL_DARK = new Color(20, 130, 102); 
    public static final Color ACCENT_RED = new Color(231, 76, 60);

    public static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(141, 155, 150);
    public static final Color BORDER_COLOR = new Color(26, 51, 44);

    public static final Font FONT_H1 = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
}

// ============================================================================
// 3. REUSABLE UI COMPONENTS
// ============================================================================

class RoundedPanel extends JPanel {
    private int radius;
    private Color startColor, endColor;

    public RoundedPanel(int radius, Color bg) {
        this.radius = radius; setBackground(bg); setOpaque(false);
    }
    public void setGradient(Color start, Color end) { this.startColor = start; this.endColor = end; }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (startColor != null && endColor != null) {
            g2.setPaint(new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
        } else {
            g2.setColor(getBackground());
        }
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
}

class ModernInput extends JTextField {
    public ModernInput() {
        setOpaque(false);
        setForeground(Color.black);
        setCaretColor(Color.black);
        setFont(AppTheme.FONT_NORMAL);
        setBorder(new EmptyBorder(6, 15, 6, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // gambar background dulu
        g2.setColor(AppTheme.BG_INPUT);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        g2.setColor(AppTheme.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

        g2.dispose();

        // baru gambar text
        super.paintComponent(g);
    }
}

class ModernPassword extends JPasswordField {
    public ModernPassword() {
        setOpaque(false);
        setForeground(Color.black);
        setCaretColor(Color.black);
        setFont(AppTheme.FONT_NORMAL);
        setBorder(new EmptyBorder(6, 15, 6, 15));
        setEchoChar('•');
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // gambar background dulu
        g2.setColor(AppTheme.BG_INPUT);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        g2.setColor(AppTheme.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

        g2.dispose();

        // gambar text setelah background
        super.paintComponent(g);
    }
}

class ModernComboBox<T> extends JComboBox<T> {
    public ModernComboBox(T[] items) {
        super(items);
        setFont(AppTheme.FONT_NORMAL); setForeground(AppTheme.TEXT_PRIMARY);
        setBackground(AppTheme.BG_INPUT); setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
    }
}

class ModernBtn extends JButton {
    private Color bgNormal, bgHover;
    public ModernBtn(String text, Color normal, Color hover) {
        super(text); this.bgNormal = normal; this.bgHover = hover;
        setFont(AppTheme.FONT_BOLD); setForeground(Color.WHITE);
        setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setBackground(bgHover); repaint(); }
            @Override public void mouseExited(MouseEvent e) { setBackground(bgNormal); repaint(); }
        });
        setBackground(bgNormal);
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground()); g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
        g2.dispose(); super.paintComponent(g);
    }
}

class DarkScrollBarUI extends BasicScrollBarUI {
    @Override protected void configureScrollBarColors() { this.thumbColor = AppTheme.BORDER_COLOR; this.trackColor = AppTheme.BG_MAIN; }
    @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
    @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
    private JButton createZeroButton() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
    @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor); g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
        g2.dispose();
    }
}

// ============================================================================
// 4. CUSTOM CHARTS
// ============================================================================

class DonutChart extends JPanel {
    private int percentage;
    public DonutChart() { setOpaque(false); }
    public void setPercentage(int p) { this.percentage = p; repaint(); }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = Math.min(getWidth(), getHeight()) - 40;
        int x = (getWidth() - size) / 2; int y = (getHeight() - size) / 2;
        
        g2.setStroke(new BasicStroke(18f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(AppTheme.BORDER_COLOR); g2.drawArc(x, y, size, size, 0, 360);
        g2.setColor(AppTheme.ACCENT_TEAL);
        int angle = (int) (360 * (percentage / 100.0));
        g2.drawArc(x, y, size, size, 90, -angle);
        
        g2.setColor(AppTheme.TEXT_PRIMARY); g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        String text = percentage + "%"; FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (size - fm.stringWidth(text))/2, y + (size/2) + 8);
        g2.dispose();
    }
}

class BarChart extends JPanel {
    private int[] data; private int maxVal;
    public BarChart() { setOpaque(false); }
    public void updateData(int[] d) { 
        this.data = d; 
        maxVal = 1; for(int v : d) if(v > maxVal) maxVal = v;
        repaint(); 
    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(data == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight(); int barWidth = 15;
        int gap = (w - (data.length * barWidth)) / (data.length + 1);
        
        for (int i = 0; i < data.length; i++) {
            int barHeight = (int) (((double)data[i] / maxVal) * (h - 30));
            int x = gap + (i * (barWidth + gap)); int y = h - barHeight - 20;
            g2.setColor(AppTheme.BORDER_COLOR); g2.fillRoundRect(x, 20, barWidth, h - 40, 8, 8);
            g2.setColor(AppTheme.ACCENT_TEAL_DARK); g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
        }
        g2.dispose();
    }
}

class LineChart extends JPanel {
    private int[] data;
    public LineChart() { setOpaque(false); }
    public void updateData(int[] d) { this.data = d; repaint(); }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(data == null || data.length < 2) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(AppTheme.ACCENT_TEAL); g2.setStroke(new BasicStroke(2.5f));
        int maxVal = 1; for(int v : data) if(v > maxVal) maxVal = v;
        
        int step = w / (data.length - 1);
        for (int i = 0; i < data.length - 1; i++) {
            int x1 = i * step; int y1 = h - (int)(((double)data[i] / maxVal) * h);
            int x2 = (i + 1) * step; int y2 = h - (int)(((double)data[i+1] / maxVal) * h);
            g2.drawLine(x1, y1, x2, y2);
        }
        g2.dispose();
    }
}

// ============================================================================
// 5. ADD RECORD MODAL (Dialog Tambah Data)
// ============================================================================

class AddRecordModal extends JDialog {
    private ModernInput txtId, txtSequence, txtDetail;
    private ModernComboBox<String> comboTipe;
    private boolean isAdded = false;

    public AddRecordModal(JFrame parent) {
        super(parent, "Add New Specimen Record", true);
        setSize(420, 520);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_CARD);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(8, 1, 0, 10));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(25, 30, 20, 30));

        form.add(createLabel("SPECIMEN ID"));
        form.add(txtId = new ModernInput());

        form.add(createLabel("DNA/RNA SEQUENCE"));
        form.add(txtSequence = new ModernInput());

        form.add(createLabel("CLASSIFICATION"));
        String[] tipe = {"Human DNA", "Bacterial Pathogen", "Virus dsDNA (I)", "Virus (+)ssRNA (IV)", "Retrovirus (VI)"};
        form.add(comboTipe = new ModernComboBox<>(tipe));

        form.add(createLabel("DETAILS / STRAIN"));
        form.add(txtDetail = new ModernInput());

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(0, 0, 20, 30));

        ModernBtn btnCancel = new ModernBtn("Cancel", AppTheme.BG_INPUT, AppTheme.BORDER_COLOR);
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dispose());

        ModernBtn btnSave = new ModernBtn("Save Data", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK);
        btnSave.setPreferredSize(new Dimension(120, 40));
        btnSave.addActionListener(e -> prosesSave());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t); l.setForeground(AppTheme.TEXT_MUTED); l.setFont(AppTheme.FONT_BOLD); return l;
    }

    private void prosesSave() {
        if(txtId.getText().isEmpty() || txtSequence.getText().isEmpty() || txtDetail.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        isAdded = true;
        dispose();
    }

    public boolean isAdded() { return isAdded; }
    public String getId() { return txtId.getText(); }
    public String getSeq() { return txtSequence.getText(); }
    public String getDetail() { return txtDetail.getText(); }
    public int getTipe() { return comboTipe.getSelectedIndex(); }
}

// ============================================================================
// 6. GUI LOGIN PAGE
// ============================================================================

class LoginFrame extends JFrame {
    private ModernInput txtUser;
    private ModernPassword txtPass;

    public LoginFrame() {
        setTitle("Genisys x AuraHealth - Login"); setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_MAIN); setLayout(new BorderLayout());

        RoundedPanel card = new RoundedPanel(25, AppTheme.BG_CARD);
        card.setBorder(new EmptyBorder(40, 40, 40, 40)); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        JLabel lblLogo = new JLabel("Genisys.io"); lblLogo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblLogo.setForeground(AppTheme.ACCENT_TEAL); lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sign in to Medical Dashboard"); lblSub.setFont(AppTheme.FONT_NORMAL);
        lblSub.setForeground(AppTheme.TEXT_MUTED); lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUser = new ModernInput();
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUser.setText(""); // kosongkan
        txtUser.setToolTipText("Masukkan nama lengkap dengan huruf kapital");

        txtPass = new ModernPassword();
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPass.setToolTipText("Masukkan NIM");

        ModernBtn btnLogin = new ModernBtn("LOGIN", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.addActionListener(e -> prosesLogin());

        card.add(Box.createVerticalStrut(20)); card.add(lblLogo); card.add(Box.createVerticalStrut(10));
        card.add(lblSub); card.add(Box.createVerticalStrut(40));
        card.add(createLabel("USERNAME (NAMA)")); card.add(txtUser); card.add(Box.createVerticalStrut(20));
        card.add(createLabel("PASSWORD (NIM)")); card.add(txtPass); card.add(Box.createVerticalStrut(40));
        card.add(btnLogin);

        JPanel wrapper = new JPanel(new GridBagLayout()); wrapper.setOpaque(false); wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String t) { 
        JLabel l = new JLabel(t); l.setFont(new Font("SansSerif", Font.BOLD, 11)); 
        l.setForeground(AppTheme.TEXT_MUTED); l.setAlignmentX(Component.CENTER_ALIGNMENT); 
        return l; 
    }
    
    private void prosesLogin() {
    String user = txtUser.getText().trim().toUpperCase();
    String pass = new String(txtPass.getPassword()).trim();

    // Database akun mahasiswa
    String[][] akunMahasiswa = {
        {"SURYAHADI PURNAMA", "4402"},
        {"LEEVI QUSHAI RAY IFTIKHAR", "4390"},
        {"MANDRIVA RADITHYA CAHYADI", "4406"},
        {"SALSABILA OKTAVIA RAMADHANI", "4393"},
        {"MUHAMMAD NADHIF FAIZURRAHMAN", "4388"},
        {"FARHAN FAUZAN AZIMA", "4389"},
        {"SAKARINA HARERA", "4407"},
        {"MUTIARA NUR HIDAYAH", "4412"}
    };

    boolean loginBerhasil = false;

    for (String[] akun : akunMahasiswa) {
        if (user.equals(akun[0]) && pass.equals(akun[1])) {
            loginBerhasil = true;
            new DashboardFrame(user).setVisible(true);
            this.dispose();
            break;
        }
    }

    if (!loginBerhasil) {
        JOptionPane.showMessageDialog(
            this,
            "Username atau Password salah!",
            "Login Gagal",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
}

// ============================================================================
// 7. MAIN DASHBOARD (Single Page Application Logic)
// ============================================================================

class DashboardFrame extends JFrame {
    private String operatorName;
    private DatabaseLab db = new DatabaseLab();
    
    private DefaultTableModel tableModel;
    private JLabel lblTotal, lblCritical;
    private DonutChart donutChart;
    private BarChart barChart;
    private LineChart lineChart;
    private ModernInput searchField;
    
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private ArrayList<JPanel> sidebarButtons = new ArrayList<>();

    public DashboardFrame(String operatorName) {
        this.operatorName = operatorName;
        setTitle("Dashboard - " + operatorName);
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_MAIN);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        add(buildSidebar(), BorderLayout.WEST);
        
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);
        mainArea.add(buildTopBar(), BorderLayout.NORTH);
        
        cardPanel.add(buildDashboardView(), "DASHBOARD");
        cardPanel.add(buildRecordsView(), "RECORDS");
        cardPanel.add(buildEmptyView("Medical Team features coming soon..."), "TEAM");
        cardPanel.add(buildEmptyView("Medications inventory coming soon..."), "MEDS");
        
        mainArea.add(cardPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(AppTheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel logo = new JLabel("Genisys.io");
        logo.setFont(AppTheme.FONT_H1); logo.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel menu = new JPanel(); menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setOpaque(false); menu.setBorder(new EmptyBorder(40, 0, 0, 0));

        menu.add(createNavBtn("Dashboard", "DASHBOARD", true));
        menu.add(Box.createVerticalStrut(10));
        menu.add(createNavBtn("Medical Team", "TEAM", false));
        menu.add(Box.createVerticalStrut(10));
        menu.add(createNavBtn("Patients / Specs", "RECORDS", false));
        menu.add(Box.createVerticalStrut(10));
        menu.add(createNavBtn("Medications", "MEDS", false));

        sidebar.add(logo, BorderLayout.NORTH); sidebar.add(menu, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createNavBtn(String text, String cardName, boolean active) {
        RoundedPanel p = new RoundedPanel(12, active ? AppTheme.ACCENT_TEAL : AppTheme.BG_SIDEBAR);
        p.setMaximumSize(new Dimension(200, 45)); p.setLayout(new BorderLayout());
        JLabel l = new JLabel("   " + text); l.setFont(AppTheme.FONT_BOLD); l.setForeground(active ? Color.WHITE : AppTheme.TEXT_MUTED);
        p.add(l, BorderLayout.CENTER);
        
        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, cardName);
                for(JPanel btn : sidebarButtons) {
                    btn.setBackground(AppTheme.BG_SIDEBAR);
                    ((JLabel)btn.getComponent(0)).setForeground(AppTheme.TEXT_MUTED);
                }
                p.setBackground(AppTheme.ACCENT_TEAL);
                l.setForeground(Color.WHITE);
            }
            public void mouseEntered(MouseEvent e) { if(p.getBackground() != AppTheme.ACCENT_TEAL) p.setBackground(AppTheme.BG_CARD); }
            public void mouseExited(MouseEvent e) { if(p.getBackground() != AppTheme.ACCENT_TEAL) p.setBackground(AppTheme.BG_SIDEBAR); }
        });
        sidebarButtons.add(p);
        return p;
    }

    private JPanel buildTopBar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setOpaque(false); topbar.setBorder(new EmptyBorder(25, 30, 10, 30));

        JLabel title = new JLabel("Overview"); title.setFont(AppTheme.FONT_H1); title.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); right.setOpaque(false);
        
        searchField = new ModernInput();
        searchField.setPreferredSize(new Dimension(250, 40));
        searchField.setText("Search records...");
        
        // Listener Pencarian Global
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(searchField.getText().equals("Search records...")) searchField.setText(""); }
        });

        RoundedPanel profileBadge = new RoundedPanel(20, AppTheme.BG_CARD);
        profileBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel lblName = new JLabel("👤 " + operatorName);
        lblName.setForeground(AppTheme.TEXT_PRIMARY); lblName.setFont(AppTheme.FONT_BOLD);
        profileBadge.add(lblName);

        right.add(searchField); right.add(profileBadge);
        topbar.add(title, BorderLayout.WEST); topbar.add(right, BorderLayout.EAST);
        return topbar;
    }

    private JScrollPane buildDashboardView() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false); scrollContent.setBorder(new EmptyBorder(20, 30, 30, 30));
        
        JPanel heroRow = new JPanel(new BorderLayout(20, 0)); heroRow.setOpaque(false); heroRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        RoundedPanel welcomeCard = new RoundedPanel(20, AppTheme.ACCENT_TEAL); welcomeCard.setGradient(AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK);
        welcomeCard.setLayout(new BorderLayout()); welcomeCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel lblGreeting = new JLabel("Welcome " + operatorName.split(" ")[0] + " 👋"); lblGreeting.setForeground(Color.WHITE); lblGreeting.setFont(AppTheme.FONT_NORMAL);
        JLabel lblMain = new JLabel("Check Specimen Analytics!"); lblMain.setForeground(Color.WHITE); lblMain.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeCard.add(lblGreeting, BorderLayout.NORTH); welcomeCard.add(lblMain, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 15)); statsPanel.setOpaque(false); statsPanel.setPreferredSize(new Dimension(300, 0));
        RoundedPanel p1 = new RoundedPanel(15, AppTheme.BG_CARD); p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS)); p1.setBorder(new EmptyBorder(15, 20, 15, 20));
        lblTotal = new JLabel("0"); lblTotal.setFont(new Font("SansSerif", Font.BOLD, 24)); lblTotal.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel t1 = new JLabel("Total Database"); t1.setFont(AppTheme.FONT_NORMAL); t1.setForeground(AppTheme.TEXT_MUTED);
        p1.add(lblTotal); p1.add(t1);

        RoundedPanel p2 = new RoundedPanel(15, AppTheme.BG_CARD); p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS)); p2.setBorder(new EmptyBorder(15, 20, 15, 20));
        lblCritical = new JLabel("0"); lblCritical.setFont(new Font("SansSerif", Font.BOLD, 24)); lblCritical.setForeground(AppTheme.ACCENT_RED);
        JLabel t2 = new JLabel("Critical Anomalies"); t2.setFont(AppTheme.FONT_NORMAL); t2.setForeground(AppTheme.TEXT_MUTED);
        p2.add(lblCritical); p2.add(t2);
        statsPanel.add(p1); statsPanel.add(p2);

        heroRow.add(welcomeCard, BorderLayout.CENTER); heroRow.add(statsPanel, BorderLayout.EAST);

        JPanel chartRow = new JPanel(new GridLayout(1, 3, 20, 0)); chartRow.setOpaque(false); chartRow.setPreferredSize(new Dimension(0, 250));
        
        RoundedPanel donutCard = new RoundedPanel(20, AppTheme.BG_CARD); donutCard.setLayout(new BorderLayout()); donutCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel ct1 = new JLabel("Mutation Ratio"); ct1.setForeground(AppTheme.TEXT_PRIMARY); ct1.setFont(AppTheme.FONT_H2);
        donutChart = new DonutChart(); donutCard.add(ct1, BorderLayout.NORTH); donutCard.add(donutChart, BorderLayout.CENTER);

        RoundedPanel lineCard = new RoundedPanel(20, AppTheme.BG_CARD); lineCard.setLayout(new BorderLayout()); lineCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel ct2 = new JLabel("Activity Timeline"); ct2.setForeground(AppTheme.TEXT_PRIMARY); ct2.setFont(AppTheme.FONT_H2);
        lineChart = new LineChart(); lineCard.add(ct2, BorderLayout.NORTH); lineCard.add(lineChart, BorderLayout.CENTER);

        RoundedPanel barCard = new RoundedPanel(20, AppTheme.BG_CARD); barCard.setLayout(new BorderLayout()); barCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel ct3 = new JLabel("Specimen Types"); ct3.setForeground(AppTheme.TEXT_PRIMARY); ct3.setFont(AppTheme.FONT_H2);
        barChart = new BarChart(); barCard.add(ct3, BorderLayout.NORTH); barCard.add(barChart, BorderLayout.CENTER);

        chartRow.add(donutCard); chartRow.add(lineCard); chartRow.add(barCard);

        scrollContent.add(heroRow); scrollContent.add(Box.createVerticalStrut(20)); scrollContent.add(chartRow);

        JScrollPane scroll = new JScrollPane(scrollContent); scroll.setBorder(null); scroll.getViewport().setBackground(AppTheme.BG_MAIN);
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI()); scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildRecordsView() {
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setOpaque(false); wrapper.setBorder(new EmptyBorder(20, 30, 30, 30));
        RoundedPanel tableCard = new RoundedPanel(20, AppTheme.BG_CARD); tableCard.setLayout(new BorderLayout(0, 15)); tableCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        JLabel title = new JLabel("Patients & Specimens Database"); title.setFont(AppTheme.FONT_H2); title.setForeground(AppTheme.TEXT_PRIMARY);
        
        ModernBtn btnAdd = new ModernBtn("+ Add Record", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK);
        btnAdd.setPreferredSize(new Dimension(120, 35));
        btnAdd.addActionListener(e -> panggilFormAdd());

        header.add(title, BorderLayout.WEST); header.add(btnAdd, BorderLayout.EAST);

        String[] kolom = {"Specimen ID", "Date", "Doctor / Operator", "Class", "Details", "Status"};
        tableModel = new DefaultTableModel(kolom, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        JTable table = new JTable(tableModel); table.setRowHeight(45); table.setFont(AppTheme.FONT_NORMAL);
        table.setBackground(AppTheme.BG_CARD); table.setForeground(AppTheme.TEXT_PRIMARY); table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0));
        
        table.setSelectionBackground(AppTheme.ACCENT_TEAL_DARK);
        table.setSelectionForeground(Color.WHITE);
        
        table.getTableHeader().setBackground(AppTheme.BG_CARD); table.getTableHeader().setForeground(AppTheme.TEXT_MUTED);
        table.getTableHeader().setFont(AppTheme.FONT_BOLD); table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // --- MENGAKTIFKAN FITUR SORTING A-Z & URUTAN TANGGAL ---
        table.setAutoCreateRowSorter(true);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        // Custom Comparator untuk Kolom Tanggal (Index 1) agar diurutkan berdasar Waktu, bukan Abjad
        sorter.setComparator(1, new Comparator<String>() {
            SimpleDateFormat f = new SimpleDateFormat("dd MMM, yyyy");
            @Override
            public int compare(String o1, String o2) {
                try {
                    Date d1 = f.parse(o1);
                    Date d2 = f.parse(o2);
                    return d1.compareTo(d2);
                } catch (Exception e) {
                    return o1.compareTo(o2);
                }
            }
        });
        // --------------------------------------------------------

        JScrollPane scroll = new JScrollPane(table); scroll.getViewport().setBackground(AppTheme.BG_CARD); scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());

        tableCard.add(header, BorderLayout.NORTH); tableCard.add(scroll, BorderLayout.CENTER);
        wrapper.add(tableCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildEmptyView(String msg) {
        JPanel p = new JPanel(new GridBagLayout()); p.setOpaque(false);
        JLabel l = new JLabel(msg); l.setFont(AppTheme.FONT_H2); l.setForeground(AppTheme.TEXT_MUTED); p.add(l);
        return p;
    }

    private void panggilFormAdd() {
        AddRecordModal modal = new AddRecordModal(this);
        modal.setVisible(true);
        if (modal.isAdded()) {
            String id = modal.getId(); String seq = modal.getSeq(); 
            String tgl = new SimpleDateFormat("dd MMM, yyyy").format(new Date());
            String detail = modal.getDetail(); int tipe = modal.getTipe();
            SampelGenetik s = null;
            
            try {
                switch(tipe) {
                    case 0: s = new DNAManusia(id, seq, tgl, detail, operatorName); break;
                    case 1: s = new BakteriPathogen(id, seq, tgl, Double.parseDouble(detail), operatorName); break;
                    case 2: s = new VirusDsDNA(id, seq, tgl, detail, operatorName); break;
                    case 3: s = new VirusSsRNAPlus(id, seq, tgl, detail, operatorName); break;
                    case 4: s = new VirusRetrovirus(id, seq, tgl, detail, operatorName); break;
                }
                db.tambahData(s);
                refreshData(); 
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal: Pastikan detail bakteri berupa angka (%).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- PENCARIAN GLOBAL (UNIVERSAL SEARCH) ---
    private void filterTable() {
        String query = searchField.getText().toLowerCase();
        if(query.equals("search records...")) query = "";
        
        tableModel.setRowCount(0);
        for(SampelGenetik s : db.getAllData()) {
            String stat = s.cekMutasiBerbahaya() ? "Critical" : "Active";
            
            // Menggabungkan seluruh data ke dalam satu String untuk dicari secara menyeluruh
            String combinedData = (s.getIdSampel() + " " + s.getTanggal() + " " + 
                                   s.getOperator() + " " + s.getTipe() + " " + 
                                   s.getDetail() + " " + stat).toLowerCase();
            
            if(combinedData.contains(query)) {
                tableModel.addRow(new Object[]{s.getIdSampel(), s.getTanggal(), s.getOperator(), s.getTipe(), s.getDetail(), stat});
            }
        }
    }

    private void refreshData() {
        filterTable(); 
        
        int total = db.getJumlahData();
        int critical = 0;
        int[] typeCounts = new int[5]; 
        int[] timeline = new int[8]; 
        
        for(SampelGenetik s : db.getAllData()) {
            if(s.cekMutasiBerbahaya()) critical++;
            if(s instanceof DNAManusia) typeCounts[0]++;
            else if(s instanceof BakteriPathogen) typeCounts[1]++;
            else if(s instanceof VirusDsDNA) typeCounts[2]++;
            else if(s instanceof VirusSsRNAPlus) typeCounts[3]++;
            else if(s instanceof VirusRetrovirus) typeCounts[4]++;
        }
        
        lblTotal.setText(String.valueOf(total));
        lblCritical.setText(String.valueOf(critical));
        
        int percent = total == 0 ? 0 : (int)((critical * 100.0)/total);
        donutChart.setPercentage(percent);
        barChart.updateData(typeCounts);
        
        for(int i=0; i<7; i++) timeline[i] = (int)(Math.random() * total);
        timeline[7] = total; 
        lineChart.updateData(timeline);
    }

    public static void main(String[] args) {
    try {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    System.setProperty("awt.useSystemAAFontSettings", "on");

    SwingUtilities.invokeLater(() -> {
        new LoginFrame().setVisible(true);
    });
}
}