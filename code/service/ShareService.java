package service;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URI;

/**
 * UML: ShareService 類別
 * -------------------------------------------------
 * 「分享決策結果」的實際行為，
 * 1. 將分享內容整理成可分享文字
 * 2. 複製文字到剪貼簿
 * 3. 根據不同平台產生對應的分享網址
 * 4. 透過瀏覽器開啟分享頁面
 */
public class ShareService {

    /**
     * 建構子
     * 目前不需要初始化任何狀態，
     * 保留是為了未來擴充（例如設定分享參數）
     */
    public ShareService() {}

    /**
     * UML: generateText(result:String) : String
     * -------------------------------------------------
     * 將決策結果包裝成「可分享文字格式」
     */
    public String generateText(String result) {
        return "我的決策結果是：\n"
                + result
                + "\n#懶人決策器";
    }

    /**
     * UML: share(platform:String, content:String) : boolean
     * -------------------------------------------------
     * 根據使用者選擇的平台，執行實際的分享動作
     */
    public ShareResul share(String platform, String content) {
        try {
            /* ================= 檢查系統是否支援 Desktop ================= */

            // 檢查目前環境是否支援 Desktop（某些系統或安全環境可能不支援）
            if (!Desktop.isDesktopSupported()) {
                return ShareResul.APP_NOT_INSTALLED;
            }

            Desktop desktop = Desktop.getDesktop();

            // 檢查是否支援「開啟瀏覽器」
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                return ShareResul.APP_NOT_INSTALLED;
            }

            /* ================= 複製文字到剪貼簿 ================= */

            // 先把分享內容複製到剪貼簿
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(content), null);

            /* ================= 組合分享網址 ================= */

            // URL encode：避免中文、換行導致網址錯誤
            String encoded = java.net.URLEncoder.encode(content, "UTF-8");
            String url;

            // 根據不同平台，產生對應的分享網址
            switch (platform) {
                case "LINE":
                    // LINE 官方桌面版文字分享
                    url = "https://social-plugins.line.me/lineit/share?text=" + encoded;
                    break;

                case "FB":
                    // Facebook 桌面版（文字不一定會自動帶入）
                    url = "https://www.facebook.com/sharer/sharer.php?u=&quote=" + encoded;
                    break;

                case "X":
                    // X（Twitter）文字分享
                    url = "https://twitter.com/intent/tweet?text=" + encoded;
                    break;

                default:
                    // 不支援的平台
                    return ShareResul.PLATFORM_UNAVAILABLE;
            }

            /* ================= 開啟瀏覽器跳轉分享頁 ================= */

            desktop.browse(new URI(url));

            // 如果成功執行到這裡，視為分享成功
            return ShareResul.SUCCESS;

        } catch (java.net.UnknownHostException e) {
            // 網路中斷或 DNS 問題
            return ShareResul.PLATFORM_UNAVAILABLE;

        } catch (Exception e) {
            // 其他未知錯誤（例如 URI 格式錯誤）
            e.printStackTrace();
            return ShareResul.REDIRECT_FAILED;
        }
    }
}
