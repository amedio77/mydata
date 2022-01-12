package fn.myfn.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Description : 인증 토큰 관리 컨트롤러
 * Date : 2022.01.12
 * History :
 * - 작성자 : 이우송, 날짜 : 2022.01.12, 설명 : 최초작성
 *
 * @author 이우송
 * @version 1.0
 */
@RestController
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    Auth auth;

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
    @GetMapping("/oauth/2.0/authorize")
    public String authorize() throws JsonProcessingException {

        String responseBody = auth.authorize();

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
    @GetMapping("/oauth/2.0/token")
    public String token() throws JsonProcessingException {

        String responseBody = auth.token();

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
    @GetMapping("/oauth/2.0/token/refresh")
    public String tokenRefresh() throws JsonProcessingException {

        String responseBody = auth.tokenRefresh();

        return responseBody;

    }


}
