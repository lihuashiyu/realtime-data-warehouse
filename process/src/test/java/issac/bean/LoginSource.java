package issac.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * ********************************************************************
 * ProjectName   ：  test
 * Package       ：  com.yuanwang.utils
 * ClassName     ：  LoginSource
 * CreateTime    ：  2023-04-18 14:31
 * IDE           ：  IntelliJ IDEA 2022.3.2
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  LoginSource 被用于 ==>
 * ********************************************************************
 */
@Slf4j
public class LoginSource extends RichSourceFunction<LoginEvent>
{
    List<String> loginStatusList;
    List<Integer> userIdList;
    List<String> userNameList;
    List<Integer> ageList;
    List<Integer> scoreList;
    List<Integer> baseFlowList;
    List<Integer> otherFlowList;
    
    Random random;
    Boolean isRunning;
    
    
    @Override
    public void open(Configuration parameters) throws Exception
    {
        super.open(parameters);
        
        loginStatusList = Arrays.asList("success", "failed", "unknown");
        userIdList = Arrays.asList(102, 103, 104, 105, 106, 107);
        userNameList = Arrays.asList("王二", "张三", "李四", "马五", "赵六", "田七");
        ageList = Arrays.asList(20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42);
        scoreList = Arrays.asList(30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90);
        baseFlowList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100);
        otherFlowList = Arrays.asList(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384);
        
        random = new Random();
        isRunning = true;
    }
    
    
    @Override
    public void run(SourceContext<LoginEvent> ctx) throws Exception
    {
        long startTime = System.currentTimeMillis();
        long sleepTime = 1000L;
        
        while (isRunning)
        {
            int index = random.nextInt(5);
            int userId = userIdList.get(index);
            String userName = userNameList.get(index);
            
            int age = ageList.get(random.nextInt(22));
            int score = scoreList.get(random.nextInt(12));
            int flow = baseFlowList.get(random.nextInt(27)) * otherFlowList.get(13);
            
            String status = loginStatusList.get(random.nextInt(2));
            
            long loginTime = System.currentTimeMillis();
            long time = (loginTime - startTime) / sleepTime;
            
            ctx.collect(new LoginEvent(userId, userName, age, score, flow, status, time, loginTime));
            
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        }
    }
    
    
    @Override
    public void cancel()
    {
        isRunning = false;
        
        loginStatusList = new ArrayList<>();
        userIdList = new ArrayList<>();
        userNameList = new ArrayList<>();
        ageList = new ArrayList<>();
        scoreList = new ArrayList<>();
        baseFlowList = new ArrayList<>();
        otherFlowList = new ArrayList<>();
    }
}
