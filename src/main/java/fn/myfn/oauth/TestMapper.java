package fn.myfn.oauth;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    String getCurrentTime();
}
