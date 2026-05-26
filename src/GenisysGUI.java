import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

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
    private final String FILE_NAME = "database_genisys_v5.dat";

    public DatabaseLab() {
        database = new ArrayList<>();
        muatDataDariFile();
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
    private boolean isSearchField = false; // Penanda apakah input ini adalah kolom pencarian

    public ModernInput() {
        setOpaque(false); setForeground(Color.black); setCaretColor(Color.black);
        setFont(AppTheme.FONT_NORMAL); 
        setBorder(new EmptyBorder(6, 15, 6, 15));
    }
    
    // Method baru untuk mengaktifkan icon kaca pembesar
    public void setSearchField(boolean val) {
        this.isSearchField = val;
        if(val) {
            // Jika search field, tambah padding kanan agar teks tidak menabrak icon
            setBorder(new EmptyBorder(6, 15, 6, 35)); 
        } else {
            setBorder(new EmptyBorder(6, 15, 6, 15));
        }
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Gambar background & border
        g2.setColor(AppTheme.BG_INPUT); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(AppTheme.BORDER_COLOR); g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
        g2.dispose(); 
        
        super.paintComponent(g); // Gambar teks-nya

        // Menggambar icon kaca pembesar jika ini disetting sebagai kolom pencarian
        if (isSearchField) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.setColor(AppTheme.TEXT_MUTED); 
            g3.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int x = getWidth() - 25; // Posisi di sisi kanan
            int y = getHeight() / 2 - 5; // Posisi di tengah secara vertikal
            
            // Menggambar lingkaran
            g3.drawOval(x, y, 10, 10);
            // Menggambar gagang kaca pembesar
            g3.drawLine(x + 8, y + 8, x + 13, y + 13);
            
            g3.dispose();
        }
    }
}

class ModernPassword extends JPasswordField {
    public ModernPassword() {
        setOpaque(false); setForeground(Color.black); setCaretColor(Color.black);
        setFont(AppTheme.FONT_NORMAL); setBorder(new EmptyBorder(6, 15, 6, 15)); setEchoChar('•');
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AppTheme.BG_INPUT); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(AppTheme.BORDER_COLOR); g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
        g2.dispose(); super.paintComponent(g);
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
    private int[] values = new int[0];
    private String[] labels = new String[0];
    private Color[] colors = new Color[0];
    private int percentage = 0;
    private String centerLabel = "Total Specimen";

    public DonutChart() { setOpaque(false); }
    public void setCenterLabel(String label) { this.centerLabel = label; }

    public void setPercentage(int percentage) {
        this.percentage = Math.max(0, Math.min(100, percentage)); repaint();
    }

    public void updateData(int[] data, String[] labels, Color[] colors) {
        this.values = data;
        this.labels = labels;
        this.colors = colors;
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int total = 0;
        if (values != null) {
            for(int v : values) total += v;
        }

        if(total == 0) {
            g2.setColor(Color.GRAY); g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            String empty = "No Data"; FontMetrics fmEmpty = g2.getFontMetrics();
            g2.drawString(empty, (getWidth() - fmEmpty.stringWidth(empty)) / 2, getHeight() / 2);
            g2.dispose(); return;
        }

        int size = Math.min(getWidth(), getHeight()) - 80;
        if (size < 100) size = Math.min(getWidth(), getHeight()) - 40;
        int x = (getWidth() - size) / 2; int y = (getHeight() - size) / 2 - 20;
        int thickness = Math.max(24, size / 10);

        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(AppTheme.BORDER_COLOR); g2.drawArc(x, y, size, size, 0, 360);

        int startAngle = 90;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == 0) continue;
            int angle = (int) Math.round((values[i] * 360.0) / total);
            if (colors != null && i < colors.length) g2.setColor(colors[i]);
            g2.drawArc(x, y, size, size, startAngle, -angle);
            startAngle -= angle;
        }

        int innerSize = size - thickness * 2;
        if (innerSize > 0) {
            g2.setColor(AppTheme.BG_CARD); g2.fillOval(x + thickness, y + thickness, innerSize, innerSize);
        }

        g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(24, thickness)));
        String totalText = String.valueOf(total); FontMetrics fmTotal = g2.getFontMetrics();
        int totalX = x + (size - fmTotal.stringWidth(totalText)) / 2;
        int totalY = y + (size + fmTotal.getAscent()) / 2 - 10;
        g2.drawString(totalText, totalX, totalY);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        FontMetrics fmLabel = g2.getFontMetrics();
        int labelX = x + (size - fmLabel.stringWidth(centerLabel)) / 2;
        int labelY = totalY + fmLabel.getHeight() + 5;
        g2.drawString(centerLabel, labelX, labelY);

        int legendY = y + size + 20; int legendX = x + (size - 100) / 2;
        if (labels != null) {
            for (int i = 0; i < labels.length; i++) {
                if (colors != null && i < colors.length) g2.setColor(colors[i]);
                g2.fillRoundRect(legendX, legendY + (i * 28), 18, 18, 5, 5);
                g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g2.drawString(labels[i], legendX + 26, legendY + (i * 28) + 14);
            }
        }
        g2.dispose();
    }
}

class BarChart extends JPanel {
    private int[] data;
    private String[] labels;
    private int maxVal;

    public BarChart() { setOpaque(false); }

    public void updateData(int[] d, String[] l) {
        this.data = d; this.labels = l; maxVal = 1;
        if (d != null) { for (int v : d) { if (v > maxVal) maxVal = v; } }
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); int h = getHeight();
        int barWidth = 20; int gap = (w - (data.length * barWidth)) / (data.length + 1);

        for (int i = 0; i < data.length; i++) {
            int barHeight = (int)(((double)data[i] / maxVal) * (h - 60));
            int x = gap + (i * (barWidth + gap)); int y = h - barHeight - 40;

            g2.setColor(AppTheme.BORDER_COLOR);
            g2.fillRoundRect(x, 20, barWidth, h - 60, 8, 8);
            g2.setColor(AppTheme.ACCENT_TEAL_DARK);
            g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

            if (data[i] > 0) {
                g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String valueText = String.valueOf(data[i]); FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valueText, x + (barWidth - fm.stringWidth(valueText)) / 2, h - 15);
            }

            g2.setColor(AppTheme.TEXT_MUTED); g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String label = (labels != null && i < labels.length) ? labels[i] : "?";
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString(label, x + (barWidth - fm2.stringWidth(label)) / 2, h - 2);
        }
        g2.dispose();
    }
}

class LineChart extends JPanel {
    private int[] data;
    private String[] labels;
    
    public LineChart() { setOpaque(false); }
    public void updateData(int[] d, String[] l) { this.data = d; this.labels = l; repaint(); }
    
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(data == null || data.length < 2) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth(), h = getHeight() - 20; 
        g2.setColor(AppTheme.ACCENT_TEAL); g2.setStroke(new BasicStroke(2.5f));
        int maxVal = 1; for(int v : data) if(v > maxVal) maxVal = v;
        
        int step = w / (data.length);
        int offset = step / 2; 
        
        for (int i = 0; i < data.length - 1; i++) {
            int x1 = offset + i * step; int y1 = h - (int)(((double)data[i] / maxVal) * h) + 10;
            int x2 = offset + (i + 1) * step; int y2 = h - (int)(((double)data[i+1] / maxVal) * h) + 10;
            g2.drawLine(x1, y1, x2, y2);
            
            g2.fillOval(x1-3, y1-3, 6, 6);
            if (i == data.length - 2) g2.fillOval(x2-3, y2-3, 6, 6);
        }

        if (labels != null) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(AppTheme.TEXT_MUTED);
            for (int i = 0; i < labels.length; i++) {
                FontMetrics fm = g2.getFontMetrics();
                int x = offset + i * step - fm.stringWidth(labels[i])/2;
                g2.drawString(labels[i], x, getHeight() - 2);
            }
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
        super(parent, "Add New Specimen Record", true); setSize(420, 520); setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_CARD); setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(8, 1, 0, 10)); form.setOpaque(false); form.setBorder(new EmptyBorder(25, 30, 20, 30));
        form.add(createLabel("SPECIMEN ID")); form.add(txtId = new ModernInput());
        form.add(createLabel("DNA/RNA SEQUENCE")); form.add(txtSequence = new ModernInput());
        form.add(createLabel("CLASSIFICATION"));
        String[] tipe = {"Human DNA", "Bacterial Pathogen", "Virus dsDNA (I)", "Virus (+)ssRNA (IV)", "Retrovirus (VI)"};
        form.add(comboTipe = new ModernComboBox<>(tipe));
        form.add(createLabel("DETAILS / STRAIN")); form.add(txtDetail = new ModernInput());
        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btnPanel.setOpaque(false); btnPanel.setBorder(new EmptyBorder(0, 0, 20, 30));
        ModernBtn btnCancel = new ModernBtn("Cancel", AppTheme.BG_INPUT, AppTheme.BORDER_COLOR); btnCancel.setPreferredSize(new Dimension(100, 40)); btnCancel.addActionListener(e -> dispose());
        ModernBtn btnSave = new ModernBtn("Save Data", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK); btnSave.setPreferredSize(new Dimension(120, 40)); btnSave.addActionListener(e -> prosesSave());
        btnPanel.add(btnCancel); btnPanel.add(btnSave); add(btnPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String t) { JLabel l = new JLabel(t); l.setForeground(AppTheme.TEXT_MUTED); l.setFont(AppTheme.FONT_BOLD); return l; }
    private void prosesSave() {
        if(txtId.getText().isEmpty() || txtSequence.getText().isEmpty() || txtDetail.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data!", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
        }
        isAdded = true; dispose();
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

    private void applyAppIcon() {
        try {
            java.net.URL url = getClass().getResource("/APKprofil.jpeg");
            if (url != null) { Image img = javax.imageio.ImageIO.read(url); setIconImage(img); }
        } catch (Exception ignored) {}
    }

    public LoginFrame() {
        setTitle("Genisys x AuraHealth - Login"); setSize(450, 500); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_MAIN); setLayout(new BorderLayout()); applyAppIcon();

        RoundedPanel card = new RoundedPanel(25, AppTheme.BG_CARD); card.setBorder(new EmptyBorder(40, 40, 40, 40)); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel lblLogo = new JLabel("Genisys.io"); lblLogo.setFont(new Font("SansSerif", Font.BOLD, 28)); lblLogo.setForeground(AppTheme.ACCENT_TEAL); lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblSub = new JLabel("Sign in to Medical Dashboard"); lblSub.setFont(AppTheme.FONT_NORMAL); lblSub.setForeground(AppTheme.TEXT_MUTED); lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUser = new ModernInput(); txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); txtUser.setText(""); txtUser.setToolTipText("Masukkan nama lengkap");
        
        ((AbstractDocument) txtUser.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null) super.insertString(fb, offset, string.toUpperCase(), attr);
            }
            @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null) super.replace(fb, offset, length, text.toUpperCase(), attrs);
            }
        });

        txtPass = new ModernPassword(); txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); txtPass.setToolTipText("Masukkan NIM");

        String hintText = "4 digit terakhir nim"; char defaultEchoChar = '•';
        txtPass.setText(hintText); txtPass.setEchoChar((char) 0); txtPass.setForeground(Color.GRAY);
        txtPass.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (String.valueOf(txtPass.getPassword()).equals(hintText)) { txtPass.setText(""); txtPass.setForeground(Color.BLACK); txtPass.setEchoChar(defaultEchoChar); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (String.valueOf(txtPass.getPassword()).isEmpty()) { txtPass.setText(hintText); txtPass.setForeground(Color.GRAY); txtPass.setEchoChar((char) 0); }
            }
        });

        ModernBtn btnLogin = new ModernBtn("LOGIN", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK); btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); btnLogin.addActionListener(e -> prosesLogin());

        card.add(Box.createVerticalStrut(20)); card.add(lblLogo); card.add(Box.createVerticalStrut(10)); card.add(lblSub); card.add(Box.createVerticalStrut(40));
        card.add(createLabel("USERNAME (NAMA)")); card.add(txtUser); card.add(Box.createVerticalStrut(20));
        card.add(createLabel("PASSWORD (NIM)")); card.add(txtPass); card.add(Box.createVerticalStrut(40)); card.add(btnLogin);

        JPanel wrapper = new JPanel(new GridBagLayout()); wrapper.setOpaque(false); wrapper.add(card); add(wrapper, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String t) { JLabel l = new JLabel(t); l.setFont(new Font("SansSerif", Font.BOLD, 11)); l.setForeground(AppTheme.TEXT_MUTED); l.setAlignmentX(Component.CENTER_ALIGNMENT); return l; }
    
    private void prosesLogin() {
        String user = txtUser.getText().trim().toUpperCase(); String pass = new String(txtPass.getPassword()).trim();
        String[][] akunMahasiswa = { {"SURYAHADI PURNAMA", "4402"}, {"LEEVI QUSHAI RAY IFTIKHAR", "4390"}, {"MANDRIVA RADITHYA CAHYADI", "4406"}, {"SALSABILA OKTAVIA RAMADHANI", "4393"}, {"MUHAMMAD NADHIF FAIZURRAHMAN", "4388"}, {"FAUZAN RAMDHANI FAJRI", "4400"}, {"SAKARINA HARERA", "4407"}, {"MUTIARA NUR HIDAYAH", "4412"} };
        boolean loginBerhasil = false;
        for (String[] akun : akunMahasiswa) {
            if (user.equals(akun[0]) && pass.equals(akun[1])) { loginBerhasil = true; new DashboardFrame(user).setVisible(true); this.dispose(); break; }
        }
        if (!loginBerhasil) JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
    }
}

// ============================================================================
// 7. MAIN DASHBOARD
// ============================================================================

class DashboardFrame extends JFrame {
    private String operatorName;
    private DatabaseLab db = new DatabaseLab();

    private void applyAppIcon() {
        try {
            java.net.URL url = getClass().getResource("/APKprofil.jpeg");
            if (url != null) { Image img = javax.imageio.ImageIO.read(url); setIconImage(img); }
        } catch (Exception ignored) {}
    }
    
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
        this.operatorName = operatorName == null ? "" : operatorName.trim().toUpperCase();
        setTitle("Dashboard - " + operatorName); setSize(1400, 900); applyAppIcon();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_MAIN); setLayout(new BorderLayout());

        cardLayout = new CardLayout(); cardPanel = new JPanel(cardLayout); cardPanel.setOpaque(false);
        add(buildSidebar(), BorderLayout.WEST);
        
        JPanel mainArea = new JPanel(new BorderLayout()); mainArea.setOpaque(false); mainArea.add(buildTopBar(), BorderLayout.NORTH);
        
        cardPanel.add(buildDashboardView(), "DASHBOARD"); cardPanel.add(buildRecordsView(), "RECORDS");
        cardPanel.add(buildTeamView(), "TEAM"); cardPanel.add(buildMedicationsView(), "MEDS");
        
        mainArea.add(cardPanel, BorderLayout.CENTER); add(mainArea, BorderLayout.CENTER);
        refreshData();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()); sidebar.setBackground(AppTheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0)); sidebar.setBorder(new EmptyBorder(25, 20, 20, 20));
        JLabel logo = new JLabel("Genisys.io"); logo.setFont(AppTheme.FONT_H1); logo.setForeground(AppTheme.TEXT_PRIMARY);
        JPanel menu = new JPanel(); menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS)); menu.setOpaque(false); menu.setBorder(new EmptyBorder(40, 0, 0, 0));

        menu.add(createNavBtn("Dashboard", "DASHBOARD", true)); menu.add(Box.createVerticalStrut(10));
        menu.add(createNavBtn("Medical Team", "TEAM", false)); menu.add(Box.createVerticalStrut(10));
        menu.add(createNavBtn("data Virus(DNA/RNA)", "RECORDS", false)); menu.add(Box.createVerticalStrut(10));
        menu.add(createNavBtn("Medications", "MEDS", false));

        sidebar.add(logo, BorderLayout.NORTH); sidebar.add(menu, BorderLayout.CENTER); return sidebar;
    }

    private JPanel createNavBtn(String text, String cardName, boolean active) {
        RoundedPanel p = new RoundedPanel(12, active ? AppTheme.ACCENT_TEAL : AppTheme.BG_SIDEBAR);
        p.setMaximumSize(new Dimension(200, 45)); p.setLayout(new BorderLayout());
        JLabel l = new JLabel("   " + text); l.setFont(AppTheme.FONT_BOLD); l.setForeground(active ? Color.WHITE : AppTheme.TEXT_MUTED);
        p.add(l, BorderLayout.CENTER);
        
        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, cardName);
                for(JPanel btn : sidebarButtons) { btn.setBackground(AppTheme.BG_SIDEBAR); ((JLabel)btn.getComponent(0)).setForeground(AppTheme.TEXT_MUTED); }
                p.setBackground(AppTheme.ACCENT_TEAL); l.setForeground(Color.WHITE);
            }
            public void mouseEntered(MouseEvent e) { if(p.getBackground() != AppTheme.ACCENT_TEAL) p.setBackground(AppTheme.BG_CARD); }
            public void mouseExited(MouseEvent e) { if(p.getBackground() != AppTheme.ACCENT_TEAL) p.setBackground(AppTheme.BG_SIDEBAR); }
        });
        sidebarButtons.add(p); return p;
    }

    private JPanel buildTopBar() {
        JPanel topbar = new JPanel(new BorderLayout()); topbar.setOpaque(false); topbar.setBorder(new EmptyBorder(25, 30, 10, 30));
        JLabel title = new JLabel("Overview"); title.setFont(AppTheme.FONT_H1); title.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); right.setOpaque(false);
        searchField = new ModernInput(); searchField.setText(""); 
        searchField.setSearchField(true); // <--- Aktifkan icon kaca pembesar
        
        RoundedPanel profileBadge = new RoundedPanel(20, AppTheme.BG_CARD); profileBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel lblName = new JLabel("👤 " + operatorName); lblName.setForeground(AppTheme.TEXT_PRIMARY); lblName.setFont(AppTheme.FONT_BOLD);
        profileBadge.add(lblName); right.add(profileBadge);
        topbar.add(title, BorderLayout.WEST); topbar.add(right, BorderLayout.EAST); return topbar;
    }

    private JScrollPane buildDashboardView() {
        JPanel scrollContent = new JPanel(); scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS)); scrollContent.setOpaque(false); scrollContent.setBorder(new EmptyBorder(20, 30, 30, 30));
        
        JPanel heroRow = new JPanel(new BorderLayout(20, 0)); heroRow.setOpaque(false); heroRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        RoundedPanel welcomeCard = new RoundedPanel(20, AppTheme.ACCENT_TEAL); welcomeCard.setGradient(AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK);
        welcomeCard.setLayout(new BorderLayout()); welcomeCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel lblGreeting = new JLabel("Welcome " + operatorName.split(" ")[0] + " 👋"); lblGreeting.setForeground(Color.WHITE); lblGreeting.setFont(AppTheme.FONT_NORMAL);
        JLabel lblMain = new JLabel("Check Specimen Analytics!"); lblMain.setForeground(Color.WHITE); lblMain.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeCard.add(lblGreeting, BorderLayout.NORTH); welcomeCard.add(lblMain, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(2,1,0,15)); statsPanel.setOpaque(false); statsPanel.setPreferredSize(new Dimension(380,0));

        RoundedPanel p1 = new RoundedPanel(20, AppTheme.BG_CARD); p1.setLayout(new BorderLayout()); p1.setBorder(new EmptyBorder(15,20,15,20));
        JPanel totalWrap = new JPanel(new FlowLayout(FlowLayout.LEFT,15,5)); totalWrap.setOpaque(false);
        JLabel icon1 = new JLabel("📋"); icon1.setFont(new Font("SansSerif", Font.PLAIN, 28));
        lblTotal = new JLabel("0"); lblTotal.setFont(new Font("SansSerif", Font.BOLD, 36)); lblTotal.setForeground(Color.WHITE);
        JLabel totalDesc = new JLabel("<html>Jumlah virus (DNA/RNA)<br>yang telah di inputkan</html>"); totalDesc.setForeground(Color.WHITE); totalDesc.setFont(AppTheme.FONT_NORMAL);
        totalWrap.add(icon1); totalWrap.add(lblTotal); totalWrap.add(totalDesc); p1.add(totalWrap, BorderLayout.CENTER);

        RoundedPanel p2 = new RoundedPanel(20, AppTheme.BG_CARD); p2.setLayout(new BorderLayout()); p2.setBorder(new EmptyBorder(15,20,15,20));
        JPanel criticalWrap = new JPanel(new FlowLayout(FlowLayout.LEFT,15,5)); criticalWrap.setOpaque(false);
        JLabel icon2 = new JLabel("⚠"); icon2.setFont(new Font("SansSerif", Font.PLAIN, 28)); icon2.setForeground(Color.RED);
        lblCritical = new JLabel("0"); lblCritical.setFont(new Font("SansSerif", Font.BOLD, 36)); lblCritical.setForeground(AppTheme.ACCENT_RED);
        JLabel criticalDesc = new JLabel("<html>Virus (DNA/RNA)<br>yang harus cepat diatasi</html>"); criticalDesc.setForeground(Color.WHITE); criticalDesc.setFont(AppTheme.FONT_NORMAL);
        criticalWrap.add(icon2); criticalWrap.add(lblCritical); criticalWrap.add(criticalDesc); p2.add(criticalWrap, BorderLayout.CENTER);

        statsPanel.add(p1); statsPanel.add(p2); statsPanel.setOpaque(false); statsPanel.setPreferredSize(new Dimension(300, 0));

        JPanel chartRow = new JPanel(new GridLayout(1, 3, 20, 0)); chartRow.setOpaque(false); chartRow.setPreferredSize(new Dimension(0, 250));
        
        RoundedPanel donutCard = new RoundedPanel(20, AppTheme.BG_CARD); donutCard.setLayout(new BorderLayout()); donutCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel ct1 = new JLabel("Status Distribution"); ct1.setForeground(AppTheme.TEXT_PRIMARY); ct1.setFont(AppTheme.FONT_H2);
        donutChart = new DonutChart(); donutChart.setCenterLabel("Specimens"); donutCard.add(ct1, BorderLayout.NORTH); donutCard.add(donutChart, BorderLayout.CENTER);

        RoundedPanel lineCard = new RoundedPanel(20, AppTheme.BG_CARD); lineCard.setLayout(new BorderLayout()); lineCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel ct2 = new JLabel("Avg Mutation Potential"); ct2.setForeground(AppTheme.TEXT_PRIMARY); ct2.setFont(AppTheme.FONT_H2);
        lineChart = new LineChart(); lineCard.add(ct2, BorderLayout.NORTH); lineCard.add(lineChart, BorderLayout.CENTER);

        RoundedPanel barCard = new RoundedPanel(20, AppTheme.BG_CARD); barCard.setLayout(new BorderLayout()); barCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel ct3 = new JLabel("Team / Operator Activity"); ct3.setForeground(AppTheme.TEXT_PRIMARY); ct3.setFont(AppTheme.FONT_H2);
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

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); headerRight.setOpaque(false);
        
        ModernInput virusIdSearch = new ModernInput(); 
        virusIdSearch.setPreferredSize(new Dimension(260, 35)); 
        virusIdSearch.setToolTipText("Cari ID virus...");
        virusIdSearch.setSearchField(true); // <--- Aktifkan icon kaca pembesar
        
        ModernBtn btnAdd = new ModernBtn("+ Add Record", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK); btnAdd.setPreferredSize(new Dimension(120, 35)); btnAdd.addActionListener(e -> panggilFormAdd());
        header.add(title, BorderLayout.WEST); headerRight.add(virusIdSearch); headerRight.add(btnAdd); header.add(headerRight, BorderLayout.EAST);

        String[] kolom = {"Specimen ID", "Date", "Doctor / Operator", "Class", "Details", "Status"};
        tableModel = new DefaultTableModel(kolom, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        JTable table = new JTable(tableModel); table.setRowHeight(45); table.setFont(AppTheme.FONT_NORMAL); table.setBackground(AppTheme.BG_CARD); table.setForeground(AppTheme.TEXT_PRIMARY); table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(AppTheme.ACCENT_TEAL_DARK); table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(AppTheme.BG_CARD); table.getTableHeader().setForeground(AppTheme.TEXT_MUTED); table.getTableHeader().setFont(AppTheme.FONT_BOLD); table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR)); table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);
                label.setFont(new Font("SansSerif", Font.BOLD, 12));

                if (isSelected) {
                    label.setBackground(AppTheme.ACCENT_TEAL_DARK);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(AppTheme.BG_CARD);
                }

                String status = String.valueOf(value);
                if ("Active".equals(status)) {
                    label.setText("● Active");
                    if (!isSelected) label.setForeground(new Color(100, 255, 100)); 
                } else if ("Critical".equals(status)) {
                    label.setText("● Critical");
                    if (!isSelected) label.setForeground(AppTheme.ACCENT_RED); 
                }

                return label;
            }
        });

        table.setAutoCreateRowSorter(true); TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel); table.setRowSorter(sorter);
        sorter.setComparator(1, new Comparator<String>() {
            SimpleDateFormat f = new SimpleDateFormat("dd MMM, yyyy");
            @Override public int compare(String o1, String o2) {
                try { Date d1 = f.parse(o1); Date d2 = f.parse(o2); return d1.compareTo(d2); } catch (Exception e) { return o1.compareTo(o2); }
            }
        });

        for (SampelGenetik s : db.getAllData()) {
            String stat = s.cekMutasiBerbahaya() ? "Critical" : "Active";
            tableModel.addRow(new Object[]{s.getIdSampel(), s.getTanggal(), s.getOperator(), s.getTipe(), s.getDetail(), stat});
        }

        virusIdSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { apply(); }
            @Override public void removeUpdate(DocumentEvent e) { apply(); }
            @Override public void changedUpdate(DocumentEvent e) { apply(); }
            private void apply() {
                String q = virusIdSearch.getText() == null ? "" : virusIdSearch.getText().trim().toLowerCase();
                if (q.isEmpty()) { sorter.setRowFilter(null); return; }
                sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String id = String.valueOf(entry.getValue(0)).toLowerCase(); String clazz = String.valueOf(entry.getValue(3)).toLowerCase();
                        return clazz.contains("virus") && id.contains(q);
                    }
                });
            }
        });

        JScrollPane scroll = new JScrollPane(table); scroll.getViewport().setBackground(AppTheme.BG_CARD); scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        tableCard.add(header, BorderLayout.NORTH); tableCard.add(scroll, BorderLayout.CENTER); wrapper.add(tableCard, BorderLayout.CENTER);
        return wrapper;
    }

    private void panggilFormAdd() {
        AddRecordModal modal = new AddRecordModal(this); modal.setVisible(true);
        if (modal.isAdded()) {
            String id = modal.getId(); String seq = modal.getSeq(); String tgl = new SimpleDateFormat("dd MMM, yyyy").format(new Date()); String detail = modal.getDetail(); int tipe = modal.getTipe(); SampelGenetik s = null;
            try {
                switch(tipe) {
                    case 0: s = new DNAManusia(id, seq, tgl, detail, operatorName); break;
                    case 1: s = new BakteriPathogen(id, seq, tgl, Double.parseDouble(detail), operatorName); break;
                    case 2: s = new VirusDsDNA(id, seq, tgl, detail, operatorName); break;
                    case 3: s = new VirusSsRNAPlus(id, seq, tgl, detail, operatorName); break;
                    case 4: s = new VirusRetrovirus(id, seq, tgl, detail, operatorName); break;
                }
                db.tambahData(s); refreshData(); JOptionPane.showMessageDialog(this, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal: Pastikan detail bakteri berupa angka (%).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterTable() {
        String query = searchField.getText().toLowerCase(); if(query.equals("search records...")) query = "";
        tableModel.setRowCount(0);
        for(SampelGenetik s : db.getAllData()) {
            String stat = s.cekMutasiBerbahaya() ? "Critical" : "Active";
            String combinedData = (s.getIdSampel() + " " + s.getTanggal() + " " + s.getOperator() + " " + s.getTipe() + " " + s.getDetail() + " " + stat).toLowerCase();
            if(combinedData.contains(query)) tableModel.addRow(new Object[]{s.getIdSampel(), s.getTanggal(), s.getOperator(), s.getTipe(), s.getDetail(), stat});
        }
    }

    private JPanel buildTeamView() {
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setOpaque(false); wrapper.setBorder(new EmptyBorder(20, 30, 30, 30));
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false); JLabel title = new JLabel("Medical Team & Researchers"); title.setFont(AppTheme.FONT_H2); title.setForeground(AppTheme.TEXT_PRIMARY);
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); topRight.setOpaque(false);
        
        ModernInput searchDokter = new ModernInput(); 
        searchDokter.setPreferredSize(new Dimension(240, 40)); 
        searchDokter.setText(""); 
        searchDokter.setToolTipText("Cari nama dokter...");
        searchDokter.setSearchField(true); // <--- Aktifkan icon kaca pembesar
        
        topRight.add(searchDokter); top.add(title, BorderLayout.WEST); top.add(topRight, BorderLayout.EAST);

        RoundedPanel tableCard = new RoundedPanel(50, AppTheme.BG_CARD); tableCard.setLayout(new BorderLayout(0, 15)); tableCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        String[] kolom = {"ID Dokter", "Nama", "Spesialisasi", "Status", "Shift", "No. Kontak"};
        DefaultTableModel teamModel = new DefaultTableModel(kolom, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        String[][] teamData = { {"DR001", "Prof. Dr. Surya", "Virologist", "Active", "Pagi", "+62-812-3456-7890"}, {"DR002", "Dr Tirta", "Geneticist", "On Break", "Malam", "+62-821-5678-9012"}, {"DR003", "Dr. Ikhsan", "Microbiologist", "Active", "Siang", "+62-813-9012-3456"}, {"DR004", "Dr. Gia", "Immunologist", "Emergency", "Pagi", "+62-822-3456-7890"} };
        for (String[] row : teamData) teamModel.addRow(row);

        JTable table = new JTable(teamModel); table.setRowHeight(45); table.setFont(AppTheme.FONT_NORMAL); table.setBackground(AppTheme.BG_CARD); table.setForeground(AppTheme.TEXT_PRIMARY); table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0)); table.setSelectionBackground(AppTheme.ACCENT_TEAL_DARK); table.setSelectionForeground(Color.WHITE); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(AppTheme.BG_CARD); table.getTableHeader().setForeground(AppTheme.TEXT_MUTED); table.getTableHeader().setFont(AppTheme.FONT_BOLD); table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR)); table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(); label.setHorizontalAlignment(JLabel.CENTER); label.setOpaque(true); label.setFont(new Font("SansSerif", Font.BOLD, 12));
                if (isSelected) { label.setBackground(AppTheme.ACCENT_TEAL_DARK); label.setForeground(Color.WHITE); } else { label.setBackground(AppTheme.BG_CARD); }
                String status = String.valueOf(value);
                if ("Active".equals(status)) { label.setText("● Active"); if (!isSelected) label.setForeground(new Color(100, 255, 100)); }
                else if ("On Break".equals(status)) { label.setText("● On Break"); if (!isSelected) label.setForeground(new Color(255, 200, 50)); }
                else if ("Emergency".equals(status)) { label.setText("● Emergency"); if (!isSelected) label.setForeground(new Color(255, 100, 100)); }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(table); scroll.getViewport().setBackground(AppTheme.BG_CARD); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());

        final String[][] doctorProfileData = {
            {"DR001", "Prof. Dr. Surya", "PhD Virology", "48", "Virologist", "Active", "Pagi", "+62-812-3456-7890", "Senior virologist leading pathogen analysis and emergency response."},
            {"DR002", "Dr. Tirta", "MSc Genetic Engineering", "35", "Geneticist", "On Break", "Malam", "+62-821-5678-9012", "Genetic specialist focused on mutation mapping and lab diagnostics."},
            {"DR003", "Dr. Ikhsan", "MSc Microbiology", "40", "Microbiologist", "Active", "Siang", "+62-813-9012-3456", "Expert in microbial surveillance and contamination prevention."},
            {"DR004", "Dr. Gia", "PhD Immunology", "42", "Immunologist", "Emergency", "Pagi", "+62-822-3456-7890", "Immunology researcher handling urgent cases and critical care coordination."}
        };

        final Map<String, ImageIcon> doctorPhotoMap = new HashMap<>();
        JLabel lblDoctorName = new JLabel("Pilih dokter untuk melihat detail"); lblDoctorName.setForeground(AppTheme.TEXT_PRIMARY); lblDoctorName.setFont(AppTheme.FONT_H2); lblDoctorName.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblDoctorStatus = new JLabel("Status: -"); lblDoctorStatus.setForeground(AppTheme.TEXT_MUTED); lblDoctorStatus.setFont(AppTheme.FONT_NORMAL); lblDoctorStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel photoPreview = new JLabel("Foto Dokter", SwingConstants.CENTER); photoPreview.setOpaque(true); photoPreview.setBackground(AppTheme.BG_INPUT); photoPreview.setForeground(AppTheme.TEXT_MUTED); photoPreview.setFont(AppTheme.FONT_BOLD); photoPreview.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 2)); photoPreview.setPreferredSize(new Dimension(220, 220)); photoPreview.setMaximumSize(new Dimension(220, 220)); photoPreview.setMinimumSize(new Dimension(220, 220)); photoPreview.setAlignmentX(Component.CENTER_ALIGNMENT); photoPreview.setVerticalTextPosition(SwingConstants.CENTER); photoPreview.setHorizontalTextPosition(SwingConstants.CENTER);
        JLabel lblDoctorId = new JLabel("--"), lblDoctorEducation = new JLabel("--"), lblDoctorAge = new JLabel("--"), lblDoctorSpec = new JLabel("--"), lblDoctorShift = new JLabel("--"), lblDoctorPhone = new JLabel("--");

        ModernBtn btnViewProfile = new ModernBtn("Lihat Identitas Lengkap", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK); btnViewProfile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); btnViewProfile.setEnabled(false);
        btnViewProfile.addActionListener(e -> {
            int selected = table.getSelectedRow(); if (selected < 0) return;
            String selectedId = teamModel.getValueAt(table.convertRowIndexToModel(selected), 0).toString();
            for (String[] row : doctorProfileData) { if (row[0].equals(selectedId)) { showDoctorProfileDialog(row); break; } }
        });

        ModernBtn btnChangePhoto = new ModernBtn("Ganti Foto", AppTheme.BG_INPUT, AppTheme.BORDER_COLOR); btnChangePhoto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); btnChangePhoto.setEnabled(false); btnChangePhoto.setToolTipText("Hanya SURYAHADI PURNAMA dapat mengganti foto.");
        btnChangePhoto.addActionListener(e -> {
            int selected = table.getSelectedRow(); if (selected < 0) return;
            String selectedId = teamModel.getValueAt(table.convertRowIndexToModel(selected), 0).toString();
            JFileChooser chooser = new JFileChooser(); chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            if (chooser.showOpenDialog(DashboardFrame.this) == JFileChooser.APPROVE_OPTION) {
                ImageIcon icon = new ImageIcon(chooser.getSelectedFile().getAbsolutePath());
                ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH));
                doctorPhotoMap.put(selectedId, scaledIcon); photoPreview.setIcon(scaledIcon); photoPreview.setText("");
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return; int selected = table.getSelectedRow();
            if (selected < 0) {
                lblDoctorName.setText("Pilih dokter untuk melihat detail"); lblDoctorStatus.setText("Status: -"); lblDoctorId.setText("--"); lblDoctorEducation.setText("--"); lblDoctorAge.setText("--"); lblDoctorSpec.setText("--"); lblDoctorShift.setText("--"); lblDoctorPhone.setText("--"); photoPreview.setIcon(null); photoPreview.setText("Foto Dokter"); btnViewProfile.setEnabled(false); btnChangePhoto.setEnabled(false); return;
            }
            String selectedId = teamModel.getValueAt(table.convertRowIndexToModel(selected), 0).toString();
            for (String[] row : doctorProfileData) {
                if (row[0].equals(selectedId)) {
                    lblDoctorName.setText(row[1]); lblDoctorStatus.setText("Status: " + row[5]); lblDoctorId.setText(row[0]); lblDoctorEducation.setText(row[2]); lblDoctorAge.setText(row[3]); lblDoctorSpec.setText(row[4]); lblDoctorShift.setText(row[6]); lblDoctorPhone.setText(row[7]);
                    if (doctorPhotoMap.containsKey(row[0])) { photoPreview.setIcon(doctorPhotoMap.get(row[0])); photoPreview.setText(""); } else { photoPreview.setIcon(null); photoPreview.setText("Foto Dokter"); }
                    btnViewProfile.setEnabled(true); btnChangePhoto.setEnabled("SURYAHADI PURNAMA".equals(operatorName)); break;
                }
            }
        });

        table.setAutoCreateRowSorter(true); TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(teamModel); table.setRowSorter(sorter);
        searchDokter.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { apply(); } @Override public void removeUpdate(DocumentEvent e) { apply(); } @Override public void changedUpdate(DocumentEvent e) { apply(); }
            private void apply() {
                final String q = searchDokter.getText() == null ? "" : searchDokter.getText().trim().toLowerCase();
                if (q.isEmpty()) { sorter.setRowFilter(null); return; }
                sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) { return (entry.getValue(1) == null ? "" : entry.getValue(1).toString().toLowerCase()).contains(q); }
                });
            }
        });

        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false); header.add(title, BorderLayout.WEST); header.add(topRight, BorderLayout.EAST);
        tableCard.add(header, BorderLayout.NORTH); tableCard.add(scroll, BorderLayout.CENTER);

        JPanel profileCard = new RoundedPanel(50, AppTheme.BG_CARD); profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS)); profileCard.setBorder(new EmptyBorder(20, 20, 20, 20)); profileCard.setPreferredSize(new Dimension(360, 0));
        profileCard.add(photoPreview); profileCard.add(Box.createVerticalStrut(15)); profileCard.add(lblDoctorName); profileCard.add(Box.createVerticalStrut(6)); profileCard.add(lblDoctorStatus); profileCard.add(Box.createVerticalStrut(20)); profileCard.add(createInfoLine("ID Dokter", lblDoctorId)); profileCard.add(createInfoLine("Pendidikan Terakhir", lblDoctorEducation)); profileCard.add(createInfoLine("Umur", lblDoctorAge)); profileCard.add(createInfoLine("Spesialisasi", lblDoctorSpec)); profileCard.add(createInfoLine("Shift", lblDoctorShift)); profileCard.add(createInfoLine("No. Kontak", lblDoctorPhone)); profileCard.add(Box.createVerticalStrut(20)); profileCard.add(btnChangePhoto); profileCard.add(Box.createVerticalStrut(10)); profileCard.add(btnViewProfile);
        wrapper.add(tableCard, BorderLayout.CENTER); wrapper.add(profileCard, BorderLayout.EAST);
        return wrapper;
    }

    private JPanel createInfoLine(String title, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout()); row.setOpaque(false); row.setBorder(new EmptyBorder(6, 0, 6, 0));
        JLabel titleLabel = new JLabel(title); titleLabel.setForeground(AppTheme.TEXT_MUTED); titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        valueLabel.setForeground(AppTheme.TEXT_PRIMARY); valueLabel.setFont(AppTheme.FONT_BOLD);
        row.add(titleLabel, BorderLayout.NORTH); row.add(valueLabel, BorderLayout.SOUTH); return row;
    }

    private void showDoctorProfileDialog(String[] profile) {
        JDialog dialog = new JDialog(this, "Identitas Dokter", true); dialog.setSize(520, 620); dialog.setLocationRelativeTo(this); dialog.getContentPane().setBackground(AppTheme.BG_MAIN);
        RoundedPanel content = new RoundedPanel(50, AppTheme.BG_CARD); content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); content.setBorder(new EmptyBorder(25, 25, 25, 25));
        JLabel title = new JLabel("Biodata Lengkap Dokter"); title.setForeground(AppTheme.TEXT_PRIMARY); title.setFont(AppTheme.FONT_H2); title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel(profile[1]); subtitle.setForeground(AppTheme.ACCENT_TEAL); subtitle.setFont(new Font("SansSerif", Font.BOLD, 18)); subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title); content.add(Box.createVerticalStrut(8)); content.add(subtitle); content.add(Box.createVerticalStrut(20));
        content.add(createInfoLine("ID Dokter", new JLabel(profile[0]))); content.add(createInfoLine("Pendidikan Terakhir", new JLabel(profile[2]))); content.add(createInfoLine("Umur", new JLabel(profile[3] + " tahun"))); content.add(createInfoLine("Spesialisasi", new JLabel(profile[4]))); content.add(createInfoLine("Status", new JLabel(profile[5]))); content.add(createInfoLine("Shift", new JLabel(profile[6]))); content.add(createInfoLine("No. Kontak", new JLabel(profile[7]))); content.add(Box.createVerticalStrut(20));
        ModernBtn closeBtn = new ModernBtn("Tutup", AppTheme.ACCENT_TEAL, AppTheme.ACCENT_TEAL_DARK); closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT); closeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); closeBtn.addActionListener(e -> dialog.dispose());
        content.add(closeBtn); dialog.add(content); dialog.setVisible(true);
    }

    private JPanel buildMedicationsView() {
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setOpaque(false); wrapper.setBorder(new EmptyBorder(20, 30, 30, 30));
        RoundedPanel tableCard = new RoundedPanel(20, AppTheme.BG_CARD); tableCard.setLayout(new BorderLayout(0, 15)); tableCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false); JLabel title = new JLabel("Medications & Treatments Database"); title.setFont(AppTheme.FONT_H2); title.setForeground(AppTheme.TEXT_PRIMARY);
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); headerRight.setOpaque(false);
        
        ModernInput obatSearch = new ModernInput(); 
        obatSearch.setPreferredSize(new Dimension(280, 35)); 
        obatSearch.setToolTipText("Cari nama obat..."); 
        obatSearch.setText("");
        obatSearch.setSearchField(true); // <--- Aktifkan icon kaca pembesar
        
        headerRight.add(obatSearch); header.add(title, BorderLayout.WEST); header.add(headerRight, BorderLayout.EAST);

        String[] kolom = {"Medicine ID", "Nama Obat", "Untuk Virus", "Stock", "Status"};
        DefaultTableModel medModel = new DefaultTableModel(kolom, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        String[][] medData = { {"MED001", "Remdesivir", "Virus (+)ssRNA", "120", "Available"}, {"MED002", "Zidovudine", "Retrovirus", "50", "Limited"}, {"MED003", "Amoxicillin", "Bacterial Pathogen", "200", "Available"}, {"MED004", "Acyclovir", "Virus dsDNA", "80", "Available"}, {"MED005", "Ritonavir", "Retrovirus", "30", "Limited"}, {"MED006", "Lopinavir", "Virus (+)ssRNA", "95", "Available"}, {"MED007", "Cephalexin", "Bacterial Pathogen", "150", "Available"}, {"MED008", "Ganciclovir", "Virus dsDNA", "45", "Limited"} };
        for (String[] row : medData) medModel.addRow(row);

        JTable table = new JTable(medModel); table.setRowHeight(45); table.setFont(AppTheme.FONT_NORMAL); table.setBackground(AppTheme.BG_CARD); table.setForeground(AppTheme.TEXT_PRIMARY); table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0)); table.setSelectionBackground(AppTheme.ACCENT_TEAL_DARK); table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(AppTheme.BG_CARD); table.getTableHeader().setForeground(AppTheme.TEXT_MUTED); table.getTableHeader().setFont(AppTheme.FONT_BOLD); table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR)); table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(); label.setHorizontalAlignment(JLabel.CENTER); label.setOpaque(true); label.setFont(new Font("SansSerif", Font.BOLD, 12));
                if (isSelected) { label.setBackground(AppTheme.ACCENT_TEAL_DARK); label.setForeground(Color.WHITE); } else { label.setBackground(AppTheme.BG_CARD); }
                String status = String.valueOf(value);
                if ("Available".equals(status)) { label.setText("● Available"); if (!isSelected) label.setForeground(new Color(100, 255, 100)); }
                else if ("Limited".equals(status)) { label.setText("● Limited"); if (!isSelected) label.setForeground(new Color(255, 200, 50)); }
                return label;
            }
        });

        table.setAutoCreateRowSorter(true); TableRowSorter<DefaultTableModel> medSorter = new TableRowSorter<>(medModel); table.setRowSorter(medSorter);
        obatSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { apply(); } @Override public void removeUpdate(DocumentEvent e) { apply(); } @Override public void changedUpdate(DocumentEvent e) { apply(); }
            private void apply() {
                String q = obatSearch.getText() == null ? "" : obatSearch.getText().trim().toLowerCase();
                if (q.isEmpty()) { medSorter.setRowFilter(null); return; }
                medSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) { return (entry.getValue(1) == null ? "" : entry.getValue(1).toString().toLowerCase()).contains(q); }
                });
            }
        });

        JScrollPane scroll = new JScrollPane(table); scroll.getViewport().setBackground(AppTheme.BG_CARD); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        tableCard.add(header, BorderLayout.NORTH); tableCard.add(scroll, BorderLayout.CENTER); wrapper.add(tableCard, BorderLayout.CENTER);
        return wrapper;
    }

    private void refreshData() {
        filterTable(); 
        
        int total = db.getJumlahData();
        int critical = 0;
        
        double[] totalMutation = new double[5]; 
        int[] typeCounts = new int[5]; 
        
        Map<String, Integer> operatorActivity = new HashMap<>();
        
        for(SampelGenetik s : db.getAllData()) {
            boolean isCritical = s.cekMutasiBerbahaya();
            if(isCritical) critical++;
            
            int index = -1;
            if(s instanceof DNAManusia) index = 0;
            else if(s instanceof BakteriPathogen) index = 1;
            else if(s instanceof VirusDsDNA) index = 2;
            else if(s instanceof VirusSsRNAPlus) index = 3;
            else if(s instanceof VirusRetrovirus) index = 4;
            
            if (index != -1) {
                typeCounts[index]++;
                totalMutation[index] += s.hitungPotensiMutasi(); 
            }
            
            String op = s.getOperator();
            if(op != null && !op.trim().isEmpty()) {
                String shortName = op.split(" ")[0];
                operatorActivity.put(shortName, operatorActivity.getOrDefault(shortName, 0) + 1);
            }
        }
        
        lblTotal.setText(String.valueOf(total));
        lblCritical.setText(String.valueOf(critical));
        
        int percent = total == 0 ? 0 : (int)((critical * 100.0)/total);
        int active = total - critical;
        
        int[] statusData = {active, critical};
        String[] statusLabels = {"Active", "Critical"};
        Color[] statusColors = {AppTheme.ACCENT_TEAL, AppTheme.ACCENT_RED}; 
        
        donutChart.updateData(statusData, statusLabels, statusColors);
        donutChart.setCenterLabel("Specimens");
        donutChart.setPercentage(percent);
        
        int[] avgMutation = new int[5];
        for (int i = 0; i < 5; i++) {
            if (typeCounts[i] > 0) {
                avgMutation[i] = (int)(totalMutation[i] / typeCounts[i]);
            } else {
                avgMutation[i] = 0;
            }
        }
        String[] typesLabels = {"Human", "Bacteri", "dsDNA", "ssRNA", "Retro"};
        lineChart.updateData(avgMutation, typesLabels);
        
        List<Map.Entry<String, Integer>> opList = new ArrayList<>(operatorActivity.entrySet());
        opList.sort((a, b) -> b.getValue().compareTo(a.getValue())); 
        
        int limit = Math.min(5, opList.size()); 
        int[] barData = new int[5];
        String[] barLabels = new String[5];
        
        for (int i = 0; i < 5; i++) {
            if (i < limit) {
                barData[i] = opList.get(i).getValue();
                String n = opList.get(i).getKey();
                barLabels[i] = n.length() > 3 ? n.substring(0, 3).toUpperCase() : n.toUpperCase();
            } else {
                barData[i] = 0;
                barLabels[i] = "-";
            }
        }
        barChart.updateData(barData, barLabels);
    }
}

public class GenisysGUI {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
            }
        } catch (Exception e) { e.printStackTrace(); }
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(() -> { new LoginFrame().setVisible(true); });
    }
}
