package cn.ys.community.service;

import cn.ys.community.dto.NotificationDTO;
import cn.ys.community.dto.PaginationDTO;
import cn.ys.community.enums.NotificationStatusEnum;
import cn.ys.community.enums.NotificationTypeEnum;
import cn.ys.community.exception.CustomizeErrorCode;
import cn.ys.community.exception.CustomizeException;
import cn.ys.community.mapper.NotificationMapper;
import cn.ys.community.mapper.UserMapper;
import cn.ys.community.model.Notification;
import cn.ys.community.model.NotificationExample;
import cn.ys.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        //获取该用户下notification总数totalCount
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
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

        //查找该user下所有的回复表
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        //获取分页数据
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        if (notifications.size() == 0) {
            return paginationDTO;
        }
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

//        //通过map()映射出notifications中每个元素的对应的的值(通知者) 去重 变成一个list集合
//        List<Long> userIds = notifications.stream().map(notify -> notify.getNotifier()).distinct().collect(Collectors.toList());
//        //通过该通知者id去查找他对应user表的数据
//        UserExample userExample = new UserExample();
//        userExample.createCriteria().andIdIn(userIds);
//        List<User> users = userMapper.selectByExample(userExample);
//        //变成Map，key是id，val是对应的user
//        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(u -> u.getId(), u -> u));

        paginationDTO.setData(notificationDTOS);
        return paginationDTO;

    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification==null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(!notification.getReceiver().equals(user.getId())){
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;

    }
}
