CREATE DATABASE IF NOT EXISTS dbproject DEFAULT CHARACTER SET utf8mb4;
USE dbproject;

-- 使用者
CREATE TABLE IF NOT EXISTS users (
  userId VARCHAR(50) PRIMARY KEY,
  nickname VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL
);

INSERT INTO users (userId, nickname, password)
VALUES ('SYSTEM', '系統預設', 'SYSTEM');


-- 系統選項資料庫（吃/玩/電影/穿搭）
CREATE TABLE options (
  optionId INT AUTO_INCREMENT PRIMARY KEY,

  userId VARCHAR(50) NOT NULL,              
  optionType VARCHAR(20) NOT NULL,
  name VARCHAR(100) NOT NULL,
  location VARCHAR(10) NOT NULL,
  temperature VARCHAR(10) NOT NULL,
  timeCost VARCHAR(10) NOT NULL,
  distance VARCHAR(10) NOT NULL,
  priceLevel VARCHAR(10) NOT NULL,
  mood VARCHAR(10) NOT NULL,

  CONSTRAINT fk_options_user
    FOREIGN KEY (userId)
    REFERENCES users(userId)
    ON DELETE CASCADE
);


-- 歷史紀錄（包含 系統決策/轉盤）
CREATE TABLE history_records (
  recordId INT AUTO_INCREMENT PRIMARY KEY,

  userId VARCHAR(50) NOT NULL,               
  decisionType VARCHAR(20) NOT NULL,
  result VARCHAR(255) NOT NULL,
  decisionTime DATETIME NOT NULL,

  CONSTRAINT fk_history_user
    FOREIGN KEY (userId)
    REFERENCES users(userId)
    ON DELETE CASCADE
);


-- 預設選項
INSERT INTO options
(userId, optionType, name, location, temperature, timeCost, distance, priceLevel, mood)
VALUES
('SYSTEM','吃什麼','火鍋','室內','冷','中等','近','中等','放鬆'),
('SYSTEM','吃什麼','燒肉','室內','冷','時間長','中等','價位高','社交'),
('SYSTEM','吃什麼','拉麵','室內','冷','時間短','近','價位低','懶惰'),
('SYSTEM','吃什麼','便當','室內','全部','時間短','近','價位低','懶惰'),
('SYSTEM','吃什麼','早午餐','室內','中等','中等','近','中等','放鬆'),
('SYSTEM','吃什麼','咖啡廳','室內','全部','中等','近','中等','放鬆'),
('SYSTEM','吃什麼','夜市小吃','室外','熱','中等','近','價位低','有能量'),
('SYSTEM','吃什麼','義大利麵','室內','中等','中等','近','中等','社交'),
('SYSTEM','吃什麼','壽司','室內','中等','中等','中等','價位高','放鬆'),
('SYSTEM','吃什麼','速食','室內','全部','時間短','近','價位低','懶惰'),

('SYSTEM','去哪玩','海邊散步','室外','熱','中等','中等','價位低','放鬆'),
('SYSTEM','去哪玩','登山健行','室外','中等','時間長','遠','價位低','有能量'),
('SYSTEM','去哪玩','逛百貨公司','室內','全部','時間長','近','中等','社交'),
('SYSTEM','去哪玩','公園野餐','室外','中等','中等','近','價位低','放鬆'),
('SYSTEM','去哪玩','泡溫泉','室內','冷','時間長','遠','價位高','放鬆'),
('SYSTEM','去哪玩','美術館','室內','全部','中等','中等','中等','放鬆'),
('SYSTEM','去哪玩','電玩中心','室內','全部','中等','近','中等','有能量'),
('SYSTEM','去哪玩','市集','室外','熱','中等','近','遠','社交'),
('SYSTEM','去哪玩','書店','室內','全部','時間短','近','價位低','懶惰'),
('SYSTEM','去哪玩','看夜景','室外','冷','中等','遠','價位低','放鬆'),

('SYSTEM','看電影','愛情片','室內','全部','時間長','近','中等','放鬆'),
('SYSTEM','看電影','喜劇片','室內','全部','時間長','近','中等','懶惰'),
('SYSTEM','看電影','動作片','室內','全部','時間長','近','中等','有能量'),
('SYSTEM','看電影','恐怖片','室內','全部','時間長','近','中等','社交'),
('SYSTEM','看電影','動畫片','室內','全部','時間長','近','中等','社交'),
('SYSTEM','看電影','紀錄片','室內','全部','時間長','近','價位低','放鬆'),
('SYSTEM','看電影','藝術電影','室內','全部','時間長','中等','中等','放鬆'),
('SYSTEM','看電影','重看老片','室內','全部','時間長','近','價位低','懶惰'),
('SYSTEM','看電影','電影院','室內','全部','時間長','中等','中等','社交'),

('SYSTEM','穿搭','冬日保暖穿搭','室外','冷','全部','全部','中等','懶惰'),
('SYSTEM','穿搭','夏日休閒穿搭','室外','熱','全部','全部','中等','放鬆'),
('SYSTEM','穿搭','正式上班穿搭','室內','中等','全部','全部','價位高','社交'),
('SYSTEM','穿搭','約會穿搭','室內','中等','全部','全部','價位高','社交'),
('SYSTEM','穿搭','運動風穿搭','室外','中等','全部','全部','中等','有能量'),
('SYSTEM','穿搭','宅家舒適穿搭','室內','全部','全部','全部','價位低','懶惰'),
('SYSTEM','穿搭','文清風穿搭','室內','中等','全部','全部','中等','放鬆'),
('SYSTEM','穿搭','街頭潮流風穿搭','室外','中等','全部','全部','價位高','有能量'),
('SYSTEM','穿搭','旅行穿搭','室外','熱','全部','全部','中等','放鬆'),
('SYSTEM','穿搭','全黑簡約穿搭','全部','全部','全部','全部','中等','社交');

