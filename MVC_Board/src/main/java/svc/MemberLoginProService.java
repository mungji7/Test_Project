package svc;

import java.sql.Connection;

import dao.MemberDAO;
import db.JdbcUtil;
import vo.MemberBean;

public class MemberLoginProService {

	public boolean loginMember(MemberBean member) {
		boolean loginResult = false;
		
		Connection con = JdbcUtil.getConnection();
		MemberDAO dao = MemberDAO.getInstance();
		dao.setConnection(con);
		
		boolean isRightUser = dao.selectMember(member);
//		System.out.println(isRightUser);
		
		if(isRightUser) { // 로그인 성공시
			JdbcUtil.commit(con);
			loginResult = true;
		} else {
			JdbcUtil.rollback(con);
		}
		
		JdbcUtil.close(con);
		
		return loginResult;
	}

}
