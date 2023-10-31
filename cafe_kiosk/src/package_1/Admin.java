package package_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Admin {


	//이 아래로 다은 수정한 것
	public static List<String[]> fileTolist() {
		List<String[]> menuList = new ArrayList<>();

        try {
            String filename = "menuFile.txt"; // 파일 이름
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
                FileWriter fileWriter = new FileWriter("menuFile.txt",true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // 메뉴 항목 추가
                String menuData = inputName + " " + inputPrice + " 30";
                bufferedWriter.write(menuData);
                bufferedWriter.newLine(); // 새로운 줄 추가

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


	public int deleteMenu(String inputName) {
		//기본적인 선언
        int result = findName(inputName);
		List<String[]> menuList;
		menuList = fileTolist();

		// result가 1이면 메뉴가 존재하지 않는 상태 & 탈출
        if (result == 1) {
            return -1; 
        } else if (result == -1) { //동일한 이름의 메뉴가 이미 파일에 존재

			//반복문을 통해 리스트를 검사하고 일치하는 메뉴 명이 있으면 리스트에서 제거
        	Iterator<String[]> iterator = menuList.iterator();
        	while(iterator.hasNext()) {
        		String[] menuData = iterator.next();
        		if(inputName.equals(menuData[0]))
        			iterator.remove();
        	}

			try {
				FileWriter fileWriter = new FileWriter("menuFile.txt");
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	
				// 메뉴 데이터 배열을 공백으로 구분된 문자열로 변환
				for (String[] menuData : menuList) {
					String line = String.join(" ", menuData);
					bufferedWriter.write(line);
					bufferedWriter.newLine();
				}
				
				
				bufferedWriter.close();
				fileWriter.close();
//				System.out.println("메뉴 리스트를 파일에 저장했습니다.");

				return 1;

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("메뉴 리스트를 파일에 저장하는 중 오류가 발생했습니다.");
			}
            
        }

		//예외적인 오류 발생 & 탈출
        return 0; 
    }

	public int changeMenu(String inputName, int inputquantity){

		//기본적인 선언
        int result = findName(inputName);
		List<String[]> menuList;

		menuList = fileTolist();

		// result가 1이면 메뉴가 존재하지 않는 상태 & 탈출
        if (result == 1) {
            return -1; 
        } else if (result == -1) { //동일한 이름의 메뉴가 이미 파일에 존재

			//반복문을 통해 리스트를 검사하고 일치하는 메뉴 명이 있으면 잔여수량 변경
			for (String[] menuData : menuList) {
				if (menuData[0].equals(inputName)) {
					menuData[2] = String.valueOf(inputquantity);
				}
			}
			
			try {
				FileWriter fileWriter = new FileWriter("menuFile.txt");
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	
				// 메뉴 데이터 배열을 공백으로 구분된 문자열로 변환
				for (String[] menuData : menuList) {
					String line = String.join(" ", menuData);
					bufferedWriter.write(line);
					bufferedWriter.newLine();
				}
				
				
				bufferedWriter.close();
				fileWriter.close();
//				System.out.println("메뉴 리스트를 파일에 저장했습니다.");

				return 1;

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("메뉴 리스트를 파일에 저장하는 중 오류가 발생했습니다.");
			}
			
			return 1;
			
		}

		//예외적인 오류 발생 & 탈출
		return 0;
	}

	public void printFile() {
        try {
            // BufferedReader 객체 생성
            BufferedReader reader = new BufferedReader(new FileReader("logFile.txt"));

            String line;

            // 라인 단위로 파일 읽고 출력
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일을 읽는 도중 오류가 발생했습니다.");
        }
	}
	
	public void printMenu() {
		List<String[]> menuList = fileTolist();
		
		System.out.println("=".repeat(12)+"메뉴판"+"=".repeat(12));
		for (String[] menuData : menuList){
			System.out.printf("  %-10s%10s원\n",menuData[0],menuData[1]);
		}
		System.out.println("=".repeat(28));
	}
}
