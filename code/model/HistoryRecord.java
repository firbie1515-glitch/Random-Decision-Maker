package model;

import db.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * UML: HistoryRecord 類別
 * --------------------------------------------------
 * 1. 儲存每一次決策結果到資料庫（依 userId）
 * 2. 依時間區間查詢該使用者的歷史紀錄
 * 3. 清除該使用者的歷史資料
 * 4. 自動清除超過指定月份的歷史紀錄
 */
public class HistoryRecord {

    /* ================= 屬性 ================= */

    private int recordId;
    private String userId;
    private String decisionType;
    private String result;
    private LocalDateTime decisionTime;

    /* ================= 建構子 ================= */

    public HistoryRecord(String userId,
                         String decisionType,
                         String result,
                         LocalDateTime decisionTime) {

        this.userId = userId;
        this.decisionType = decisionType;
        this.result = result;
        this.decisionTime = decisionTime;
    }

    /* ================= Getter ================= */

    public String getUserId() { return userId; }
    public String getDecisionType() { return decisionType; }
    public String getResult() { return result; }
    public LocalDateTime getDecisionTime() { return decisionTime; }

    /* ================= 資料庫操作 ================= */

    /**
     * UML: save() : boolean
     * 將目前這一筆歷史紀錄寫入資料庫
     */
    public boolean save() {

        String sql =
            "INSERT INTO history_records(userId, decisionType, result, decisionTime) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, decisionType);
            ps.setString(3,
                    result.length() > 250 ? result.substring(0, 250) : result
            );
            ps.setString(4,
                    decisionTime.format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UML: clearAll(userId:String) : boolean
     * 清除該使用者所有歷史紀錄
     */
    public static boolean clearAll(String userId) {

        String sql = "DELETE FROM history_records WHERE userId=?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UML: cleanupOverMonths(months:int) : void
     * --------------------------------------------------
     * 自動清除「超過指定月份」的歷史紀錄
     */
    public static void cleanupOverMonths(String userId, int months) {

        String sql =
            "DELETE FROM history_records " +
            "WHERE userId=? AND decisionTime < (NOW() - INTERVAL ? MONTH)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setInt(2, months);
            ps.executeUpdate();

        } catch (Exception e) {
            // 清除失敗不影響使用者操作，只記錄錯誤
            e.printStackTrace();
        }
    }

    /**
     * UML: findByRange(userId:String, range:String) : List<HistoryRecord>
     * 依時間範圍查詢歷史紀錄
     */
    public static List<HistoryRecord> findByRange(String userId, String range) {

        List<HistoryRecord> list = new ArrayList<>();

        String sql =
            "SELECT decisionType, result, decisionTime " +
            "FROM history_records WHERE userId=? ";

        if ("近一周".equals(range)) {
            sql += "AND decisionTime >= (NOW() - INTERVAL 7 DAY) ";
        } else if ("近一個月".equals(range)) {
            sql += "AND decisionTime >= (NOW() - INTERVAL 1 MONTH) ";
        } else if ("近三個月".equals(range)) {
            sql += "AND decisionTime >= (NOW() - INTERVAL 3 MONTH) ";
        }

        sql += "ORDER BY decisionTime DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new HistoryRecord(
                            userId,
                            rs.getString("decisionType"),
                            rs.getString("result"),
                            rs.getTimestamp("decisionTime").toLocalDateTime()
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
