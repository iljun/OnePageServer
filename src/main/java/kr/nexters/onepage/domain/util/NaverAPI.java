package kr.nexters.onepage.domain.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NaverAPI {
	public static String convertKorToEng(String korName) {
		String clientId = "Y0j1Our0NJo0iZ7ya1ZR";//애플리케이션 클라이언트 아이디값";
		String clientSecret = "_exGuxcsRF";//애플리케이션 클라이언트 시크릿값";
		try {
			String text = URLEncoder.encode(korName, "UTF-8");
			String apiURL = "https://openapi.naver.com/v1/language/translate";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			// post request
			String postParams = "source=ko&target=en&text=" + text;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {  // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();

			JSONObject parse = (JSONObject) JSONValue.parse(response.toString());
			JSONObject message = (JSONObject) parse.get("message");
			JSONObject result = (JSONObject) message.get("result");
			String engName = (String)result.get("translatedText");
			if(engName.substring(engName.length()).equals(",") || engName.substring(engName.length()).equals("."))
				engName= engName.substring(0, engName.length()-1);
			return engName;
		} catch (Exception e) {
			log.error("convertKorToEng : " + e.getMessage(), e);
		}
		return "";
	}
}
