package svc;

import java.sql.Connection;

import dao.MemberDAO;
import db.JdbcUtil;
import vo.MemberBean;

public class MemberJoinProService {

	// 회원가입
	public boolean joinMember(MemberBean member) {
		boolean isJoinSuccess = false;
		
		Connection con = JdbcUtil.getConnection();
		MemberDAO dao = MemberDAO.getInstance();
		dao.setConnection(con);
		
		// MemberDAO의 insertMember() 메서드를 호출하여 답글 쓰기 작업 요청
		// => 파라미터 : MemberBean 객체   리턴 : int(insertCount)
		int insertCount = dao.insertMember(member);
		
		if(insertCount>0) {
			JdbcUtil.commit(con);
			isJoinSuccess = true;
		} else {
			JdbcUtil.rollback(con);
		}
		
		JdbcUtil.close(con);
				
		return isJoinSuccess;
	}

}
