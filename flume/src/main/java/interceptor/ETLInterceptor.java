package interceptor;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class ETLInterceptor implements Interceptor
{
    @Override
    public void initialize() { }
    
    
    // 过滤 event 中的数据是否为 json 格式
    @Override
    public Event intercept(Event event)
    {
        byte[] body = event.getBody();                                         // 1. 获取 body 中的数据
        String log = new String(body, StandardCharsets.UTF_8);
        
        if (JSONUtils.isJSONValidate(log)) { return event; }                   // 2. 校验是否为 json 
        else { return null; }
    }
    
    
    // 将处理过之后为 null 的 event 删除掉
    @Override
    public List<Event> intercept(List<Event> list)
    {
        Iterator<Event> iterator = list.iterator();
        while (iterator.hasNext())
        {
            Event event = iterator.next();
            if (ObjectUtils.isEmpty(event))
            {
                iterator.remove();
            }
        }
        
        // list.removeIf(next -> ObjectUtils.isEmpty(intercept(next)));
        // list.removeIf(ObjectUtils::isEmpty);
        
        return list;
    }
    
    
    @Override
    public void close() { }
    
    
    public static class Builder implements Interceptor.Builder
    {
        @Override
        public Interceptor build() { return new ETLInterceptor(); }
        
        @Override
        public void configure(Context context) { }
    }
}
