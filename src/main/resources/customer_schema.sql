DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
                             `id` bigint NOT NULL,
                             `address` varchar(255) DEFAULT NULL,
                             `email` varchar(255) DEFAULT NULL,
                             `name` varchar(255) DEFAULT NULL,
                             `phone` varchar(255) DEFAULT NULL,
                             `snd_dtm` datetime DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`)
);