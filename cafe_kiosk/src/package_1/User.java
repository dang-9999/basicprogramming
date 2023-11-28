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
    
    // static String filename = "userFile.txt"; // 파일 이름
    public static int askInfo(){

        Scanner scanner = new Scanner(System.in);


        System.out.println("결제 시 회원 정보를 입력하시겠습니까?\n1.회원정보 입력으로 구매시: yes \n2.회원정보 미입력으로 구매시: no  \n3.구매화면에서 나갈시: exit");
        String answer = scanner.nextLine().trim();

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

    public static List<String[]> fileTolist(String filename) {
		List<String[]> userList = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // 공백으로 분할하여 String 배열로 저장
                String[] parts = line.trim().split("\\s+");
                userList.add(parts);
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userList;

	}


    public static int findPhoneNumInUser(String phoneNum) {
        //기본적인 선언
		List<String[]> userList;
		Boolean found = false;

		userList = fileTolist("userFile.txt");

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

    public static int findPhoneNumInLog(String phoneNum) {
        //기본적인 선언
		List<String[]> logList;
		Boolean found = false;

		logList = fileTolist("logFile.txt");

		//저장된 유저정보가 없을 때
		if (logList == null){
			System.out.println("저장된 이름이 없습니다.");
			return 0;
		}

		//반복문을 통해 list를 하나씩 꺼냄
		for (String[] logInfo : logList){
			String userNum = logInfo[1];
            
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
        int resultInUserFile;

        // 입력된 번호가 문법적으로 옳은지 확인
        if (!isValidPhoneNum(phoneNum)) {
            return 0;
        }

        // 이미 기록이 있는지 확인
        resultInUserFile = findPhoneNumInUser(phoneNum);
        if (resultInUserFile == 1) {
            // 이미 회원 정보가 있는 경우
            return -1;
        } else if (resultInUserFile == -1) {
            // 회원 정보가 없는 경우
            try {
                
			    FileReader fileReader = new FileReader("userFile.txt");  
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder userFileCont = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    userFileCont.append(line).append("\n");
                }
                bufferedReader.close();
                FileWriter fileWriter = new FileWriter("userFile.txt");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                // FileWriter와 BufferedWriter 객체 생성 (파일을 쓰기 모드로 열기)
                // 메뉴 항목 추가
                String userData = phoneNum + " 0" + " 0"; //전화번호//쿠폰// 누적금액 
                bufferedWriter.write(userFileCont.toString());
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
    
    public static int modifyPhoneNum(String[] phone) {  	
        //phone[0]: 기존 전화번호, phone[1]: 바꾸고 싶은 전화번호

    	if(!isValidPhoneNum(phone[0]) || !isValidPhoneNum(phone[1])){	//전화번호 문법규칙 검사
    		// System.out.println("규칙에 어긋나는 키 입력입니다.");
    		return 0;
    	}

        int resultInUserFile = 0;

        // 회원정보파일 내에서 회원 번호 바꾸는 부분
    	resultInUserFile = findPhoneNumInUser(phone[0]);	//기존 회원인지 검사 

    	if(resultInUserFile == 1) {
    		resultInUserFile = findPhoneNumInUser(phone[1]);	//회원 중복 여부
    		if(resultInUserFile == 1) {	//회원 존재
    			System.out.println("이미 같은 번호로 등록된 회원이있습니다.");
    			return -1;
    		}
    		else if(resultInUserFile == -1) {	//회원 정보 변경

                //유저파일에서 변경
    			List<String[]> userlist = fileTolist("userFile.txt");
                List<String[]> logList = fileTolist("logFile.txt");

                //유저파일 값 변경하기
    			String price = null;
    			String coupon = null;
    			for(String[] user : userlist) {
    				if(user[0].equals(phone[0])) {
    					user[0] = phone[1];
    					price = user[1];
    					coupon = user[2];
    				}
    			}
                
                //로그파일 값 변경하기 
                if(findPhoneNumInLog(phone[0]) == 1 ){

                    try {

                        for(String[] user: logList){if(user[1].equals(phone[0])){user[1] = phone[1];}}

                        FileWriter filewriter = new FileWriter("logFile.txt");
                        BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
                        
                        for(String[] logData : logList) {
                            String line = String.join(" ", logData);
                            bufferedwriter.write(line);
                            bufferedwriter.newLine();
                        }
                        
                        bufferedwriter.close();
                        filewriter.close();


                        
                    } catch (IOException e) {e.printStackTrace();return 0;}

                    
                }

                
    			//파일에 작성하기
    			try {
    				FileWriter filewriter = new FileWriter("userFile.txt");
    				BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
    				
    				for(String[] user : userlist) {
    					String line = String.join(" ", user);
    					bufferedwriter.write(line);
    					bufferedwriter.newLine();
    				}
    				
    				bufferedwriter.close();
    				filewriter.close();
    				
    				System.out.println("누적 결제액 "+price+"원과 쿠폰 "+coupon+"개가 이전되었습니다.");
                    return 1;
    				
    			} catch(IOException e) {e.printStackTrace();return 0;}
            
    		}
    	}
    	else if(resultInUserFile == -1) {	//기존 회원 아님
    		System.out.println("존재하지 않는 회원입니다.");
    		return 0;
    	}

        return 0;
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
