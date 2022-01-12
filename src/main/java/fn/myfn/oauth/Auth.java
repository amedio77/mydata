package fn.myfn.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import fn.myfn.comm.CommMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import fn.myfn.comm.Util;

/**
 * Description : 인증 토큰 관리
 * Date : 2022.01.12
 * History :
 * - 작성자 : 이우송, 날짜 : 2022.01.12, 설명 : 최초작성
 *
 * @author 이우송
 * @version 1.0
 */
@Service
public class Auth {


    @Value("${config.user-seq}")
    int iUsrSeq;
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
    @Value("${config.mydata-korea-url}")
    String mUrl;


    private static final Logger LOGGER = LoggerFactory.getLogger(Auth.class);

    @Autowired
    AuthMapper authMapper;

    @Autowired
    CommMapper commMapper;

    /**
     * Description : 인가코드 발글 요청
     * Method Name : authorize
     * date : 2022.01.12
     * author : 이우송
     * history :
     * ----------------------------------------------------------------------------------
     * 변경일                        작성자                              변경내역
     * -------------- -------------- ----------------------------------------------------
     * 2022.01.12  이우송       최초작성
     * ----------------------------------------------------------------------------------
     */
    public String authorize() throws JsonProcessingException {
        Util util = new Util();

        String sStartTime= "";
        String sEndTime = "";
        String orgCode = "";
        String userCi = "";
        String sTrdDt = util.getCurrentDateTime("yyyyMMdd");

        Map<String,String> userData = authMapper.getUserData(iUsrSeq,"");


        orgCode = userData.get("ORG_CODE");
        userCi = userData.get("USER_CI");

        LOGGER.info("userData :: ORG_CODE="+orgCode);
        LOGGER.info("userData :: USER_CI="+userCi);


        sStartTime = util.getCurrentDateTime();
        String sApiTranID   = sFnGuideCode + "M" + sApiCode + util.getCurrentDateTime();
        String sURL = mUrl+"/oauth/2.0/authorize?org_code=" + orgCode + "&response_type=code"
                + "&client_id=" + sClientId + "&redirect_uri=" + sCallbackURL + "&app_scheme=" + appScheme + "&state=manage";

        String sApiParam = "org_code=" + orgCode + "&response_type=code"
                + "&client_id=" + sClientId + "&redirect_uri=" + sCallbackURL + "&app_scheme=" + appScheme + "&state=manage";

        LOGGER.info("userData :: sURL="+sURL);

        String returnToken = "";
        Map<String, String> requestHeaders = new HashMap<>();

        requestHeaders.put("x-user-ci", userCi); // 헤더 추가
        requestHeaders.put("x-api-tran-id", sApiTranID); // 헤더 추가
        requestHeaders.put("X-FSI-MEM-NO", "FSI00000508"); // 헤더 추가
        requestHeaders.put("X-FSI-BUS-SEQ-NO", "81"); // 헤더 추가
        requestHeaders.put("X-FSI-SVC-DATA-KEY", "N"); // 헤더 추가
        requestHeaders.put("X-FSI-UTCT-TYPE", "TGC00001"); // 헤더 추가

        String responseBody = util.sendRequest("GET", sURL, requestHeaders);

        JSONObject jsonObject = util.getJsonObject(responseBody);
        String sAuthCode = jsonObject.get("code").toString();

        sEndTime = util.getCurrentDateTime();
        commMapper.SaveLog(3, iUsrSeq, sOrgCode, sTrdDt, sApiCode, sApiTranID, sURL, sApiParam, sStartTime, sEndTime, false, "");

        return responseBody;
    }

    /**
     * Description : 접근코드 발글 요청
     * Method Name : token
     * date : 2022.01.12
     * author : 이우송
     * history :
     * ----------------------------------------------------------------------------------
     * 변경일                        작성자                              변경내역
     * -------------- -------------- ----------------------------------------------------
     * 2022.01.12  이우송       최초작성
     * ----------------------------------------------------------------------------------
     */
    public String token() throws JsonProcessingException,ParseException {
        String responseBody = authorize();
        System.out.println(responseBody);

        String sAuthCode = "" ; //responseBody 에서 sAuthCode 추출
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(responseBody);
        sAuthCode = (String) jsonObject.get("code");
        System.out.println("sAuthCode="+sAuthCode);

        Util util = new Util();
        String sApiTranID   = sFnGuideCode + "M" +sApiCode+util.getCurrentDateTime();
        String sURL = mUrl+"/oauth/2.0/token";

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

        responseBody = util.sendRequest("POST",sURL,requestHeaders);
        System.out.println("responseBody="+responseBody);

        jsonParser = new JSONParser();
        jsonObject = (JSONObject) jsonParser.parse(responseBody);
        sAuthCode = (String) jsonObject.get("code");
        String sTokenTM = "";
        String sToken = (String) jsonObject.get("ACCESS_TOKEN");
        String sExpires_in = (String) jsonObject.get("EXPIRES_IN");
        String sReToken = (String) jsonObject.get("REFRESH_TOKEN");
        String sReExpires_in = (String) jsonObject.get("REFRESH_TOKEN_EXPIRES_IN");
        String sScope = (String) jsonObject.get("SCOPE");

        authMapper.updateUserData(sTokenTM , sToken , sExpires_in , sReToken,
                sReExpires_in ,sScope , iUsrSeq , sOrgCode);

        return responseBody;

    }

    /**
     * Description : 접근코드 갱신 요청
     * Method Name : tokenRefresh
     * date : 2022.01.12
     * author : 이우송
     * history :
     * ----------------------------------------------------------------------------------
     * 변경일                        작성자                              변경내역
     * -------------- -------------- ----------------------------------------------------
     * 2022.01.12  이우송       최초작성
     * ----------------------------------------------------------------------------------
     */
    public String tokenRefresh() throws JsonProcessingException {
        String responseBody = authorize();
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

        Util util = new Util();
        String sApiTranID   = sFnGuideCode + "M" +sApiCode+util.getCurrentDateTime();


        String sReToken = "";



        String sURL = mUrl+"/oauth/2.0/token";

        String sParam = "org_code=" + sOrgCode + "&grant_type=refresh_token&refresh_token=" + sReToken
                + "&client_id=" + sClientId + "&client_secret=" + sClientSecret;

        sURL = sURL +"?"+ sParam;

        Map<String, String> requestHeaders = new HashMap<>();

        requestHeaders.put("x-user-ci", sUserCi); // 헤더 추가
        requestHeaders.put("x-api-tran-id", sApiTranID); // 헤더 추가
        requestHeaders.put("X-FSI-MEM-NO", "FSI00000508"); // 헤더 추가
        requestHeaders.put("X-FSI-BUS-SEQ-NO", "81"); // 헤더 추가
        requestHeaders.put("X-FSI-SVC-DATA-KEY", "N"); // 헤더 추가
        requestHeaders.put("X-FSI-UTCT-TYPE", "TGC00001"); // 헤더 추가

        responseBody = util.sendRequest("POST",sURL,requestHeaders);
        System.out.println("responseBody="+responseBody);

        return responseBody;

    }


}
