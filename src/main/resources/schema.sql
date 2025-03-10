CREATE TABLE IF NOT EXISTS genres (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    name varchar(256) NOT NULL,
    CONSTRAINT genres_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS ratings (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    name varchar(16) NOT NULL,
    CONSTRAINT ratings_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS films (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    name varchar(256) NOT NULL,
    description varchar(2048),
    release_date varchar(32),
    duration integer,
    rating_id integer references ratings,
    CONSTRAINT films_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS films_by_genres (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    film_id integer references films,
    genre_id integer references genres,
    CONSTRAINT films_by_genres_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    email varchar(128) NOT NULL,
    login varchar(128),
    name varchar(128),
    birthday varchar(32),
    CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS friends (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    user_id integer references users,
    friend_id integer references users,
    request_status varchar(128),
    CONSTRAINT friends_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS likes (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    user_id integer references users,
    film_id integer references films,
    CONSTRAINT likes_pk PRIMARY KEY (id)
);
