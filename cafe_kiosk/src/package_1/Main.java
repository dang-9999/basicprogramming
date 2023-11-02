package package_1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		boolean systemOP = true;

		
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
		TimeManager timeManager;
		System.out.println("시스템 시간을 입력해주세요. (예시: MMdd/HHmmss)");
		String initTime = scanner.nextLine();
		try{
			timeManager = new TimeManager(initTime);
		} catch (ParseException e) {
			System.out.println("오류)시스템시간오류");
			return;
		}


		

		while(systemOP){

			//회원정보 입력여부
			int infoChoice = User.askInfo();
			
			if (infoChoice == 1) {
				// 사용자가 yes를 대답
				// 전화번호 입력 프롬프트 출력

				System.out.println("전화번호를 입력해주세요. (예시: 01012345678)");
				String phoneNum = scanner.nextLine();
				int result = User.addPhoneNum(phoneNum);

				if (result == 1){
					//number와 phoneNum과 일치하는 정보가 있다면
					//Order 메소드 호출
					Order order = new Order(timeManager, phoneNum);
					//run()
					order.run();


					
				} else if(result ==-1 ){
					//number와 phoneNum과 일치하는 정보가 없다면
					//addPhoneNum
					//Order 메소드 호출
					Order order = new Order(timeManager, phoneNum);
					//run()
					order.run();

					

				} else {
					//오류가 발생한다면
					System.out.println("오류가 발생하였습니다. ");
				
				}
				continue;

			} else if (infoChoice == 2) {
				// 사용자가 no를 대답
				//Order 메소드 호출
				Order order = new Order(timeManager);
				//run()
				order.run();
			


			systemOP = false;

			} else if (infoChoice == 3) {
				// 사용자가 admin을 대답, 
				//관리자 모드로 진입
				boolean Admin1 = true;
				while(Admin1) {
					System.out.print("패스워드 입력: ");
					String password = scanner.nextLine();
					int loginChoice = AdminLogin(password);
					
					switch(loginChoice) {
						case -1 -> System.out.println("규칙에 어긋나는 키 입력입니다.");
						case 0 -> System.out.println("패스워드가 틀렸습니다.");
						case 1 -> 
						{
							boolean Admin2 = true;
							while(Admin2){
								try {
								System.out.println("1.판매로그확인\n2.메뉴 추가\n3.메뉴 삭제\n4.메뉴 수량 조정\n5.관리자 모드 종료");
								int adminChoice = scanner.nextInt();
								Admin admin = new Admin();
								switch(adminChoice) {
								case 1 -> admin.printFile(); //판매로그
								case 2 -> //메뉴 추가
								 {
									while(true) {
										scanner.nextLine();
										admin.printMenu();
										System.out.println("추가하실 메뉴의 이름을 입력하세요");
										System.out.print("추가하실 메뉴: ");
										String menu = scanner.nextLine();
										if(menu.isBlank()) {System.out.println("실행이 취소되었습니다."); break;}
										else if(isMenuName(menu) == 0) System.out.println("규칙에 어긋나는 키 입력입니다.");
										else {
											String price;
											do {
											System.out.print("추가하실 메뉴의 가격을 입력하세요.\n가격 : ");
											price = scanner.nextLine();
											if(price.isBlank()) {System.out.println("실행이 취소되었습니다."); break;}
											else if(isMenuPrice(price) == 0) System.out.println("메뉴 가격(숫자)을 입력해주세요.");
											} while(isMenuPrice(price) == 0);
											if(price.isBlank()) break;
											
											int iprice = Integer.parseInt(price);
											
											int result = admin.addMenu(menu, iprice);
											if(result == 1) {System.out.println("메뉴를 추가했습니다("+menu+")."); break;}
											if(result == -1) {System.out.println("이미 존재하는 메뉴입니다."); break;}
											if(result == 0) {System.out.println("오류 발생"); break;}
										}
									}
								 } 
								case 3 -> //메뉴 삭제
								 {
									while(true) {
										scanner.nextLine();
										admin.printMenu();
										System.out.println("삭제하실 메뉴의 이름을 입력하세요");
										System.out.print("삭제하실 메뉴: ");
										String menu = scanner.nextLine();
										if(menu.isBlank()) {System.out.println("실행이 취소되었습니다."); break;}
										else if(isMenuName(menu) == 0) System.out.println("규칙에 어긋나는 키 입력입니다.");
										else {
											int result = admin.deleteMenu(menu); 
											if(result == 1) {System.out.println("메뉴를 삭제했습니다("+menu+")."); break;}
											if(result == -1){System.out.println("해당 메뉴를 찾을 수 없습니다.");  break;}
											if(result == 0) {System.out.println("오류 발생"); break;}
										}
									}
								 }
								case 4 -> //메뉴 수량 조정
								 {
									while(true) {
										scanner.nextLine();
										System.out.println("품절 표시 및 수량을 변경할 메뉴를 입력하세요.");
										System.out.print("메뉴 이름: ");
										String menu = scanner.nextLine();
										if(menu.isBlank()) {System.out.println("실행이 취소되었습니다."); break;}
										else if(isMenuName(menu) == 0) System.out.println("규칙에 어긋나는 키 입력입니다.");
										else {
											String quantity;
											do {
											System.out.println("선택하신 메뉴의 변경할 잔여 수량을 입력해주세요.");
											System.out.println("품절로 변경을 원한다면 0을 입력해주세요.");
											System.out.println("메뉴 이름: "+menu);
											System.out.print("잔여 수량 변경: ");
											quantity = scanner.nextLine();
											if(isOrderQuantity(quantity) == 0) System.out.println("규칙에 어긋나는 키 입력입니다.");
											} while(isOrderQuantity(quantity) == 0);
											
											int iquantity = Integer.parseInt(quantity);
											
											int result = admin.changeMenu(menu, iquantity); 
											if(result == 1) {System.out.println("변경되었습니다("+menu+")."); break;}
											if(result == -1) {System.out.println("해당 메뉴를 찾을 수 없습니다."); break;}
											if(result == 0) {System.out.println("오류 발생"); break;}
										}
									}
								 }
								case 5 -> 
								 {
									Admin2 = false; 
									scanner.nextLine();
									System.out.println("관리자모드를 종료합니다.\n\n"); //관리자 모드 종료
								 }
								default -> System.out.println("규칙에 어긋나는 키 입력입니다."); //예외
								}
								} catch(InputMismatchException e) {
									System.out.println("규칙에 어긋나는 키 입력입니다."); //예외
									scanner.nextLine();
								}
							}
							Admin1 = false;
						}
					}
					
				}

			} else if (infoChoice == 4) {
				// 사용자가 exit를 대답, 종료한 경우
				System.out.println("이용해주셔서 감사합니다.");
				systemOP = false;

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
			if(index.length == 3) {
				//전화번호, 쿠폰 개수(수량), 누적 금액(메뉴 가격) 문법 검사 함수 호출
				if(isMenuName(index[0]) == 0 || isOrderQuantity(index[1]) == 0 || isMenuPrice(index[2]) == 0)
					return 0;
				else 
					return 1;
			}
			return 0;
		}
		//메뉴 파일
		if(filepath == "menuFile.txt") {
			if(index.length == 0) return 1;
			if(index.length == 3) {
				//메뉴 이름, 메뉴 가격, 잔여 수량 문법 검사 함수 호출
				if(isMenuName(index[0]) == 0 || isMenuPrice(index[1]) == 0 || isOrderQuantity(index[2]) == 0)
					return 0;
				else 
					return 1;
			}
			return 0;
		}
		//판매 로그
		if(filepath == "logFile.txt") {
			if(index.length == 0) return 1;
			if(index.length == 4) {
				//전화번호, 메뉴 이름, 주문 수량 문법 검사 함수 호출 (시간 문법 검사 함수 없음)
				if(isPhoneNumber(index[1]) == 0 || isMenuName(index[2]) == 0 || isOrderQuantity(index[3]) == 0)
					return 0;
				else 
					return 1;
			}
			return 0;
		}
		return 0;
	}
	
	// 검사함수들입니다-peace
	
	public static int isPhoneNumber(String phoneNumber) {	// 전화번호 검사 함수 - return 1==true
		// 정규식 정의
		String regex = "010[0-9]{4}[0-9]{4}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phoneNumber);
		if(matcher.matches() && phoneNumber.length() == 11) {
			return 1;
		}else{
			return 0;
		}

	}
	
	public static int isMenuName(String menuName) {		// 메뉴이름 검사 함수 - return 1==true
		// 검사할 문자열이 "결제하기"인 경우 false 반환
        if ("결제하기".equals(menuName)) {
            return 0;
        }
        
        // 문자열의 길이가 1 이상이어야 함
        if (menuName.length() < 1) {
        	return 0;
        }

        // 문자열의 모든 문자가 실제 문자인지 확인
        for (char c : menuName.toCharArray()) {
            if (!Character.isLetter(c)) {
            	return 0;
            }
        }
        
        // 탭이나 개행이 없어야 함
        if (menuName.contains("\t") || menuName.contains("\n")) {
        	return 0;
        }
        
        // 모든 조건을 만족하면 유효한 메뉴 이름
        return 1;
		}
	
	public static int isMenuPrice(String menuPrice) {	// 메뉴가격 검사 함수 - return 1==true
		// "0"이라는 길이 1짜리 문자열이거나
        if (menuPrice.equals("0")) {
            return 1;
        }

        // 숫자로만 이루어져 있고 첫 글자가 0이 아니며 길이가 2 이상인 문자열
        if (menuPrice.matches("[1-9][0-9]+")) {
            return 1;
        }

        // 위의 조건을 모두 만족하지 않는 경우
        return 0;
	}
	public static int isOrderQuantity(String orderQuantity) {	// 주문수량(잔여수량) 검사 함수 - return 1==true
		// 길이가 1 이상
        if (orderQuantity.length() < 1) {
            return 0;
        }

        // 탭이나 개행이 없어야 함
        if (orderQuantity.contains("\t") || orderQuantity.contains("\n")) {
        	return 0;
        }

        try {
            // 문자열을 정수로 변환
            int quantity = Integer.parseInt(orderQuantity);

            // 0을 포함하고 양의 정수여야 함
            return (quantity >= 0) ? 1 : 0;
        } catch (NumberFormatException e) {
            // 정수로 변환할 수 없는 경우
            return 0;
        }
	}
	
	//관리자 모드 로그인 메소드
	public static int AdminLogin(String password) {
		final String PASSWORD = "adminA12!";
		
		String[] pwArray = password.trim().split("\\s+");
		
		if(password.length() < 6 || password.length() > 12 || pwArray.length != 1)
			return -1;
		else if(!(password.equals(PASSWORD)))
			return 0;
		else
			return 1;
	}

}
