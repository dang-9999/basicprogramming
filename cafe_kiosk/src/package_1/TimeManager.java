package package_1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManager {
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd/HHmmss");
	long startTime;
	Date initTime;
	public TimeManager(String initTime) throws ParseException {
		startTime = System.currentTimeMillis();
		this.initTime = dateFormat.parse(initTime);
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
}
