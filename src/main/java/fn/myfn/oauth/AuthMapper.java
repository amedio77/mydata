package fn.myfn.oauth;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface AuthMapper {
    String getCurrentTime();
    Map getUserData(int iUsrSeq, String sOrgCode);
}
