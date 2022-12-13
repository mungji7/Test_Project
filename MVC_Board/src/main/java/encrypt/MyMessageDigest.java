package encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MyMessageDigest {
	private String hashAlgorithm = ""; // 해싱 알고리즘명 저장할 변수

	// 파라미터 생성자 정의
	// => 암호화 알고리즘명을 전달받아 초기화
	public MyMessageDigest(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}
	
	// 해싱(= 단방향 암호화) 작업을 수행할 hashing() 메서드 정의
	// => 파라미터 : 평문 문자열        리턴타입 : String(암호문)
	public String hashing(String strPlainText) {
		String strCipherText = ""; // 암호화 결과 문자열(= 암호문)를 저장할 변수
		
		try {
			// java.security.MessageDigest 클래스의
			// static 메서드 getInstance() 메서드를 호출하여 MessageDigest 객체 리턴받기
			// => 파라미터 : 암호화 알고리즘명(= 해시 함수명) 전달
			// => 주의! 해당 알고리즘이 존재하지 않을 경우 NoSuchAlgorithmException 발생
			MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
			
			// 평문이 저장된 String 객체의 getBytes() 메서드를 호출하여
			// 평문을 byte[] 타입으로 변환
			byte[] byteData = strPlainText.getBytes();
			System.out.println(Arrays.toString(byteData));
			
			// MessageDigest 객체의 update() 메서드를 호출하여
			// byte[] 타입 평문 암호를 전달하며 MessageDigest 객체를 갱신
			md.update(byteData);
			
			// MessageDigest 객체의 digest() 메서드를 호출하여 평문을 암호문으로 변환(= 해싱)
			// => 해싱 결과값도 byte[] 타입으로 리턴(기본 32byte 짜리 암호문으로 리턴됨)
			byte[] digestResult = md.digest();
			System.out.println(Arrays.toString(digestResult));
			
			// for문을 사용하여 암호문 배열 크기만큼 반복
			for(int i=0;i<=digestResult.length;i++) {
				
			}
			
		} catch (NoSuchAlgorithmException e) {
			System.out.println(hashAlgorithm + "알고리즘이 존재하지 않습니다!");
			e.printStackTrace();
		}
		
		return strCipherText;
	}
	
}
