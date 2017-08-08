package com.silho.ideo.meetuserver.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MeetusController {

    @RequestMapping("/")
    public String home(){
        return "home";
    }

    @RequestMapping(value = "/apiKey", method = RequestMethod.GET)
    public String getApiKey(){
        return "AIzaSyDuioRSRLgLzgBwGGaY3qPln411JJhRUIA";
    }
}
