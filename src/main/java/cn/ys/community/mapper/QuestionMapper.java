package cn.ys.community.mapper;

import cn.ys.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 关于操作Question的持久层Mapper
 */
@Mapper
public interface QuestionMapper {
    //插入一个问题
    @Insert("INSERT INTO question (title,description,gmt_create,gmt_modified,creator,tag) VALUES (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void create(Question question);
    //分页，查找从第offset条数据起往后size个数据，如0，5则为第一条数据开始往后size个数据
    @Select("select * from question LIMIT #{offset} ,#{size}")
    List<Question> list(@Param(value = "offset") Integer offset,@Param(value = "size") Integer size);
    //查找question表中总数
    @Select("select count(1) from question;")
    Integer count();
}
