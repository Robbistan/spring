package com.example.demo;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spring Boot Hello案例
 *
 * Created by bysocket on 26/09/2017.
 */
@RestController
public class AppController {

    @RequestMapping(value = "/app",method = RequestMethod.GET)
    public String sayHello() {
        return "Hello";
    }

    @RequestMapping(value = "/app",method = RequestMethod.POST)
    public String postBot(@RequestBody Map<String,Object> webhook) throws IOException {
        System.out.println(webhook);
        String response = "{\"fulfillmentText\": \"\",\n" +
                "     \"source\": \"dad jokes\"\n" +
                "    }";
        if (webhook.get("queryResult") != null){
            Map<String, Object> query =  ((Map<String, Object>)webhook.get("queryResult"));
            if (query.get("parameters") != null) {
                Map<String, Object> parameters =  ((Map<String, Object>)query.get("parameters"));
                if (parameters.get("subject") != null) {
                    String subject =  ((String)parameters.get("subject"));
                    if ((subject != null) && !(subject.equals(""))) {
                        response = "{\"fulfillmentText\": \"" + process(subject) + "\",\n" +
                                "     \"source\": \"dad jokes\"\n" +
                                "    }";

                    }
                }
            }
        }

        ;
        return response;
    }

    private String doQuery(String keyword) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.guinnessworldrecords.com/search/results?Header=&Term="+ keyword + "&Page=1&Type=all&Max=20&FacetType=&FacetContentType=&FacetCategories=&FacetLocation=&FacetIndustry=&FacetPurpose=&FacetPubDate=&Partial=_Results&Filter1=&Filter2=&Filter3=&AudienceType=&AutoSearch=false")
                .method("GET", null)
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Referer", "https://www.guinnessworldrecords.com/search?term=mountaneering&page=1&type=all&max=20&partial=_Results&")
                .addHeader("Accept-Language", "en-US,en;q=0.9,he;q=0.8")
                .addHeader("Cookie", "_fbp=fb.1.1592073934021.674382307; __SessionId=jcju0yefxtnjwhbigvzegya4; _ga=GA1.2.849953986.1592073937; _gid=GA1.2.1793213006.1592073937; visitor_id175512=239304190; visitor_id175512-hash=fc55262ea8af0ce772d716e2b92b51f0c4e74c42f138a097cd79ee92cfec09266558e803afe5b7b31594bb7e6f39f08623683f24; cookiesAllowed=true; _uetsid=cda46542-02df-4dcf-1ed6-fa537ea0c63d; _uetvid=74ba0022-040e-f14d-163f-9629c8119af0; AWSALB=dgLPbVYswGgHMadNOh4jPKGuiMhrbTkhSwkj9SP0YF5lhM6lhovFJYWMzXziXoxgmxX0mhp/XHPgT4ByH6TmOC79AUdFxuVBDxHLIQlGgN/BmQ2NOZf0Wg6+2s6z; AWSALBCORS=dgLPbVYswGgHMadNOh4jPKGuiMhrbTkhSwkj9SP0YF5lhM6lhovFJYWMzXziXoxgmxX0mhp/XHPgT4ByH6TmOC79AUdFxuVBDxHLIQlGgN/BmQ2NOZf0Wg6+2s6z; AWSALB=V6uNo5xjis5i/OdoV3nOF45amAPnFJQHN9w5ZdiebiFGWvHekMZQCw4wuVbPzmc8kh9PdvIniHPDKVBJf81CCuAXF/gm+Z+qaN8N8y2sR8u6U7YacEm5thke9L4Y; AWSALBCORS=V6uNo5xjis5i/OdoV3nOF45amAPnFJQHN9w5ZdiebiFGWvHekMZQCw4wuVbPzmc8kh9PdvIniHPDKVBJf81CCuAXF/gm+Z+qaN8N8y2sR8u6U7YacEm5thke9L4Y")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    private String process(String keyword) throws IOException {
        String res = "";
        String text = doQuery(keyword);
        System.out.println(text.replace("\n",""));
        Pattern title = Pattern.compile("<a href=\"(/world-records/[0-9a-z\\-]+)\">(.*)</a></h3>        </header>");
        Pattern price = Pattern.compile("<span class=\"a-offscreen\">([0-9.$]+)</span><span aria-hidden=\"true\"><span class=\"a-price-symbol\">");
        String[] articles = (text.replace("\n","").split("</article><article"));
        for (String  a : articles) {
            System.out.println(a);
            Matcher m = title.matcher(a);
            if (m.find()) {
                res += m.group(1) + "->" + m.group(2);
            }

        }
        return res;
    }
}