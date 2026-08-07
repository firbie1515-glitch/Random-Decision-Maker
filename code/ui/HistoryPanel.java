package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

/**
 * HistoryPanel
 * -------------------------------------------------
 * 顯示「目前登入使用者」的決策歷史紀錄
 */
public class HistoryPanel extends JPanel {

    private final User currentUser;

    private JComboBox<String> cbRange;
    private DefaultTableModel model;
    private JTable table;

    public HistoryPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(14,14,14,14));

        add(buildMain(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildMain() {

        JPanel root = new JPanel(new BorderLayout(12,12));

        /* ===== 上方操作區 ===== */
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbRange = new JComboBox<>(
                new String[]{"全部", "近一周", "近一個月", "近三個月"}
        );

        JButton btnRefresh = new JButton("更新");
        JButton btnClear = new JButton("清除所有紀錄");

        btnRefresh.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> clearAllRecords());

        top.add(new JLabel("時間範圍："));
        top.add(cbRange);
        top.add(btnRefresh);
        top.add(btnClear);

        /* ===== 表格 ===== */
        model = new DefaultTableModel(
                new String[]{"類型", "結果", "時間"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        return root;
    }

    private void refresh() {
        HistoryRecord.cleanupOverMonths(currentUser.getUserId(), 3);

        model.setRowCount(0);

        List<HistoryRecord> records =
                HistoryRecord.findByRange(
                        currentUser.getUserId(),
                        (String) cbRange.getSelectedItem()
                );

        for (HistoryRecord hr : records) {
            model.addRow(new Object[]{
                    hr.getDecisionType(),
                    hr.getResult(),
                    hr.getDecisionTime()
            });
        }
    }

    private void clearAllRecords() {

        if (JOptionPane.showConfirmDialog(
                this,
                "確定清除自己的所有歷史紀錄？",
                "確認",
                JOptionPane.OK_CANCEL_OPTION
        ) != JOptionPane.OK_OPTION) return;

        if (HistoryRecord.clearAll(currentUser.getUserId())) {
            refresh();
        }
    }
}
