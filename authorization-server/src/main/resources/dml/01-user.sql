delete from user where name = 'Happy';

INSERT INTO `user` (`name`,`email`,`mobile_number`, `pwd`, `timestamp`)
VALUES ('Happy',
        'happy@126.com',
        '60022365',
        '{bcrypt}$2a$10$aA9n0FFfilGNfe4wQxFaaOxyNXVYkH/J8yC3J0rG54VDUqm5a9RW.', -- happy
        CURDATE());

INSERT INTO `user` (`name`,`email`,`mobile_number`, `pwd`, `timestamp`)
VALUES ('Ellen',
        'liuxiaojing11@126.com',
        '60022666',
        '{bcrypt}$2a$10$/0tnGF.ONxDU/xKb/UgfxeolBYJyAW4YwARNZ.hp.y6K3V2aAWbaG', -- iYd8F#s0Eg
        CURDATE());

INSERT INTO `user` (`name`,`email`,`mobile_number`, `pwd`, `timestamp`)
VALUES ('api-banking',
        'apis@126.com',
        '60022666',
        '{bcrypt}$2a$10$aA9n0FFfilGNfe4wQxFaaOxyNXVYkH/J8yC3J0rG54VDUqm5a9RW.', -- happy
        CURDATE());
