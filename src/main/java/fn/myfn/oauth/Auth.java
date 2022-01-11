package fn.myfn.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fn.myfn.comm.Util;

@RestController
public class Auth {

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

    @Autowired
    TestMapper testMapper;

    @GetMapping("/oauth/2.0/token")
    public String token() throws JsonProcessingException {

        System.out.println(" DB에서 가지고온 현재시간 : " + testMapper.getCurrentTime() );

        Util util = new Util();

        String sApiTranID   = sFnGuideCode + "M" +sApiCode+util.getCurrentDateTime();


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

        String responseBody = util.sendRequest("GET", apiURL,requestHeaders);
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



    private  String getData(String iUsrSeq, String sClientId, String sClientSecret, String sCallbackURL, String sOrgCode, String sAuthCode){
        Util util = new Util();
        String sApiTranID   = sFnGuideCode + "M" +sApiCode+util.getCurrentDateTime();
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

        String responseBody = util.sendRequest("POST",sURL,requestHeaders);
        System.out.println("getData responseBody::"+responseBody);


        return responseBody;
    }



}
