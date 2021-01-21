create table if not exists tb_user (
  id_user int(6) NOT NULL unique key AUTO_INCREMENT,
  uid varchar(100) NOT NULL,
  phone varchar(14) NOT NULL,
  first_name varchar(60) NOT NULL,
  last_name varchar(60) NOT NULL,
  email varchar(100) NOT NULL,
  url_photo varchar(300) DEFAULT NULL,
  city varchar(50) DEFAULT NULL,
  uf varchar(2) NOT NULL,
  date_since longblob NOT NULL,
  cat_default varchar(20) NOT NULL
);
create table if not exists tb_announcement (
  id_announcement int(6) NOT NULL unique key AUTO_INCREMENT,
  situation varchar(10) NOT NULL,
  date_cade LONGBLOB not null,
  status_announce varchar(10) not null,
  title varchar(40) NOT NULL,
  description varchar(1000) NOT NULL,
  price decimal(10,2) DEFAULT NULL,
  phone varchar(13) DEFAULT NULL,
  uf varchar(2) DEFAULT NULL,
  uid varchar(100) not null,
  city varchar(50) DEFAULT NULL,
  id_user int(60) DEFAULT NULL,
  category varchar(20) not null,
  lat double,
  lang double,
  status_item varchar(20) not null,
  KEY fk_tb_user_id_user (id_user),
  CONSTRAINT fk_tb_user_id_user FOREIGN KEY (id_user) REFERENCES tb_user(id_user)
);
 create table if not exists tb_image (
  id_image int(10) NOT NULL unique key AUTO_INCREMENT,
  url_image varchar(1000) NOT NULL,
  id_announcement int(6) DEFAULT NULL,
  KEY fk_tb_announcement_id_announcement(id_announcement),
  CONSTRAINT fk_tb_announcement_id_announcement FOREIGN KEY (id_announcement) REFERENCES tb_announcement (id_announcement)
); 
CREATE TABLE if not exists tb_favourites (
  id_favourite int(6) NOT NULL unique key AUTO_INCREMENT,
  id_user int(6) DEFAULT NULL,
  id_announcement int(6) DEFAULT NULL,
  KEY fk_tb_announcement_tb_favourites_id_announcement (id_announcement),
  KEY fk_tb_user_tb_favourites_id_user (id_user),
  CONSTRAINT fk_tb_announcement_tb_favourites_id_announcement FOREIGN KEY (id_announcement) REFERENCES tb_announcement (id_announcement),
  CONSTRAINT fk_tb_user_tb_favourites_id_user FOREIGN KEY (id_user) REFERENCES tb_user (id_user)
)


