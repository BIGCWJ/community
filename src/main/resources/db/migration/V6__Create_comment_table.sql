create table comment
(
	id BIGINT auto_increment primary key,
	parent_id BIGINT not null ,
	type int not null ,
	commentator int not null,
	gmt_create BIGINT not null,
	modified BIGINT not null,
	like_count int default 0
);


