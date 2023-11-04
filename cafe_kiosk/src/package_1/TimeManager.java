package package_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManager {
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd/HHmmss");
	private long startTime;
	private Date initTime;
	private final String logFilePath = "logFile.txt";

	public TimeManager(){
	}

	public int setInitTime(String initTime){
		try {
			Date inputTime = dateFormat.parse(initTime);
			FileReader fileReader = new FileReader(logFilePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String nline = "", line = "";
			while ((nline = bufferedReader.readLine()) != null) {
				line= nline;
			}
			bufferedReader.close();
			if (!line.equals("")) {
				Date lastTime = dateFormat.parse(line.trim().split("\\s+")[0]);
				// System.out.println(inputTime);
				// System.out.println(lastTime);
				if (lastTime.compareTo(inputTime) >= 0) {
					System.err.println("오류)시스템시간입력 의미오류: 마지막구동시간과 같거나 이릅니다.");
					return 1;
				}
			}
			startTime = System.currentTimeMillis();
			this.initTime = inputTime;
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("오류)시간비교 오류");
		} catch (ParseException e) {
			System.err.println("오류)시스템시간입력 문법오류");
		}
		return 1;
	}
	public String getTimeNow() {
		// 현재 시간을 가져와서 initTime으로부터 경과한 밀리초를 계산
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        // initTime에 경과한 밀리초를 더한 시간을 구함
        Date currentTimeDate = new Date(initTime.getTime() + elapsedTime);

        // 시간을 원하는 형식으로 포맷팅하여 반환
        return dateFormat.format(currentTimeDate);
	}
	public int getFlowTime() {
		// 현재 시간을 가져와서 initTime으로부터 경과한 밀리초를 계산
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

		return (int) elapsedTime;
	}
}
