create table question
(
	id int auto_increment primary key,
	title VARCHAR(50),
	description text,
	gmt_create BIGINT,
	gmt_modified BIGINT,
	creator int,
	comment_count int default 0,
	view_count int default 0,
	like_count int default 0
);

