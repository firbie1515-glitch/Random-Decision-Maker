import javax.swing.*;
import ui.WelcomeFrame;

// 程式進入點
public class Main {
    public static void main(String[] args) {
        // 把建立顯示UI交給Swing的EDT做
        SwingUtilities.invokeLater(() -> {
            // 設定外觀
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            // 建立物件，畫面顯示
            new WelcomeFrame().setVisible(true);
        });
    }
}



