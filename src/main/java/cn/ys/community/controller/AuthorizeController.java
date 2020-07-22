package cn.ys.community.controller;

import cn.ys.community.dto.AccessTokenDTO;
import cn.ys.community.dto.GithubUser;
import cn.ys.community.mapper.UserMapper;
import cn.ys.community.model.User;
import cn.ys.community.provider.GithubProvider;
import cn.ys.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 控制器层，负责授权登录
 */
@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;


    @Autowired
    private UserMapper userMapper;

    /**
     * 以下三个参数为配置文件中参数，@Value 可以读取
     */
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    /**
     * 若后缀为“/callback”，则跳转到该控制器
     * @param code 页面发出请求后，github将传回来串url，获取其中code
     * @param state 设置为1
     * @param response
     * @return 返回一个视图
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {
        //new AccessToken对象，将所需要的值放进去
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        //app id
        accessTokenDTO.setClient_id(clientId);
        //app secret
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        //返回成功后回调网址
        accessTokenDTO.setRedirect_uri(redirectUri);

        //得到access_token值，来进行下一步操作获取github上的用户信息
        String assessToken = githubProvider.getAccessToken(accessTokenDTO);
        //获取github用户信息
        GithubUser githubUser = githubProvider.getUser(assessToken);
        //解析并将一些信息放入User表中
        if (githubUser != null) {
            User user = new User();
            //通过UUID生成一个随机值
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrUpdate(user);
            //发出的请求中放入一个Cookie，add该用户为token值,以便判断是否为登录状态
            response.addCookie(new Cookie("token",token));
            //重定向
            return "redirect:/";
        } else {
            //登录失败，重新登录
            return "redirect:/";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }















}
