package package_1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class User {

    public static int askInfo(){

        Scanner scanner = new Scanner(System.in);


        System.out.println("결제 시 회원 정보를 입력하시겠습니까?\n-회원정보 입력으로 구매시: yes \n-회원정보 미입력으로 구매시: no  \n-구매화면에서 나갈시: exit");
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


    public static int findPhoneNum(String phoneNum) {
        try {
            // FileReader와 BufferedReader 객체 생성
            FileReader fileReader = new FileReader("userFile.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                // 각 라인에서 휴대폰 번호를 추출 (가정: 번호는 라인의 처음에 있음)
                String number = line.trim();

                // 입력된 번호와 비교
                if (number.equals(phoneNum)) {
                    // 일치하는 정보가 있다면 1 반환
                    bufferedReader.close();
                    fileReader.close();
                    return 1;
                }
            }

            // 일치하는 정보가 없으면 -1 반환
            bufferedReader.close();
            fileReader.close();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 발생 시 0 반환
            return 0;
        }
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
                FileWriter fileWriter = new FileWriter("userFile.txt", true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // 회원 정보를 파일의 마지막 다음 행에 추가
                bufferedWriter.write(phoneNum);
                bufferedWriter.newLine();

                // 파일 닫기
                bufferedWriter.close();
                fileWriter.close();

                return 1; // 성공
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