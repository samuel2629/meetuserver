package com.silho.ideo.meetuserver.controller;

import com.silho.ideo.meetuserver.helpers.AndroidPushNotificationsService;
import com.silho.ideo.meetuserver.helpers.FirebaseResponse;
import com.silho.ideo.meetuserver.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class MeetusController {


    @RequestMapping("/")
    public String home(){
        return "home";
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    @ResponseBody
    public String request(@RequestParam(value = "duration", required = false) String duration,
                          @RequestParam("latitudeDestination") double latitudeDestination,
                          @RequestParam("longitudeDestination") double longitudeDestination,
                          @RequestParam("idFacebook") String idFacebook,
                          @RequestParam("username") String username,
                          @RequestParam("placeName") String placeName,
                          @RequestParam("time") long time,
                          @RequestParam(value = "friendsList", required = false)ArrayList<User> users) throws JSONException {
        send(latitudeDestination, longitudeDestination, placeName, username, duration, idFacebook, time, users);
        return "ok";
    }

    private static final Logger log = LoggerFactory.getLogger(MeetusController.class);

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> send(double latitudeDestination, double longitudeDestination, String placeName,
                                       String username, String duration, String idFacebook, long time, ArrayList<User> users) throws JSONException {

            JSONObject body = new JSONObject();
            JSONArray registration_ids = new JSONArray();
            for(User user: users) {
                String id = user.getToken();
                registration_ids.put(id);
            }
            body.put("to", "cZ0-H85fWdE:APA91bE2CVA7y2ADdkAepOUGcLPzkxdxSxJrJu6I39duxX7tWYQ540QXfKlj7G7iU3LgNifxso0KkUmOz9JC2FAdYtqDK1M9Mfa-EF7VDxb2ER_F8xifkAl7TZPjT5tacQwHIXZ4mnHh");
            body.put("priority", "high");

            // body.put("dry_run", true);

            JSONObject notification = new JSONObject();
            notification.put("body", "Meetus ?");
            notification.put("title", username);
            // notification.put("icon", "myicon");

            JSONObject data = new JSONObject();
            data.put("latitudeDestination", latitudeDestination);
            data.put("longitudeDestination", longitudeDestination);
            data.put("idFacebook", idFacebook);
            data.put("placeName", placeName);
            data.put("durationSender", duration);
            data.put("time", time);
            data.put("friendsList", users);

            body.put("notification", notification);
            body.put("data", data);


            HttpEntity<String> request = new HttpEntity<>(body.toString());


            CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.send(request);
            CompletableFuture.allOf(pushNotification).join();

            try {
                FirebaseResponse firebaseResponse = pushNotification.get();
                if (firebaseResponse.getSuccess() == 1) {
                    log.info("push notification sent ok!");
                } else {
                    log.error("error sending push notifications: " + firebaseResponse.toString());
                }
                return new ResponseEntity<>(firebaseResponse.toString(), HttpStatus.OK);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return new ResponseEntity<>("the push notification cannot be send.", HttpStatus.BAD_REQUEST);
    }
}
