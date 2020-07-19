package cn.ys.community.controller;

import cn.ys.community.mapper.QuestionMapper;
import cn.ys.community.mapper.UserMapper;
import cn.ys.community.model.Question;
import cn.ys.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 控制器层，主要处理发布页面
 */
@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //url中输入/publish时返回页面
    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    //提交/publish时调用以下方法，并返回主页
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("tag") String tag,
            HttpServletRequest request,
            Model model
    ) {
        //将值绑定前端
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        //判断
        if (title == null || title == "") {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (description == null || description == "") {
            model.addAttribute("error", "内容不能为空");
            return "publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }


        User user = null;
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
                    user = userMapper.findByToken(token);
                    if (user != null) {
                        //user存在，则将user设置到session中
                        request.getSession().setAttribute("user", user);
                    }
                    //有找到，则跳出循环
                    break;
                }
            }
        }

        //判断是否登录，未登录则直接返回，刷新页面
        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        //插入一个Question数据
        Question question = new Question();
        question.setTitle(title);
        question.setTag(tag);
        question.setDescription(description);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.create(question);

        //执行成功返回主页
        return "redirect:/";
    }


}
