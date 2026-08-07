package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

/**
 * LoginFrame
 * -------------------------
 * 1. 提供使用者輸入帳號（ID）與密碼
 * 2. 呼叫 User.login() 進行登入驗證
 * 3. 根據登入結果顯示對應錯誤訊息
 * 4. 登入成功後進入系統主頁面
 */
public class LoginFrame extends JFrame {

    // 建構子：建立登入視窗與介面元件
    public LoginFrame() {

        /* ===== 視窗基本設定 ===== */
        setTitle("會員登入");                 // 視窗標題
        setSize(520, 320);                   // 視窗大小
        setLocationRelativeTo(null);         // 視窗置中
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 關閉視窗即結束程式

        /* ===== 輸入欄位 ===== */

        // 使用者輸入 ID 的文字欄位
        JTextField tfUserId = new JTextField();

        // 使用者輸入密碼的欄位（顯示為 ●）
        JPasswordField pfPassword = new JPasswordField();

        /* ===== 操作按鈕 ===== */

        JButton btnLogin = new JButton("登入"); // 登入按鈕
        JButton btnBack = new JButton("返回");  // 返回歡迎頁面

        /* ===== 表單區塊 ===== */

        // 使用 GridLayout 排列表單欄位
        JPanel form = new JPanel(new GridLayout(4, 1, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // 表單內容
        form.add(new JLabel("ID："));
        form.add(tfUserId);
        form.add(new JLabel("密碼："));
        form.add(pfPassword);

        /* ===== 下方按鈕區 ===== */

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBack);
        bottom.add(btnLogin);

        /* ===== 返回按鈕事件 ===== */
        // 點擊「返回」→ 關閉目前登入視窗 → 回到歡迎畫面
        btnBack.addActionListener(e -> {
            dispose();                      // 關閉登入視窗
            new WelcomeFrame().setVisible(true); // 開啟歡迎頁面
        });

        /* ===== 登入按鈕事件 ===== */
        btnLogin.addActionListener(e -> {

                String userId = tfUserId.getText().trim();
                String password = new String(pfPassword.getPassword());

                User u = new User();
                String result = u.login(userId, password);

                switch (result) {

                    case "SUCCESS":
                        dispose();
                        new MainFrame1(u).setVisible(true);
                        break;

                    case "EMPTY_FIELD":
                        JOptionPane.showMessageDialog(
                                this,
                                "請輸入帳號與密碼",
                                "登入失敗",
                                JOptionPane.WARNING_MESSAGE
                        );
                        break;

                    case "ID_NOT_FOUND":
                        JOptionPane.showMessageDialog(
                                this,
                                "此 ID 不存在",
                                "登入失敗",
                                JOptionPane.WARNING_MESSAGE
                        );
                        break;

                    case "PASSWORD_ERROR":
                        JOptionPane.showMessageDialog(
                                this,
                                "密碼錯誤",
                                "登入失敗",
                                JOptionPane.WARNING_MESSAGE
                        );
                        break;

                    case "DB_ERROR":
                        JOptionPane.showMessageDialog(
                                this,
                                "系統錯誤，請稍後再試",
                                "錯誤",
                                JOptionPane.ERROR_MESSAGE
                        );
                        break;

                    default:
                        JOptionPane.showMessageDialog(
                                this,
                                "未知錯誤",
                                "錯誤",
                                JOptionPane.ERROR_MESSAGE
                        );
                }
        });

        /* ===== 視窗版面配置 ===== */

        // 標題
        add(new JLabel("會員登入", SwingConstants.CENTER), BorderLayout.NORTH);

        // 中央表單
        add(form, BorderLayout.CENTER);

        // 下方按鈕
        add(bottom, BorderLayout.SOUTH);
    }
}
