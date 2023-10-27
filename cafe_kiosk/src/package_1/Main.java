package package_1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		boolean systemOP = true;




		// TODO Auto-generated method stub
		
		//파일 경로, 파일 객체
		String userFilePath = "userFile.txt";
		String menuFilePath = "menuFile.txt";
		String logFilePath = "logFile.txt";
		
		File userFile = new File(userFilePath);
		File menuFile = new File(menuFilePath);
		File logFile = new File(logFilePath);
		//파일 경로, 파일 객체 끝
		
		//파일 없으면 생성, 있으면 생성x, 오류면 종료
		if(createFile(userFile) == -1) return;
		if(createFile(menuFile) == -1) return;
		if(createFile(logFile) == -1) return;
		//끝	
		
		//파일 무결성 확인 시작
		if(ReadFile(userFilePath) <= 0) return;
		if(ReadFile(menuFilePath) <= 0) return;
		if(ReadFile(logFilePath) <= 0) return;
		//파일 무결성 확인 끝
		System.out.println("파일 무결성임!!!!");


		//시간입력프롬프트
		//시스템 시간 입력
		System.out.println("시스템 시간을 입력해주세요. (예시: 1026161500)");
		String answer = scanner.nextLine();
		// setInitTime(answer) 시간 저장하고 모듈 불러오는 함수 추가하기 


		//회원정보 입력여부
		int infoChoice = User.askInfo();

		while(systemOP){

			if (infoChoice == 1) {
				// 사용자가 yes를 대답
				// 전화번호 입력 프롬프트 출력

				System.out.println("전화번호를 입력해주세요. (예시: 01012345678)");
				String phoneNum = scanner.nextLine();
				int result = User.addPhoneNum(phoneNum);

				if (result == 1){
					//number와 phoneNum과 일치하는 정보가 있다면
					//Order 메소드 호출
					//showMenus()
					
				} else if(result ==-1 ){
					//number와 phoneNum과 일치하는 정보가 없다면
					//Order 메소드 호출
					//addPhoneNum
					//showMenus()
					

				} else {
					//오류가 발생한다면
					System.out.println("오류가 발생하였습니다. ");
					continue;
				}
				break;

			} else if (infoChoice == 2) {
				// 사용자가 no를 대답
				//메뉴 주문 프롬프트 출력
			


				break;

			} else if (infoChoice == 3) {
				// 사용자가 admin을 대답, 
				//관리자 모드로 진입
				break;

			} else if (infoChoice == 4) {
				// 사용자가 exit를 대답, 종료한 경우

				break;
			} else {
				// 오류 발생
				System.out.println("규칙에 어긋나는 키 입력입니다. ");
				continue;
			}
		}










		
	}


	//파일 생성하는 함수
	public static int createFile(File file) { //정상결과 0, 오류 -1
		if(!file.exists()) {
			try {
				if(file.createNewFile())return 0;
				else return -1;
			}catch(IOException e) {
				return -1;
			}
		}else {
			return 0;
		}
	}
	//파일을 읽어서 문법에 맞는지 검사
	public static int ReadFile(String filepath) { //맞으면 1, 틀리면 0, 오류면 -1
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while((line = bufferedReader.readLine()) != null) {
				if(checkLine(filepath, line) == 0) return 0; 
			}
			bufferedReader.close();
			return 1;
		}catch(IOException e) {
			return -1;
		}
	}
	//파일을 읽어서 배열을 반환
	public static ArrayList<String> ReadFile2(String filepath) {
		try {
			ArrayList<String> argument = new ArrayList<String>();
			FileReader fileReader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while((line = bufferedReader.readLine()) != null) {
				 String[] vals = line.trim().split("\\s+");
				 for(String str : vals) {
					 argument.add(str);
				 }
			}
			bufferedReader.close();
			return argument;
		}catch(IOException e) {
			return null;
		}
	}

	public static int checkLine(String filepath, String line) { //맞으면 1, 틀리면 0
		// TODO Auto-generated method stub
		String index[] = line.trim().split("\\s+");
		if(filepath == "userFile.txt") {
			if(index.length == 0) return 1;
			if(index.length == 1) {
				//전화번호 문법 검사 함수 호출
			}
			return 0;
		}
		//메뉴 파일
		if(filepath == "menuFile.txt") {
			if(index.length == 0) return 1;
			if(index.length == 3) {
				//메뉴 이름, 메뉴 가격, 잔여 수량 문법 검사 함수 호출
			}
			return 0;
		}
		//판매 로그
		if(filepath == "logFile.txt") {
			if(index.length == 0) return 1;
			if(index.length == 3) {
				//전화번호, 메뉴 이름, 주문 수량 문법 검사 함수 호출
			}
			return 0;
		}
		return 0;
	}

}
