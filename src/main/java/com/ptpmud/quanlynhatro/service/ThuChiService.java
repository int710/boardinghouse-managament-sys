package com.ptpmud.quanlynhatro.service;

import com.ptpmud.quanlynhatro.dao.ThuChiDAO;
import com.ptpmud.quanlynhatro.model.ThuChi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for ThuChi (Revenue and Expense) management
 * 
 * @author Admin
 */
public class ThuChiService {
    private final ThuChiDAO dao = new ThuChiDAO();

    public List<ThuChi> getAll() {
        return dao.findAll();
    }

    public ThuChi findById(int id) {
        return dao.findById(id);
    }

    public List<ThuChi> findByLoai(String loai) {
        return dao.findByLoai(loai);
    }

    public List<ThuChi> findByDanhMuc(String danhMuc) {
        return dao.findByDanhMuc(danhMuc);
    }

    public List<ThuChi> findByMonth(int month, int year) {
        return dao.findByMonth(month, year);
    }

    public List<ThuChi> findByMonthAndLoai(int month, int year, String loai) {
        return dao.findByMonthAndLoai(month, year, loai);
    }

    public List<ThuChi> findByPhong(int idPhong) {
        return dao.findByPhong(idPhong);
    }

    public boolean add(ThuChi thuChi) {
        return dao.insert(thuChi);
    }

    public boolean update(ThuChi thuChi) {
        return dao.update(thuChi);
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }

    /**
     * Add revenue (THU) when invoice is marked as paid
     */
    public boolean addRevenue(int idPhong, LocalDate date, double amount, String description) {
        ThuChi tc = new ThuChi(date, "THU", amount, "TienPhong", description, idPhong);
        return dao.insert(tc);
    }

    /**
     * Add expense (CHI)
     */
    public boolean addExpense(LocalDate date, double amount, String danhMuc, String description, Integer idPhong) {
        ThuChi tc = new ThuChi(date, "CHI", amount, danhMuc, description, idPhong);
        return dao.insert(tc);
    }

    /**
     * Get statistics by month
     */
    public Map<String, Object> getMonthlyStats(int month, int year) {
        Map<String, Object> stats = new HashMap<>();
        
        double thu = dao.sumByMonthAndLoai(month, year, "THU");
        double chi = dao.sumByMonthAndLoai(month, year, "CHI");
        double no = 0; // TODO: Calculate from unpaid invoices if needed
        
        stats.put("month", month);
        stats.put("year", year);
        stats.put("thu", thu);
        stats.put("chi", chi);
        stats.put("noPhaiThu", no);
        stats.put("net", thu - chi);
        
        return stats;
    }

    /**
     * Get detailed category stats for a month
     */
    public Map<String, Double> getCategoryStats(int month, int year, String loai) {
        Map<String, Double> stats = new HashMap<>();
        List<ThuChi> records = dao.findByMonthAndLoai(month, year, loai);
        
        for (ThuChi tc : records) {
            String danhMuc = tc.getDanhMuc();
            stats.put(danhMuc, stats.getOrDefault(danhMuc, 0.0) + tc.getSoTien());
        }
        
        return stats;
    }

    /**
     * Get all unique categories for CHI
     */
    public List<String> getAllChiCategories() {
        // In reality, should query from database
        // For now, return common categories
        List<String> categories = new ArrayList<>();
        categories.add("TienDien");
        categories.add("TienNuoc");
        categories.add("SuaChua");
        categories.add("VeSinh");
        categories.add("MuaSam");
        categories.add("NhanCong");
        categories.add("Khac");
        return categories;
    }
}
