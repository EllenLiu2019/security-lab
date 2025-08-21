drop table if exists `user_group`;

CREATE TABLE `user_group`
(
    `id`                  int         NOT NULL AUTO_INCREMENT,
    `group_id`            varchar(50) NOT NULL,
    `user_id`             int         NOT NULL,
    PRIMARY KEY (`id`),
    KEY `user_id` (`user_id`),
    CONSTRAINT `group_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);