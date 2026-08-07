package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

/**
 * 左側功能選單面板
 *-------------------------
 * 1. 顯示系統主要功能按鈕（主頁、系統決策、決策輪、歷史紀錄）
 * 2. 負責處理「登出流程」與所有相關異常狀況
 */
public class LeftMenuPanel1 extends JPanel {

    // 主視窗（負責頁面切換）
    private final MainFrame1 mainFrame;

    // 目前登入的使用者（用於登出）
    private final User currentUser;

    /**
     * 建構子
     */
    public LeftMenuPanel1(MainFrame1 frame, User user) {
        this.mainFrame = frame;
        this.currentUser = user;

        /* ===== 基本版面設定 ===== */

        // 左側選單固定寬度 180px，高度由外層決定
        setPreferredSize(new Dimension(180, 0));

        // 垂直排列功能按鈕（9 行，行距 8px）
        setLayout(new GridLayout(9, 1, 8, 8));

        // 內距，讓內容不要貼邊
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        /* ===== 功能按鈕 ===== */

        JButton btnHome = new JButton("主頁面");
        JButton btnSystem = new JButton("系統決策");
        JButton btnWheel = new JButton("決策輪");
        JButton btnHistory = new JButton("歷史紀錄");
        JButton btnLogout = new JButton("登出");

        /* ===== 頁面切換（交給 MainFrame 控制） ===== */

        btnHome.addActionListener(e ->
                mainFrame.showPage(MainFrame1.HOME));

        btnSystem.addActionListener(e ->
                mainFrame.showPage(MainFrame1.SYSTEM));

        btnWheel.addActionListener(e ->
                mainFrame.showPage(MainFrame1.WHEEL));

        btnHistory.addActionListener(e ->
                mainFrame.showPage(MainFrame1.HISTORY));

        /* ===== 登出流程（獨立方法，方便維護） ===== */

        btnLogout.addActionListener(e -> attemptLogout());

        /* ===== 組裝畫面 ===== */

        add(new JLabel("功能選單", SwingConstants.CENTER));
        add(btnHome);
        add(btnSystem);
        add(btnWheel);
        add(btnHistory);
        add(new JLabel("")); // 空白 spacer，讓版面好看
        add(btnLogout);
    }

    /* =====================================================
       登出流程主方法
       1 使用者取消登出
       2 登出過程清除暫存資料失敗
       3 網路中斷或跳轉失敗
       ===================================================== */

    private void attemptLogout() {

        /* ===== 使用者確認是否登出 ===== */

        int choice = JOptionPane.showConfirmDialog(
                this,
                "確定要登出嗎？",
                "登出確認",
                JOptionPane.OK_CANCEL_OPTION
        );

        // 使用者選擇取消 → 直接返回系統
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        /* ===== 嘗試清除使用者登入狀態 ===== */

        try {
            // 登出
            currentUser.logout();
        } catch (Exception ex) {
            // 登出失敗（例如資料清除失敗）
            JOptionPane.showMessageDialog(
                    this,
                    "登出失敗 請稍後再試",
                    "錯誤",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        /* ===== 嘗試跳轉回歡迎頁 ===== */

        boolean success = goToWelcomeSafely();

        if (!success) {
            // 網路或跳轉失敗 → 顯示重試選項
            showNetworkErrorRetryDialog();
        }
    }

    /* =====================================================
       嘗試安全地回到歡迎畫面
       若發生例外，回傳 false 讓上層處理
       ===================================================== */

    private boolean goToWelcomeSafely() {
        try {
            // 關閉目前主視窗
            mainFrame.dispose();

            // 開啟歡迎畫面
            new WelcomeFrame().setVisible(true);
            return true;

        } catch (Exception ex) {
            // 任何例外都視為跳轉失敗
            ex.printStackTrace();
            return false;
        }
    }

    /* =====================================================
       網路異常 → 重試 / 取消流程
       ===================================================== */

    private void showNetworkErrorRetryDialog() {

        Object[] options = {"重試", "取消"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "網路異常 無法登出",
                "錯誤",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) { // 使用者選擇「重試」
            boolean success = goToWelcomeSafely();

            if (!success) {
                // 重試仍失敗
                JOptionPane.showMessageDialog(
                        this,
                        "仍然無法登出，請稍後再試",
                        "錯誤",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
