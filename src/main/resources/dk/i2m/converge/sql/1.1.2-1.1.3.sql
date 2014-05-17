CREATE TABLE `log_entry` (
    `id` BIGINT NOT NULL AUTO_INCREMENT ,
    `description` TEXT DEFAULT '',
    `severity` VARCHAR(255) DEFAULT '',
    `log_date` datetime DEFAULT null,
    PRIMARY KEY (`id`)
);

CREATE TABLE `log_subject` (
    `id` BIGINT NOT NULL AUTO_INCREMENT ,
    `entity` VARCHAR(255) DEFAULT '',
    `entity_id` VARCHAR(255) DEFAULT '',
    `link` VARCHAR(255) DEFAULT '',
    `log_entry_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE INDEX `idx_severity` ON `log_entry` (`severity`);
CREATE INDEX `idx_entity_and_id` ON `log_subject` (`entity`, `entity_id`);
CREATE INDEX `idx_entity` ON `log_subject` (`entity`);

ALTER TABLE newswire_service ADD COLUMN `processing` TINYINT(1) DEFAULT '0';
ALTER TABLE media_item ADD COLUMN `held` TINYINT(1) DEFAULT '0';

ALTER TABLE user_account ADD COLUMN default_work_day tinyint(1) NOT NULL DEFAULT '1';

CREATE TABLE `outlet_subscriber` (
    `id` BIGINT NOT NULL AUTO_INCREMENT ,
    `subscription_date` datetime DEFAULT null,
    `unsubscription_date` datetime DEFAULT null,
    `name` VARCHAR(255) DEFAULT '',
    `email` VARCHAR(255) DEFAULT '',
    `phone` VARCHAR(255) DEFAULT '',
    `date_of_birth` datetime DEFAULT null,
    `location` VARCHAR(255) DEFAULT '',
    `source` VARCHAR(255) DEFAULT '',
    `subscribed` tinyint(1) DEFAULT '0',
    `outlet_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE `catalogue` ADD COLUMN `user_role_id` bigint(20) DEFAULT NULL;
CREATE INDEX `fk_catalogue_user_role` ON `catalogue` (`user_role_id`);

ALTER TABLE `catalogue` ADD COLUMN `max_file_upload_size` bigint(20) DEFAULT 0;
