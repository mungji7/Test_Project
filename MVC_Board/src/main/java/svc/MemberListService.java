package svc;

import java.sql.Connection;
import java.util.List;

import dao.MemberDAO;
import db.JdbcUtil;
import vo.MemberBean;

public class MemberListService {

	public List<MemberBean> getMemberList() {
		List<MemberBean> memberList = null;
		
		Connection con = JdbcUtil.getConnection();
		MemberDAO dao = MemberDAO.getInstance();
		dao.setConnection(con);
		
		memberList = dao.selectMemberList();
//		System.out.println(memberList);
//		System.out.println(sId);
		
		JdbcUtil.close(con);
		
		return memberList;
	}

}
