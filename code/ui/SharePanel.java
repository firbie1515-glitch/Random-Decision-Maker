package ui;

import java.awt.*;
import javax.swing.*;
import service.ShareResul;
import service.ShareService;

/**
 * SharePanel
 * -----------------------
 * 分享結果用的對話視窗（Dialog）
 *
 * 功能說明：
 * 1. 顯示決策結果的分享預覽內容
 * 2. 讓使用者選擇分享平台（LINE / FB / X）
 * 3. 支援同一個結果重複分享至不同平台
 * 4. 分享成功後不關閉視窗，使用者可自行選擇關閉
 */
public class SharePanel extends JDialog {

    /**
     * 建構子
     *
     * @param owner     呼叫此視窗的元件（用來定位視窗位置）
     * @param resultText 要分享的決策結果文字
     */
    public SharePanel(Component owner, String resultText) {

        /*
         * 設定 Dialog 的父視窗、標題與模式
         * APPLICATION_MODAL：此視窗開啟時，其他視窗無法操作
         */
        super(
            SwingUtilities.getWindowAncestor(owner),
            "分享結果",
            ModalityType.APPLICATION_MODAL
        );

        // 視窗基本設定
        setSize(520, 360);                 // 視窗大小
        setLocationRelativeTo(owner);      // 視窗置中於呼叫者
        setLayout(new BorderLayout());     // 使用 BorderLayout 排版
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 關閉時釋放資源

        /* ================= Service ================= */

        // 分享邏輯由 ShareService 負責（UI 與邏輯分離）
        ShareService ss = new ShareService();

        /* ================= 預覽內容 ================= */

        // 用來顯示即將分享的文字內容
        JTextArea preview = new JTextArea();
        preview.setLineWrap(true);          // 自動換行
        preview.setWrapStyleWord(true);     // 以單字為單位換行
        preview.setEditable(false);         // 不允許使用者編輯
        preview.setText(
                ss.generateText(resultText) // 由 Service 統一產生分享格式
        );

        /* ================= 分享選項 ================= */

        // 目前只支援文字分享（保留 RadioButton 擴充用）
        JRadioButton rbText = new JRadioButton("文字", true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbText);

        // 平台選擇下拉選單
        JComboBox<String> cbPlatform =
                new JComboBox<>(new String[]{"LINE", "FB", "X"});

        // 分享與關閉按鈕
        JButton btnShare = new JButton("分享");
        JButton btnClose = new JButton("關閉");

        /* ================= 分享流程 ================= */

        /*
         * 使用者點擊「分享」時：
         * 1. 取得選擇的平台
         * 2. 取得預覽文字內容
         * 3. 呼叫 ShareService 執行分享
         * 4. 根據回傳結果顯示對應提示
         */
        btnShare.addActionListener(e -> {
            String platform = (String) cbPlatform.getSelectedItem();
            String content = preview.getText();

            ShareResul result = ss.share(platform, content);

            switch (result) {

                case SUCCESS:
                    // 分享成功：顯示提示，但不關閉視窗
                    JOptionPane.showMessageDialog(
                            this,
                            "分享成功！\n內容已自動帶入或複製至剪貼簿。"
                    );
                    break;

                case APP_NOT_INSTALLED:
                    // 使用者電腦不支援或未安裝對應應用
                    JOptionPane.showMessageDialog(
                            this,
                            "尚未安裝此應用程式，請選擇其他分享方式",
                            "分享失敗",
                            JOptionPane.WARNING_MESSAGE
                    );
                    break;

                case PLATFORM_UNAVAILABLE:
                    // 網路或平台服務暫時不可用
                    JOptionPane.showMessageDialog(
                            this,
                            "目前無法連接此服務，請稍後再試",
                            "服務異常",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;

                case REDIRECT_FAILED:
                    // 瀏覽器跳轉或分享流程發生錯誤
                    JOptionPane.showMessageDialog(
                            this,
                            "分享失敗，請重新嘗試",
                            "錯誤",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;

                case USER_CANCEL:
                    // 使用者取消（目前保留結構，未實作）
                    break;
            }
        });

        // 點擊「關閉」只關閉分享視窗
        btnClose.addActionListener(e -> dispose());

        /* ================= 上方區 ================= */

        // 上方區塊：分享設定（格式 + 平台）
        JPanel top = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));

        left.add(new JLabel("格式："));
        left.add(rbText);
        left.add(new JLabel("平台："));
        left.add(cbPlatform);

        top.add(left, BorderLayout.CENTER);

        /* ================= 下方區 ================= */

        // 下方區塊：操作按鈕
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnShare);
        bottom.add(btnClose);

        /* ================= 組裝 ================= */

        // 將各區塊加入 Dialog
        add(top, BorderLayout.NORTH);                  // 設定區
        add(new JScrollPane(preview), BorderLayout.CENTER); // 預覽內容
        add(bottom, BorderLayout.SOUTH);               // 操作按鈕
    }
}
