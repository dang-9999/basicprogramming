package package_1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class User {

    public static int askInfo(){

        Scanner scanner = new Scanner(System.in);


        System.out.println("결제 시 회원 정보를 입력하시겠습니까?\n1.회원정보 입력으로 구매시: yes \n2.회원정보 미입력으로 구매시: no  \n3.구매화면에서 나갈시: exit");
        String answer = scanner.nextLine();

        // 문자열을 소문자로 변환
        answer = answer.toLowerCase();

        //확인
        if ("yes".equals(answer)) {
            return 1;
        } else if ("no".equals(answer)) {
            return 2;
        } else if ("admin".equals(answer)) {
            return 3;
        } else if ("exit".equals(answer)){
            return 4;
        }else {
            return 0; //오류발생
        }
    }

    public static List<String[]> fileTolist() {
		List<String[]> userList = new ArrayList<>();

        try {
<<<<<<< HEAD
            String filename = "userFile.txt"; // 파일 이름
            FileReader fileReader = new FileReader(filename);
=======
            // FileReader와 BufferedReader 객체 생성
            FileReader fileReader = new FileReader("userFile.txt");
>>>>>>> 034661b658bc65275dbe1493c59314438c9f53e9
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // 공백으로 분할하여 String 배열로 저장
                String[] parts = line.split(" ");
                userList.add(parts);
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userList;

	}


    public static int findPhoneNum(String phoneNum) {
        //기본적인 선언
		List<String[]> userList;
		Boolean found = false;

		userList = fileTolist();

		//저장된 유저정보가 없을 때
		if (userList == null){
			System.out.println("저장된 이름이 없습니다.");
			return 0;
		}

		//반복문을 통해 list를 하나씩 꺼냄
		for (String[] userInfo : userList){
			String userNum = userInfo[0];

			if (userNum.equals(phoneNum)) {

				//유저 정보가 존재함 & 탈출
				found = true; 
                return 1;
            }
		}

		// 유저 정보가 존재하지 않을 경우 % 탈출
		if (!found) {
            return -1; 
        }

		//오류상황 & 탈출
        return 0;
    }

    

    public static int addPhoneNum(String phoneNum) {
        int result;

        // 입력된 번호가 문법적으로 옳은지 확인
        if (!isValidPhoneNum(phoneNum)) {
            System.out.println("규칙에 어긋나는 키 입력 입니다.");
            return 0;
        }

        // 이미 기록이 있는지 확인
        result = findPhoneNum(phoneNum);

        if (result == 1) {
            // 이미 회원 정보가 있는 경우







            return -1;
        } else if (result == -1) {
            // 회원 정보가 없는 경우
            try {
                // FileWriter와 BufferedWriter 객체 생성 (파일을 쓰기 모드로 열기)
<<<<<<< HEAD
                FileWriter fileWriter = new FileWriter("userFile.txt");
=======
                FileWriter fileWriter = new FileWriter("userFile.txt", true);
>>>>>>> 034661b658bc65275dbe1493c59314438c9f53e9
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // 메뉴 항목 추가
                String userData = phoneNum + " 0" + " 0"; //전화번호//쿠폰// 누적금액 
                bufferedWriter.write(userData);
                bufferedWriter.newLine(); // 새로운 줄 추가
                

                bufferedWriter.close();
                fileWriter.close();
                return 1; //회원정보 저장 성공
            } catch (IOException e) {
                e.printStackTrace();
                return 0; // 예외 발생
            }
        }

        return 0; // 다른 상황
    }

    public static boolean isValidPhoneNum(String phoneNum) {
        // 정규표현식 패턴
        String pattern = "010[0-9]{4}[0-9]{4}";

        // 패턴과 입력된 전화번호를 비교
        if (Pattern.matches(pattern, phoneNum)) {
            return true; // 유효한 전화번호
        } else {
            return false; // 유효하지 않은 전화번호
        }
    }
    
}
