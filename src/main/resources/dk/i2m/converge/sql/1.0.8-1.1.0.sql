ALTER TABLE rendition DROP COLUMN opt_lock;
ALTER TABLE rendition ADD COLUMN label varchar(255);
ALTER TABLE rendition ADD COLUMN default_height int DEFAULT '0';
ALTER TABLE rendition ADD COLUMN default_width int DEFAULT '0';
ALTER TABLE media_item DROP COLUMN parent_id;

RENAME TABLE media_repository TO catalogue;
ALTER TABLE catalogue ADD COLUMN preview_rendition BIGINT;
ALTER TABLE catalogue ADD COLUMN original_rendition BIGINT;

ALTER TABLE `media_item` CHANGE COLUMN `media_repository_id` `catalogue_id` BIGINT(20) NULL DEFAULT NULL;

INSERT INTO rendition (name, label, description) VALUES ('rnd:thumbnail', 'Thumbnail', 'A very small rendition of an image, giving only a general idea of its content.');
INSERT INTO rendition (name, label, description) VALUES ('rnd:preview', 'Preview', 'Preview resolution image or video');
INSERT INTO rendition (name, label, description) VALUES ('rnd:lowRes', 'Low resolution', 'Low resolution image or video');
INSERT INTO rendition (name, label, description) VALUES ('rnd:highRes', 'High resolution', 'High resolution image or video');
INSERT INTO rendition (name, label, description) VALUES ('rnd:print', 'Content for print', 'Content intended to appear in print');
INSERT INTO rendition (name, label, description) VALUES ('rnd:web', 'Content for a web page', 'Content intended to appear on a web page');
INSERT INTO rendition (name, label, description) VALUES ('rnd:sms', 'Content for short message', 'Content intended to appear in a short messaging system');
INSERT INTO rendition (name, label, description) VALUES ('rnd:mobile', 'Content for a mobile device', 'Content intended to appear on a mobile or handheld device');

CREATE  TABLE `media_item_rendition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `rendition_id` BIGINT NULL ,
  `filename` TEXT DEFAULT '',
  `path` TEXT DEFAULT '',
  `content_type` VARCHAR(255) DEFAULT '' ,
  `file_size` BIGINT DEFAULT 0,
  `width` INT NULL ,
  `height` INT NULL ,
  `colourSpace` VARCHAR(255) DEFAULT '' ,
  `resolution` INT NULL ,
  `audio_bitrate` INT NULL ,
  `audio_channels` VARCHAR(255) DEFAULT '',
  `audio_codec` VARCHAR(255) DEFAULT '',
  `audio_sample_size` INT NULL ,
  `audio_sample_rate` INT NULL ,
  `audio_variable_bitrate` TINYINT(1) DEFAULT '0',
  `duration` INT NULL,
  `video_codec` VARCHAR(255) DEFAULT '',
  `video_average_bit_rate` INT NULL,
  `video_variable_bit_rate` TINYINT(1) DEFAULT '0',
  `video_frame_rate` INT NULL,
  `video_scan_technique` VARCHAR(255) DEFAULT '',
  `video_aspect_ratio` VARCHAR(255) DEFAULT '',
  `video_sampling_method` VARCHAR(255) DEFAULT '',
  `media_item_id` BIGINT NULL ,
  PRIMARY KEY (`id`));

INSERT INTO media_item_rendition (rendition_id, filename, content_type, media_item_id)
 SELECT (SELECT id FROM rendition WHERE name LIKE "rnd:highRes"), media_item.filename, media_item.contentType, media_item.id FROM media_item;

CREATE TABLE `catalogue_rendition` (
  `catalogue_id` BIGINT NOT NULL ,
  `rendition_id` BIGINT NOT NULL ,
  PRIMARY KEY (`catalogue_id`, `rendition_id`) );

CREATE TABLE `catalogue_hook` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `execute_order` int(11) DEFAULT NULL,
  `hook_class` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `catalogue_id` bigint(20) DEFAULT NULL,
  `manual` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_catalogue_hook_catalogue` (`catalogue_id`)
);

CREATE TABLE `catalogue_hook_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `property_key` varchar(255) DEFAULT NULL,
  `property_value` varchar(255) DEFAULT NULL,
  `catalogue_hook_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_catalogue_hook_property` (`catalogue_hook_id`)
);


ALTER TABLE newswire_item_attachment ADD COLUMN rendition_id bigint(20) DEFAULT null;

ALTER TABLE news_item_media_attachment DROP COLUMN opt_lock;
ALTER TABLE news_item_media_attachment ADD COLUMN display_order int(11) DEFAULT 0;

ALTER TABLE news_item_workflow_state_transition DROP COLUMN opt_lock;
ALTER TABLE news_item_workflow_state_transition ADD COLUMN submitted tinyint(1) DEFAULT '0';

ALTER TABLE workflow_step DROP COLUMN opt_lock;
ALTER TABLE workflow_step ADD COLUMN submitted tinyint(1) DEFAULT '0';

DROP TABLE `app_version`;

CREATE TABLE `app_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_version` varchar(255) NOT NULL,
  `to_version` varchar(255) NOT NULL,
  `migrated` tinyint(1) NOT NULL DEFAULT '0',
  `migrated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO `app_version` (from_version, to_version, migrated, migrated_date) VALUES ('', '1.0.8', 1, '2011-07-25');
INSERT INTO `app_version` (from_version, to_version) VALUES ('1.0.8', '1.0.9');

CREATE TABLE `workflow_step_user_role` (
  `workflow_step_id` bigint(20) NOT NULL,
  `user_role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`workflow_step_id`, `user_role_id`)
);


ALTER TABLE user_account ADD COLUMN default_search_results_order_by varchar(255);
ALTER TABLE user_account ADD COLUMN default_search_results_order tinyint(1) NOT NULL DEFAULT '0';

CREATE TABLE `background_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `task_start` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `open_calais_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_group` varchar(255) NOT NULL,
  `field` varchar(255) NOT NULL,
  `field_value` varchar(255) NOT NULL,
  `concept_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
);


# Activity Stream
ALTER TABLE notification ADD COLUMN link varchar(255);
ALTER TABLE notification ADD COLUMN sender_id bigint(20);

# Fix NewsML properties
UPDATE newswire_service_property SET property_key = 'PROPERTY_NEWSWIRE_LOCATION' WHERE property_key='Location of newswires';
UPDATE newswire_service_property SET property_key = 'PROPERTY_ATTACHMENT_CATALOGUE' WHERE property_key='Attachment catalogue';
UPDATE newswire_service_property SET property_key = 'PROPERTY_NEWSWIRE_PROCESSED_LOCATION' WHERE property_key='Location of processed newswires';
UPDATE newswire_service_property SET property_key = 'PROPERTY_NEWSWIRE_DELETE_AFTER_PROCESS' WHERE property_key='Delete processed newswires';
UPDATE newswire_service_property SET property_key = 'PROPERTY_RENDITION_MAPPING' WHERE property_key='Attachment rendition mapping (converge name;attachment description)';

# Fix RSS properties
UPDATE newswire_service_property SET property_key = 'ENABLE_OPEN_CALAIS' WHERE property_key='Enable OpenCalais';

# Fix URL Callback properties
UPDATE outlet_edition_action_property SET property_key = 'TIMEOUT' WHERE property_key='Timeout';
UPDATE outlet_edition_action_property SET property_key = 'CALLBACK_URL' WHERE property_key='Callback URL';

# Expiration of newswire items
ALTER TABLE `newswire_service` ADD COLUMN `days_to_keep` int(11) DEFAULT '0';
