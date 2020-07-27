alter table QUESTION alter column like_count BIGINT DEFAULT NOT NULL;

alter table QUESTION alter column view_count BIGINT DEFAULT NOT NULL;

alter table QUESTION alter column comment_count BIGINT DEFAULT NOT NULL;
