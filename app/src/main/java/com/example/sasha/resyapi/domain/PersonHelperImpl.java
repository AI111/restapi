package com.example.sasha.resyapi.domain;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.sasha.resyapi.MainActivity;
import com.example.sasha.resyapi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sasha on 15.10.15.
 */
public class PersonHelperImpl implements PersonRestHelper {
    String url = "192.168.0.101:8080";

    @Override
    public StringRequest createPerson(Response.Listener<String> response, Response.ErrorListener error) {
        return null;
    }

    @Override
    public StringRequest deletePerson(Response.Listener<String> response, Response.ErrorListener error) {
        return null;
    }

    @Override
    public JsonArrayRequest getAllPersons(Response.Listener<JSONArray> response, Response.ErrorListener error) {
        return null;
    }

    @Override
    public JsonObjectRequest getPerson(Response.Listener<JSONObject> response, Response.ErrorListener error) {
        return null;
    }
}
