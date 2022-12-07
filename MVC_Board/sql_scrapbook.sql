SHOW TABLES;

CREATE TABLE board (
	board_num INT PRIMARY KEY,
	board_name VARCHAR(20) NOT NULL,
	board_pass VARCHAR(16) NOT NULL,
	board_subject VARCHAR(50) NOT NULL,
	board_content VARCHAR(2000) NOT NULL,
	board_file VARCHAR(200) NOT NULL,
	board_real_file VARCHAR(200) NOT NULL,
	board_re_ref INT NOT NULL,
	board_re_lev INT NOT NULL,
	board_re_seq INT NOT NULL,
	board_readcount INT DEFAULT 0,
	board_date DATETIME
);

DESC board;

SELECT MAX(board_num) FROM board;

DELETE FROM board;

SELECT * FROM board;

SELECT * FROM board WHERE board_num = 1;