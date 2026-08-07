package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.HistoryRecord;
import model.User;
import model.WheelDecision;

/**
 * 決策輪版面設定
 * -------------------------
 * 1. 可編輯標題
 * 2. 選項欄位
 * 3. 轉盤繪製
 * 4. 旋轉動畫
 */
public class WheelPanelUI extends JPanel {

    private final User currentUser;
    private final WheelDecision wheelDecision;

    private DefaultListModel<String> optionModel;
    private JList<String> optionList;
    private WheelCanvas wheelCanvas;

    private JTextField titleField;   // 可編輯標題

    private Timer spinTimer;
    private double angle = 0;
    private double speed;

    private String lastResult = null;

    public WheelPanelUI(User user) {
        this.currentUser = user;
        this.wheelDecision = new WheelDecision("決策輪");

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initDefaultOptions();

        // 標題區
        add(buildTitlePanel(), BorderLayout.NORTH);

        wheelCanvas = new WheelCanvas();
        add(wheelCanvas, BorderLayout.CENTER);
        add(buildOptionPanel(), BorderLayout.EAST);
    }

    /* ================= 標題區 ================= */

    private JPanel buildTitlePanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));

        JLabel label = new JLabel("轉盤標題：");

        titleField = new JTextField(wheelDecision.getTitle());
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 18));

        // 標題即時同步到 WheelDecision
        titleField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                wheelDecision.setTitle(titleField.getText().trim());
                wheelCanvas.repaint();
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        p.add(label, BorderLayout.WEST);
        p.add(titleField, BorderLayout.CENTER);

        return p;
    }

    /* ================= 初始化 ================= */

    private void initDefaultOptions() {
        wheelDecision.addOption("請輸入選項內容");
        wheelDecision.addOption("請輸入選項內容");
    }

    /* ================= 右側選項欄 ================= */

    private JPanel buildOptionPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setPreferredSize(new Dimension(320, 0));
        p.setBorder(BorderFactory.createTitledBorder("選項列表（自動編號）"));

        optionModel = new DefaultListModel<>();
        refreshOptionModel();

        optionList = new JList<>(optionModel);
        optionList.setFont(new Font("SansSerif", Font.BOLD, 16));

        optionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editOption();
            }
        });

        JButton add = new JButton("新增");
        JButton edit = new JButton("編輯");
        JButton remove = new JButton("刪除");
        JButton spin = new JButton("開始旋轉");
        JButton share = new JButton("分享結果");

        add.addActionListener(e -> addOption());
        edit.addActionListener(e -> editOption());
        remove.addActionListener(e -> removeOption());
        spin.addActionListener(e -> startSpin());
        share.addActionListener(e -> shareResult());

        JPanel btns = new JPanel(new GridLayout(5, 1, 8, 8));
        btns.add(add);
        btns.add(edit);
        btns.add(remove);
        btns.add(spin);
        btns.add(share);

        p.add(new JScrollPane(optionList), BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);

        return p;
    }

    private void refreshOptionModel() {
        optionModel.clear();
        int idx = 1;
        for (WheelDecision.WheelOption o : wheelDecision.getWheelOptions()) {
            optionModel.addElement(idx + ". " + o.getContent());
            idx++;
        }

        angle = 0;
        wheelCanvas.repaint();
    }

    /* ================= 選項操作 ================= */

    private void addOption() {
        if (!wheelDecision.canAddOption()) {
            JOptionPane.showMessageDialog(this,
                    "選項數量已達上限（" + WheelDecision.MAX_OPTIONS + "）");
            return;
        }

        String input = JOptionPane.showInputDialog(this, "輸入新選項內容：");
        if (input == null || input.trim().isEmpty()) return;

        wheelDecision.addOption(input.trim());
        refreshOptionModel();
    }

    private void editOption() {
        int idx = optionList.getSelectedIndex();
        if (idx < 0) return;

        WheelDecision.WheelOption target =
                wheelDecision.getWheelOptions().get(idx);

        String input = JOptionPane.showInputDialog(
                this, "編輯選項內容：", target.getContent());

        if (input == null || input.trim().isEmpty()) return;

        target.setContent(input.trim());
        refreshOptionModel();
    }

    private void removeOption() {
        int idx = optionList.getSelectedIndex();
        if (idx < 0) return;

        if (!wheelDecision.canRemoveOption()) {
            JOptionPane.showMessageDialog(this,
                    "轉盤至少需要 2 個選項");
            return;
        }

        wheelDecision.getWheelOptions().remove(idx);
        refreshOptionModel();
    }

    /* ================= 分享 ================= */

    private void shareResult() {
        if (lastResult == null) {
            JOptionPane.showMessageDialog(this,
                    "請先進行一次決策後再分享");
            return;
        }
        new SharePanel(this,
                "【" + wheelDecision.getTitle() + "】\n< " + lastResult + " >")
                .setVisible(true);
    }

    /* ================= 旋轉動畫 ================= */

    private void startSpin() {
        if (spinTimer != null && spinTimer.isRunning()) return;

        if (wheelDecision.getWheelOptions().size() < 2) {
            JOptionPane.showMessageDialog(this, "選項不足，無法旋轉");
            return;
        }

        speed = 0.4 + new Random().nextDouble() * 0.3;

        spinTimer = new Timer(16, e -> {
            angle += speed;
            speed *= 0.985;
            wheelCanvas.repaint();

            if (speed < 0.002) {
                spinTimer.stop();
                finishSpin();
            }
        });
        spinTimer.start();
    }

    private void finishSpin() {
        int n = wheelDecision.getWheelOptions().size();
        if (n < 2) return;

        double rotationDeg = Math.toDegrees(angle);
        rotationDeg = ((rotationDeg % 360) + 360) % 360;

        double arc = 360.0 / n;
        double hitDeg = (90 - rotationDeg + 360) % 360;
        int index = (int) (hitDeg / arc);

        WheelDecision.WheelOption result =
                wheelDecision.getWheelOptions().get(index);

        lastResult = result.getContent();

        JOptionPane.showMessageDialog(
                this,
                "【" + wheelDecision.getTitle() + "】\n" + lastResult,
                "決策結果",
                JOptionPane.INFORMATION_MESSAGE
        );

        new HistoryRecord(
                currentUser.getUserId(),
                "決策輪",
                wheelDecision.getTitle() + "：" + lastResult,
                LocalDateTime.now()
        ).save();
    }

    /* ================= 轉盤繪製 ================= */

    private class WheelCanvas extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            /* ===== 標題 ===== */
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            FontMetrics titleFm = g2.getFontMetrics();
            String title = wheelDecision.getTitle();
            g2.drawString(title,
                    (w - titleFm.stringWidth(title)) / 2,
                    28);

            int r = Math.min(w, h) / 2 - 40;
            int cx = w / 2;
            int cy = h / 2 + 10;

            int n = wheelDecision.getWheelOptions().size();
            if (n == 0) return;

            double rotation = Math.toDegrees(angle);
            double arc = 360.0 / n;

            double startAngle = rotation;
            double accumulated = 0;

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < n; i++) {

                // ⭐ 最後一塊吃掉剩餘角度，避免白邊
                double sweep = (i == n - 1)
                        ? 360.0 - accumulated
                        : arc;

                accumulated += sweep;

                // ===== 畫扇形 =====
                g2.setColor(Color.getHSBColor(i / (float) n, 0.6f, 0.9f));
                g2.fillArc(
                        cx - r, cy - r,
                        r * 2, r * 2,
                        (int) Math.round(startAngle),
                        (int) Math.round(sweep)
                );

                // ===== 畫編號 =====
                double mid = startAngle + sweep / 2.0;
                double rad = Math.toRadians(mid);

                int tx = cx + (int) (Math.cos(rad) * r * 0.65);
                int ty = cy - (int) (Math.sin(rad) * r * 0.65);

                String label = String.valueOf(i + 1);
                g2.setColor(Color.BLACK);
                g2.drawString(
                        label,
                        tx - fm.stringWidth(label) / 2,
                        ty + fm.getAscent() / 2
                );

                startAngle += sweep;
            }

            /* ===== 指針 ===== */
            g2.setColor(Color.RED);
            g2.fillPolygon(
                    new int[]{cx, cx - 12, cx + 12},
                    new int[]{cy - r - 2, cy - r - 22, cy - r - 22},
                    3
            );

            /* ===== 中心圓 ===== */
            g2.setColor(Color.WHITE);
            g2.fillOval(cx - 18, cy - 18, 36, 36);
            g2.setColor(Color.GRAY);
            g2.drawOval(cx - 18, cy - 18, 36, 36);
        }
    }

}
