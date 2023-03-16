package interceptor;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TimeStampInterceptor implements Interceptor
{
    @Override
    public void initialize() { }
    
    
    // 将日志拦下，取出 header 里面的 key，取出 body 里面的对应的日志时间；  将 ts 的值赋值给 header 的 key  timestamp
    @Override
    public Event intercept(Event event)
    {
        // 1 获取 header 头
        Map<String, String> headers = event.getHeaders();
        
        // 2 获取 body 中的 ts
        byte[] body = event.getBody();
        String log = new String(body, StandardCharsets.UTF_8);
    
        // 3 将 json 数据转为 对象
        JSONObject jsonObject = JSONObject.parseObject(log);
        String ts = jsonObject.getString("ts");
        
        // 4 将 ts 赋值给 timestamp
        headers.put("timestamp", ts);
        
        return event;
    }
    
    
    @Override
    public List<Event> intercept(List<Event> eventList)
    {
        for (Event event : eventList) { intercept(event); }
        return eventList;
    }
    
    
    @Override
    public void close() { }
    
    
    public static class Builder implements Interceptor.Builder
    {
        @Override
        public Interceptor build() { return new TimeStampInterceptor(); }
        
        @Override
        public void configure(Context context) { }
    }
}
