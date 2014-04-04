INSERT INTO USER (
	id,
	zh_name,
	en_name,
	email,
	mobile,
	md5,
	create_time,
	login_time,
	salt,
	disabled
)
VALUES
	(
		'startagain',
		'台俊峰',
		'peak',
		'taijunfeng_it@sina.com',
		'18626337274',
		'ea42974e2cf40a9f26ad69f643ddbaf7',
		now(),
		null,
		'd759b0204c7e469f9574849dc0648f67',
		0
	);

INSERT INTO sys_admin (admin_id)
VALUES
	('startagain');

