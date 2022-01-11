package fn.myfn;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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


@SpringBootApplication
public class MydataApplication {




    public static void main(String[] args) {
        SpringApplication.run(MydataApplication.class, args);
        System.out.println("spring boot test !!!");
    }
/*

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
*/

}
