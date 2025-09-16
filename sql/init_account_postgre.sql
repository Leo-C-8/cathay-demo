-- 檢查 'user_account' 資料表是否存在，若存在則刪除
DROP TABLE IF EXISTS user_account;

-- 創建 'user_account' 資料表
CREATE TABLE user_account (
    -- 'id' 欄位作為主鍵，使用序列自動遞增
    id SERIAL PRIMARY KEY,

    -- 'user_name' 欄位，不可為空，且必須是唯一的
    user_name VARCHAR(255) UNIQUE NOT NULL,

    -- 'password' 欄位，不可為空
    password VARCHAR(255) NOT NULL,

    -- 'create_date' 欄位，自動填入當前時間戳
    create_date TIMESTAMPTZ NOT NULL DEFAULT NOW()
);