package cn.ys.community.service;

import cn.ys.community.dto.PaginationDTO;
import cn.ys.community.dto.QuestionDTO;
import cn.ys.community.exception.CustomizeErrorCode;
import cn.ys.community.exception.CustomizeException;
import cn.ys.community.mapper.QuestionExtMapper;
import cn.ys.community.mapper.QuestionMapper;
import cn.ys.community.mapper.UserMapper;
import cn.ys.community.model.Question;
import cn.ys.community.model.QuestionExample;
import cn.ys.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 关于主页Question数据及其分页的业务层
 */
@Service
public class QuestService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    //返回page页的size条数据及其分页面所需元素的数据
    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        //获取question总数totalCount
        QuestionExample example = new QuestionExample();
        Integer totalCount = (int) questionMapper.countByExample(example);
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
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(questionExample, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //循环将每个question中的所有值取出加上对应creator属性来查找对应的user并一起封装成QuestionDTO
        for (Question question : questions) {
            //通过question的creator属性获取用户id，并通过该值查找出该USER信息
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question中所有属性set进questionDTO中去
            BeanUtils.copyProperties(question, questionDTO);
            //setUser()
            questionDTO.setUser(user);
            //往questionDTOList中添加一个questionDTO
            questionDTOList.add(questionDTO);
        }
        //结合分页
        paginationDTO.setData(questionDTOList);
        //返回paginationDTO
        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        //获取该用户下question总数totalCount
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
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
        //查找userId用户从第offset条数据起往后size个数据

        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(example, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        //循环将每个question中的所有值取出加上对应creator属性来查找对应的user并一起封装成QuestionDTO
        for (Question question : questions) {
            //通过question的creator属性获取用户id，并通过该值查找出该USER信息
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question中所有属性set进questionDTO中去
            BeanUtils.copyProperties(question, questionDTO);
            //setUser()
            questionDTO.setUser(user);
            //往questionDTOList中添加一个questionDTO
            questionDTOList.add(questionDTO);
        }
        //结合分页
        paginationDTO.setData(questionDTOList);
        //返回paginationDTO
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        //如果publish页面返回的question的id为空时，执行插入操作
        if (question.getId() == null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0L);
            question.setViewCount(0L);
            question.setLikeCount(0L);
            questionMapper.insert(question);
        } else {
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int update = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (update != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }

    }

    public void incView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1L);
        questionExtMapper.incView(question);

    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }

        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return questionDTOS;

    }
}
