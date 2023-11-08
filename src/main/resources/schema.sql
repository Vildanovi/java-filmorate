CREATE TABLE if not exists film_mpa_rating (
  id int not null PRIMARY KEY auto_increment,
  name varchar(10),
  description varchar(255)
);

CREATE TABLE if not exists genre (
  id int PRIMARY KEY not null auto_increment,
  name varchar(50)
);

CREATE TABLE if not exists films (
  id int PRIMARY KEY not null auto_increment,
  name varchar(50) NOT NULL,
  description varchar(200),
  release_date date NOT NULL,
  duration int,
  rating_mpa_id int references film_mpa_rating (id),
  created_date timestamp,
  last_updated timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE if not exists film_genre (
  film_id int references films (id),
  genre_id int references genre (id),
  last_updated timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE if not exists users (
  id int PRIMARY KEY,
  email varchar(50) NOT NULL,
  login varchar(50),
  name varchar(50),
  birthday date,
  created_date timestamp,
  last_updated timestamp DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (email),
  UNIQUE (login)
);

CREATE TABLE if not exists user_film_likes (
  film_id int references films (id),
  user_id int references users (id),
  created_date timestamp,
  PRIMARY KEY (film_id, user_id)
);

CREATE TABLE if not exists friends_status (
  status_id int PRIMARY KEY auto_increment,
  name varchar(20)
);

CREATE TABLE if not exists user_friends (
  user1_id int references users (id),
  user2_id int references users (id),
  initiator_id int references users (id),
  status_id int references friends_status (status_id),
  PRIMARY KEY (user1_id, user2_id)
);
