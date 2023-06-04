package issac.utils;

import issac.annotation.TransientSink;
import issac.constant.ApplicationConstant;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.reflect.Field;

public class ClickHouseUtil
{
    public static <T> SinkFunction<T> getSinkFunction(String sql)
    {
        JdbcExecutionOptions jdbcExecutionOptions = new JdbcExecutionOptions.Builder()
            .withBatchSize(5).withBatchIntervalMs(1000L).build();
        
        JdbcConnectionOptions jdbcConnectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
            .withDriverName(ApplicationConstant.CLICKHOUSE_DRIVER).withUrl(ApplicationConstant.CLICKHOUSE_URL).build();
        
        return JdbcSink.sink(sql, (JdbcStatementBuilder<T>) (preparedStatement, t) ->
            {
                // 使用反射的方式获取t对象中的数据
                Class<?> tClz = t.getClass();
        
                //获取并遍历属性
                Field[] declaredFields = tClz.getDeclaredFields();
                int offset = 0;
                for (int i = 0; i < declaredFields.length; i++)
                {
                    // 获取单个属性
                    Field field = declaredFields[i];
                    field.setAccessible(true);
            
                    // 尝试获取字段上的自定义注解
                    TransientSink transientSink = field.getAnnotation(TransientSink.class);
                    if (transientSink != null)
                    {
                        offset++;
                        continue;
                    }
            
                    // 获取属性值
                    Object value = null;
                    
                    try { value = field.get(t); } 
                    catch (IllegalAccessException e) { e.printStackTrace(); }
    
                    // 给占位符赋值
                    preparedStatement.setObject(i + 1 - offset, value);
            
                }
            }, jdbcExecutionOptions, jdbcConnectionOptions
        );
    }
}
