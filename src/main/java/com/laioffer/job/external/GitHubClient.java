package com.laioffer.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class GitHubClient {
    // 发送请求都用↓作为URL template模板
    private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEFAULT_KEYWORD = "";

    // Implement search function有了↓，对API来说可以发送http请求。发送请求至Github，返回一个string
    // 目前返回String，但后面会转换成Java model class
    public List<Item> search(double lat, double lon, String keyword) {
        // sanity check (corner case)如果没有输入keyword，设为默认值
        if (keyword == null) {
            keyword = DEFAULT_KEYWORD;
        }

        // 编码(把文字变成代码,空格变成%20),才能变成一个valid的URL请求
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 填充通配符，建立完整的URL(↓创建格式化的字符串以及连接多个字符串对象)
        String url = String.format(URL_TEMPLATE, keyword, lat, lon);

        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 先执行这个请求(57行execute),再去用（43-54行创建的）handler。所以需要call back response
        // 用lambda function来create response handler（可用匿名类override function代替）
        // 是synchronized。虽然是call back，但其实是个blocking call
        ResponseHandler<List<Item>> responseHandler = response -> {//请求已发生，已得到响应了
            //已知要返回String↑。若有以下几种情况，都只返回空字符串
            if (response.getStatusLine().getStatusCode() != 200) {
                // return empty时无需创建新的实例，直接用emptyList()即可。
                // public static final↓，无法修改。语义上,search也不需要你修改
                // 如果别人尝试修改/塞东西，程序就会crush->安全
                return Collections.emptyList();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }
            // 如果成功，return一个string。
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = entity.getContent();
            //                                              JSON response valueType(响应是array)
            List<Item> items = Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));
             extractKeywords(items);
             return items;

        };
        // 如果只写execute不写try-catch,会报错：exception没有handle
        try {
            return httpClient.execute(new HttpGet(url), responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    // 加上item的keywords
    private void extractKeywords(List<Item> items) {
        MonkeyLearnClient monkeyLearnClient = new MonkeyLearnClient();
        /*
        List<String> descriptions = new ArrayList<>();
        for (Item item : items) {
            descriptions.add(item.getDescription());
        } */
        // 可利用Java8的string.↑↓运行时间大致相同，但↓更简洁，扩展性更强：map完了还可以做
        // chaining等一系列操作。reactive programming
        //先把item的stream拿出来->可给下游提供数据(相当于一个for loop)
        // stream()可暂时理解成可读的interface,每个item会像一个流一样传给你
        List<String> descriptions = items.stream()
                // 用lambda:call了一次getDescription(),返回String
                .map(Item::getDescription)
                // .collect(Collectors.toSet());也可以
                //可以改成pipeline，可以做各种操作，非常强大。
                // 这里只是简单地做了一个for loop的操作
                .collect(Collectors.toList());

        List<Set<String>> keywordList = monkeyLearnClient.extract(descriptions);
        // extract完成后用个setter，设置每个item的keywords
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setKeywords(keywordList.get(i));
        }
    }
}
