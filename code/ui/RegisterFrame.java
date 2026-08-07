package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

/**
 * RegisterFrame
 * -------------------------
 * 會員註冊畫面（UI）
 * 讓使用者輸入暱稱、帳號 ID 與密碼，並呼叫 User.register() 進行註冊
 *
 * 流程概念：
 * 使用者輸入資料 → 點擊「註冊」
 * → 呼叫 Model（User）
 * → 根據回傳的 RegisterResul 顯示對應訊息
 */
public class RegisterFrame extends JFrame {

    /**
     * 建構子：負責初始化整個註冊畫面
     */
    public RegisterFrame() {

        /* ================= 視窗基本設定 ================= */

        setTitle("會員註冊");                  // 視窗標題
        setSize(520, 360);                     // 視窗大小
        setLocationRelativeTo(null);            // 視窗置中
        setDefaultCloseOperation(EXIT_ON_CLOSE);// 關閉視窗即結束程式


        /* ================= 輸入欄位 ================= */

        // 使用者輸入暱稱
        JTextField tfNickname = new JTextField();

        // 使用者輸入帳號 ID
        JTextField tfUserId = new JTextField();

        // 使用者輸入密碼（使用 JPasswordField 保護顯示）
        JPasswordField pfPassword = new JPasswordField();


        /* ================= 按鈕 ================= */

        JButton btnRegister = new JButton("註冊");
        JButton btnBack = new JButton("返回");


        /* ================= 表單區（中間） ================= */

        // 使用 GridLayout：6 列 1 欄，間距 8px
        JPanel form = new JPanel(new GridLayout(6, 1, 8, 8));

        // 設定表單內距，讓畫面不要貼邊
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // 依序加入標籤與輸入框
        form.add(new JLabel("暱稱："));
        form.add(tfNickname);
        form.add(new JLabel("帳號ID："));
        form.add(tfUserId);
        form.add(new JLabel("密碼："));
        form.add(pfPassword);


        /* ================= 底部按鈕區 ================= */

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBack);
        bottom.add(btnRegister);


        /* ================= 返回按鈕事件 ================= */

        // 點擊「返回」：
        btnBack.addActionListener(e -> {
            dispose();                     // 關閉目前視窗
            new WelcomeFrame().setVisible(true); //回到歡迎頁面
        });


        /* ================= 註冊按鈕事件（核心邏輯） ================= */

        btnRegister.addActionListener(e -> {

            // 取得使用者輸入資料
            String nickname = tfNickname.getText().trim();
            String userId = tfUserId.getText().trim();
            String password = new String(pfPassword.getPassword());

            // 建立 User 物件（Model）
            User u = new User();

            // 呼叫註冊方法，回傳註冊結果
            String result = u.register(nickname, userId, password);

            /* ===== 根據註冊結果進行不同處理 ===== */
            switch (result) {

                // 情況 1：欄位空白
                case "EMPTY_FIELD":
                    JOptionPane.showMessageDialog(
                        this,
                        "請輸入有效資料",
                        "註冊失敗",
                        JOptionPane.WARNING_MESSAGE
                    );
                    break;

                // 情況 2：帳號 ID 已存在
                case "ID_EXISTS":
                    int choice = JOptionPane.showConfirmDialog(
                        this,
                        "此 ID 已被使用，可前往登入頁",
                        "ID 已存在",
                        JOptionPane.YES_NO_OPTION
                    );

                    // 使用者選擇「是」→ 跳轉至登入頁
                    if (choice == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginFrame().setVisible(true);
                    }
                    break;

                // 情況 3：資料庫錯誤
                case "DB_ERROR":
                    JOptionPane.showMessageDialog(
                        this,
                        "註冊失敗，請稍後再試",
                        "系統錯誤",
                        JOptionPane.ERROR_MESSAGE
                    );
                    break;

                // 情況 4：註冊成功
                case "SUCCESS":
                    JOptionPane.showMessageDialog(
                        this,
                        "註冊成功！",
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    // 關閉註冊畫面 → 前往登入頁
                    dispose();
                    new LoginFrame().setVisible(true);
                    break;
            }
        });


        /* ================= 將元件加入視窗 ================= */

        // 上方標題
        add(new JLabel("會員註冊", SwingConstants.CENTER), BorderLayout.NORTH);

        // 中間表單
        add(form, BorderLayout.CENTER);

        // 下方按鈕
        add(bottom, BorderLayout.SOUTH);
    }
}
