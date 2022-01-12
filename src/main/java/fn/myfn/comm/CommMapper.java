package fn.myfn.comm;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface CommMapper {
    void SaveLog(int iLogType, int iUsrSeq, String sOrgCode, String sTrdDt,
                 String sApiCode, String sApiTranID, String sApiUrl, String sApiParam,
                 String sStartTime, String sEndTime, Boolean bErrYn, String sRmrk);
}
