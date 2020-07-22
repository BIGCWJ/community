package cn.ys.community.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器层，主要处理发布页面
 */
@Controller
public class PublishController {

    @Autowired
    private QuestService questService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Integer id,Model model){
        QuestionDTO question = questService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        return "publish";
    }


    //url中输入/publish时返回页面
    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    //提交/publish时调用以下方法，并返回主页
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title" ,required = false) String title,
            @RequestParam(value = "description" , required = false) String description,
            @RequestParam(value = "tag" ,required = false) String tag,
            @RequestParam(value = "id" , required = false) Integer id,
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

        User user = (User) request.getSession().getAttribute("user");

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
        question.setId(id);
        questService.createOrUpdate(question);

        //执行成功返回主页
        return "redirect:/";
    }


}
