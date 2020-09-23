import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class TestHttpClient {

    public static void main(String[] args) throws IOException {
        //1、创建客户端，发出http请求
        DefaultHttpClient httpClient = new DefaultHttpClient();

        //2、创建请求对象 get、post
        HttpGet httpGet = new HttpGet("http://www.baidu.com");

        //3、调用客户端，发送请求
        HttpResponse response = httpClient.execute(httpGet);

        //获取相应状态码
        int statusCode = response.getStatusLine().getStatusCode();

        if(statusCode==200){
            //获取相应对象
            HttpEntity entity = response.getEntity();

            //把响应对象转换为字符串
            String s = EntityUtils.toString(entity, "UTF-8");

            System.out.println(s);

        }

    }
}
