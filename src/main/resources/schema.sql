DROP TABLE IF EXISTS "FRIENDS" CASCADE;
DROP TABLE IF EXISTS "FILM_GENRES" CASCADE;
DROP TABLE IF EXISTS "FILM_LIKES" CASCADE;
DROP TABLE IF EXISTS "USERS" CASCADE;
DROP TABLE IF EXISTS "FILMS" CASCADE;
DROP TABLE IF EXISTS "RATING_MPA" CASCADE;
DROP TABLE IF EXISTS "GENRES" CASCADE;
DROP TABLE IF EXISTS "REVIEWS" CASCADE;
DROP TABLE IF EXISTS "REVIEW_LIKES" CASCADE;
DROP TABLE IF EXISTS "REVIEW_DISLIKES" CASCADE;
DROP TABLE IF EXISTS "DIRECTORS" CASCADE;
DROP TABLE IF EXISTS "FILM_DIRECTORS" CASCADE;


-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email varchar(50) NOT NULL,
	login varchar(50) NOT NULL,
	name varchar(50) NOT NULL,
	birthday timestamp NOT NULL,
	CONSTRAINT not_blank CHECK (email <> '' AND login <> '')
);

-- Создание таблицы friends
CREATE TABLE IF NOT EXISTS friends (
	user_id INTEGER REFERENCES users (id),
	friend_id INTEGER REFERENCES users (id),
	status boolean NOT NULL DEFAULT true,
	CONSTRAINT pk_friends PRIMARY KEY(user_id, friend_id)
);

-- Создание таблицы rating_MPA
CREATE TABLE IF NOT EXISTS rating_MPA (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(5) NOT NULL,
	CONSTRAINT rating_not_blank CHECK (name <> '')
);

-- Создание таблицы films
CREATE TABLE IF NOT EXISTS films (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(200) NOT NULL,
	description varchar(200),
	release_date timestamp NOT NULL,
	duration INTEGER NOT NULL,
	likes INTEGER DEFAULT 0,
	rating_id INTEGER REFERENCES rating_MPA (id),
	CONSTRAINT name_not_blank CHECK (name <> ''),
	CONSTRAINT positive CHECK (duration > 0)
);

-- Создание таблицы film_likes
CREATE TABLE IF NOT EXISTS film_likes (
	film_id INTEGER REFERENCES films (id) ON DELETE CASCADE,
	user_id INTEGER REFERENCES users (id),
	CONSTRAINT pk_film_likes PRIMARY KEY(film_id, user_id)

);

--Создание таблицы genres
CREATE TABLE IF NOT EXISTS genres (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(20) UNIQUE NOT NULL,
	CONSTRAINT genre_name_not_blank CHECK (name <> '')
);

-- Создание таблицы film_genres
CREATE TABLE IF NOT EXISTS film_genres (
	film_id INTEGER REFERENCES films (id) ON DELETE CASCADE,
	genre_id INTEGER REFERENCES genres (id),
	CONSTRAINT pk_film_genres PRIMARY KEY(film_id, genre_id)
);

-- Создание таблицы film_genres
CREATE TABLE IF NOT EXISTS reviews (
   id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   content varchar(1000),
   is_positive BOOLEAN,
   user_id BIGINT REFERENCES users (id),
   film_id BIGINT REFERENCES films (id) ON DELETE CASCADE,
   useful INTEGER DEFAULT 0

   CONSTRAINT content_not_blank CHECK (reviews.content <> '')
);

-- Создание таблицы review_likes
CREATE TABLE IF NOT EXISTS review_likes (
    review_id INTEGER REFERENCES reviews (id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (id),

    CONSTRAINT pk_review_likes PRIMARY KEY(review_id, user_id)
);

-- Создание таблицы review_dislikes
CREATE TABLE IF NOT EXISTS review_dislikes (
    review_id INTEGER REFERENCES reviews (id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (id),
    CONSTRAINT pk_review_dislikes PRIMARY KEY(review_id, user_id)
);

-- Создание таблицы directors
CREATE TABLE IF NOT EXISTS directors (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(100) NOT NULL,
	CONSTRAINT directors_name_not_blank CHECK (name <> '')
);

-- Создание таблицы film_directors
CREATE TABLE IF NOT EXISTS film_directors (
    film_id INTEGER REFERENCES films (id) ON DELETE CASCADE,
    director_id BIGINT REFERENCES directors (id) ON DELETE CASCADE,
    CONSTRAINT pk_film_directors PRIMARY KEY(film_id, director_id)
);

