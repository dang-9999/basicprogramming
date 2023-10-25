package package_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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


	//다은 수정 중
	public static List<String[]> fileTolist() {
		List<String[]> menuList = new ArrayList<>();

        try {
            String filename = "Menu.txt"; // 파일 이름
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // 공백으로 분할하여 String 배열로 저장
                String[] parts = line.split(" ");
                menuList.add(parts);
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return menuList;

	}

	public int findName(String inputName){
		//기본적인 선언
		List<String[]> menuList;
		Boolean found = false;

		menuList = fileTolist();

		//저장된 메뉴가 없을 때
		if (menuList == null){
			System.out.println("저장된 메뉴가 없습니다.");
			return 0;
		}

		//반복문을 통해 list를 하나씩 꺼냄
		for (String[] menuData : menuList){
			String menuName = menuData[0];

			// 이미 메뉴에 추가할 메뉴이름이 존재할 경우
			if (menuName.equals(inputName)) {

				// 메뉴 이름이 존재함 & 탈출
				found = true; 
                return -1;
            }
		}

		// 메뉴에 추가할 메뉴이름이 존재하지 않을 경우 % 탈출
		if (!found) {
            return 1; 
        }

		//오류상황 & 탈출
        return 0; 
	}


	public int addMenu(String inputName, int inputPrice) {
        int result = findName(inputName);

		//result가 1이면 메뉴이름이 존재하지 않음
        if (result == 1) {
            try {
                FileWriter fileWriter = new FileWriter("Menu.txt", true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // 메뉴 항목 추가
                String menuData = inputName + " " + inputPrice + " 30";
                bufferedWriter.newLine(); // 새로운 줄 추가
                bufferedWriter.write(menuData);

                bufferedWriter.close();
                fileWriter.close();
                return 1; // 메뉴가 존재하지 않는 상태
            } catch (IOException e) {
				// 예외적인 오류
                e.printStackTrace();
                return 0; 
            }
        } else if (result == -1) { //result가 -1이면 메뉴 이름이 존재하지 않음
            return -1; // 이미 메뉴가 존재하는 상태 & 탈출
        }

		//예외적인 오류 발생 & 탈출
        return 0; 
    }


	public int deleteMenu(String name) {
		//초기화
        int result = findName(name);

		// result가 1이면 메뉴가 존재하지 않는 상태 & 탈출
        if (result == 1) {
            return -1; 
        } else if (result == -1) { //동일한 이름의 메뉴가 이미 파일에 존재
            
        }

        return 0; // 다른 상황
    }

	
}
