package cn.ys.community.service;

import cn.ys.community.dto.PaginationDTO;
import cn.ys.community.dto.QuestionDTO;
import cn.ys.community.mapper.QuestionMapper;
import cn.ys.community.mapper.UserMapper;
import cn.ys.community.model.Question;
import cn.ys.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于主页Question数据及其分页的业务层
 */
@Service
public class QuestService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //返回page页的五条数据及其分页面所需元素的数据
    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        //获取question总数totalCount
        Integer totalCount = questionMapper.count();
        //将totalCount，page，size传递paginationDTO中，对其中所有属性进行运算赋值
        paginationDTO.setPagination(totalCount, page, size);
        //判断page合法性，page小于1时都为1
        if (page < 1) {
            page = 1;
        }
        //page大于总页数的时候都为总页数的值
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }
        //size*(page-1)
        Integer offset = size * (page - 1);
        //查找从第offset条数据起往后size个数据
        List<Question> questionList = questionMapper.list(offset, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //循环将每个question中的所有值取出加上对应creator属性来查找对应的user并一起封装成QuestionDTO
        for (Question question : questionList) {
            //通过question的creator属性获取用户id，并通过该值查找出该USER信息
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question中所有属性set进questionDTO中去
            BeanUtils.copyProperties(question, questionDTO);
            //setUser()
            questionDTO.setUser(user);
            //往questionDTOList中添加一个questionDTO
            questionDTOList.add(questionDTO);
        }
        //结合分页
        paginationDTO.setQuestions(questionDTOList);
        //返回paginationDTO
        return paginationDTO;
    }
}
