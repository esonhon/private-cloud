create table irext_collect.collect_remote
(
	id int auto_increment,
	category_id int null,
	category_name varchar(20) null,
	brand_id int null,
	brand_name varchar(20) null,
	city_code varchar(6) null,
	city_name varchar(20) null,
	operator_id int null,
	operator_name varchar(32) null,
	remote_map varchar(128) null,
	protocol varchar(64) null,
	remote varchar(64) null,
	constraint collect_remote_pk
		primary key (id)
)
comment 'Collected Remote Info';

create table irext_collect.collect_key
(
  id                int auto_increment
    primary key,
  collect_remote_id int         not null,
  key_id            int         not null,
  key_name          varchar(20) not null,
  key_value         text        not null
);
