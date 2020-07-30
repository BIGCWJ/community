package cn.ys.community.controller;

import cn.ys.community.dto.NotificationDTO;
import cn.ys.community.dto.PaginationDTO;
import cn.ys.community.enums.NotificationTypeEnum;
import cn.ys.community.model.Notification;
import cn.ys.community.model.User;
import cn.ys.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id, user);
        if (NotificationTypeEnum.REPLY_COMMMENT.getType() == notificationDTO.getType()
                ||NotificationTypeEnum.REPLY_QUESTION.getType()==notificationDTO.getType()
        ){
            return "redirect:/question/"+notificationDTO.getOutid();
        }else {
            return "redirect:/";
        }
    }


}
