package com.silho.ideo.meetuserver.model;

import java.io.Serializable;

/**
 * Created by Samuel on 01/08/2017.
 */

public class User implements Serializable {

    public String token, idFacebook, name, profilPic;
    public double latitude;
    public double longitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilPic() {
        return profilPic;
    }

    public void setProfilPic(String profilPic) {
        this.profilPic = profilPic;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public User(){}

    public User(String token, double myLatitude, double myLongitude, String idFacebook, String name, String profilPic){
        this.token = token;
        this.latitude = myLatitude;
        this.longitude = myLongitude;
        this.idFacebook = idFacebook;
        this.name = name;
        this.profilPic = profilPic;

    }
}
