CREATE TABLE if not exists `user_info` (
`id` long not null auto_increment,
`user_id` varchar(36) unique not null,
`username` varchar(50) not null,
`password` varchar(50) not null,
`status` integer not null DEFAULT 0,
`time_created` varchar(19) not null,
`time_updated` varchar(19) not null,
primary key (`id`),
unique key `user_id_key` (`user_id`) using btree
);
