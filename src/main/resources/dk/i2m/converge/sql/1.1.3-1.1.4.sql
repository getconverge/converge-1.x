CREATE TABLE `wiki_page` (
    `id` BIGINT NOT NULL AUTO_INCREMENT ,
    `show_submenu` tinyint(1) DEFAULT '0',
    `submenu_style` VARCHAR(255) DEFAULT '',
    `title` VARCHAR(255) DEFAULT '',
    `display_order` INT NULL,
    `page_content` TEXT DEFAULT '',
    `last_updater` bigint(20) DEFAULT NULL,
    `updated` datetime DEFAULT null,
    `created` datetime DEFAULT null,
    PRIMARY KEY (`id`)
);

ALTER TABLE `newswire_service` ADD COLUMN `copyright` TEXT DEFAULT '';

ALTER TABLE `catalogue_hook` ADD COLUMN `asynchronous` TINYINT(1) DEFAULT '0';

-- Drupal Client
CREATE TABLE `news_item_edition_state` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `eid` int(11) DEFAULT NULL,
  `nid` int(11) DEFAULT NULL,
  `label` TEXT,
  `property` TEXT,
  `value` TEXT,
  `visible` tinyint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
