package issac.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前工具类可以适用于任何JDBC方式访问的数据库中的任何查询语句
 * 单行单列：select count(*) from t;
 * 单行多列：select * from t where id='1001'; id为主键
 * 多行单列：select name from t;
 * 多行多列：select * from t;
 * Map<String,List<Object>>
 * List<JSON>
 * List<Map>
 */
public class JdbcUtil
{
    public static <T> List<T> queryList(Connection connection, String sql, Class<T> clz, boolean underScoreToCamel) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException
    {
        ArrayList<T> result = new ArrayList<>();                     // 创建集合用于存放结果数据
        
        PreparedStatement preparedStatement = connection.prepareStatement(sql);     // 编译SQL语句
        
        ResultSet resultSet = preparedStatement.executeQuery();      // 执行查询
        
        ResultSetMetaData metaData = resultSet.getMetaData();        // 获取查询的元数据信息
        int columnCount = metaData.getColumnCount();
        
        // 遍历结果集,将每行数据转换为T对象并加入集合   行遍历
        while (resultSet.next())
        {
            T t = clz.newInstance();                                 // 创建T对象
            
            // 列遍历,并给T对象赋值
            for (int i = 0; i < columnCount; i++)
            {
                // 获取列名与列值
                String columnName = metaData.getColumnName(i + 1);
                Object value = resultSet.getObject(columnName);
                
                // 判断是否需要进行下划线与驼峰命名转换
                if (underScoreToCamel)
                {
                    columnName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName.toLowerCase());
                }
                
                // 赋值
                BeanUtils.setProperty(t, columnName, value);
            }
            
            // 将 T 对象放入集合
            result.add(t);
        }
        
        resultSet.close();
        preparedStatement.close();
        
        // 返回集合
        return result;
    }
}
