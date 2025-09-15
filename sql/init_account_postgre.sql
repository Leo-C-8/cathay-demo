-- 如果 enum 類型已存在，先刪除再重建
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'thumbnail_status') THEN
        DROP TYPE thumbnail_status;
    END IF;
END$$;

-- 建立 enum 類型
CREATE TYPE thumbnail_status AS ENUM (
    'processing',
    'completed',
    'failed'
);

-- 如果資料表已存在，先刪除再重建
DROP TABLE IF EXISTS file_info;

-- 建立資料表
CREATE TABLE file_info (
    id SERIAL PRIMARY KEY,
    file_name TEXT NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    thumbnail_status thumbnail_status NOT NULL DEFAULT 'processing',
    thumbnail_download_link TEXT
);
