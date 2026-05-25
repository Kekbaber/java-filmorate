/* ------------- Справочник жанров ------------- */
CREATE TABLE IF NOT EXISTS genres (
    id   SMALLINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

/*  Если запись уже существует – она не будет вставлена   */
MERGE INTO genres (name) KEY(name) VALUES
    ('COMEDY'), ('DRAMA'), ('ANIMATION'),
    ('THRILLER'), ('DOCUMENTARY'), ('ACTION');

/* ------------- Справочник рейтингов ------------- */
CREATE TABLE IF NOT EXISTS ratings (
    id   SMALLINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

MERGE INTO ratings (name) KEY(name) VALUES
    ('G'), ('PG'), ('PG_13'), ('R'), ('NC_17');

/* ------------- Таблица фильмов ------------- */
CREATE TABLE IF NOT EXISTS films (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(200),                     -- максимум 200 символов
    release_date DATE NOT NULL,
    duration     BIGINT NOT NULL,
    genre_id     SMALLINT,
    rating_id    SMALLINT,
    CONSTRAINT fk_films_genre  FOREIGN KEY (genre_id)  REFERENCES genres(id)  ON DELETE SET NULL,
    CONSTRAINT fk_films_rating FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE SET NULL,
    CONSTRAINT chk_release_date CHECK (release_date >= DATE '1895-12-28'),
    CONSTRAINT chk_duration     CHECK (duration > 0)
);

/* ------------- Таблица пользователей ------------- */
CREATE TABLE IF NOT EXISTS users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,     -- уникальный email
    login    VARCHAR(255) NOT NULL UNIQUE,     -- уникальный логин (приложением проверяем без пробелов)
    name     VARCHAR(255),
    birthday DATE NOT NULL,
    CONSTRAINT chk_birthday CHECK (birthday <= CURRENT_DATE)
);

/* ------------- Таблица лайков ------------- */
CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_likes_film FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

/* ------------- Таблица дружбы ------------- */
CREATE TABLE IF NOT EXISTS friendships (
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    confirmed BOOLEAN DEFAULT FALSE,            -- подтверждено или нет
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friendship_user   FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_not_self CHECK (user_id <> friend_id)
);

/* ------------- Индексы ------------- */
CREATE INDEX IF NOT EXISTS idx_friendships_user_id ON friendships(user_id);
CREATE INDEX IF NOT EXISTS idx_friendships_friend_id ON friendships(friend_id);