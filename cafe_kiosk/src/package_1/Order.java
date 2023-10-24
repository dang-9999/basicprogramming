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

public class Order {
	private List<Menu> menuItems;
	private List<Menu> orderItems;
	private String menuFilePath = "menuFile.txt";
	private String logFilePath = "logFile.txt";
	private String userName;
	private TimeManager tm;
	private Scanner scan = new Scanner(System.in);
	
	public Order(TimeManager tm) {
		this.tm = tm;
		//userName초기화
		userName = "-";
		//ItemList초기화
		menuItems = new ArrayList<>();
		orderItems = new ArrayList<>();
		//파일불러오기
		File menuFile = new File(menuFilePath);
		//메뉴Item불러오기
		try (BufferedReader br = new BufferedReader(new FileReader(menuFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    String name = parts[0];
                    int price = Integer.parseInt(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    Menu item = new Menu(name, price, quantity);
                    menuItems.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//		System.out.println("initialize");
	}
	public Order(TimeManager tm, String uN) {
		this(tm);
		this.userName = uN;
	}
	
	public void run() {
		while(true) {
			showMenus();
			if(getInput()==1) break;
		}
	}
	private void showMenus() {
		System.out.println("====================");
		System.out.println("메뉴\t가격\t메뉴잔량");
		if(menuItems.size()>0) {
			for(Menu item: menuItems) {
				System.out.println(item.toString());
			}
		}
		System.out.println("====================");
		if(orderItems.size()>0) {
			System.out.println("주문목록\t가격\t주문수량");
			int priceSum = 0;
			for(Menu item: orderItems) {
				System.out.println(item.toString());
				priceSum+=item.getQuantity()*item.getPrice();
			}
			System.out.print("합계: ");
			System.out.println(priceSum);
			System.out.println("====================");
		}
		System.out.println("메뉴를 주문하려면 \"{메뉴이름} {수량}\", 결제하려면 \"결제하기\"를 입력해주세요");
	}
	private int getInput() {
		System.out.print(">");
		String userInput = this.scan.nextLine();
        String[] parts = userInput.trim().split("\\s+");
    	System.out.println(parts.length);
        switch(parts.length) {
        case 1:
        	if(parts[0].equals("결제하기"))
        		return this.payItems();
        	System.out.println("알림)올바른 형식이 아닙니다.");
        	break;
        case 2:
        	String inputname = parts[0];
        	String inputqStr = parts[1];
        	try {
        		int q = Integer.parseInt(inputqStr);
        		for(Menu menu: menuItems) {
        			if(menu.getName().equals(inputname)) {
        				//메뉴판에 존재하는 메뉴입력
        				if(menu.getQuantity()>q && q!=0) {
        					//적절한 주문수량
        					int sum = q;
        					for(Menu item: orderItems) {
        						if(item.getName().equals(inputname)) {
        							//기존주문수량과의 합에따른 예외처리
        							sum +=item.getQuantity();
        							break;
        						}
        					}
        					if(sum==q) {
        						//기존주문과 중복없음.
        						if(sum>0)
                					return this.addOrderItem(menu, q);
        						//음수값 주문
        					}else {
        						//기존주문과 중북
        						//최종주문수량이 적절한 범위.
        						if(sum>=0 && menu.getQuantity()>=sum) {
        							return adjOrderItem(menu, q);
        						}
        						//최종주문수량이 적절하지 않은 주문수량
        					}
    						//오류실행
        				}
        				//적절하지 않은 주문수량 0 혹은 잔량이상의 값.
        				throw new NumberFormatException();
        			}
        		}
        		System.out.println("알림)메뉴판에 해당 메뉴가 존재하지 않습니다.");
        	}catch(NumberFormatException e){
        		System.out.println("알림)적절하지 않은 주문 수량입니다.\n알림)주문수량이 메뉴잔량보다 작은 양의정수값을 입력해주세요.");
        	}
        	break;
        default:
        	System.out.println("알림)올바르지 않은 입력입니다.");
        }
		return 0;
	}
	private int adjOrderItem(Menu menu, int q) {
		//기존주문 변경
		for(Menu item: orderItems) {
			if(item.getName().equals(menu.getName())) {
				item.setQuantity(item.getQuantity()+q);
				//최종주문수량이 0이면 주문목록에서 삭제
				if(item.getQuantity()==0)
					orderItems.remove(item);
				return 0;
			}
		}
		throw new NumberFormatException();
	}
	private int addOrderItem(Menu menu, int quantity) {
		//신규주문
		Menu orderItem = new Menu(menu.getName(), menu.getPrice(), quantity);
		orderItems.add(orderItem);
		return 0;
	}
	private int payItems() {
		//총액 구하고 출력
		int totalprice = 0;
		for(Menu item: orderItems) {
			totalprice+= item.getPrice()*item.getQuantity();
		}
		System.out.print("총");
		System.out.print(totalprice);
		System.out.println("원입니다.");
		//결제방법 선택
		while(true) {
			System.out.println("결제하기)결제방법을 입력해주세요\n매장식사>yes/포장하기>no/취소하기>(공백)");
			System.out.print(">");
			String userInput = this.scan.nextLine().trim();
			if(userInput.equals("")) return 0;
			else if(userInput.equals("yes")) break;
			else if(userInput.equals("no")) break;
			System.out.println("알림)적절한 입력이 아닙니다.");
		}
//		while(true) {
//			System.out.print("결제수단을 선택해주세요: 카드/현금\n>");
//			String userInput = scan.nextLine().trim();
//			if(userInput.equals("카드")) break;
//			else if(userInput.equals("현금")) break;
//			System.out.println("알림)적절한 입력이 아닙니다.");
//		}
		//로그내용 작성 및 메뉴리스트 수정
		String log = "";
		String timeStr = this.tm.getTimeNow();
		for(Menu item: orderItems) {
			log+= timeStr+"\t"+this.userName+"\t"+item.getName()+"\t"+item.getQuantity()+"\n";
			totalprice+= item.getPrice()*item.getQuantity();
			for(Menu menu: menuItems) {
				if(item.getName().equals(menu.getName())) {
					menu.setQuantity(menu.getQuantity()-item.getQuantity());
					break;
				}
			}
		}
		try {
			//메뉴파일 작성
			String line = "";
			for(Menu menu: menuItems) {
				line += menu.toString()+"\n";
			}
			FileWriter fileWriter = new FileWriter(menuFilePath);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(line);
            bufferedWriter.close();
            // 로그파일 읽기
            FileReader fileReader = new FileReader(logFilePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder logFileCont = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                logFileCont.append(line).append("\n");
            }
            bufferedReader.close();

            // 로그파일 쓰기
            fileWriter = new FileWriter(logFilePath);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(logFileCont.toString());	//기존파일내용
            bufferedWriter.write(log);						//추가되는내용
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		//
		System.out.println("이용해주셔서 감사합니다.");
		return 1;
	}
}
