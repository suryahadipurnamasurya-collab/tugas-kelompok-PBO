<div align="center">
  <img src="https://cdn-icons-png.flaticon.com/512/2070/2070802.png" alt="Genisys Logo" width="120"/>
  <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Logo_UNESA.png/600px-Logo_UNESA.png" alt="UNESA Logo" width="120"/>

  <h1>🧬 GENISYS COMMAND CENTER 🧬</h1>
  <p><b>Sistem Analisis Jejak Genetik & Deteksi Mutasi Spesimen (Baltimore System)</b></p>

  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java"/></a>
  <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/swing/"><img src="https://img.shields.io/badge/GUI-Java_Swing-blue?style=for-the-badge" alt="Java Swing"/></a>
  <a href="#"><img src="https://img.shields.io/badge/Status-Completed-success?style=for-the-badge" alt="Status"/></a>
  <a href="#"><img src="https://img.shields.io/badge/UNESA-PBO_Project-navy?style=for-the-badge" alt="UNESA"/></a>
</div>

---

## 🔬 Tentang Proyek
**Genisys Command Center** adalah aplikasi simulasi laboratorium bio-informatika berbasis *Desktop* (Java Swing). Aplikasi ini dirancang untuk mendeteksi potensi tingkat bahaya dan laju mutasi dari suatu sampel biologi secara otomatis menggunakan dasar ilmu genetika dunia nyata, yaitu **Sistem Klasifikasi Baltimore**.

Proyek ini dikembangkan sebagai pemenuhan **Ujian Tengah Semester (UTS) Mata Kuliah Pemrograman Berorientasi Objek (PBO)** di Universitas Negeri Surabaya (UNESA).

### 👨‍💻 Pengembang
* **Nama:** tim dari unesa teknik informatika (2025 H)
* **Divisi/Program Studi:** S1 Teknik Informatika, Universitas Negeri Surabaya

---

## ✨ Fitur Utama
1. **Bio-Encoding (Input):** Pencatatan *Sequence DNA/RNA* dari berbagai jenis spesimen (Manusia, Bakteri, Virus).
2. **Polymorphic Calculation:** Menghitung persentase "Potensi Mutasi" dan "Status Bahaya" secara dinamis berdasarkan jenis spesimen.
3. **Smart Sorting (📶):** Mengurutkan spesimen dari yang paling mematikan/berbahaya (Laju Mutasi Tertinggi) menggunakan algoritma **Selection Sort (Descending)**.
4. **DNA Tracking (🔍):** Melacak data spesimen menggunakan ID dengan algoritma **Linear Search**.
5. **Secure Database:** Pengelolaan data terisolasi menggunakan struktur *Array of Objects* murni (Tanpa koneksi SQL eksternal).

---

## 💻 Implementasi PBO (OOP Concepts)
Proyek ini mengimplementasikan 9 pilar utama Pemrograman Berorientasi Objek yang disyaratkan:

| Konsep OOP | Penjelasan Implementasi di Genisys |
| :--- | :--- |
| **1. Class & Object** | Terdiri dari class utama `GenisysGUI`, class data `DatabaseLab`, dan hierarki spesimen. Objek diinstansiasi dengan perintah `new`. |
| **2. Encapsulation** | Variabel di class induk dilindungi dengan modifier `protected` dan `private`, diakses melalui metode *Getter*. |
| **3. Inheritance** | Terdapat 5 class konkrit (ex: `VirusRetrovirus`, `DNAManusia`) yang menggunakan `extends` untuk mewarisi class `SampelGenetik`. |
| **4. Polymorphism** | Penggunaan `@Override` pada fungsi `hitungPotensiMutasi()` yang memiliki rumus kalkulasi berbeda-beda di setiap turunan class. |
| **5. Interface** | Penggunaan `implements DeteksiAnomali` untuk memaksa implementasi metode pengecekan bahaya. |
| **6. Array** | Database sistem di- *handle* murni menggunakan kumpulan objek: `SampelGenetik[] arrayDatabase`. |
| **7. Input/Output** | Interaksi *User* melalui GUI (JTable, JTextField, JComboBox, JTextArea untuk Console Log). |
| **8. Sorting** | Menerapkan `Selection Sort` untuk mengurutkan spesimen berdasarkan tingkat mutasinya. |
| **9. Searching** | Menerapkan `Linear Search` untuk mencari spesimen berdasar `idSampel`. |

---

## 📸 Tampilan Antarmuka (Screenshots)

<div align="center">
  <img src="https://via.placeholder.com/800x450.png?text=Screenshot+Aplikasi+Genisys+Di+Sini" alt="Screenshot Aplikasi" width="800"/>
</div>

---

## 🚀 Cara Menjalankan Aplikasi Lokal
Bagi Anda yang ingin menguji coba aplikasi ini di perangkat lokal (atau mengeklonenya):
1. Pastikan **Java Development Kit (JDK) 8** atau versi lebih baru sudah terpasang di komputer Anda.
2. *Clone repository* ini:
   ```bash
   git clone (https://github.com/suryahadipurnamasurya-collab/tugas-kelompok-PBO)](https://github.com/suryahadipurnamasurya-collab/tugas-kelompok-PBO/edit/main/README.md)
