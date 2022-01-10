package com.fnguide.mydata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@SpringBootApplication
public class MydataApplication {

    @Value("${config.user-seq}")
    String iUsrSeq;
    @Value("${config.org-code}")
    String sOrgCode;
    @Value("${config.client-id}")
    String sClientId;
    @Value("${config.client-secret}")
    String sClientSecret;
    @Value("${config.callback-url}")
    String sCallbackURL;
    @Value("${config.user-ci}")
    String sUserCi;
    @Value("${config.api-code}")
    String sApiCode;
    @Value("${config.fnguide-code}")
    String sFnGuideCode;
    @Value("${config.app-scheme}")
    String appScheme;


    public static void main(String[] args) {
        SpringApplication.run(MydataApplication.class, args);
        System.out.println("spring boot test !!!");
    }

    @GetMapping("/test")
    public String callAPI() throws JsonProcessingException {

        String clientId = "QVuAYhMTsLm_wmhCfTov";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "1Xi4gaD2dX";//애플리케이션 클라이언트 시크릿값";


        String text = null;
        try {
            text = URLEncoder.encode("비트코인", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;    // json 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = sendRequest("GET", apiURL,requestHeaders);

        System.out.println(responseBody);

        return responseBody;

    }


    @GetMapping("/token")
    public String getToken() throws JsonProcessingException {

        //DateTime.Now.ToString("yyMMddHHmm");
        String sApiTranID   = sFnGuideCode + "M" +sApiCode+getCurrentDateTime();


        String apiURL = "https://developers.mydatakorea.org:9443/oauth/2.0/authorize?org_code=" + sOrgCode + "&response_type=code"
                + "&client_id=" + sClientId + "&redirect_uri=" + sCallbackURL + "&app_scheme=" + appScheme + "&state=manage";

        String returnToken = "";
        Map<String, String> requestHeaders = new HashMap<>();

        requestHeaders.put("x-user-ci", sUserCi); // 헤더 추가
        requestHeaders.put("x-api-tran-id", sApiTranID); // 헤더 추가
        requestHeaders.put("X-FSI-MEM-NO", "FSI00000508"); // 헤더 추가
        requestHeaders.put("X-FSI-BUS-SEQ-NO", "81"); // 헤더 추가
        requestHeaders.put("X-FSI-SVC-DATA-KEY", "N"); // 헤더 추가
        requestHeaders.put("X-FSI-UTCT-TYPE", "TGC00001"); // 헤더 추가

        String responseBody = sendRequest("GET", apiURL,requestHeaders);
        System.out.println(responseBody);

       String sAuthCode = "" ; //responseBody 에서 sAuthCode 추출
       JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(responseBody);
            sAuthCode = (String) jsonObject.get("code");
            System.out.println("sAuthCode="+sAuthCode);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        responseBody = getData(iUsrSeq, sClientId, sClientSecret, sCallbackURL, sOrgCode, sAuthCode);
        System.out.println("responseBody="+responseBody);

        return responseBody;

    }

    public static String getCurrentDateTime() {
        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyMMddHHmm"; //hhmmss로 시간,분,초만 뽑기도 가능....
        SimpleDateFormat formatter = new SimpleDateFormat(pattern,
                currentLocale);
        return formatter.format(today);
    }

    private static String getData(String iUsrSeq, String sClientId, String sClientSecret, String sCallbackURL, String sOrgCode, String sAuthCode){

        String sUserCi       = "NsoFzzUqcMfSseGcFVbkARcukDJtCfxt";
        String sApiCode      = "AU01";
        String sFnGuideCode  = "2208191972";
        String sApiTranID   = sFnGuideCode + "M" +sApiCode+getCurrentDateTime();
        String sURL = "https://developers.mydatakorea.org:9443/oauth/2.0/token";

        String sParam = "org_code=" + sOrgCode + "&grant_type=authorization_code" + "&code=" + sAuthCode
                + "&client_id=" + sClientId + "&client_secret=" + sClientSecret + "&redirect_uri=" + sCallbackURL;

        sURL = sURL +"?"+ sParam;

        Map<String, String> requestHeaders = new HashMap<>();

        requestHeaders.put("x-user-ci", sUserCi); // 헤더 추가
        requestHeaders.put("x-api-tran-id", sApiTranID); // 헤더 추가
        requestHeaders.put("X-FSI-MEM-NO", "FSI00000508"); // 헤더 추가
        requestHeaders.put("X-FSI-BUS-SEQ-NO", "81"); // 헤더 추가
        requestHeaders.put("X-FSI-SVC-DATA-KEY", "N"); // 헤더 추가
        requestHeaders.put("X-FSI-UTCT-TYPE", "TGC00001"); // 헤더 추가

        String responseBody = sendRequest("POST",sURL,requestHeaders);
        System.out.println("getData responseBody::"+responseBody);


        return responseBody;
    }


    private static String sendRequest(String method, String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod(method);
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                System.out.println("get::정상호출");
                return readBody(con.getInputStream());
            } else { // 에러 발생
                System.out.println("get::에러발생");
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
