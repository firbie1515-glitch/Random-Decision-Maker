package ui;

import db.DBConnector;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

/**
 * 系統決策主面板
 * -------------------------
 * 1. 顯示資料庫中的選項
 * 2. 新增 / 刪除選項
 * 3. 隨機決策
 * 4. 偏好加權決策
 * 5. 顯示決策結果與後續操作（再決策 / 分享）
 */
public class SystemDecisionPanel extends JPanel {

    /* ================= 成員變數 ================= */

    // 目前登入的使用者（保留以便之後擴充權限或紀錄）
    private final User currentUser;

    // 決策類型下拉選單（吃什麼 / 去哪玩 ...）
    private JComboBox<String> cbType;

    // 顯示選項資料的表格
    private JTable table;
    private DefaultTableModel tableModel;

    // 顯示決策結果的文字區
    private JTextArea resultArea;

    // 偏好決策用的條件下拉選單
    private JComboBox<String> cbLocation, cbTemp, cbTime, cbDistance, cbPrice, cbMood;

    /**
     * 建構子
     * 初始化版面與載入初始資料
     */
    public SystemDecisionPanel(User user) {
        this.currentUser = user;

        // 使用 BorderLayout 作為整體版面
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // 建立並加入主要 UI
        add(buildMain(), BorderLayout.CENTER);

        // 初次載入表格資料
        refreshTable();
    }

    /**
     * 建立整個畫面的 UI 結構
     */
    private JPanel buildMain() {
        JPanel root = new JPanel(new BorderLayout(12,12));

        /* ========= 上方：決策類型選擇 ========= */
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbType = new JComboBox<>(new String[]{"吃什麼", "去哪玩", "看電影", "穿搭"});

        JButton btnLoad = new JButton("載入資料");
        // 點擊後依選擇類型重新讀取資料
        btnLoad.addActionListener(e -> refreshTable());

        top.add(new JLabel("選擇類型（必選）："));
        top.add(cbType);
        top.add(btnLoad);

        /* ========= 中間：選項表格 ========= */
        tableModel = new DefaultTableModel(
                new String[]{"ID","名稱","地點","氣溫","時間","距離","價位","情緒"}, 0
        ) {
            // 表格僅供顯示，不允許直接編輯
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        JScrollPane tableScroll = new JScrollPane(table);

        /* ========= 表格上方操作按鈕 ========= */
        JPanel ops = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = new JButton("新增選項");
        JButton btnDelete = new JButton("刪除選項");
        JButton btnRandom = new JButton("隨機決策");

        btnAdd.addActionListener(e -> onAddOption());
        btnDelete.addActionListener(e -> onDeleteOption());
        btnRandom.addActionListener(e -> onRandomDecision());

        ops.add(btnAdd);
        ops.add(btnDelete);
        ops.add(btnRandom);

        /* ========= 右側：偏好加權決策區 ========= */
        JPanel pref = new JPanel(new GridLayout(3, 4, 10, 10));
        pref.setBorder(BorderFactory.createTitledBorder("偏好決策（加權）"));

        // 各項偏好條件（與資料庫中文內容一致）
        cbLocation = new JComboBox<>(new String[]{"全部","室內","室外"});
        cbTemp = new JComboBox<>(new String[]{"全部","熱","冷","中等"});
        cbTime = new JComboBox<>(new String[]{"全部","時間短","中等","時間長"});
        cbDistance = new JComboBox<>(new String[]{"全部","近","中等","遠"});
        cbPrice = new JComboBox<>(new String[]{"全部","價格低","中等","價格高"});
        cbMood = new JComboBox<>(new String[]{"全部","放鬆","有能量","懶惰","社交"});

        pref.add(new JLabel("地點：")); pref.add(cbLocation);
        pref.add(new JLabel("氣溫：")); pref.add(cbTemp);
        pref.add(new JLabel("時間：")); pref.add(cbTime);
        pref.add(new JLabel("距離：")); pref.add(cbDistance);
        pref.add(new JLabel("價位：")); pref.add(cbPrice);
        pref.add(new JLabel("情緒：")); pref.add(cbMood);

        JButton btnPref = new JButton("開始決策（加權）");
        btnPref.addActionListener(e -> onPreferenceDecision());

        JPanel prefBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        prefBtns.add(btnPref);

        /* ========= 下方：決策結果顯示 ========= */
        resultArea = new JTextArea(8, 28);
        resultArea.setLineWrap(true);
        resultArea.setEditable(false);

        JPanel resultPanel = new JPanel(new BorderLayout(8,8));
        resultPanel.setBorder(BorderFactory.createTitledBorder("決策結果"));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        /* ========= 組合版面 ========= */
        JPanel center = new JPanel(new BorderLayout(8,8));
        center.add(ops, BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(8,8));
        right.add(pref, BorderLayout.NORTH);
        right.add(prefBtns, BorderLayout.CENTER);
        right.add(resultPanel, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(right, BorderLayout.EAST);

        return root;
    }

    /* ================= 功能邏輯 ================= */

    /**
     * 依選擇的類型，從資料庫重新載入選項資料
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        String type = (String) cbType.getSelectedItem();

        RandomDecision rd =
                new RandomDecision(type, currentUser.getUserId());

        List<Option> opts = rd.loadOptions(type);

        for (Option o : opts) {
            tableModel.addRow(new Object[]{
                    o.getOptionId(), o.getName(), o.getLocation(),
                    o.getTemperature(), o.getTimeCost(),
                    o.getDistance(), o.getPriceLevel(), o.getMood(),
                    o.getUserId().equals("SYSTEM") ? "系統" : "我的"
            });
        }
    }

    /**
     * 新增選項（完整偏好版本）
     * --------------------------------------------------
     * 使用 Dialog 讓使用者一次輸入：
     *  - 名稱
     *  - 地點 / 氣溫 / 時間 / 距離 / 價位 / 情緒
     *
     * 新增的選項會：
     *  - 綁定目前登入的 userId
     *  - 可立即用於隨機 / 偏好加權決策
     */
    private void onAddOption() {

        String type = (String) cbType.getSelectedItem();

        /* ===== 建立輸入元件 ===== */

        JTextField tfName = new JTextField();

        JComboBox<String> cbLoc =
                new JComboBox<>(new String[]{"全部", "室內", "室外"});
        JComboBox<String> cbTemp =

                new JComboBox<>(new String[]{"全部", "熱", "冷", "中等"});
        JComboBox<String> cbTime =
                new JComboBox<>(new String[]{"全部", "時間短", "中等", "時間長"});
        JComboBox<String> cbDist =
                new JComboBox<>(new String[]{"全部", "近", "中等", "遠"});
        JComboBox<String> cbPrice =
                new JComboBox<>(new String[]{"全部", "價格低", "中等", "價格高"});
        JComboBox<String> cbMood =
                new JComboBox<>(new String[]{"全部", "放鬆", "有能量", "懶惰", "社交"});

        /* ===== 排版用 Panel ===== */

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));

        form.add(new JLabel("選項名稱："));
        form.add(tfName);

        form.add(new JLabel("地點："));
        form.add(cbLoc);

        form.add(new JLabel("氣溫："));
        form.add(cbTemp);

        form.add(new JLabel("時間："));
        form.add(cbTime);

        form.add(new JLabel("距離："));
        form.add(cbDist);

        form.add(new JLabel("價位："));
        form.add(cbPrice);

        form.add(new JLabel("情緒："));
        form.add(cbMood);

        /* ===== 顯示 Dialog ===== */

        int ok = JOptionPane.showConfirmDialog(
                this,
                form,
                "新增選項（設定偏好）",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (ok != JOptionPane.OK_OPTION) return;

        /* ===== 基本檢查 ===== */

        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "選項名稱不可為空");
            return;
        }

        /* ===== 寫入資料庫 ===== */

        String sql =
            "INSERT INTO options " +
            "(userId, optionType, name, location, temperature, timeCost, distance, priceLevel, mood) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currentUser.getUserId());
            ps.setString(2, type);
            ps.setString(3, name);

            ps.setString(4, (String) cbLoc.getSelectedItem());
            ps.setString(5, (String) cbTemp.getSelectedItem());
            ps.setString(6, (String) cbTime.getSelectedItem());
            ps.setString(7, (String) cbDist.getSelectedItem());
            ps.setString(8, (String) cbPrice.getSelectedItem());
            ps.setString(9, (String) cbMood.getSelectedItem());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "新增失敗，請稍後再試");
            return;
        }

        /* ===== 更新畫面 ===== */

        refreshTable();
    }


    /**
     * 刪除目前在表格中選取的選項
     */
    private void onDeleteOption() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "請先選取資料");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this, "確定要刪除這筆資料嗎？",
                "刪除確認", JOptionPane.OK_CANCEL_OPTION);

        if (ok != JOptionPane.OK_OPTION) return;

        int id = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement("DELETE FROM options WHERE optionId=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "刪除失敗");
        }
        refreshTable();
    }

    /**
     * 隨機決策：從目前類型中隨機選一個結果
     */
    private void onRandomDecision() {
        String type = (String) cbType.getSelectedItem();
        RandomDecision rd = new RandomDecision(type, currentUser.getUserId());
        List<Option> options = rd.loadOptions(type);

        if (options == null || options.isEmpty()) {
            JOptionPane.showMessageDialog(this, "目前沒有可用的選項");
            return;
        }

        String result = rd.makeDecision();
        showResultAndNextStep(type, result);
    }

    /**
     * 偏好加權決策：依使用者選擇的偏好條件計分後選出最佳結果
     */
    private void onPreferenceDecision() {
        FilterCondition fc = new FilterCondition(
                mapAll((String)cbLocation.getSelectedItem()),
                mapAll((String)cbTemp.getSelectedItem()),
                mapAll((String)cbDistance.getSelectedItem()),
                mapAll((String)cbTime.getSelectedItem()),
                mapAll((String)cbPrice.getSelectedItem()),
                mapAll((String)cbMood.getSelectedItem())
        );

        String type = (String) cbType.getSelectedItem();
        PreferenceDecision pd = new PreferenceDecision(type, fc, currentUser.getUserId());
        String result = pd.makeDecision();
        showResultAndNextStep(type, result);
    }

    /**
     * 顯示決策結果，並提供後續操作（返回 / 再決策 / 分享）
     */
    private void showResultAndNextStep(String type, String result) {
        resultArea.setText(result);

        // 儲存歷史紀錄
        new HistoryRecord(currentUser.getUserId(),type, result, LocalDateTime.now()).save();

        Object[] options = {"返回", "再決策一次", "分享結果"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "決策完成！\n請選擇接下來的操作",
                "完成",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0:
                // 返回：不做任何事，留在原畫面
                break;
            case 1:
                // 再執行一次隨機決策
                onRandomDecision();
                break;
            case 2:
                // 開啟分享視窗
                new SharePanel(this, result).setVisible(true);
                break;
        }
    }

    /**
     * 將「全部」轉為系統用標記，其餘維持原值
     */
    private String mapAll(String v) {
        return "全部".equals(v) ? "All" : v;
    }
}
