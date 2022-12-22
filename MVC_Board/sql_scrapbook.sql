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

CREATE TABLE member (
   name VARCHAR(20) NOT NULL,
   id VARCHAR(16) PRIMARY KEY,
   passwd VARCHAR(16) NOT NULL,
   email VARCHAR(50) UNIQUE NOT NULL,
   gender VARCHAR(1) NOT NULL,
   date DATE NOT NULL
);

SELECT * FROM member;

/* 암호화 패스워드 관리를 위해 passwd 컬럼 타입을 VARCHAR(100)으로 변경 */
ALTER TABLE member CHANGE passwd passwd VARCHAR(100) NOT NULL;

DESC member;


