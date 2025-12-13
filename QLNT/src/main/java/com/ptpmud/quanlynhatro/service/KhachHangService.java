
package com.ptpmud.quanlynhatro.service;
import com.ptpmud.quanlynhatro.dao.KhachHangDAO;
import com.ptpmud.quanlynhatro.model.KhachHang;

import java.util.List;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */


public class KhachHangService {
    private final KhachHangDAO dao = new KhachHangDAO();

    public List<KhachHang> getAll() { return dao.findAll(); }

    public boolean create(KhachHang k) {
        if (dao.existsCCCD(k.getSoCccd())) return false;
        return dao.insert(k);
    }

    public boolean update(KhachHang k) {
        return dao.update(k);
    }

    public boolean delete(int id) {
        return dao.deleteKhachById(id);
    }

    public List<KhachHang> search(String key) {
        return dao.search(key);
    }

    public KhachHang findById(int id) {
        return dao.findById(id);
    }
}
