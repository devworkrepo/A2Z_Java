package com.a2zsuvidhaa.in.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserModel {

    public static String id_m,gender_m="0",marital_m="0";

    public static String getId_m() {
        return id_m;
    }

    public static void setId_m(String id_m) {
        UserModel.id_m = id_m;
    }

    public static String getGender_m() {
        return gender_m;
    }

    public static void setGender_m(String gender_m) {
        UserModel.gender_m = gender_m;
    }

    public static String getMarital_m() {
        return marital_m;
    }

    public static void setMarital_m(String marital_m) {
        UserModel.marital_m = marital_m;
    }

    public static void setDetails(String id, String gender, String marital)
    {
       id_m=id;
       gender_m=gender;
       marital_m=marital;

    }

    public String getMarital() {
        return marital;
    }

    public void setMarital(String marital) {
        this.marital = marital;
    }

    String name,img,fname,pro_id,mname,img_str,samaj,marital;

    public String getSamaj() {
        return samaj;
    }

    public void setSamaj(String samaj) {
        this.samaj = samaj;
    }

    String contact,email,mobile,address,add_office,cast;
    int relation=-1;
    String type,photo;

    public boolean isType_mem() {
        return type_mem;
    }

    public void setType_mem(boolean type_mem) {
        this.type_mem = type_mem;
    }

    boolean selected,type_mem;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg_str() {
        return img_str;
    }

    public void setImg_str(String img_str) {
        this.img_str = img_str;
    }

    JSONArray addressObj;
    JSONObject obj;
    boolean isImg;

    public boolean isImg() {
        return isImg;
    }

    public void setImg(boolean img) {
        isImg = img;
    }

    public JSONObject getObj() {
        return obj;
    }

    public void setObj(JSONObject obj) {
        this.obj = obj;
    }

    public JSONArray getAddressObj() {
        return addressObj;
    }

    public void setAddressObj(JSONArray addressObj) {
        this.addressObj = addressObj;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getCast() {

        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public UserModel() {

    }

    public UserModel(String id, String name, String fatherName, String motherName, String goatra, String cast, String mobile, String imageUrl) {

        this.pro_id = id;
        this.name = name;
        this.fname = fatherName;
        this.mname = motherName;
        this.goatra = goatra;
        this.cast = cast;
        this.mobile=mobile;
        this.img = imageUrl;
    }

    public UserModel(JSONObject obj, String id, String name, String fatherName, String motherName, String goatra, String cast, String mobile, String email, JSONArray addressObj, String imageUrl) {
        this.obj=obj;
        this.pro_id = id;
        this.name = name;
        this.fname = fatherName;
        this.mname = motherName;
        this.goatra = goatra;
        this.cast = cast;
        this.mobile=mobile;
        this.email=email;
        this.addressObj=addressObj;
        this.img = imageUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdd_office() {
        return add_office;
    }

    public void setAdd_office(String add_office) {
        this.add_office = add_office;
    }

    public String getPro_id() {
        return pro_id;
    }

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    String gender;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGoatra() {
        return goatra;
    }

    public void setGoatra(String goatra) {
        this.goatra = goatra;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    String dob,dot,weight,goatra,age;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

}
