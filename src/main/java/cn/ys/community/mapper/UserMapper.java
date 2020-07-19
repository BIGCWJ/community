package cn.ys.community.mapper;

import cn.ys.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 持久层接口，负责对持久层操作
 */
@Mapper
public interface UserMapper {

    //插入一个用户数据
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified,avatar_url) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    //通过一个token值来获取对应的所有在USER表中的用户信息
    @Select("select * from user where token =#{token}")
    User findByToken(@Param("token") String token);

    //通过用户id来查找user信息
    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);

}
