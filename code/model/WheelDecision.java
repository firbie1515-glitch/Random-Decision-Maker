package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * UML: WheelDecision 類別
 * --------------------------------------------------
 * 1. 管理轉盤上的所有選項（新增 / 刪除）
 * 2. 控制選項數量上限
 * 3. 執行轉盤抽選邏輯（隨機選一個）
 * 4. 記錄決策結果與決策時間
 */
public class WheelDecision extends Decision {

    /** 系統允許的最大選項數量（避免轉盤過於複雜） */
    public static final int MAX_OPTIONS = 15;

    /** 轉盤標題（例如：吃什麼、今天做什麼） */
    private String title;

    /** 轉盤上的所有選項清單 */
    private List<WheelOption> wheelOptions = new ArrayList<>();

    /**
     * 建構子
     */
    public WheelDecision(String title) {
        this.title = title;
        this.decisionType = "轉盤"; 
    }

    /* ================= 基本 getter / setter ================= */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 取得目前轉盤上的所有選項
     */
    public List<WheelOption> getWheelOptions() {
        return wheelOptions;
    }

    /* ================= 狀態判斷 ================= */

    /**
     * 判斷是否已達選項上限
     */
    public boolean isReachMax() {
        return wheelOptions.size() >= MAX_OPTIONS;
    }

    /**
     * 清空所有轉盤選項
     * （例如重新建立轉盤時使用）
     */
    public void clearOptions() {
        wheelOptions.clear();
    }

    /* ================= 選項操作 ================= */

    /**
     * UML: addOption(content:String) : void
     * 新增一個轉盤選項
     * optionId 會自動遞增（1,2,3...）
     */
    public void addOption(String content) {
        // 如果目前沒有選項，從 1 開始編號
        // 否則接續最後一個 optionId
        int nextId = wheelOptions.size() == 0
                ? 1
                : wheelOptions.get(wheelOptions.size() - 1).optionId + 1;

        wheelOptions.add(new WheelOption(nextId, content));
    }

    /**
     * UML: removeOption(optionId:int) : void
     * 根據 optionId 刪除指定選項
     */
    public void removeOption(int optionId) {
        wheelOptions.removeIf(o -> o.optionId == optionId);
    }

    /* ================= 決策核心 ================= */

    /**
     * UML: spinWheel() : String
     * 執行轉盤抽選邏輯
     * 從目前選項中隨機選一個
     */
    public String spinWheel() {
        // 轉盤至少要有 2 個選項
        if (wheelOptions.size() < 2) {
            return "轉盤至少需要 2 個選項";
        }

        // 隨機抽取一個選項
        WheelOption pick =
                wheelOptions.get(new Random().nextInt(wheelOptions.size()));

        // 記錄決策結果與時間（繼承自 Decision）
        this.result = pick.content;
        this.decisionTime = LocalDateTime.now();

        return result;
    }

    /**
     * UML: makeDecision() : String
     * 轉盤模式下，決策即為 spinWheel()
     */
    @Override
    public String makeDecision() {
        return spinWheel();
    }

    /* ================= 內部類別：轉盤選項 ================= */

    /**
     * WheelOption
     * --------------------------------------------------
     * 代表轉盤上的單一選項
     * 僅包含「編號」與「顯示內容」
     */
    public static class WheelOption {

        /** 選項編號（用於顯示與辨識） */
        int optionId;

        /** 選項內容（例如：火鍋、披薩） */
        String content;

        WheelOption(int id, String c) {
            this.optionId = id;
            this.content = c;
        }

        public int getOptionId() {
            return optionId;
        }

        public String getContent() {
            return content;
        }

        /**
         * 修改選項內容（編號不可改）
         */
        public void setContent(String content) {
            this.content = content;
        }
    }

    /* ================= UI 輔助判斷（給 Panel 用） ================= */

    /**
     * 判斷是否還可以新增選項
     */
    public boolean canAddOption() {
        return wheelOptions.size() < MAX_OPTIONS;
    }

    /**
     * 判斷是否還可以刪除選項
     * 轉盤至少保留 2 個選項
     */
    public boolean canRemoveOption() {
        return wheelOptions.size() > 2;
    }
}
