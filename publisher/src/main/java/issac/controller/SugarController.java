package issac.controller;

import issac.bean.KeywordStats;
import issac.bean.ProductStats;
import issac.bean.ProvinceStats;
import issac.bean.VisitorStats;
import issac.service.KeywordStatsService;
import issac.service.ProductStatsService;
import issac.service.ProvinceStatsService;
import issac.service.VisitorStatsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 大屏展示的控制层
@RestController
@RequestMapping("/api/sugar")
public class SugarController
{
    @Autowired
    ProductStatsService productStatsService;
    
    @Autowired
    ProvinceStatsService provinceStatsService;
    
    @Autowired
    VisitorStatsService visitorStatsService;
    
    @Autowired
    KeywordStatsService keywordStatsService;
    
    @RequestMapping("/keyword")
    public String getKeywordStats(@RequestParam(value = "date", defaultValue = "0") Integer date, @RequestParam(value = "limit", defaultValue = "20") int limit)
    {
        if (date == 0) { date = now(); }
        
        // 查询数据
        List<KeywordStats> keywordStatsList = keywordStatsService.getKeywordStats(date, limit);
        StringBuilder jsonSb = new StringBuilder("{\"status\":0,\"msg\":\"\",\"data\":[");
        
        // 循环拼接字符串
        for (int i = 0; i < keywordStatsList.size(); i++)
        {
            KeywordStats keywordStats = keywordStatsList.get(i);
            if (i >= 1) {jsonSb.append(",");}
            jsonSb.append("{\"name\":\"").append(keywordStats.getKeyword()).append("\",")
                .append("\"value\":").append(keywordStats.getCt()).append("}");
        }
        
        jsonSb.append("]}");
        return jsonSb.toString();
    }
    
    
    @RequestMapping("/hr")
    public String getVisitorStatsByHr(@RequestParam(value = "date", defaultValue = "0") Integer date)
    {
        if (date == 0) { date = now(); }
        
        // 从service层中获取分时访问数据
        List<VisitorStats> visitorStatsByHrList = visitorStatsService.getVisitorStatsByHr(date);
        
        // 因为有的小时可能没有数据，为了把每个小时都展示出来，我们创建一个数组，用来保存每个小时对应的访问情况
        VisitorStats[] visitorStatsArr = new VisitorStats[24];
        for (VisitorStats visitorStats : visitorStatsByHrList)
        {
            visitorStatsArr[visitorStats.getHr()] = visitorStats;
        }
        
        // 定义存放小时、uv、pv、新用户的List集合
        List<String> hrList = new ArrayList<>();
        List<Long> uvList = new ArrayList<>();
        List<Long> pvList = new ArrayList<>();
        List<Long> newVisitorList = new ArrayList<>();
        
        //对数组进行遍历，将0~23点的数据查询出来，分别放到对应的List集合中保存起来
        for (int i = 0; i <= 23; i++)
        {
            VisitorStats visitorStats = visitorStatsArr[i];
            if (visitorStats != null)
            {
                uvList.add(visitorStats.getUv_ct());
                pvList.add(visitorStats.getPv_ct());
                newVisitorList.add(visitorStats.getNew_uv());
            } else
            {
                uvList.add(0L);
                pvList.add(0L);
                newVisitorList.add(0L);
            }
            
            // 小时位不足2位的时候，前面补0
            hrList.add(String.format("%02d", i));
        }
        // 拼接字符串
        return "{\"status\":0,\"data\":{" + "\"categories\":" +
            "[\"" + StringUtils.join(hrList, "\",\"") + "\"],\"series\":[" +
            "{\"name\":\"uv\",\"data\":[" + StringUtils.join(uvList, ",") + "]}," +
            "{\"name\":\"pv\",\"data\":[" + StringUtils.join(pvList, ",") + "]}," +
            "{\"name\":\"新用户\",\"data\":[" + StringUtils.join(newVisitorList, ",") + "]}]}}";
    }
    
    
    @RequestMapping("/visitor")
    public Map<String, Object> getVisitorStatsByNewFlag(@RequestParam(value = "date", defaultValue = "0") Integer date)
    {
        if (date == 0) { date = now();}
        
        // 调用service层，获取访客统计相关指标数据
        List<VisitorStats> visitorStatsByNewFlagList = visitorStatsService.getVisitorStatsByNewFlag(date);
        
        // 定义两个对象，分别接收新老访客统计的结果
        VisitorStats newVisitorStats = new VisitorStats();
        VisitorStats oldVisitorStats = new VisitorStats();
        
        // 对查询的数据进行遍历，给新老访客统计对象赋值
        for (VisitorStats visitorStats : visitorStatsByNewFlagList)
        {
            if ("1".equals(visitorStats.getIs_new())) { newVisitorStats = visitorStats; } 
            else { oldVisitorStats = visitorStats; }
        }
        
        //返回的 json 字符串的处理
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("status", 0);
        
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("combineNum", 1);
        
        //表头
        List<Map<String, String>> columnList = new ArrayList<>();
        Map<String, String> typeHeader = new HashMap<>();
        typeHeader.put("name", "类别");
        typeHeader.put("id", "type");
        columnList.add(typeHeader);
        
        Map<String, String> newHeader = new HashMap<>();
        newHeader.put("name", "新用户");
        newHeader.put("id", "new");
        columnList.add(newHeader);
        
        Map<String, String> oldHeader = new HashMap<>();
        oldHeader.put("name", "老用户");
        oldHeader.put("id", "old");
        columnList.add(oldHeader);
        dataMap.put("columns", columnList);
        
        // 表格 bady
        List<Map<String, Object>> rowList = new ArrayList<>();
        
        // 用户数
        Map<String, Object> userCount = new HashMap<>();
        userCount.put("type", "用户数(人)");
        userCount.put("new", newVisitorStats.getUv_ct());
        userCount.put("old", oldVisitorStats.getUv_ct());
        rowList.add(userCount);
        
        // 总访问页面
        Map<String, Object> pageTotal = new HashMap<>();
        pageTotal.put("type", "总访问页面(次)");
        pageTotal.put("new", newVisitorStats.getPv_ct());
        pageTotal.put("old", oldVisitorStats.getPv_ct());
        rowList.add(pageTotal);
        
        // 跳出率
        Map<String, Object> jumRate = new HashMap<>();
        jumRate.put("type", "跳出率(%)");
        jumRate.put("new", newVisitorStats.getUjRate());
        jumRate.put("old", oldVisitorStats.getUjRate());
        rowList.add(jumRate);
        
        // 平均在线时长
        Map<String, Object> ageDurTime = new HashMap<>();
        ageDurTime.put("type", "平均在线时长(秒)");
        ageDurTime.put("new", newVisitorStats.getDurPerSv());
        ageDurTime.put("old", oldVisitorStats.getDurPerSv());
        rowList.add(ageDurTime);
        
        // 平均页面访问人数
        Map<String, Object> ageVisitCount = new HashMap<>();
        ageVisitCount.put("type", "平均访问人数(人次)");
        ageVisitCount.put("new", newVisitorStats.getPvPerSv());
        ageVisitCount.put("old", oldVisitorStats.getPvPerSv());
        rowList.add(ageVisitCount);
        
        dataMap.put("rows", rowList);
        resMap.put("data", dataMap);
        return resMap;
    }
    
    
    /**
     * {
     * "status": 0,
     * "data": {
     * "mapData": [
     * {
     * "name": "北京",
     * "value": 7489
     * }
     * ]
     * }
     * }
     */
    @RequestMapping("/province")
    public String getProvinceStats(@RequestParam(value = "date", defaultValue = "0") Integer date)
    {
        if (date == 0) { date = now(); }

        // 从service中获取地区统计数据
        List<ProvinceStats> provinceStatsList = provinceStatsService.getProvinceStats(date);
        StringBuilder jsonBuilder = new StringBuilder("{\"status\": 0,\"data\": {\"mapData\": [");
        
        for (int i = 0; i < provinceStatsList.size(); i++)
        {
            ProvinceStats provinceStats = provinceStatsList.get(i);
            if (i >= 1) { jsonBuilder.append(","); }
            
            jsonBuilder.append("{\"name\": \"").append(provinceStats.getProvince_name())
                .append("\",\"value\": ").append(provinceStats.getOrder_amount()).append("}");
        }
        
        jsonBuilder.append("]}}");
        return jsonBuilder.toString();
    }
    
    
    /**
     * 请求路径
     * $API_HOST/api/sugar/spu?limit=10
     * -返回数据的格式
     * {
     * "status": 0,
     * "data": {
     * "columns": [{
     * "name": "商品SPU名称",
     * "id": "spu_name"
     * },
     * {
     * "name": "交易额",
     * "id": "order_amount"
     * },
     * ],
     * "rows": [
     * {
     * "spu_name": "XXX",
     * "order_amount": "XXX"*
     * }
     * ]
     * }
     * }
     */
    @RequestMapping("/spu")
    public String getProductStatsBySPU(@RequestParam(value = "date", defaultValue = "0") Integer date, @RequestParam(value = "limit", defaultValue = "10") Integer limit)
    {
        if (date == 0) { date = now(); }
        
        // 调用service层方法，获取按spu统计数据
        List<ProductStats> productStatsBySPUList = productStatsService.getProductStatsBySPU(date, limit);
        
        // 初始化表头信息
        StringBuilder jsonBuilder = new StringBuilder("{" +
            "\"status\": 0," +
            "\"data\": {" +
            "\"columns\": [{" +
            "\"name\": \"商品SPU名称\"," +
            "\"id\": \"spu_name\"" +
            "}," +
            "{" +
            "\"name\": \"交易额\"," +
            "\"id\": \"order_amount\"" +
            "}," +
            "{" +
            "\"name\": \"订单数\"," +
            "\"id\": \"order_ct\"" +
            "}" +
            "]," +
            "\"rows\": [");
        
        // 对查询出来的数据进行遍历，将每一条遍历的结果封装为json的一行数据
        for (int i = 0; i < productStatsBySPUList.size(); i++)
        {
            ProductStats productStats = productStatsBySPUList.get(i);
            if (i >= 1) { jsonBuilder.append(","); }

            jsonBuilder.append("{" + "\"spu_name\": \"").append(productStats.getSpu_name()).append("\",")
                .append("\"order_amount\":").append(productStats.getOrder_amount()).append(",")
                .append("\"order_ct\":").append(productStats.getOrder_ct()).append("}");
        }
        
        jsonBuilder.append("]}}");
        return jsonBuilder.toString();
    }
    
    /**
     * 处理请求的路径
     * $API_HOST/api/sugar/category3?limit=5
     * 返回值格式
     * {
     * "status": 0,
     * "data": [
     * {
     * "name": "PC",
     * "value": 97
     * },
     * {
     * "name": "iOS",
     * "value": 50
     * }
     * ]
     * }
     */
    @RequestMapping("/category3")
    public Map<String, Object> getProductStatsByCategory3(@RequestParam(value = "date", defaultValue = "0") Integer date, @RequestParam(value = "limit", defaultValue = "10") Integer limit)
    {
        if (date == 0) { date = now(); }
        
        // 调用service获取品类交易额排行
        List<ProductStats> productStatsByCategory3List = productStatsService.getProductStatsByCategory3(date, limit);
        
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("status", 0);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ProductStats productStats : productStatsByCategory3List)
        {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("name", productStats.getCategory3_name());
            dataMap.put("value", productStats.getOrder_amount());
            dataList.add(dataMap);
        }
        
        resMap.put("data", dataList);
        return resMap;
    }

    /*
        -请求地址
		$API_HOST/api/sugar/trademark?limit=5

	-返回数据的格式
		{
		  "status": 0,
		  "data": {
		    "categories": ["苹果","三星","华为"],
		    "series": [
		      {
		        "data": [9387,8095,8863]
		      }
		    ]
		  }
		}
     */
    /*
    方式1：使用字符串拼接的方式处理返回的json数据
    @RequestMapping("/trademark")
    public String getProductStatsByTrademark(
        @RequestParam(value = "date", defaultValue = "0") Integer date,
        @RequestParam(value = "limit", defaultValue = "10") Integer limit) {

        //如果没有传递日期参数，那么将日期设置为当前日期
        if (date == 0) {
            date = now();
        }
        //调用service根据品牌获取交易额排名
        List<ProductStats> productStatsByTrademarkList = productStatsService.getProductStatsByTrademark(date, limit);

        //定义两个集合，分别存放品牌的名称以及品牌的交易额
        List<String> trademarkNameList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();

        //对获取到的品牌交易额进行遍历
        for (ProductStats productStats : productStatsByTrademarkList) {
            trademarkNameList.add(productStats.getTm_name());
            amountList.add(productStats.getOrder_amount());

        }
        String json = "{" +
            "\"status\": 0," +
            "\"data\": {" +
            "\"categories\": [\"" + StringUtils.join(trademarkNameList, "\",\"") + "\"]," +
            "\"series\": [" +
            "{" +
            "\"data\": [" + StringUtils.join(amountList, ",") + "]" +
            "}]}}";

        return json;
    }*/
    
    // 方式2：封装对象，通过将对象转换的json格式字符串的方式 返回json数据
    @RequestMapping("/trademark")
    public Map<String, Object> getProductStatsByTrademark(@RequestParam(value = "date", defaultValue = "0") Integer date, @RequestParam(value = "limit", defaultValue = "10") Integer limit)
    {
        // 如果没有传递日期参数，那么将日期设置为当前日期
        if (date == 0) { date = now(); }
        
        // 调用service根据品牌获取交易额排名
        List<ProductStats> productStatsByTrademarkList = productStatsService.getProductStatsByTrademark(date, limit);
        
        // 定义两个集合，分别存放品牌的名称以及品牌的交易额
        List<String> trademarkNameList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();
        
        // 对获取到的品牌交易额进行遍历
        for (ProductStats productStats : productStatsByTrademarkList)
        {
            trademarkNameList.add(productStats.getTm_name());
            amountList.add(productStats.getOrder_amount());
        }
        
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("status", 0);
        
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("categories", trademarkNameList);
        
        List<Map<String, Object>> seriesList = new ArrayList<>();
        Map<String, Object> seriesDataMap = new HashMap<>();
        seriesDataMap.put("data", amountList);
        seriesList.add(seriesDataMap);
        dataMap.put("series", seriesList);
        resMap.put("data", dataMap);
        return resMap;
    }
    
    
    /**
     * 请求路径： /api/sugar/gmv
     * 返回值类型：
     * {
     * "status": 0,
     * "msg": "",
     * "data": 1201076.1961842624
     * }
     */
    @RequestMapping("/gmv")
    public String getGMV(@RequestParam(value = "date", defaultValue = "0") Integer date)
    {
        if (date == 0) {date = now(); }
        
        BigDecimal gmv = productStatsService.getGMV(date);
        return "{" + "\"status\": 0," + "\"data\": " + gmv + "}";
    }
    
    private Integer now()
    {
        String yyyyMMdd = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return Integer.valueOf(yyyyMMdd);
    }
}
