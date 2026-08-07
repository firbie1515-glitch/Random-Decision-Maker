package model;

import db.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * UML: User 類別
 * -------------------------
 * 1. 負責使用者註冊
 * 2. 負責使用者登入驗證
 * 3. 保存目前登入中的使用者狀態
 * 4. 執行登出與清除暫存資料
 */
public class User {

    /* ================= 使用者基本屬性 ================= */

    private String userId;
    private String nickname;
    private String password;
    private boolean isLogin;

    public User() {}

    /* ================= Getter ================= */

    public String getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public boolean isLogin() { return isLogin; }

    /* =========================================================
       註冊功能
       UML: register(...) : String
       ========================================================= */

    public String register(String nickname, String userId, String password) {

        if (nickname == null || nickname.isEmpty() ||
            userId == null || userId.isEmpty() ||
            password == null || password.isEmpty()) {
            return "EMPTY_FIELD";
        }

        String checkSql = "SELECT userId FROM users WHERE userId=?";
        String insertSql =
                "INSERT INTO users(userId,nickname,password) VALUES(?,?,?)";

        try (Connection conn = DBConnector.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return "ID_EXISTS";
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, userId);
                ps.setString(2, nickname);
                ps.setString(3, password);
                ps.executeUpdate();
            }

            return "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            return "DB_ERROR";
        }
    }

    /* =========================================================
       UML: login(userId, password) : String
       ========================================================= */

    public String login(String userId, String password) {

        if (userId == null || userId.isEmpty() ||
            password == null || password.isEmpty()) {
            return "EMPTY_FIELD";
        }

        String sql =
                "SELECT userId, nickname, password FROM users WHERE userId=?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return "ID_NOT_FOUND";
                }

                String dbPass = rs.getString("password");

                if (!dbPass.equals(password)) {
                    return "PASSWORD_ERROR";
                }

                // 登入成功，寫入 User 狀態
                this.userId = rs.getString("userId");
                this.nickname = rs.getString("nickname");
                this.password = dbPass;
                this.isLogin = true;

                return "SUCCESS";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "DB_ERROR";
        }
    }

    /* =========================================================
       登出功能
       ========================================================= */

    public void logout() throws LogoutClearCacheException {
        try {
            this.isLogin = false;
            this.userId = null;
            this.nickname = null;
            this.password = null;
        } catch (Exception ex) {
            throw new LogoutClearCacheException("清除暫存資料失敗", ex);
        }
    }
}

/* =========================================================
   自訂例外類別：登出清除暫存失敗
   ========================================================= */
class LogoutClearCacheException extends Exception {

    public LogoutClearCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogoutClearCacheException(String message) {
        super(message);
    }
}
