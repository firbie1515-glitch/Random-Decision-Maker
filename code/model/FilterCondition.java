package model;

/**
 * UML: FilterCondition類別
 * --------------------------------------------------
 * 「根據使用者選擇的偏好條件，計算每一個選項的加權分數」
 * 負責「計分規則」。
 * 會被 PreferenceDecision 使用。
 */
public class FilterCondition {

    // 使用者選擇的偏好條件（來自 UI）
    private String preferredLocation;      // 偏好地點（例如：室內 / 室外 / 全部）
    private String preferredTemperature;   // 偏好氣溫（熱 / 冷 / 中等 / 全部）
    private String maxDistance;             // 可接受距離（近 / 中等 / 遠 / 全部）
    private String availableTime;           // 可用時間（時間短 / 中等 / 時間長 / 全部）
    private String budgetLevel;             // 預算（價格低 / 中等 / 價格高 / 全部）
    private String mood;                    // 心情（放鬆 / 有能量 / 懶惰 / 社交 / 全部）

    /**
     * 建構子
     * --------------------------------------------------
     * 在 SystemDecisionPanel 中建立 FilterCondition 時，
     * 會把使用者在下拉選單選的偏好全部傳進來。
     */
    public FilterCondition(String preferredLocation,
                           String preferredTemperature,
                           String maxDistance,
                           String availableTime,
                           String budgetLevel,
                           String mood) {
        this.preferredLocation = preferredLocation;
        this.preferredTemperature = preferredTemperature;
        this.maxDistance = maxDistance;
        this.availableTime = availableTime;
        this.budgetLevel = budgetLevel;
        this.mood = mood;
    }

    /**
     * UML: calculateScore(option:Option) : int
     * --------------------------------------------------
     * 計算「某一個選項」的總加權分數
     * 傳入：
     *  - option：資料庫撈出來的一個 Option
     * 回傳：
     *  - int 分數（分數越高，代表越符合使用者偏好）
     * 設計概念：
     *  - 每一個條件都有不同的權重
     *  - 符合偏好 / 選「全部」 → 加分
     *  - 不符合 → 不加分
     */
    public int calculateScore(Option option) {
        int score = 0;

        // 地點偏好（權重 3）
        score += scoreOne(preferredLocation, option.getLocation(), 3);

        // 氣溫偏好（權重 2）
        score += scoreOne(preferredTemperature, option.getTemperature(), 2);

        // 距離偏好（權重 3）
        score += scoreOne(maxDistance, option.getDistance(), 3);

        // 時間偏好（權重 2）
        score += scoreOne(availableTime, option.getTimeCost(), 2);

        // 預算偏好（權重 3）
        score += scoreOne(budgetLevel, option.getPriceLevel(), 3);

        // 心情偏好（權重 2）
        score += scoreOne(mood, option.getMood(), 2);

        return score;
    }

    /**
     * scoreOne
     * --------------------------------------------------
     * 單一條件的計分邏輯
     * 傳入：
     *  - preferred：使用者選的偏好
     *  - actual：資料庫中該選項的實際值
     *  - weight：此條件的權重
     * 回傳：
     *  - 符合 → 回傳權重
     *  -  選「全部」 → 回傳權重2
     *  - 不符合 → 回傳 0
     */
    private int scoreOne(String preferred, String actual, int weight) {

        // 使用者選「全部」→ 加2分
        if ("全部".equals(preferred)) return 2;

        // 資料庫值為空 → 不加分
        if (actual == null) return 0;

        // 偏好與實際值完全相同 → 加權分數
        return preferred.equals(actual) ? weight : 0;
    }
}
