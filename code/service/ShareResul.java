package service;

public enum ShareResul {
    SUCCESS,                // 分享成功
    APP_NOT_INSTALLED,      // 裝置不支援此分享方式
    PLATFORM_UNAVAILABLE,   // 平台暫時無法使用
    REDIRECT_FAILED,        // 跳轉失敗
    USER_CANCEL             // 使用者取消
}
