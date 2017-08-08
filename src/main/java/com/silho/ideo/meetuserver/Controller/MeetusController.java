package com.silho.ideo.meetuserver.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MeetusController {


    @RequestMapping("/")
    public String home(){
        return "home";
    }

    @RequestMapping(value = "/apiKey", method = RequestMethod.GET)
    public String getApiKey(){
        return System.getenv("API_KEY");
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    @ResponseBody
    public String request(@RequestParam("duration") String duration,
                          @RequestParam("myLatitude") double myLatitude,
                          @RequestParam("myLongitude") double myLongitude,
                          @RequestParam("latitudeDestination") double latitudeDestination,
                          @RequestParam("longitudeDestination") double longitudeDestination){
        return duration + myLatitude + myLongitude + latitudeDestination + longitudeDestination;
    }
}
