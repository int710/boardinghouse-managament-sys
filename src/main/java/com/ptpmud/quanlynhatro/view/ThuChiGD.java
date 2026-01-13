package com.ptpmud.quanlynhatro.view;

import com.ptpmud.quanlynhatro.model.ThuChi;
import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.service.ThuChiService;
import com.ptpmud.quanlynhatro.service.PhongService;
import com.ptpmud.quanlynhatro.utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * GUI for managing ThuChi (Revenue and Expense)
 * 
 * @author Admin
 */
public class ThuChiGD extends javax.swing.JPanel {

    private final ThuChiService thuChiService = new ThuChiService();
    private final PhongService phongService = new PhongService();
    private DefaultTableModel tableModel;
    private DefaultTableModel statTableModel;
    private JTable table;
    private JTable statTable;
    
    // Statistics labels
    private JLabel lblThuTotal;
    private JLabel lblChiTotal;
    private JLabel lblNetTotal;
    
    // Filter components
    private JComboBox<String> cbFilterMonth;
    private JComboBox<String> cbFilterYear;
    private JComboBox<String> cbFilterType;
    
    // Statistics filter components
    private JComboBox<String> cbStatMonth;
    private JComboBox<String> cbStatYear;

    public ThuChiGD() {
        initComponents();
        setupTables();
        loadData();
    }

    private void setupTables() {
        // Table model for listing ThuChi records
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Ngày", "Loại", "Danh mục", "Số tiền", "Phòng", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table.setModel(tableModel);
        table.setRowHeight(24);

        // Statistics table model
        statTableModel = new DefaultTableModel(
            new String[]{"Danh mục", "Số tiền"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        statTable.setModel(statTableModel);
        statTable.setRowHeight(24);
    }

    private void loadData() {
        loadFilteredThuChi();
        updateStatistics();
    }

    private void loadFilteredThuChi() {
        String monthStr = (String) cbFilterMonth.getSelectedItem();
        String yearStr = (String) cbFilterYear.getSelectedItem();
        String type = (String) cbFilterType.getSelectedItem();
        
        List<ThuChi> list;
        
        if ("Tất cả".equals(monthStr) && "Tất cả".equals(yearStr)) {
            if ("Tất cả".equals(type)) {
                list = thuChiService.getAll();
            } else {
                list = thuChiService.findByLoai(type);
            }
        } else {
            int month = "Tất cả".equals(monthStr) ? LocalDate.now().getMonthValue() : Integer.parseInt(monthStr);
            int year = "Tất cả".equals(yearStr) ? LocalDate.now().getYear() : Integer.parseInt(yearStr);
            
            if ("Tất cả".equals(type)) {
                list = thuChiService.findByMonth(month, year);
            } else {
                list = thuChiService.findByMonthAndLoai(month, year, type);
            }
        }
        
        tableModel.setRowCount(0);
        for (ThuChi tc : list) {
            String tenPhong = "";
            if (tc.getIdPhong() != null) {
                Phong p = phongService.findById(tc.getIdPhong());
                tenPhong = p != null ? p.getTenPhong() : "";
            }
            
            tableModel.addRow(new Object[]{
                tc.getIdThuChi(),
                tc.getNgayLap(),
                tc.getLoai(),
                tc.getDanhMuc(),
                Utils.formatCurrency(tc.getSoTien()),
                tenPhong,
                tc.getGhiChu()
            });
        }
    }

    private void updateStatistics() {
        String monthStr = (String) cbStatMonth.getSelectedItem();
        String yearStr = (String) cbStatYear.getSelectedItem();
        
        int month = Integer.parseInt(monthStr);
        int year = Integer.parseInt(yearStr);
        
        Map<String, Object> stats = thuChiService.getMonthlyStats(month, year);
        double thu = (double) stats.get("thu");
        double chi = (double) stats.get("chi");
        double net = (double) stats.get("net");
        
        lblThuTotal.setText("Tổng thu: " + Utils.formatCurrency(thu));
        lblChiTotal.setText("Tổng chi: " + Utils.formatCurrency(chi));
        lblNetTotal.setText("Lợi nhuận: " + Utils.formatCurrency(net));
        
        // Update category breakdown
        statTableModel.setRowCount(0);
        
        Map<String, Double> thuCategories = thuChiService.getCategoryStats(month, year, "THU");
        for (Map.Entry<String, Double> entry : thuCategories.entrySet()) {
            statTableModel.addRow(new Object[]{
                entry.getKey() + " (THU)",
                Utils.formatCurrency(entry.getValue())
            });
        }

        Map<String, Double> chiCategories = thuChiService.getCategoryStats(month, year, "CHI");
        for (Map.Entry<String, Double> entry : chiCategories.entrySet()) {
            statTableModel.addRow(new Object[]{
                entry.getKey() + " (CHI)",
                Utils.formatCurrency(entry.getValue())
            });
        }
    }
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // TAB 1: Quản lý Thu Chi (Nhập chi + Danh sách)
        JPanel managePanel = createManagePanel();
        tabbedPane.addTab("Quản lý Thu Chi", managePanel);

        // TAB 2: Thống kê
        JPanel statsPanel = createStatsPanel();
        tabbedPane.addTab("Thống kê", statsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Left side: Input form
        JPanel leftPanel = createInputPanel();
        leftPanel.setPreferredSize(new Dimension(350, 0));
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Right side: List
        JPanel rightPanel = createListPanel();
        panel.add(rightPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Nhập Chi Phí Mới"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Ngày chi:"), gbc);
        JTextField txtDate = new JTextField(LocalDate.now().toString());
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtDate, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Danh mục:"), gbc);
        JComboBox<String> cbCategory = new JComboBox<>();
        for (String cat : thuChiService.getAllChiCategories()) {
            cbCategory.addItem(cat);
        }
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cbCategory, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Số tiền:"), gbc);
        JTextField txtAmount = new JTextField();
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtAmount, gbc);

        // Room
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Phòng:"), gbc);
        JComboBox<String> cbRoom = new JComboBox<>();
        cbRoom.addItem("---");
        for (Phong p : phongService.getAll()) {
            cbRoom.addItem(p.getIdPhong() + " - " + p.getTenPhong());
        }
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cbRoom, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Ghi chú:"), gbc);
        JTextArea txtNote = new JTextArea(4, 20);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        JScrollPane scrollNote = new JScrollPane(txtNote);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formPanel.add(scrollNote, gbc);

        // Button
        JButton btnAdd = new JButton("Thêm Chi Phí");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(txtDate.getText().trim());
                String category = (String) cbCategory.getSelectedItem();
                double amount = Double.parseDouble(txtAmount.getText().trim());
                String note = txtNote.getText().trim();
                Integer idPhong = null;
                
                String roomSelect = (String) cbRoom.getSelectedItem();
                if (!roomSelect.equals("---")) {
                    idPhong = Integer.parseInt(roomSelect.split(" - ")[0]);
                }

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean ok = thuChiService.addExpense(date, amount, category, note, idPhong);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã thêm chi phí thành công!");
                    txtDate.setText(LocalDate.now().toString());
                    txtAmount.setText("");
                    txtNote.setText("");
                    cbRoom.setSelectedIndex(0);
                    loadFilteredThuChi();
                    updateStatistics();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thêm chi phí!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(btnAdd, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Danh Sách Thu Chi"));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        filterPanel.add(new JLabel("Tháng:"));
        cbFilterMonth = new JComboBox<>();
        cbFilterMonth.addItem("Tất cả");
        for (int m = 1; m <= 12; m++) {
            cbFilterMonth.addItem(String.valueOf(m));
        }
        cbFilterMonth.setSelectedItem(String.valueOf(LocalDate.now().getMonthValue()));
        filterPanel.add(cbFilterMonth);
        
        filterPanel.add(new JLabel("Năm:"));
        cbFilterYear = new JComboBox<>();
        cbFilterYear.addItem("Tất cả");
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 3; y <= currentYear + 1; y++) {
            cbFilterYear.addItem(String.valueOf(y));
        }
        cbFilterYear.setSelectedItem(String.valueOf(currentYear));
        filterPanel.add(cbFilterYear);
        
        filterPanel.add(new JLabel("Loại:"));
        cbFilterType = new JComboBox<>(new String[]{"Tất cả", "THU", "CHI"});
        filterPanel.add(cbFilterType);
        
        JButton btnFilter = new JButton("Lọc");
        btnFilter.setBackground(new Color(0, 123, 255));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> loadFilteredThuChi());
        filterPanel.add(btnFilter);
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            cbFilterMonth.setSelectedItem(String.valueOf(LocalDate.now().getMonthValue()));
            cbFilterYear.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
            cbFilterType.setSelectedIndex(0);
            loadFilteredThuChi();
        });
        filterPanel.add(btnRefresh);
        
        panel.add(filterPanel, BorderLayout.NORTH);

        // Table
        table = new JTable();
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> deleteSelected());
        btnPanel.add(btnDelete);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (thuChiService.delete(id)) {
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                loadFilteredThuChi();
                updateStatistics();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Chọn Tháng/Năm Thống Kê"));
        
        filterPanel.add(new JLabel("Tháng:"));
        cbStatMonth = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cbStatMonth.addItem(String.valueOf(m));
        }
        cbStatMonth.setSelectedItem(String.valueOf(LocalDate.now().getMonthValue()));
        filterPanel.add(cbStatMonth);
        
        filterPanel.add(new JLabel("Năm:"));
        cbStatYear = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 3; y <= currentYear + 1; y++) {
            cbStatYear.addItem(String.valueOf(y));
        }
        cbStatYear.setSelectedItem(String.valueOf(currentYear));
        filterPanel.add(cbStatYear);
        
        JButton btnStats = new JButton("Thống Kê");
        btnStats.setBackground(new Color(0, 123, 255));
        btnStats.setForeground(Color.WHITE);
        btnStats.setFocusPainted(false);
        btnStats.addActionListener(e -> updateStatistics());
        filterPanel.add(btnStats);
        
        JButton btnRefreshStats = new JButton("Làm mới");
        btnRefreshStats.addActionListener(e -> {
            cbStatMonth.setSelectedItem(String.valueOf(LocalDate.now().getMonthValue()));
            cbStatYear.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
            updateStatistics();
        });
        filterPanel.add(btnRefreshStats);
        
        panel.add(filterPanel, BorderLayout.NORTH);

        // Center: Main content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        
        // Stats summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Tổng Hợp"));
        summaryPanel.setPreferredSize(new Dimension(0, 100));
        
        lblThuTotal = createStatLabel("Tổng thu: 0 ₫", new Color(40, 167, 69));
        lblChiTotal = createStatLabel("Tổng chi: 0 ₫", new Color(220, 53, 69));
        lblNetTotal = createStatLabel("Lợi nhuận: 0 ₫", new Color(0, 123, 255));
        
        summaryPanel.add(createStatCard(lblThuTotal, new Color(40, 167, 69)));
        summaryPanel.add(createStatCard(lblChiTotal, new Color(220, 53, 69)));
        summaryPanel.add(createStatCard(lblNetTotal, new Color(0, 123, 255)));
        
        contentPanel.add(summaryPanel, BorderLayout.NORTH);

        // Category breakdown table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Chi Tiết Theo Danh Mục"));
        
        statTable = new JTable();
        statTable.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(statTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tablePanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }
    
    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(color);
        return label;
    }
    
    private JPanel createStatCard(JLabel label, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            new EmptyBorder(15, 10, 15, 10)
        ));
        card.add(label, BorderLayout.CENTER);
        return card;
    }
}
