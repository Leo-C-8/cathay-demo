-- 若資料表存在，先刪除
DROP TABLE IF EXISTS file_info;

-- 若 enum 類型存在，再刪除（此時不會有依賴）
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'thumbnail_status') THEN
        EXECUTE 'DROP TYPE thumbnail_status';
    END IF;
END$$;

-- 建立 enum 類型
CREATE TYPE thumbnail_status AS ENUM (
    'processing',
    'completed',
    'failed'
);

-- 建立資料表
CREATE TABLE file_info (
    id SERIAL PRIMARY KEY,
    file_name TEXT NOT NULL,
    original_file_name TEXT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    thumbnail_status thumbnail_status NOT NULL DEFAULT 'processing',
    thumbnail_download_link TEXT
);