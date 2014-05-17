INSERT INTO `config` (`config_value`, `config_key`)
VALUES
	('http://converge.mytimes.com/converge/','CONVERGE_HOME_URL'),
        ('ldap://localhost:1389/','LDAP_PROVIDER_URL'),
        ('cn=Directory Manager','LDAP_SECURITY_PRINCIPAL'),
	('secret','LDAP_SECURITY_CREDENTIALS'),
	('simple','LDAP_SECURITY_AUTHENTICATION'),
	('dc=x,dc=y,dc=z','LDAP_BASE'),
	('cn=converge,ou=groups,dc=x,dc=y,dc=z','LDAP_GROUP_USERS'),
	('cn=converge-administrator,ou=groups,dc=z,dc=y,dc=z','LDAP_GROUP_ADMINISTRATORS'),
	('/home/converge','WORKING_DIRECTORY'),
	('EN','LANGUAGE'),
	('KE','COUNTRY'),
	('GMT+3','TIME_ZONE');