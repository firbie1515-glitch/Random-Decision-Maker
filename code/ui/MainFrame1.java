package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

/**
 * 系統主視窗（Main Frame）
 * -------------------------
 * 1. 作為整個系統的最外層視窗（JFrame）
 * 2. 左側固定功能選單
 * 3. 右側使用 CardLayout 切換不同功能頁面
 */
public class MainFrame1 extends JFrame {

    /* ================= 頁面代號（CardLayout 使用） ================= */

    // 主頁面
    public static final String HOME = "HOME";

    // 系統決策（資料庫 + 隨機 / 偏好）
    public static final String SYSTEM = "SYSTEM";

    // 決策輪（轉盤）
    public static final String WHEEL = "WHEEL";

    // 歷史紀錄
    public static final String HISTORY = "HISTORY";

    /* ================= 主要版面控制 ================= */

    // 控制右側內容頁面切換的版面配置器
    private CardLayout cardLayout;

    // 右側內容容器（放所有功能頁）
    private JPanel contentPanel;

    public MainFrame1(User user) {

        /* ===== JFrame 基本設定 ===== */
        setTitle("決策系統");
        setSize(1100, 680);
        setLocationRelativeTo(null);              // 視窗置中
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* ===== 左側功能選單（固定不變） ===== */
        // LeftMenuPanel1 負責顯示功能按鈕
        // 點擊後會呼叫 MainFrame1.showPage()
        add(new LeftMenuPanel1(this, user), BorderLayout.WEST);

        /* ===== 右側內容區（會切換） ===== */
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 主頁面
        contentPanel.add(new HomePanel(user, this), HOME);

        // 系統決策（隨機 / 偏好）
        contentPanel.add(new SystemDecisionPanel(user), SYSTEM);

        // 決策輪（轉盤）
        contentPanel.add(new WheelPanelUI(user), WHEEL);

        // 歷史紀錄
        contentPanel.add(new HistoryPanel(user), HISTORY);

        // 將右側內容加入主視窗
        add(contentPanel, BorderLayout.CENTER);

        /* ===== 預設顯示主頁 ===== */
        showPage(HOME);
    }

    /**
     * 切換右側顯示的頁面
     */
    public void showPage(String name) {
        cardLayout.show(contentPanel, name);
    }
}
