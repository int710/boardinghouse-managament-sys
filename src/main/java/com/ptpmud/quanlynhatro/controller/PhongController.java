package com.ptpmud.quanlynhatro.controller;

import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.PhongService;

import java.time.LocalDate;
import java.util.List;

public class PhongController {
    private final PhongService service = new PhongService();

    public List<Phong> loadAll() { return service.getAll(); }
    public List<Phong> loadByStatus(String status) { return service.getByStatus(status); }
    public List<Phong> search(String keyword) { return service.search(keyword); }
    public boolean createPhong(Phong p) { return service.create(p); }
    public boolean updatePhong(Phong p) { return service.update(p); }
    public boolean removePhong(int id) { return service.delete(id); }
    public boolean setTrangThai(int id, String tt) { return service.setTrangThai(id, tt); }
    public int countStatus(String s) { return service.countStatus(s); }

    // assign tenant wrapper: createAccount flag to request creating TaiKhoan
    public PhongService.AssignResult assignTenantToPhong(int idPhong, int idKhachHang, LocalDate ngayBatDau, LocalDate ngayKetThuc, double tienCoc, boolean createAccount) {
        return service.assignTenantToPhong(idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, createAccount);
    }

    public PhongService.TenantInfo getTenantInfoByPhong(int idPhong) {
        return service.getTenantInfoByPhong(idPhong);
    }
    
    /**
     * Lấy danh sách khách hàng có thể gán vào phòng (chưa có hợp đồng đang thuê)
     */
    public List<KhachHang> getAvailableCustomers() {
        return service.getAvailableCustomers();
    }
}
