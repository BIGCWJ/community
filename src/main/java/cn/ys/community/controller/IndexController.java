package cn.ys.community.controller;

import cn.ys.community.dto.PaginationDTO;
import cn.ys.community.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器层，负责判断处理登录状态
 */
@Controller
public class IndexController {

    @Autowired
    private QuestService questService;

    @GetMapping("/")
    public String Index(HttpServletRequest request, Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size) {

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
