package com.silho.ideo.meetuserver.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MeetusController {

    @RequestMapping("/")
    public String home(){
        return "home";
    }
}
