package cn.ys.community.provider;

import cn.ys.community.dto.AccessTokenDTO;
import cn.ys.community.dto.GithubUser;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 一个组件
 */
@Component
public class GithubProvider {

    //以下参考okHttp文档
    //获取令牌值
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        //得到一个客户端
        OkHttpClient client = new OkHttpClient();
        //将AccessTokenDTO转换成Json放入body中
        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));
        //全部弄好
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        //execute：执行
        try (Response response = client.newCall(request).execute()) {
            //获得一串带有access_token的值，并截取返回
            String string = response.body().string();
            String[] split = string.split("&");
            String token = split[0].split("=")[1];
            System.out.println("access_token="+token);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取 github用户的信息
    public GithubUser getUser(String accessToken) {
        //得到一个客户端
        OkHttpClient client = new OkHttpClient();
        //参照githubapi文档，将网址跟access_token搭配
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            //执行
            Response response = client.newCall(request).execute();
            //获取一个JSON格式的用户表单，包含用户name，accountid...
            String string = response.body().string();
            System.out.println("string:"+string);
            //解析该表单，放入githubUser对象中
            //使用的是FASTJSON
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            //返回
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
