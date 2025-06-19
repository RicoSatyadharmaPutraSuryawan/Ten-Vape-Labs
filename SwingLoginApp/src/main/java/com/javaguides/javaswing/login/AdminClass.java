/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
package com.javaguides.javaswing.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Kelas AdminClass digunakan untuk menyimpan informasi admin dan menangani logika terkait.
 * 
 * Penulis: Rega Kristama
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminClass {
    private int id;
    private String username;
    private String password;
    private String namaLengkap;
    private String email;
    private String jabatan;
    private boolean isActive;

    // List static untuk menyimpan data admin simulasi (seolah database)
    private static List<AdminClass> daftarAdmin = new ArrayList<>();

    public AdminClass() {
        this.isActive = true;
    }

    public AdminClass(int id, String username, String password, String namaLengkap, String email, String jabatan) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.jabatan = jabatan;
        this.isActive = true;
    }

    // ======================= GETTER & SETTER ============================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // ======================= METHOD STATIS ============================

    // Menambahkan admin baru
    public static void tambahAdmin(AdminClass admin) {
        daftarAdmin.add(admin);
    }

    // Mendapatkan daftar seluruh admin
    public static List<AdminClass> getSemuaAdmin() {
        return daftarAdmin;
    }

    // Validasi login admin
    public static AdminClass login(String username, String password) {
        for (AdminClass admin : daftarAdmin) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password) && admin.isActive()) {
                return admin;
            }
        }
        return null;
    }

    // Cari admin berdasarkan username
    public static AdminClass cariAdmin(String username) {
        for (AdminClass admin : daftarAdmin) {
            if (admin.getUsername().equalsIgnoreCase(username)) {
                return admin;
            }
        }
        return null;
    }

    // Nonaktifkan admin
    public static boolean nonaktifkanAdmin(String username) {
        AdminClass admin = cariAdmin(username);
        if (admin != null) {
            admin.setActive(false);
            return true;
        }
        return false;
    }

    // Cetak info admin
    public void cetakInfo() {
        System.out.println("===== INFO ADMIN =====");
        System.out.println("ID        : " + id);
        System.out.println("Username  : " + username);
        System.out.println("Nama      : " + namaLengkap);
        System.out.println("Email     : " + email);
        System.out.println("Jabatan   : " + jabatan);
        System.out.println("Status    : " + (isActive ? "Aktif" : "Nonaktif"));
        System.out.println("======================");
    }

    // Override toString
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nama='" + namaLengkap + '\'' +
                ", jabatan='" + jabatan + '\'' +
                ", aktif=" + isActive +
                '}';
    }

    // Override equals dan hashCode untuk perbandingan admin
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminClass)) return false;
        AdminClass admin = (AdminClass) o;
        return id == admin.id && Objects.equals(username, admin.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    // ======================= MAIN TEST ============================
    public static void main(String[] args) {
        // Tambahkan admin
        AdminClass admin1 = new AdminClass(1, "admin", "admin123", "Rega Kristama", "rega@example.com", "Super Admin");
        AdminClass admin2 = new AdminClass(2, "user", "user123", "Budi Santoso", "budi@example.com", "Admin");

        tambahAdmin(admin1);
        tambahAdmin(admin2);

        // Coba login
        AdminClass login = login("admin", "admin123");
        if (login != null) {
            System.out.println("Login berhasil!");
            login.cetakInfo();
        } else {
            System.out.println("Login gagal!");
        }

        // Cetak semua admin
        System.out.println("\nDaftar Admin:");
        for (AdminClass a : getSemuaAdmin()) {
            System.out.println(a);
        }
    }
}

