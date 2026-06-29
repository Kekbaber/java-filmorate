create TABLE IF NOT EXISTS genres (
    id SMALLINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

merge into genres (name) KEY(name) VALUES
    ('Комедия'), ('Драма'), ('Мультфильм'),
    ('Триллер'), ('Документальный'), ('Боевик');

create TABLE IF NOT EXISTS ratings (
    id SMALLINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

merge into ratings (name) KEY(name) VALUES
    ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

create TABLE IF NOT EXISTS films (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration BIGINT NOT NULL,
    rating_id SMALLINT,
    CONSTRAINT fk_films_rating FOREIGN KEY (rating_id) REFERENCES ratings(id) ON delete SET NULL,
    CONSTRAINT chk_release_date CHECK (release_date >= '1895-12-28'),
    CONSTRAINT chk_duration CHECK (duration > 0)
);

create TABLE IF NOT EXISTS directors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

create TABLE IF NOT EXISTS director_films (
    director_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    PRIMARY KEY (director_id, film_id),
    CONSTRAINT fk_df_director FOREIGN KEY (director_id) REFERENCES directors(id) ON DELETE CASCADE,
    CONSTRAINT fk_df_film FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE
);

create TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL,
    genre_id SMALLINT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_fg_film FOREIGN KEY (film_id) REFERENCES films(id) ON delete CASCADE,
    CONSTRAINT fk_fg_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON delete CASCADE
);

create TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE NOT NULL,
    CONSTRAINT chk_birthday CHECK (birthday <= CURRENT_DATE)
);

create TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_likes_film FOREIGN KEY (film_id) REFERENCES films(id) ON delete CASCADE,
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE
);

create TABLE IF NOT EXISTS friendships (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    confirmed BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friendship_user FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE,
    CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON delete CASCADE,
    CONSTRAINT chk_not_self CHECK (user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(500) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_film FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_likes (
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_like BOOLEAN NOT NULL,
    PRIMARY KEY (review_id, user_id),
    CONSTRAINT fk_rl_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_rl_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

create index IF NOT EXISTS idx_friendships_user_id ON friendships(user_id);
create index IF NOT EXISTS idx_friendships_friend_id ON friendships(friend_id);