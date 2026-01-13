// 3. Service: HopDongService.java
// Package: com.quanlynhatro.service
package com.ptpmud.quanlynhatro.service;

import com.ptpmud.quanlynhatro.dao.HopDongDAO;
import com.ptpmud.quanlynhatro.dao.PhongDAO;
import com.ptpmud.quanlynhatro.model.HopDong;
import com.ptpmud.quanlynhatro.model.Phong;

import java.time.LocalDate;
import java.util.List;

public class HopDongService {
    private final HopDongDAO dao = new HopDongDAO();
    private final PhongDAO phongDAO = new PhongDAO();

    public boolean create(HopDong hopDong) {
        // validate room
        Phong p = phongDAO.findById(hopDong.getIdPhong());
        if (p == null) return false;
        if ("baoTri".equalsIgnoreCase(p.getTrangThai()) || "daThue".equalsIgnoreCase(p.getTrangThai())) {
            return false;
        }
        if (dao.findActiveByPhong(hopDong.getIdPhong()) != null) {
            return false;
        }
        if (hopDong.getTrangThai() == null) hopDong.setTrangThai("dangThue");
        boolean ok = dao.insert(hopDong);
        if (ok) {
            phongDAO.setTrangThai(hopDong.getIdPhong(), "daThue");
        }
        return ok;
    }

    public List<HopDong> findAll() { return dao.findAll(); }
    public List<HopDong> find(String status) { return dao.findByStatus(status); }
    public List<HopDong> search(String keyword) { return dao.search(keyword); }
    public HopDong findById(int id) { return dao.findById(id); }
    public HopDong findActiveByPhong(int idPhong) { return dao.findActiveByPhong(idPhong); }
    public HopDong findActiveByKhachHang(int idKhachHang) { return dao.findActiveByKhachHang(idKhachHang); }

    public boolean update(HopDong hopDong) { return dao.update(hopDong); }

    public boolean endContract(int id) {
        // kết thúc và trả phòng về trạng thái trống (trigger cũng xử lý)
        boolean ok = dao.endContract(id, LocalDate.now());
        if (ok) {
            HopDong hd = dao.findById(id);
            if (hd != null) phongDAO.setTrangThai(hd.getIdPhong(), "trong");
        }
        return ok;
    }

    public boolean renewContract(int id, LocalDate newEndDate) {
        boolean ok = dao.renewContract(id, newEndDate);
        if (ok) {
            HopDong hd = dao.findById(id);
            if (hd != null) phongDAO.setTrangThai(hd.getIdPhong(), "daThue");
        }
        return ok;
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }

    public List<HopDong> findHistoryByPhong(int idPhong) { return dao.findHistoryByPhong(idPhong); }
    public long countAll() { return dao.countAll(); }
    public long countByStatus(String status) { return dao.countByStatus(status); }
}