drop table if exists `user`;

CREATE TABLE `user`
(
    `id`            int          NOT NULL AUTO_INCREMENT,
    `name`          varchar(100) NOT NULL,
    `email`         varchar(100) NOT NULL,
    `mobile_number` varchar(20)  NOT NULL,
    `pwd`           varchar(500) NOT NULL,
    `timestamp`     timestamp    DEFAULT NULL,
    PRIMARY KEY (`id`)
);

