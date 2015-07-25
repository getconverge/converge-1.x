/* CON-33: Custom styling for a workflow state. */
ALTER TABLE `workflow_state` ADD COLUMN `style` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `workflow_state` ADD COLUMN `style_fg` VARCHAR(255) NOT NULL DEFAULT '';

/* CON-33: Introduce a "Converge" user that is the owner of automated workflow transitions. */
INSERT INTO `user_account` (`username`,  `opt_lock`, `dn`, `full_name`, `given_name`, `surname`, `job_title`, `organisation`, `email`) VALUES ('converge', 1, '', 'Converge', 'Converge', '', '', '', 'converge@getconverge.com');
SELECT @converge_user_id:=LAST_INSERT_ID();
INSERT INTO `user_role` (`role_name`, `description`) VALUES ('System administrator', 'Built-in security role for Converge system administrators');
SELECT @converge_user_role_id:=LAST_INSERT_ID();
INSERT INTO `user_role_privilege` (`role_id`, `privilege_id`) VALUES (@converge_user_role_id, 'SUPER_USER');
/* Note, defect in naming of columns in user account management. user_account_id is actually the user_role_id and vice versa */
INSERT INTO `user_account_membership` (`user_account_id`, `user_role_id`) VALUES (@converge_user_role_id, @converge_user_id);


