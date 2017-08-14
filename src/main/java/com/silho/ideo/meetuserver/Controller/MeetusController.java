package com.silho.ideo.meetuserver.Controller;

import com.silho.ideo.meetuserver.helpers.AndroidPushNotificationsService;
import com.silho.ideo.meetuserver.helpers.FirebaseResponse;
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
    public String request(@RequestParam("duration") String duration,
                          @RequestParam("latitude") double myLatitude,
                          @RequestParam("longitude") double myLongitude,
                          @RequestParam("latitudeDestination") double latitudeDestination,
                          @RequestParam("longitudeDestination") double longitudeDestination,
                          @RequestParam("token") String token,
                          @RequestParam("idFacebook") String idFacebook,
                          @RequestParam("username") String username,
                          @RequestParam("friendToken") String friendToken,
                          @RequestParam("placeName") String placeName,
                          @RequestParam("urlProfilPic") String profilPic) throws JSONException {
        send(token, latitudeDestination, longitudeDestination, placeName, username, duration, idFacebook, profilPic);
        return duration + " my latitude : " + myLatitude
                + " my longitude : "+myLongitude
                +" destination latitude : "+ latitudeDestination
                +" destination longitude : "+ longitudeDestination
                + " token : " + token + " idFacebook : " + idFacebook
                + " username : " + username + " place username : " + placeName
                + " url profil pic : " + profilPic;
    }

    private static final Logger log = LoggerFactory.getLogger(MeetusController.class);

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> send(String token, double latitudeDestination, double longitudeDestination, String placeName,
                                       String username, String duration, String idFacebook, String profilPic) throws JSONException {


        JSONObject body = new JSONObject();
        // JsonArray registration_ids = new JsonArray();
        // body.put("registration_ids", registration_ids);
        body.put("to", token);
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
        data.put("profilPic", profilPic);

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
