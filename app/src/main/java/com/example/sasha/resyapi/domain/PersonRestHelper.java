package com.example.sasha.resyapi.domain;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.sasha.resyapi.domain.Person;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sasha on 04.10.15.
 */
public interface PersonRestHelper {
    StringRequest createPerson(Response.Listener<String> response,Response.ErrorListener error);
    StringRequest deletePerson(Response.Listener<String> response,Response.ErrorListener error);
    JsonArrayRequest getAllPersons(Response.Listener<JSONArray> response,Response.ErrorListener error);
    JsonObjectRequest getPerson(Response.Listener<JSONObject> response,Response.ErrorListener error);
}
