package package_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Order {
	private List<Menu> menuItems;
	private List<Menu> orderItems;
	private final String menuFilePath = "menuFile.txt";
	private final String logFilePath = "logFile.txt";
	private final String userFilePath = "userFile.txt";
	private Menu user;
	private TimeManager tm;
	private Scanner scan = new Scanner(System.in);
	private String wishList;
	private final int COUPONPRICE = 1000;
	private final int COUPONPROVIDE = COUPONPRICE * 10;
	private final long TIMEVALIDATE = 30 * 24 * 60 * 60;
	private final String DEFAULTUSERNAME = "-";
	//생성자: 비회원 로그인(기본값)
	public Order(TimeManager tm) {
		this.tm = tm;
		//userName초기화
		user = new Menu(DEFAULTUSERNAME, 0, 0);
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
			System.err.println("오류)메뉴파일을 읽어오는데 실패했습니다");
			System.err.println(e);
		}
	}
	//생성자: 회원 로그인(비회원로그인+ 유저이름, 유저세팅)
	public Order(TimeManager tm, String uN) {
		this(tm);
		this.setUser(uN);
	}
	//(2차수정)유저파일 정보 가져오기 -> 즐겨찾기 불러오기
	private void setUser(String uN) {
		this.user.setName(uN);
		File userFile = new File(userFilePath);
		try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.trim().split("\\s+");
				if (parts[0].equals(this.user.getName())) {
					parts = Arrays.copyOfRange(parts, 1, parts.length);
					this.wishList = "";
					for (String part : parts) {
						this.wishList += part+"/t";
					}
					return;
				}
			}
		} catch (Exception e) {
			System.err.println("오류)유저파일을 읽어오는데 실패했습니다");
			System.err.println(e);
		}
	}

	private void runbookmark(int input) { //0이면 보여주기, 1이상이면 해당 즐겨찾기 주문
		if (input == 0) {
			String[] parts = this.wishList.trim().split("\\s+");
			int i = 1;
			for (String part : parts) {
				int available = 0;
				String[] list = part.trim().split(";");
				for (String item : list) {
					String menu = item.trim().split("|")[0];
					int q = Integer.parseInt(item.trim().split("|")[1]);
					available = menuOrder(menu, q, 0);
				}
				part.replace("|", " x").replace(";", ", ");
				System.out.println("> "+Integer.toString(i)+part+((available==1)?"":"\t(일부매진)"));
			}
		}
		else if(input>0){
			String[] parts = this.wishList.trim().split("\\s+");
			int i = 1;
			for (String part : parts) {
				if (i++ == input) {
					String[] list = part.trim().split(";");
					for (String item : list) {
						
					}
					
				}
			}
		} else {

		}
	}

	//(2차수정)판매로그에서 쿠폰개수 구하기-> return값: 변경있으면 1
	//완료
	private int getCoupon() {
		if (this.user.getName().equals(DEFAULTUSERNAME))
			return 0;
		int hasChanged = 0;
		int totalCoupon = 0;
		int totalMoney = 0;
		File logFile = new File(logFilePath);
		try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.trim().split("\\s+");
				if (parts.length == 4) {// 시간 전화번호 메뉴이름 주문수량 
					Date time = tm.matchTimeFormat(parts[0], 1);
					if (tm.compareTime(time) < TIMEVALIDATE) { // 시간 비교
						if (parts[1].equals(this.user.getName())) { // 전화번호 비교
							if (parts[2].equals("쿠폰발행")) {
								totalCoupon++;
								totalMoney = Integer.parseInt(parts[3]);
							} else if (parts[2].equals("결제완료")) {
								totalMoney += Integer.parseInt(parts[3]);
							} else if (parts[2].equals("쿠폰사용")) {
								Date timeUsed = tm.matchTimeFormat(parts[3], 1);
								if (tm.compareTime(timeUsed) < TIMEVALIDATE && tm.compareTime(timeUsed) > 0) {
									totalCoupon--;
								}
							}
						}
					}
				}
			}
			if (this.user.getQuantity() != totalCoupon)
				hasChanged++;
			// System.out.println(Integer.toString(totalMoney)+"\n"+Integer.toString(totalMoney));
			this.user.setPrice(totalMoney);
			this.user.setQuantity(totalCoupon);

		} catch (Exception e) {
			System.err.println("오류)로그파일을 읽어오는데 실패했습니다");
			System.err.println(e);
		}
		return hasChanged;
	}
	//쿠폰의 미사용 발행일자들을 반환하는 메소드
	//완료
	private List<Date> getCouponDates() {
		if(this.user.getName().equals(DEFAULTUSERNAME)) return null;
		List<Date> CouponDate = new ArrayList<>();
		File logFile = new File(logFilePath);
		try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.trim().split("\\s+");
				if (parts.length == 4) {//시간 전화번호 메뉴이름(결제완료 쿠폰발행 쿠폰사용) 인자
					Date time = tm.matchTimeFormat(parts[0],1);
					if (tm.compareTime(time) < TIMEVALIDATE && parts[1].equals(this.user.getName())) {
						if (parts[2].equals("쿠폰발행")) {
							//유효기간 내 쿠폰발행확인
							CouponDate.add(time);
						}
						else if (parts[2].equals("쿠폰사용")) {
							Date timeUsed = tm.matchTimeFormat(parts[3],1);
							if (tm.compareTime(timeUsed) < TIMEVALIDATE && tm.compareTime(timeUsed) > 0) {
								//유효기간 내 발행된 쿠폰의 사용확인
								int removed = 0;
								for (Date date : CouponDate) {
									if (date.equals(timeUsed)) {
										CouponDate.remove(date);
										removed++;
										break;
									}
								}
								if (removed == 0) {
									//사용된 쿠폰은 있으나 발행된 쿠폰이 없음. 오류
									System.err.println("오류)쿠폰사용 로그에 이상이 있습니다.");
									return null;
								}
							}
						}
						
					}
				}
			}
		} catch (Exception e) {
			System.err.println("오류)쿠폰정보를 읽어오는데 실패했습니다");
			System.err.println(e);
		}
		return CouponDate;
	}
	
	private void showMenus() {
		System.out.println("====================");
		//(2차수정)즐겨찾기 표시 추가
		runbookmark(0);
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

	public int run() {
		showMenus();
		System.out.print(">");
		String userInput = this.scan.nextLine();
		String[] parts = userInput.trim().split("\\s+");
		// System.out.println(parts.length);
		switch (parts.length) {
			case 1:
				if (parts[0].equals("결제하기"))
					return this.payItems();
				try {
					//(2차수정)즐겨찾기 입력처리
					int input = Integer.parseInt(parts[0]);

				} catch (NumberFormatException e) {
					System.out.println("알림)적절하지 않은 주문 수량입니다.\n알림)주문수량이 메뉴잔량보다 작은 양의정수값을 입력해주세요.");
				}
				System.out.println("알림)올바른 형식이 아닙니다.");
				break;
			case 2:
				String inputname = parts[0];
				String inputqStr = parts[1];
				try {
					int q = Integer.parseInt(inputqStr);
					if (q == 0)
						return 0; //의미없는 입력
					menuOrder(inputname, q, 1);

					System.out.println("알림)메뉴판에 해당 메뉴가 존재하지 않습니다.");
				} catch (NumberFormatException e) {
					System.out.println("알림)적절하지 않은 주문 수량입니다.\n알림)주문수량이 메뉴잔량보다 작은 양의정수값을 입력해주세요.");
				}
				break;
			default:
				System.out.println("알림)올바르지 않은 입력입니다.");
		}
		return 0;
	}

	private int menuOrder(String inputname, int q, int available) {
		for (Menu menu : menuItems) {
			if (menu.getName().equals(inputname)) {
				//메뉴판에 존재하는 메뉴입력
				if (menu.getQuantity() >= q) {
					//적절한 주문수량
					int sum = q;
					for (Menu item : orderItems) {
						if (item.getName().equals(inputname)) {
							//기존주문수량과의 합에따른 예외처리
							sum += item.getQuantity();
							break;
						}
					}
					if (sum == q) {
						//기존주문과 중복없음.
						if (sum > 0){
							if(available>0)
								return this.addOrderItem(menu, q);
							return 1;
						//음수값 주문
						}
					} else {
						//기존주문과 중북
						//최종주문수량이 적절한 범위.
						if (sum >= 0 && menu.getQuantity() >= sum) {
							if(available>0)
								return adjOrderItem(menu, q);
							return 1;
						}
						//최종주문수량이 적절하지 않은 주문수량
					}
					//오류실행
				}
				//적절하지 않은 주문수량 0 혹은 잔량이상의 값.
				throw new NumberFormatException();
			}
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
		for (Menu item : orderItems)
			totalprice += item.getPrice() * item.getQuantity();
		System.out.print("총");
		System.out.print(totalprice);
		System.out.println("원입니다.");
		//사용할 쿠폰의 정보를 사용자로부터 입력받기
		int useCoupon = this.getNumCouponUse(totalprice);
		//쿠폰사용 적용하고 적용내역 출력하기
		//this.user.setQuantity(this.user.getQuantity()+ (this.user.getPrice() % COUPONPROVIDE + totalprice) / COUPONPROVIDE - useCoupon);
		//this.user.setPrice(this.user.getPrice() + ((totalprice > useCoupon * COUPONPRICE) ? totalprice : 0));

		//로그내용 작성 및 메뉴리스트 수정
		String log = "";
		String timeStr = this.tm.getTimeNow();
		for (Menu item : orderItems) {
			log += timeStr + "\t" + this.user.getName() + "\t" + item.toLogString();
			// totalprice += item.getPrice() * item.getQuantity();
			for (Menu menu : menuItems) {
				if (item.getName().equals(menu.getName())) {
					menu.setQuantity(menu.getQuantity() - item.getQuantity());
					break;
				}
			}
		}
		//쿠폰사용 로그추가
		List<Date> couponDates = this.getCouponDates();
		// System.out.println(couponDates);
		while (useCoupon-- > 0) {
			log += timeStr + "\t" + this.user.getName() + "\t쿠폰사용\t" + tm.toDateFormat(couponDates.get(0))+"\n";
			couponDates.remove(0);
			totalprice -= COUPONPRICE;
		}
		//결제완료 로그추가
		if(totalprice>0)
			log += timeStr + "\t" + this.user.getName() + "\t결제완료\t" + Integer.toString(totalprice) + "\n";
		//쿠폰발행 로그추가
		totalprice += this.user.getPrice();
		while (totalprice > COUPONPROVIDE) {
			log += timeStr + "\t" + this.user.getName() + "\t쿠폰발행\t" + Integer.toString(totalprice -= 10000) + "\n";
		}
		
		if(this.user.getName().equals(DEFAULTUSERNAME))
		while (true) {
			System.out.println("현재 주문하신 정보를 즐겨찾기에 추가할까요? (Y or N)");
			String ans = scan.nextLine().toUpperCase();
			if (ans.equals("Y")) {
				//즐겨찾기 추가
				break;
			} else if (ans.equals("N")) {

				break;
			} else {
				System.out.println("잘못 입력하셨습니다. 재입력 부탁드립니다. ");
				continue;
			}
		}
		System.out.println("이용해주셔서 감사합니다.");
		//파일 관리
		try {
			FileWriter fileWriter;
			FileReader fileReader;
			BufferedWriter bufferedWriter;
			BufferedReader bufferedReader;
			String line = "";
			//메뉴파일 작성
			for (Menu menu : menuItems) {
				line += menu.toString() + "\n";
			}
			fileWriter = new FileWriter(menuFilePath);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(line);
			bufferedWriter.close();

			//회원정보 수정하기
			if (!this.user.getName().equals(DEFAULTUSERNAME)) {
				fileReader = new FileReader(userFilePath);
				bufferedReader = new BufferedReader(fileReader);
				StringBuilder userFileCont = new StringBuilder();
				while ((line = bufferedReader.readLine()) != null) {
					if (line.trim().split("\\s+")[0].equals(user.getName())) {
						continue;
					}
					userFileCont.append(line).append("\n");
				}
				bufferedReader.close();
				//
				fileWriter = new FileWriter(userFilePath);
				bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(userFileCont.toString()); //기존파일내용
				// System.out.println(user.toString()+"\n");
				bufferedWriter.write(user.toString() + "\n"); //추가되는내용
				bufferedWriter.close();
				fileWriter.close();
			}

			// 로그파일 읽기
			fileReader = new FileReader(logFilePath);
			bufferedReader = new BufferedReader(fileReader);
			StringBuilder logFileCont = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				logFileCont.append(line).append("\n");
			}
			bufferedReader.close();

			// 로그파일 쓰기
			fileWriter = new FileWriter(logFilePath);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(logFileCont.toString()); //기존파일내용
			bufferedWriter.write(log); //추가되는내용
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//
		return 1;
	}
	//사용자로부터 사용할 쿠폰 개수와 결제방법을 입력받는 메소드
	private int getNumCouponUse(int totalprice) {
		int useCoupon = 0;
		this.getCoupon();
		while (true) {
			//쿠폰보유확인
			// int cntCouponHas = this.user.getPrice()/COUPONPROVIDE - this.user.getQuantity();
			int cntCouponHas = this.user.getQuantity();
			if (cntCouponHas < 0) {
				System.err.println("오류)쿠폰개수 오류 결제에 실패했습니다.");
				return 1;
			}
			//회원에 한해 쿠폰보유량 표시
			if (!this.user.getName().equals(DEFAULTUSERNAME)) {
				//(2차수정) 쿠폰 날짜와 함께 표시
				List<Date> couponDates = this.getCouponDates();
				if (couponDates != null) {
					System.out.println("=보유한 쿠폰 및 잔여기간=");
					System.out.print("보유한쿠폰개수: ");
					System.out.println(cntCouponHas);
					for (Date date : couponDates) {
						System.out.print("발급일:");
						System.out.print(tm.toDateFormat(date));
						System.out.print("\t만료일");
						System.out.println(tm.toDateFormat(new Date(date.getTime()+TIMEVALIDATE*1000)));
					}
				}
			}
			if (cntCouponHas > 0 && totalprice > 0) { //쿠폰개수가 0이상, 쿠폰으로 결제할 금액이 존재하는지.
				while (true) {
					System.out.print("쿠폰이 사용가능합니다. 사용할 쿠폰개수를 입력해주세요.(공백>취소하기)\n최대사용가능한 쿠폰개수: ");
					int MaxUsableCoupan = totalprice / COUPONPRICE + ((totalprice % COUPONPRICE == 0) ? 0 : 1);
					System.out.print(MaxUsableCoupan);
					System.out.print("\n>");
					String userInput = this.scan.nextLine();
					String[] parts = userInput.trim().split("\\s+");

					try {
						useCoupon = Integer.parseInt(parts[0]);
						if (parts.length == 1) {
							//사용할쿠폰이 최대사용가능개수, 보유개수이하, 0이상일경우 적절한 입력
							if (MaxUsableCoupan >= useCoupon && cntCouponHas >= useCoupon && useCoupon > 0) {

								System.out.print("쿠폰적용개수: ");
								System.out.print(useCoupon);
								System.out.print(" 쿠폰적용후 결제금액: ");
								System.out.println(totalprice -= useCoupon * COUPONPRICE);
								break;
							}
							if (useCoupon == 0)
								break;
						}
					} catch (NumberFormatException e) {
						if (parts[0] == "") //취소입력
							return 0;
						System.out.println("규칙에 어긋나는 키 입력입니다.");
						continue;
					}
					System.out.println("올바르지 않은 쿠폰 수량 입력입니다.");
				}
			}
			//결제방법 선택
			while (true) {
				System.out.print("결제하기)결제방법을 입력해주세요\n(카드/현금)(공백>취소하기)\n>");
				String userInput = this.scan.nextLine().trim();
				if (userInput.equals(""))
					return 0;
				else if (userInput.equals("카드"))
					break;
				else if (userInput.equals("현금"))
					break;
				System.out.println("\"카드\"혹은 \"현금\"으로 입력해주세요.");
			}
			//쿠폰정보 변경확인
			if (this.getCoupon() == 0 || this.user.getName().equals(DEFAULTUSERNAME))
				break;
			System.out.println("오류)쿠폰정보가 변경되었습니다. 쿠폰정보를 다시 확인하고 진행해주세요.");
		}
		return useCoupon;
	}
}
