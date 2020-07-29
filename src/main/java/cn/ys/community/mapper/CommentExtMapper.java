package cn.ys.community.mapper;

import cn.ys.community.model.Comment;
import cn.ys.community.model.CommentExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment record);
}
