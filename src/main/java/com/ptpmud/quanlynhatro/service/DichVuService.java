package com.ptpmud.quanlynhatro.service;

import com.ptpmud.quanlynhatro.dao.DichVuDAO;
import com.ptpmud.quanlynhatro.dao.PhongDichVuDAO;
import com.ptpmud.quanlynhatro.model.DichVu;
import com.ptpmud.quanlynhatro.model.PhongDichVu;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DichVuService {
    private final DichVuDAO dvDAO = new DichVuDAO();
    private final PhongDichVuDAO pdvDAO = new PhongDichVuDAO();

    // DichVu CRUD
    public List<DichVu> getAll() { return dvDAO.findAll(); }
    public DichVu getById(int id) { return dvDAO.findById(id); }
    public boolean create(DichVu dv) { return dvDAO.insert(dv); }
    public boolean update(DichVu dv) { return dvDAO.update(dv); }
    public boolean delete(int id) { return dvDAO.delete(id); }

    // PhongDichVu operations
    public List<PhongDichVu> getServicesForPhong(int idPhong) { return pdvDAO.findByPhong(idPhong); }

    /**
     * Gán dịch vụ cho phòng.
     * Nếu thang/nam là null => dịch vụ cố định hàng tháng.
     * Sau insert, gọi stored procedure spTaoHoaDonChoPhong nếu cần (thang==null -> current month).
     */
    public boolean assignServiceToPhong(PhongDichVu pdv) {
        boolean ok = pdvDAO.insert(pdv);
        if (!ok) return false;
        // gọi stored procedure để cập nhật hoá đơn
        int thang = (pdv.getThang() == null) ? java.time.LocalDate.now().getMonthValue() : pdv.getThang();
        int nam = (pdv.getNam() == null) ? java.time.LocalDate.now().getYear() : pdv.getNam();
        try (Connection c = DBConnection.getConnection();
             CallableStatement cs = c.prepareCall("{CALL spTaoHoaDonChoPhong(?, ?, ?)}")) {
            cs.setInt(1, pdv.getIdPhong());
            cs.setInt(2, thang);
            cs.setInt(3, nam);
            cs.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
            // dịch vụ đã insert nhưng sp lỗi -> vẫn trả true (tùy bạn muốn rollback)
        }
        return true;
    }

    public boolean removeServiceFromPhong(int idPhongDichVu, Integer idPhong, Integer thang, Integer nam) {
        // lấy thông tin idPhong trước (nếu cần) — pdvDAO.map không public, nên caller phải truyền idPhong nếu muốn recalc
        boolean ok = pdvDAO.remove(idPhongDichVu);
        if (!ok) return false;
        // gọi sp để cập nhật hoá đơn cho tháng tương ứng; nếu thang==null -> current month
        int t = (thang == null) ? java.time.LocalDate.now().getMonthValue() : thang;
        int y = (nam == null) ? java.time.LocalDate.now().getYear() : nam;
        if (idPhong != null) {
            try (Connection c = DBConnection.getConnection();
                 CallableStatement cs = c.prepareCall("{CALL spTaoHoaDonChoPhong(?, ?, ?)}")) {
                cs.setInt(1, idPhong);
                cs.setInt(2, t);
                cs.setInt(3, y);
                cs.execute();
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return true;
    }
}
