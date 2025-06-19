package com.javaguides.javaswing.produk;

import java.util.*;

/**
 * Class Produk merepresentasikan entitas produk dalam sistem.
 */
class Produk {
    private int id;
    private String nama;
    private String kategori;
    private double harga;
    private int stok;
    private String deskripsi;

    public Produk(int id, String nama, String kategori, double harga, int stok, String deskripsi) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
        this.stok = stok;
        this.deskripsi = deskripsi;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
    public String getDeskripsi() { return deskripsi; }

    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setHarga(double harga) { this.harga = harga; }
    public void setStok(int stok) { this.stok = stok; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public void tampilkanInfo() {
        System.out.println("ID Produk       : " + id);
        System.out.println("Nama Produk     : " + nama);
        System.out.println("Kategori        : " + kategori);
        System.out.println("Harga           : Rp " + harga);
        System.out.println("Stok            : " + stok);
        System.out.println("Deskripsi       : " + deskripsi);
        System.out.println("------------------------------------------");
    }

    @Override
    public String toString() {
        return "Produk{" +
                "id=" + id +
                ", nama='" + nama + '\'' +
                ", kategori='" + kategori + '\'' +
                ", harga=" + harga +
                ", stok=" + stok +
                ", deskripsi='" + deskripsi + '\'' +
                '}';
    }
}

/**
 * ProdukManager digunakan untuk melakukan operasi CRUD terhadap produk.
 */
public class ProdukManager {
    private List<Produk> daftarProduk;

    public ProdukManager() {
        daftarProduk = new ArrayList<>();
    }

    // CREATE
    public void tambahProduk(Produk p) {
        if (cariProdukById(p.getId()) != null) {
            System.out.println("Produk dengan ID tersebut sudah ada.");
            return;
        }
        daftarProduk.add(p);
        System.out.println("Produk berhasil ditambahkan.");
    }

    // READ
    public void tampilkanSemuaProduk() {
        if (daftarProduk.isEmpty()) {
            System.out.println("Belum ada produk.");
        }
        for (Produk p : daftarProduk) {
            p.tampilkanInfo();
        }
    }

    public Produk cariProdukById(int id) {
        for (Produk p : daftarProduk) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // UPDATE
    public void updateProduk(int id, String nama, String kategori, double harga, int stok, String deskripsi) {
        Produk p = cariProdukById(id);
        if (p != null) {
            p.setNama(nama);
            p.setKategori(kategori);
            p.setHarga(harga);
            p.setStok(stok);
            p.setDeskripsi(deskripsi);
            System.out.println("Produk berhasil diupdate.");
        } else {
            System.out.println("Produk tidak ditemukan.");
        }
    }

    // DELETE
    public void hapusProduk(int id) {
        Produk p = cariProdukById(id);
        if (p != null) {
            daftarProduk.remove(p);
            System.out.println("Produk berhasil dihapus.");
        } else {
            System.out.println("Produk tidak ditemukan.");
        }
    }

    // SEARCH
    public void cariProdukByNama(String keyword) {
        boolean ditemukan = false;
        for (Produk p : daftarProduk) {
            if (p.getNama().toLowerCase().contains(keyword.toLowerCase())) {
                p.tampilkanInfo();
                ditemukan = true;
            }
        }
        if (!ditemukan) {
            System.out.println("Produk tidak ditemukan.");
        }
    }

    // Simulasi CLI
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProdukManager pm = new ProdukManager();

        while (true) {
            System.out.println("===== MENU PRODUK =====");
            System.out.println("1. Tambah Produk");
            System.out.println("2. Lihat Semua Produk");
            System.out.println("3. Cari Produk (by ID)");
            System.out.println("4. Update Produk");
            System.out.println("5. Hapus Produk");
            System.out.println("6. Cari Produk (by Nama)");
            System.out.println("7. Keluar");
            System.out.print("Pilih: ");

            int pilihan = Integer.parseInt(scanner.nextLine());

            switch (pilihan) {
                case 1:
                    System.out.print("ID Produk: ");
                    int id = Integer.parseInt(scanner.nextLine());

                    System.out.print("Nama Produk: ");
                    String nama = scanner.nextLine();

                    System.out.print("Kategori: ");
                    String kategori = scanner.nextLine();

                    System.out.print("Harga: ");
                    double harga = Double.parseDouble(scanner.nextLine());

                    System.out.print("Stok: ");
                    int stok = Integer.parseInt(scanner.nextLine());

                    System.out.print("Deskripsi: ");
                    String deskripsi = scanner.nextLine();

                    Produk baru = new Produk(id, nama, kategori, harga, stok, deskripsi);
                    pm.tambahProduk(baru);
                    break;

                case 2:
                    pm.tampilkanSemuaProduk();
                    break;

                case 3:
                    System.out.print("ID Produk: ");
                    int cariId = Integer.parseInt(scanner.nextLine());
                    Produk hasil = pm.cariProdukById(cariId);
                    if (hasil != null) {
                        hasil.tampilkanInfo();
                    } else {
                        System.out.println("Produk tidak ditemukan.");
                    }
                    break;

                case 4:
                    System.out.print("ID Produk: ");
                    int idUpdate = Integer.parseInt(scanner.nextLine());

                    System.out.print("Nama Baru: ");
                    String namaBaru = scanner.nextLine();

                    System.out.print("Kategori Baru: ");
                    String kategoriBaru = scanner.nextLine();

                    System.out.print("Harga Baru: ");
                    double hargaBaru = Double.parseDouble(scanner.nextLine());

                    System.out.print("Stok Baru: ");
                    int stokBaru = Integer.parseInt(scanner.nextLine());

                    System.out.print("Deskripsi Baru: ");
                    String deskripsiBaru = scanner.nextLine();

                    pm.updateProduk(idUpdate, namaBaru, kategoriBaru, hargaBaru, stokBaru, deskripsiBaru);
                    break;

                case 5:
                    System.out.print("ID Produk yang dihapus: ");
                    int idHapus = Integer.parseInt(scanner.nextLine());
                    pm.hapusProduk(idHapus);
                    break;

                case 6:
                    System.out.print("Nama Produk yang dicari: ");
                    String keyword = scanner.nextLine();
                    pm.cariProdukByNama(keyword);
                    break;

                case 7:
                    System.out.println("Keluar...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Pilihan tidak valid.");
                    break;
            }

            System.out.println("\n\n");
        }
    }
}
