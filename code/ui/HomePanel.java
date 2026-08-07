package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

/**
 * HomePanel
 * -----------------------
 * 系統登入後的「主頁面」
 * 負責顯示歡迎訊息，以及三個主要功能入口：
 * 1. 系統決策
 * 2. 決策輪
 * 3. 歷史紀錄
 */
public class HomePanel extends JPanel {

    /** 目前登入的使用者 */
    private final User currentUser;

    /**
     * 建構子
     */
    public HomePanel(User user, MainFrame1 frame) {
        this.currentUser = user;

        // 使用 BorderLayout 作為整體版面
        setLayout(new BorderLayout());

        // 設定整個主頁面的外距（讓畫面不要貼邊）
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        /* ================= 歡迎文字 ================= */

        // 顯示「歡迎回來，使用者暱稱（ID）」
        JLabel hello = new JLabel(
            "歡迎回來，" + currentUser.getNickname() + "（" + currentUser.getUserId() + "）"
        );

        // 設定字型：SansSerif、粗體、18px
        hello.setFont(new Font("SansSerif", Font.BOLD, 18));

        /* ================= 功能卡片區 ================= */

        // 使用 GridLayout，橫向 3 個卡片
        // 每個卡片間距 16px
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));

        /* ===== 系統決策卡片 ===== */
        cards.add(buildCard(
            "系統決策",
            // 卡片說明文字（使用 \n 換行）
            "讓系統幫你選出結果。\n" +
            "可選擇類型（吃/玩/電影/穿搭）。\n" +
            "可新增/刪除資料庫選項。\n" +
            "使用隨機決策或用偏好加權決策得到結果。",
            // 點擊後切換到「系統決策頁面」
            () -> frame.showPage(MainFrame1.SYSTEM)
        ));

        /* ===== 決策輪卡片 ===== */
        cards.add(buildCard(
            "決策輪",
            "彩色的趣味轉盤決策。\n" +
            "自己輸入選項（至少 2 個，至多 15 個）。",
            // 點擊後切換到「決策輪頁面」
            () -> frame.showPage(MainFrame1.WHEEL)
        ));

        /* ===== 歷史紀錄卡片 ===== */
        cards.add(buildCard(
            "歷史紀錄",
            "所有決策（系統/轉盤）都會自動記錄。\n" +
            "可切換近一週/一個月/三個月。\n" +
            "超過三個月自動刪除。",
            // 點擊後切換到「歷史紀錄頁面」
            () -> frame.showPage(MainFrame1.HISTORY)
        ));

        /* ================= 組裝畫面 ================= */

        // 上方：歡迎訊息
        add(hello, BorderLayout.NORTH);

        // 中間：功能卡片
        add(cards, BorderLayout.CENTER);
    }

    /**
     * buildCard
     * -----------------------
     * 建立一個功能卡片（標題 + 說明 + 前往按鈕）
     */
    private JPanel buildCard(String title, String desc, Runnable action) {

        // 使用 BorderLayout：
        // 中：說明文字
        // 下：前往按鈕
        JPanel p = new JPanel(new BorderLayout(8, 8));

        // 使用 TitledBorder 顯示卡片標題
        p.setBorder(BorderFactory.createTitledBorder(title));

        /* ===== 說明文字 ===== */

        JTextArea t = new JTextArea(desc);
        t.setEditable(false);   // 不可編輯
        t.setOpaque(false);     // 透明背景（看起來像標籤）

        /* ===== 前往按鈕 ===== */
        JButton btn = new JButton("前往");

        // 點擊後執行傳入的 action（通常是切頁）
        btn.addActionListener(e -> action.run());

        /* ===== 組裝卡片 ===== */
        p.add(t, BorderLayout.CENTER);
        p.add(btn, BorderLayout.SOUTH);

        return p;
    }
}
