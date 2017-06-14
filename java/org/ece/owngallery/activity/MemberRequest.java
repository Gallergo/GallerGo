package org.ece.owngallery.activity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by woong on 2017-04-19.
 */

public class MemberRequest extends StringRequest {

    final static private String URL = "http://192.168.0.3/Register.php";
    private Map<String, String> parameters;

    public MemberRequest(String userID, String userpassword, String userName, int userAge, Response.Listener<String>listener)
    {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userpassword);
        parameters.put("userName", userName);
        parameters.put("userAge", userAge + "");

    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
