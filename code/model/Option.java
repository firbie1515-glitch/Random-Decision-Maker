package model;

/**
 * UML: Option 類別
 * --------------------------------------------------
 * 代表一個「可被決策的選項」
 */
public class Option {

    /* ===== 使用者識別 ===== */

    // 此選項所屬的使用者（SYSTEM 或 實際會員 ID）
    private String userId;

    /* ===== 基本識別資料 ===== */

    private int optionId;
    private String optionType;
    private String name;

    /* ===== 決策屬性 ===== */

    private String location;
    private String temperature;
    private String timeCost;
    private String distance;
    private String priceLevel;
    private String mood;

    public Option() {}

    /**
     * 完整建構子（含 userId）
     */
    public Option(int optionId, String userId, String optionType, String name,
                  String location, String temperature, String timeCost,
                  String distance, String priceLevel, String mood) {

        this.optionId = optionId;
        this.userId = userId;
        this.optionType = optionType;
        this.name = name;
        this.location = location;
        this.temperature = temperature;
        this.timeCost = timeCost;
        this.distance = distance;
        this.priceLevel = priceLevel;
        this.mood = mood;
    }

    /* ===== Getter ===== */

    public String getUserId() { return userId; }
    public int getOptionId() { return optionId; }
    public String getOptionType() { return optionType; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getTemperature() { return temperature; }
    public String getTimeCost() { return timeCost; }
    public String getDistance() { return distance; }
    public String getPriceLevel() { return priceLevel; }
    public String getMood() { return mood; }

    /**
     * UML: getDescription() : String
     */
    public String getDescription() {
        return String.format(
                "< %s >\n" +
                "｜地點:%s｜氣溫:%s｜時間:%s｜\n" +
                "｜距離:%s｜價位:%s｜情緒:%s｜",
                name,
                location,
                temperature,
                timeCost,
                distance,
                priceLevel,
                mood
        );
    }
}
