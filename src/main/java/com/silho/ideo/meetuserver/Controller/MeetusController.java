package com.silho.ideo.meetuserver.controller;

import com.silho.ideo.meetuserver.helpers.AndroidPushNotificationsService;
import com.silho.ideo.meetuserver.helpers.FirebaseResponse;
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
                          @RequestParam(value = "friendsList", required = false)JSONArray users) throws JSONException {
        send(latitudeDestination, longitudeDestination, placeName, username, duration, idFacebook, time, users);
        return users.getJSONObject(0).getString("token") + users.getJSONObject(1).getString("token");
    }

    private static final Logger log = LoggerFactory.getLogger(MeetusController.class);

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> send(double latitudeDestination, double longitudeDestination, String placeName,
                                       String username, String duration, String idFacebook, long time, JSONArray users) throws JSONException {

        JSONObject body = getJsonObject(latitudeDestination, longitudeDestination, placeName, username, duration, idFacebook, time, users);

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

    private JSONObject getJsonObject(double latitudeDestination, double longitudeDestination, String placeName, String username, String duration, String idFacebook, long time, JSONArray users) throws JSONException {
        JSONObject body = new JSONObject();
            // JsonArray registration_ids = new JsonArray();
            // body.put("registration_ids", registration_ids);
        for(int i =0; i<users.length()-1; i++) {
            body.put("to", users.getJSONObject(i).getString("token"));
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

            body.put("notification", notification);
            body.put("data", data);
        }
        return body;
    }
}
