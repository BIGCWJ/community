package cn.ys.community.controller;

import cn.ys.community.dto.PaginationDTO;
import cn.ys.community.dto.QuestionDTO;
import cn.ys.community.mapper.QuestionMapper;
import cn.ys.community.mapper.UserMapper;
import cn.ys.community.model.Question;
import cn.ys.community.model.User;
import cn.ys.community.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 控制器层，负责判断处理登录状态
 */
@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestService questService;

    @GetMapping("/")
    public String Index(HttpServletRequest request, Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size) {
        //获取当前浏览器的所有cookies
        Cookie[] cookies = request.getCookies();
        //判断是否为null，若不判断有可能空指针异常
        if (cookies != null) {
            //遍历cookies，查找是否有叫token的Cookie
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    //找到该cookie，取出对应值
                    String token = cookie.getValue();
                    //调用findByToken获取user值
                    User user = userMapper.findByToken(token);
                    if (user != null) {
                        //user存在，则将user设置到session中
                        request.getSession().setAttribute("user", user);
                    }
                    //有找到，则跳出循环
                    break;
                }
            }
        }

        //通过questServices的list()方法获取该page页的五条数据及其分页面所需元素的数据
        PaginationDTO pagination = questService.list(page,size);
        //将该数据绑定前端
        model.addAttribute("pagination", pagination);
        //测试连接成功一次pong
        System.out.println("pong");
        //返回主页
        return "index";
    }

}
