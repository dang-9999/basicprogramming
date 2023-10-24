package package_1;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin {
	static Scanner scan = new Scanner(System.in);
	
	public int deleteMenu() {
		String menu = scan.nextLine().trim();
		String[] index = menu.split("\\s+");
		//문법 검사 시작
		int length = indexLength(index);
		if(length < 0 || length >=2) {
			System.out.println("해당 메뉴를 찾을 수 없습니다.");
			deleteMenu();
		}
		if(length == 0) {
			System.out.println("실행이 취소되었습니다.");
			//관리자 모드 프롬프트 실행
			
			//
		}
		if(length == 1) {
			File file = new File("test.txt");
			//동치인 메뉴가 있는지 확인
			ArrayList<String> argument = Main.ReadFile2("menuFile.txt");
			//for(int i=0; i<);
		}
		//문법 검사 끝
		return 0;
	}

	public int indexLength(String[] index) {
		// TODO Auto-generated method stub
		if(index.length == 0) return 0;
		if(index.length == 1) return 1;
		if(index.length == 2) return 2;
		return -1;
	}
	
}
