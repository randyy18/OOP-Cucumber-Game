Hide My Cucumber

Hide My Cucumber adalah game 2D dimana pemain harus mengumpulkan item cucumber yang terletak di beberapa ruangan sambil menghindari guard yang berpatroli. Game ini kami buat sebagai proyek Object-Oriented Programming (OOP) dengan fokus pada penerapan 4 pilar OOP (abstraction, inheritance, polymorphism, encapsulation), struktur class yang independen, penggunaan collection (ArrayList), validasi input & exception handling, serta penyimpanan data (save/load dan leaderboard) menggunakan database.



Fitur:

- Map dengan sistem ruangan + pintu dengan mekanisme lock/unlock berdasarkan progress cucumber yang telah diambil
- Guard AI yang berpatroli dan punya lingkaran radius deteksi
- Obstacle yang tidak bisa dilalui pemain (collision blocking)
- Power-up (Speed Boost / Immunity / Freeze Guards)
- Save/Load dan High Scores (Leaderboard) yang tersimpan secara permanen



Cara Menjalankan:

Requirements:
- Java Development Kit (JDK) 17 atau versi terbaru yang kompatibel
- IDE (disarankan): Eclipse / IntelliJ IDEA / NetBeans

Langkah Menjalankan (IDE):
1. Ekstrak file project `.zip`.
2. Buka project di IDE.
3. Pastikan folder `src` terdeteksi sebagai source folder.
4. Jalankan class `Main`.
5. Game akan muncul pada jendela GUI.

Kontrol:
- Gerak: WASD / Arrow Keys
- Save game: F5
- Load game: F9
- Difficulty: 1 / 2 / 3
- Navigasi menu: ↑ / ↓ dan Enter / Space



Daftar Class dan Fungsinya

1) `Main`
- Entry point aplikasi.
- Menjalankan window/game pertama kali.

2) `GameWindow` (GUI Container)
- Membuat dan mengatur window utama (turunan dari JFrame).
- Meng-handle setup awal tampilan.

3) `GamePanel` (Game Loop & Rendering)
- Mengatur inti jalannya game yaitu update per frame, render grafik, dan state menu/game.
- Mengatur input user (menu dan gameplay).
- Mengatur transisi state yaitu menu → bermain → game over → win.
- Memanggil fitur Save/Load dan menampilkan High Score.

4) `GameState`
- Menyimpan state global progress permainan, seperti MENU, PLAYING, GAMEOVER, atau WIN.
- Membantu kontrol alur program agar jelas dan tidak bercampur.

5) `Entity` (Abstract Class)
- Abstraksi untuk objek bergerak/aktif di game.
- Memiliki kontrak method:
  - `update()` (logika per frame)
  - `draw(Graphics2D)` (render ke layar)
- Berisi logic umum seperti bounding box/collision (`getBounds()` dan helper collision).

6) `Player` (extends `Entity`)
- Mengatur pergerakan player berdasarkan input.
- Validate posisi agar tidak keluar area map (boundary clamp).
- Interaksi dengan objek lain: obstacle, cucumber, power-up, dan guard.

7) `Guard` (extends `Entity`)
- AI guard yaitu pergerakan patrol guard dari titik A → B dan sebaliknya.
- Memiliki logika untuk mendeteksi player dalam radius tertentu.
- Deteksi guard dibatasi konteks area (ruangan) agar tidak menembus tembok ruangan masing-masing.

8) `RoomManager`
- Mengatur kumpulan ruangan (rooms) dan koneksinya (doors).
- Mengatur perpindahan antar room dan aturan unlock.

9) `Room`
- Representasi satu ruangan: layout, obstacle, dan target (cucumber).
- Menyimpan progress ruangan, termasuk mekanisme `isUnlocked()` sesuai kondisi cucumber telah diambil atau belum.

10) `Obstacle`
- Objek penghalang (contoh: kasur).
- Memiliki area collision sehingga player tidak dapat melewati obstacle.

11) `PowerUp`
- Item yang dapat diambil untuk memberi efek sementara:
  - Speed Boost
  - Immunity
  - Freeze Guards
- Mengatur durasi dan efek terhadap state permainan.

12) `DatabaseManager`
- Mengelola koneksi database H2 (embedded) via JDBC.
- Menangani:
  - Save data game (posisi player + progress)
  - Load data game
  - Penyimpanan dan pengambilan High Scores (leaderboard)
- Memiliki exception handling agar program tetap berjalan meskipun DB error.

13) `AudioManager`
- Mengelola background music (BGM) dan sound effects (SFX).
- Memiliki try-catch agar game tetap berjalan meskipun file audio error.



Konsep OOP yang Dipakai

1) Abstraction
Abstraction diterapkan melalui `abstract class Entity`, yang mendefinisikan kontrak `update()` dan `draw(Graphics2D)` untuk semua entitas di dalam game. Dengan cara ini, game loop memproses berbagai entitas secara konsisten tanpa perlu mengetahui detail implementasinya satu per satu.

Contoh: `Player` dan `Guard` sama-sama wajib mengimplementasikan `update()` dan `draw()`.

2) Inheritance
`Player` dan `Guard` merupakan turunan dari `Entity`. Pewarisan ini memungkinkan reuse atribut dasar (posisi, ukuran, speed) dan fungsi umum (bounding box/collision). Dengan inheritance, struktur entitas menjadi lebih rapi dan mudah dikembangkan jika ingin menambah jenis entitas baru.

Contoh: `Player extends Entity`, `Guard extends Entity`.

3) Polymorphism
Polymorphism diterapkan pada method overriding di `update()` dan `draw()`:
- `Player.update()` fokus pada input movement, collision, dan boundary clamp.
- `Guard.update()` fokus pada AI patrol dan deteksi pemain.
Walaupun method yang dipanggil sama-sama `update()`, hasil perilakunya berbeda tergantung object-nya saat runtime.

4) Encapsulation
Encapsulation digunakan dengan menyimpan atribut penting sebagai `private` dan mengontrol perubahan state melalui method. Ini menjaga integritas data dan mencegah perubahan state yang tidak valid.

Contoh penerapan:
- Movement state player tidak dapat diakses langsung dari luar class.
- Unlock ruangan ditentukan melalui method `isUnlocked()`, bukan mengubah variabel sembarangan.
- Save data dibungkus dalam struktur data tersendiri agar konsisten saat disimpan/diload.

++ Catatan Tambahan
- Project kami menggunakan collection berupa ArrayList untuk mengelola objek dinamis seperti rooms, obstacle, dan data high score.
- Project kami memiliki validasi input (boundary + collision blocking) dan exception handling (DB + audio + setup UI) untuk meningkatkan robustness.
- Penyimpanan data menggunakan H2 embedded database sehingga tidak memerlukan server tambahan.


Thank you! Selamat bermain :)