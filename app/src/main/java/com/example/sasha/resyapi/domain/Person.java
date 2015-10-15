package com.example.sasha.resyapi.domain;

import android.util.Log;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.sasha.resyapi.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sasha on 04.10.15.
 */
public class Person {
    static private ColorGenerator generator = ColorGenerator.MATERIAL;

    int id;
    public String firstName;
    public String middleName;
    public String lastname;
    private Date birthday;
    private TextDrawable drawable;
    public Person(int id, String firstName, String middleName, String lastname, Date birthday) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastname = lastname;
        this.birthday = birthday;
        drawable=TextDrawable.builder()
                .buildRound(firstName.substring(0, 1).toUpperCase(), generator.getRandomColor());
    }
    public Person( String firstName, String middleName, String lastname, Date birthday) {

        this.firstName = firstName;
        this.middleName = middleName;
        this.lastname = lastname;
        this.birthday = birthday;
        drawable=TextDrawable.builder()
                .buildRound(firstName.substring(0, 1).toUpperCase(), generator.getRandomColor());
    }

    public Person() {

    }

    public TextDrawable getDrawable() {
        return drawable;
    }

    public void setDrawable(TextDrawable drawable) {
        this.drawable = drawable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    public static Person getPersonFromJSON(JSONObject object) throws JSONException {

        JSONObject name=object.getJSONObject("name");
        JSONObject bDay = object.optJSONObject("birthDay");
        return new Person(object.getInt("_id"),name.getString("firstName"),name.getString("middleName"),name.getString("lastName"),Date.valueOf(bDay.getString("year")+"-"+bDay.getString("month")+"-"+bDay.getString("day")));
    }
    public static List<Person> getPersonListFromJSONArray(JSONArray array) throws JSONException {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int len = array.length();
        LinkedList<Person> persons = new LinkedList<>();

        for (int i = 0; i <len ; i++) {
            JSONObject object = array.getJSONObject(i);
            persons.add(getPersonFromJSON(object));
        }
        return persons;
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject object= new JSONObject();

        JSONObject name = new JSONObject();
        name.put("firstName",firstName);
        name.put("middleName",middleName);
        name.put("lastName", lastname);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthday);
        JSONObject bDay = new JSONObject();
        bDay.put("day",calendar.get(Calendar.DAY_OF_MONTH));
        bDay.put("month",calendar.get(Calendar.MONTH));
        bDay.put("year",calendar.get(Calendar.YEAR));
        object.put("birthDay",bDay);
        object.put("name",name);
        Log.d(MainActivity.LOG_TAG,object.toString());
        return object;

    }

}
