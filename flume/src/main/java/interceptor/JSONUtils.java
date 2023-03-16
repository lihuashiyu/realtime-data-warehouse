package interceptor;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONUtils
{
    // 通过异常捕捉，验证数据是否为 json
    public static boolean isJSONValidate(String logString)
    {
        try
        {
            JSONObject.parse(logString);
            return true;
        } catch (JSONException e) 
        {
            log.warn("数据 {} 非法", logString);
            return false; 
        }
    }
}
