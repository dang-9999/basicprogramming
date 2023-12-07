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
	private List<Menu> menuItems; //메뉴파일의 메뉴정보를 menuObject의 리스트 형태로 저장
	private List<Menu> orderItems; //주문목록의 메뉴정보를 menuObject의 리스트 형태로 저장
	private final String menuFilePath = "menuFile.txt";
	private final String logFilePath = "logFile.txt";
	private final String userFilePath = "userFile.txt";
	private Menu user; //유저의 이름, 쿠폰수량, 누적결제액을 저장
	private TimeManager tm; //시스템의 TimeManager를 전달받아 저장
	private Scanner scan = new Scanner(System.in);
	private String bookmark; //즐겨찾기 데이터
	private final int COUPONPRICE = 1000; //쿠폰의 가치 1000원으로 사용
	private final int COUPONPROVIDE = COUPONPRICE * 10; //쿠폰의 발급조건 10000원에 한장
	private final long TIMEVALIDATE = 30 * 24 * 60 * 60; //쿠폰의 유효기간 30일(30일*24시간*60분*60초)
	private final String DEFAULTUSERNAME = "-";//기본 유저이름(비회원)

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
					this.bookmark = "";
					for (String part : parts) {
						this.bookmark += part+"\t";
					}
					return;
				}
			}
		} catch (Exception e) {
			System.err.println("오류)유저파일을 읽어오는데 실패했습니다");
			System.err.println(e);
		}
	}
	
	private void showMenus() {
		//(2차수정)즐겨찾기 표시 추가
		if(!this.user.getName().equals(DEFAULTUSERNAME))
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
					return this.payItems(1);
				try {
					//(2차수정)즐겨찾기 입력처리 (양수-> 해당 즐겨찾기 주문실행 / 음수-> 해당 즐겨찾기 삭제.)
					int input = Integer.parseInt(parts[0]);
					if(input !=0)
						return runbookmark(input);
				} catch (NumberFormatException e) {
					System.out.println("알림)적절하지 않은 주문 수량입니다.\n알림)주문수량이 메뉴잔량보다 작은 양의정수값을 입력해주세요.");
				}
				System.out.println("알림)올바른 형식이 아닙니다.");
				break;
			case 2: //메뉴주문 입력처리 2개단어
				String inputname = parts[0];
				String inputqStr = parts[1];
				return menuOrder(inputname, inputqStr, 1);
				// break;
			default:
				System.out.println("알림)올바르지 않은 입력입니다.");
		}
		return 0;
	}
	//menu주문 혹은 메뉴의 주문가능여부를 판단하는 함수: inputqStr이 숫자이고 주문가능한 수량인지 체크
	// available이 1이면-> 해당 수만큼 주문을 실행한다.
	// available이 0이면-> 주문가능 여부만 판단하여 반환한다. //0-> 이상없음(주문가능)
	private int menuOrder(String inputname, String inputqStr, int available) {
		try {
			int q = Integer.parseInt(inputqStr);
			if (q == 0)
				return 0; //의미없는 입력
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
							if (sum > 0) {
								if (available > 0)
									return this.addOrderItem(menu, q);
								return 0;
								//음수값 주문
							}
						} else {
							//기존주문과 중북
							//최종주문수량이 적절한 범위.
							if (sum >= 0 && menu.getQuantity() >= sum) {
								if (available > 0)
									return adjOrderItem(menu, q);
								return 0;
							}
							//최종주문수량이 적절하지 않은 주문수량
						}
						//오류실행
					}
					//적절하지 않은 주문수량 0 혹은 잔량이상의 값.
					throw new NumberFormatException();
				}
			}
			//메뉴의 존재를 확인할 수 없음.
			if(available>0)
				System.out.println("알림)메뉴판에 해당 메뉴가 존재하지 않습니다.");
			return -1;
		} catch (NumberFormatException e) {
			if(available>0)
				System.out.println("알림)적절하지 않은 주문 수량입니다.\n알림)주문수량이 메뉴잔량보다 작은 양의정수값을 입력해주세요.");
		}
		
		return 1;
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
	//즐겨찾기의 기능 처리(0이면 show, 1이상이면 즐겨찾기 주문, 음수면)
	private int runbookmark(int input) { //0이면 보여주기, 1이상이면 해당 즐겨찾기 주문
		if (input == 0) {
			String[] parts = this.bookmark.trim().split("\\s+");
			int bookmarkNum = 1;
			if(this.bookmark.equals(""))
				return 0;
			try{
				System.out.println("====================");
				System.out.println("즐겨찾기 목록(메뉴이름x수량)");
				for (String part : parts) {
					int available = 0;
					// System.out.println(part);
					String[] list = part.trim().split(";");
					for (int i = 0; i < list.length; i++) {
						String[] menu = list[i].trim().split("#");
						// System.out.println(menu.length);
						// System.out.println(menu[0] + menu[1] + "보여주기");
						available = menuOrder(menu[0], menu[1], 0);
						if (available != 0)
							break;
					}
					part = part.replace("#", " x").replace(";", ", ");
					// System.out.println(available);
					System.out.println(((available==0)?"> ":(available>0)?"(일부품절)":"(메뉴변경)")+Integer.toString(bookmarkNum++)+" "+part);
				}
			} catch (Exception e) {
				System.err.println("오류)즐겨찾기에 오류가 있습니다.");
			}
		}
		else if(input>0){
			String[] parts = this.bookmark.trim().split("\\s+");
			int bmNum = 1;
			orderItems.clear();//기존 주문정보를 지우고 즐겨찾기의 정보만을 가지고 결제진행.
			for (String part : parts) {
				if (bmNum++ == input) {
					int available = 0;
					// System.out.println(part);
					String[] list = part.trim().split(";");
					for (int i = 0; i < list.length; i++) {
						String[] menu = list[i].trim().split("#");
						// System.out.println(menu.length);
						// System.out.println(menu[3]);
						// System.out.println(menu[0] + menu[1] + "주문가능");
						available += menuOrder(menu[0], menu[1], 0);
					}
					if (available > 0) {
						System.out.println("오류)즐겨찾기의 일부항목이 품절되었습니다. 잔여수량을 확인후 주문해주세요");
						return 0;
					}
					for (int i = 0; i < list.length; i++) {
						String[] menu = list[i].trim().split("#");
						// System.out.println(menu.length);
						// System.out.println(menu[3]);
						// System.out.println(menu[0] + menu[2] + "주문");
						menuOrder(menu[0], menu[1], 1);
					}
					return payItems(0);
				}
			}
			System.out.println("즐겨찾기목록에 존재하지 않는 주문입력입니다. ");
		} else {
			String[] parts = this.bookmark.trim().split("\\s+");
			String newbm = "";
			input *= -1;
			int bmNum = 1;
			int delete = 0;
			for (String part : parts) {
				if (bmNum++ == input) {
					delete++;
					continue;
				}
				newbm += part + "\t";
			}
			if (delete > 0) {
				System.out.print(input);
				System.out.println("번 즐겨찾기 삭제완료.");
				this.bookmark = newbm;
			} else {
				System.out.println("즐겨찾기목록에 존재하지 않는 삭제입력입니다.");
			}
		}
		return 0;
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
	
    private int payItems(int addbm) {
        //총액 구하고 출력
        int totalprice = 0;
        for (Menu item : orderItems)
            totalprice += item.getPrice() * item.getQuantity();
        System.out.print("총");
        System.out.print(totalprice);
        System.out.println("원입니다.");
        //사용할 쿠폰의 정보를 사용자로부터 입력받기
		int useCoupon = this.getNumCouponUse(totalprice);
		if (useCoupon<0)
			return 0;
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
            String bookmarkLine = this.user.getName()+"\t"+this.bookmark;
			String[] bookmark_array;
            String menuItemString = "";
            String tmpData ="";
            if (!this.user.getName().equals(DEFAULTUSERNAME) && addbm>0) { //회원정보가 있으면
                
                //즐겨찾기 여부 물어보기 
                while (true) {
                    System.out.println("현재 주문하신 정보를 즐겨찾기에 추가할까요? (Y or N)");
                    String ans = scan.nextLine();
                    if (ans.equals("Y") || ans.equals("y")) {
						//menuItemString 앞으로 즐겨찾기에 추가할 데이터 
                        bookmark_array = this.bookmark.split("\\s+");
                        
                        for(Menu item : orderItems) {
                            // 각 메뉴 정보를 문자열로 변환
                            menuItemString = item.getName() + "#" + item.getQuantity()+ ";";;

                            // 모든 메뉴 정보를 공백으로 구분하여 한 줄로 합치기
                            bookmarkLine += menuItemString ; 
                            tmpData += menuItemString;
                        }


						//파일데이터와 현재 즐겨찾기에 추가하고 싶은 데이터 비교
						for (String subBookMark : bookmark_array){
                            if (subBookMark.equals(tmpData)){
                                System.out.println("이미 존재하는 즐겨찾기 입니다.");
                                bookmarkLine = this.user.getName()+"\t"+this.bookmark;
                                break;
                            }
                        }


						break;

                    }
                    else if (ans.equals("N") || ans.equals("n")) {break;}
                    else {System.out.println("잘못 입력하셨습니다. 재입력 부탁드립니다. "); continue;}
                }

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
                bufferedWriter.write(bookmarkLine + "\n"); //추가되는내용 여기?
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
        System.out.println("이용해주셔서 감사합니다.");

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
                            return -1;
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
                    return -1;
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

