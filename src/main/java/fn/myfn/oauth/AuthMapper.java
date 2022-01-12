package fn.myfn.oauth;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface AuthMapper {

    String getCurrentTime();

    Map getUserData(int iUsrSeq, String sOrgCode);

    void updateUserData(String  sTokenTM , String sToken , String sExpires_in , String  sReToken,
            String sReExpires_in ,String sScope ,int  iUsrSeq , String  sOrgCode);
}
