package package_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
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
