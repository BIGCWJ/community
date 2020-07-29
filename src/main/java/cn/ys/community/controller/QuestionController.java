package cn.ys.community.controller;

import cn.ys.community.dto.CommentDTO;
import cn.ys.community.dto.QuestionDTO;
import cn.ys.community.enums.CommentTpyeEnum;
import cn.ys.community.service.CommentService;
import cn.ys.community.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestService questService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,Model model) {
        QuestionDTO questionDTO = questService.getById(id);
        List<QuestionDTO> relateQuestions = questService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTpyeEnum.QUESTION);
        //累加阅读数
        questService.incView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relateQuestions",relateQuestions);
        return "question";
    }


}
