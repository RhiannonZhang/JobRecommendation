package com.laioffer.job.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.ExtractRequestBody;
import com.laioffer.job.entity.ExtractResponseItem;
import com.laioffer.job.entity.Extraction;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MonkeyLearnClient {
    private static final String EXTRACT_URL = "https://api.monkeylearn.com/v3/extractors/ex_YCya9nrn/extract/";
    private static final String AUTH_TOKEN = "27b9d94927f9dcf76d9aff56076d8f664572f2a0";
    // 输入：每句话是一个String,多句话->list of strings.不可并成1个string，不然keywords会混淆
    //输出：每一个article最多3个keywords->放入set中。有多个articles -> list

    public List<Set<String>> extract(List<String> articles) {
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost request = new HttpPost(EXTRACT_URL);
        // 相当于在postman设置headers(meta-data，大多都是标准的fields比如token..)
        request.setHeader("Content-type","application/json");
        request.setHeader("Authorization","Token " + AUTH_TOKEN);
        // 自定义的data structure.input:(data,maxKeywords)
        ExtractRequestBody body = new ExtractRequestBody(articles, 3);

        String jsonBody;
        try{ // 用mapper把jsonBody还原成String形式。
            jsonBody = mapper.writeValueAsString(body);
        } catch(JsonProcessingException e) {
            // 遇到异常，最好返回给客户让他知道哪里出错了。但是这里为了方便，
            // 如果没有还原成功就只是返回一个空list
            return Collections.emptyList();
        }
        try { //如果成功了，就把它放入请求的StringEntity内
            request.setEntity(new StringEntity(jsonBody));
        } catch (UnsupportedEncodingException e) {
            return Collections.emptyList();
        }
        ResponseHandler<List<Set<String>>> responseHandler = response -> {
            if (response.getStatusLine().getStatusCode() != 200) {
                return Collections.emptyList();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }
            //                                                  数据从哪里来，          数据的类型都是这种数组
            ExtractResponseItem[] results = mapper.readValue(entity.getContent(), ExtractResponseItem[].class);
            // 我们只需要extractions里面的parseValue：
            List<Set<String>> keywordList = new ArrayList<>();
            for (ExtractResponseItem result : results) {
                // 每一个item里，把对应于每个article的keywords拿出来
                Set<String> keywords = new HashSet<>();
                // 对每个article,把每个keyword extraction都放到Set<String> keywords里
                for (Extraction extraction : result.extractions) {
                    keywords.add(extraction.parseValue);
                }
                // 把每个article的keywords
                keywordList.add(keywords);
            }
            return keywordList;
        };

        try{
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        List<String> articles = Arrays.asList( "Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.",
                "Former Auburn University football coach Tommy Tuberville defeated ex-US Attorney General Jeff Sessions in Tuesday nights runoff for the Republican nomination for the U.S. Senate. ",
                "The NEOWISE comet has been delighting skygazers around the world this month – with photographers turning their lenses upward and capturing it above landmarks across the Northern Hemisphere."
        );
        MonkeyLearnClient client = new MonkeyLearnClient();

        List<Set<String>> keywordList = client.extract(articles);
        System.out.println(keywordList);
    }
}
