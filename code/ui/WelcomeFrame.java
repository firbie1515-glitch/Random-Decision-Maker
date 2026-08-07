package ui;

import java.awt.*;
import javax.swing.*;

/**
 * 歡迎頁面
 * -------------------------
 * 1. 顯示系統標題
 * 2. 提供「登入」與「註冊」入口
 * 3. 負責導向對應畫面
 */
public class WelcomeFrame extends JFrame {

    /**
     * 建構子：建立並初始化歡迎畫面 UI
     */
    public WelcomeFrame() {

        /* ===== 視窗基本設定 ===== */

        // 設定視窗標題
        setTitle("歡迎大廳");

        // 設定視窗大小（寬 x 高）
        setSize(520, 320);

        // 視窗顯示在螢幕正中央
        setLocationRelativeTo(null);

        // 關閉視窗時，結束整個程式
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /* ===== 標題區 ===== */

        // 建立系統標題文字，置中顯示
        JLabel title = new JLabel("懶人決策器", SwingConstants.CENTER);

        // 設定字型（字體、粗體、大小）
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        /* ===== 功能按鈕 ===== */

        // 建立「會員登入」按鈕
        JButton btnLogin = new JButton("會員登入");

        // 建立「會員註冊」按鈕
        JButton btnRegister = new JButton("會員註冊");

        /* ===== 按鈕事件處理 ===== */

        // 點擊「會員登入」
        btnLogin.addActionListener(e -> {
            // 關閉目前歡迎頁面
            dispose();

            // 開啟登入畫面
            new LoginFrame().setVisible(true);
        });

        // 點擊「會員註冊」
        btnRegister.addActionListener(e -> {
            // 關閉目前歡迎頁面
            dispose();

            // 開啟註冊畫面
            new RegisterFrame().setVisible(true);
        });

        /* ===== 中央容器（放按鈕） ===== */

        // 使用 GridLayout：2 行 1 列，按鈕垂直排列
        JPanel center = new JPanel(new GridLayout(2, 1, 12, 12));

        // 設定內距（上、左、下、右）
        center.setBorder(
                BorderFactory.createEmptyBorder(30, 80, 30, 80)
        );

        // 將按鈕加入容器
        center.add(btnLogin);
        center.add(btnRegister);

        /* ===== 將元件加入視窗 ===== */

        // 標題放在上方（North）
        add(title, BorderLayout.NORTH);

        // 按鈕區放在中央（Center）
        add(center, BorderLayout.CENTER);
    }
}
