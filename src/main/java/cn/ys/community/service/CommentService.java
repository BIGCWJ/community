package cn.ys.community.service;

import cn.ys.community.dto.CommentDTO;
import cn.ys.community.enums.CommentTpyeEnum;
import cn.ys.community.enums.NotificationStatusEnum;
import cn.ys.community.enums.NotificationTypeEnum;
import cn.ys.community.exception.CustomizeErrorCode;
import cn.ys.community.exception.CustomizeException;
import cn.ys.community.mapper.*;
import cn.ys.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    //插入评论
    //该注解解释如果未全部执行成功，回滚
    @Transactional
    public void insert(Comment comment, User commentator) {
        //评论的问题不能为空
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        //评论的类型不能为空或者不能存在其他类型的评论
        if (comment.getType() == null || !CommentTpyeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        //回复评论
        if (comment.getType() == CommentTpyeEnum.COMMENT.getType()) {
            //查找该评论的上一级的评论对象
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            //如果为空，则抛出评论谁啊未找到的异常
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_MOT_FOUND);
            }

            //回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            //插入该评论
            commentMapper.insert(comment);
            //增加上一级评论的评论数
            dbComment.setCommentCount(1);
            //执行
            commentExtMapper.incCommentCount(dbComment);

            //创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMMENT, question.getId());

        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());

            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            question.setCommentCount(1L);
            questionExtMapper.incCommentCount(question);

            //创建通知
            createNotify(comment, question.getCreator(),commentator.getName() ,question.getTitle() ,NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outid) {
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        //该类型为回复评论类型
        notification.setType(notificationType.getType());
        //设置question的id作为outid
        notification.setOutid(outid);
        //设置评论者的id（关联userid）作为（谁谁谁）回复了 ***
        notification.setNotifier(comment.getCommentator());
        //设置未读状态
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        //设置回复对象
        notification.setReceiver(receiver);
        //设置通知者名字
        notification.setNotifierName(notifierName);
        //设置标题
        notification.setOuterTitle(outerTitle);
        //执行
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTpyeEnum type) {

        CommentExample commentExample = new CommentExample();
        //获取该type型的parentId下对应的所有评论数据
        commentExample.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type.getType());
        //根据时间倒序排列
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        //若找不到，返回一个空的ArrayList
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList();
        userIds.addAll(commentators);
        //获取评论人转换成Map，key是用户id，value是用户
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //转换comment为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
