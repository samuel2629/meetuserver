package com.silho.ideo.meetuserver.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MeetusController {

    private String apiKey = System.getenv("API_KEY");

    @RequestMapping("/")
    public String home(){
        return "home";
    }

    @RequestMapping(value = "/apiKey", method = RequestMethod.GET)
    @ResponseBody
    public String getApiKey(){
        return apiKey;
    }
}