package db.movie;

import java.util.InputMismatchException;
import java.util.Scanner;

import db.util.DBConn;

public class MainUI {

	public static void main(String[] args) {
		Scanner sc =  new Scanner(System.in);
		
		AdminUI admin = new AdminUI();
		UserUI user = new UserUI();
		
		final String admin_id = "1234";
		final String admin_pw = "1234";
		
		System.out.println("▶영화 예매 키오스크◀");
		int ch;
		
		try {
			while(true) {
				System.out.println("\n[ Main ]");

				try {
					System.out.print("1.관리자 2.고객 3.종료 => ");
					ch = sc.nextInt();
					
					if(ch ==3) break;
					
					if(ch==1) {
						System.out.print("id ? ");
						String id = sc.next();
						System.out.print("pw ? ");
						String pw = sc.next();

						if (id.equals(admin_id) && pw.equals(admin_pw)) {
							admin.adminManage();
						} else {
							System.out.println("비밀번호가 일치하지 않습니다.");
							continue;
						}
					} else if (ch == 2) { // 2. 고객 메뉴로
						user.userManage();
					} else {
						return;
					}
					
					switch(ch) {
					case 1: admin.adminManage(); break;
					case 2: user.userManage(); break;
					}
					
				} catch (InputMismatchException e) {

					System.out.println("문자는 입력 불가합니다.");
					sc.nextLine();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				}
			} finally {
				sc.close();
				DBConn.close();
		}
		
	}

}
