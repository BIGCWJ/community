package cn.ys.community.controller;

import cn.ys.community.dto.CommentCreateDTO;
import cn.ys.community.dto.CommentDTO;
import cn.ys.community.dto.ResultDTO;
import cn.ys.community.enums.CommentTpyeEnum;
import cn.ys.community.exception.CustomizeErrorCode;
import cn.ys.community.model.Comment;
import cn.ys.community.model.User;
import cn.ys.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0);
        commentService.insert(comment,user);
        return ResultDTO.okOf();
    }


    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id ){
        List<CommentDTO> commentDTOS=commentService.listByTargetId(id,CommentTpyeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }









}
