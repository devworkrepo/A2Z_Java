package com.a2zsuvidhaa.in;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.a2zsuvidhaa.in.model.AepsDriver;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.Security;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AppPreference {

    private static AppPreference appPreference;
    private static SharedPreferences mSharedPref;
    private static final String SHARED_PREF = "shared_preference";
    private static final String ID = "id";
    private static final String CHANGEPIN = "change_pin";
    private static final String TOKEN = "token";
    private static final String POPUP = "popup";
    private static final String POPUPSEE = "popupsee";
    private static final String NAME = "name";
    private static final String FATHER = "father";
    private static final String DOB = "dob";
    private static final String EMAIL = "email";
    private static final String USER_BALANCE = "user_balance";
    private static final String MOBILE = "mobile";
    private static final String OTP_NUMBER = "otp_number";
    private static final String PROFILE_PIC = "profile_pic";
    private static final String PANCARD_PIC = "pancard_pic";
    private static final String ADHAAR_PIC = "adhaar_pic";
    private static final String ADHAAR_BACK_PIC = "adhaar_back_pic";
    private static final String ROLL_ID = "roll_id";
    private static final String ROLE_TITLE = "role_title";
    private static final String SELECT_RD_SERVICE_DEVICE = "selected_rd_service_device";


    private static final String IS_SETTLEMENT_BANK = "is_settlement_bank";
    private static final String IS_AADHAAR_KYC = "is_aadhaar_kyc";
    private static final String IS_LOGIN_ID_CREATED = "is_login_id_created";
    private static final String IS_VIDEO_KYC = "is_video_kyc";
    private static final String IS_AEPS_KYC = "is_aeps_kyc";


    private static final String GENDER = "gender";
    private static final String STATE_ID = "state_id";
    private static final String CITY = "city";
    // private static final String ROLL_ID = "roll_id";
    //private static final String ROLL_ID = "roll_id";

    private static final String SHOP_NAME = "shop_name";
    private static final String ADDRESS = "address";
    private static final String SHOP_ADRESS = "shop_address";
    private static final String JOINING_DATE = "joining_date";
    private static final String LAST_UPDATE = "last_update";
    private static final String MATM_SERVICE_STATUS = "matm_service_status";
    private static final String MPOS_SERVICE_STATUS = "mpos_service_status";


    private static final String AUTO_LOGIN = "auto_login";


    private static final String LOGIN_PASSWORD = "login_password";
    private static final String LOGIN_ID = "login_id";

    private static final String APP_START_COUNT = "app_start_count";
    private static final String APP_NORMAL_UPDATE_TIME = "normal_update_time";
    private static final String APP_NORMAL_REAL_UPDATE_TIME = "normal_real_update_time";
    private static final String AEPS_DRIVIERS = "aeps_drviers";


    public AppPreference(Context context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(SHARED_PREF, Activity.MODE_PRIVATE);
        }
    }

    static Security security;

    public static AppPreference getInstance(Context context) {
        if (appPreference == null) {
            appPreference = new AppPreference(context);
        }
        security = new Security(APIs.ENCRYPTED_KEY);
        return appPreference;
    }


    //////////// id /////////////////////
    public void setId(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(ID, id);
        editor.apply();
    }

    public void setKYC(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("KYC_STATUS_1", id);
        editor.apply();
    }

    public void setMobileVerified(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("MobileVerified", id);
        editor.apply();
    }

    public void setEmailVerified(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("EmailVerified", id);
        editor.apply();
    }

    public void setUserKYC(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("USER_KYC_STATUS_1", id);
        editor.apply();
    }


    public void setSelectRdServiceDevice(String rdServiceDevice) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(SELECT_RD_SERVICE_DEVICE, rdServiceDevice);
        editor.apply();
    }

    public String getSelectedRdServiceDevice() {
        return mSharedPref.getString(SELECT_RD_SERVICE_DEVICE, "MANTRA");
    }

    public int getMobileVerified() {
        return mSharedPref.getInt("MobileVerified", 0);
    }

    public int getEmailVerified() {
        return mSharedPref.getInt("EmailVerified", 0);
    }

    public int getKYC() {
        return mSharedPref.getInt("KYC_STATUS_1", 0);
    }

    public int getUserKYC() {
        return mSharedPref.getInt("USER_KYC_STATUS_1", 0);
    }

    public int getCHANGEPIN() {
        return mSharedPref.getInt(CHANGEPIN, 0);
    }

    public void setChangePin(int changepin) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(CHANGEPIN, changepin);
        editor.apply();
    }

    public String getGender() {
        return mSharedPref.getString(GENDER, "");
    }

    public void setGender(String gender) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(GENDER, gender);
        editor.apply();
    }

    public String getSTATEID() {
        return mSharedPref.getString(STATE_ID, "");
    }

    public void setSTATEID(String state_id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(STATE_ID, state_id);
        editor.apply();
    }

    public String getCity() {
        return mSharedPref.getString(CITY, "");
    }

    public void setCity(String city) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(CITY, city);
        editor.apply();
    }

    public void delete_id() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(ID);
        editor.apply();
    }

    public int getId() {
        return mSharedPref.getInt(ID, 0);
    }


    //////////// id /////////////////////
    public void setRollId(int id) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(ROLL_ID, id);
        editor.apply();
    }

    public void deleteRollId() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(ROLL_ID);
        editor.apply();
    }

    public int getRollId() {
        return mSharedPref.getInt(ROLL_ID, 0);
    }

    //
    public void setSettlementBankInfo(int value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(IS_SETTLEMENT_BANK, value);
        editor.apply();
    }

    public int getSettlementBankInfo() {
        return mSharedPref.getInt(IS_SETTLEMENT_BANK, 0);
    }


    public void setIsAadhaarKyc(int value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(IS_AADHAAR_KYC, value);
        editor.apply();
    }


    public int getIsAadhaarKyc() {
        return mSharedPref.getInt(IS_AADHAAR_KYC, 0);
    }


    public int getIsLoginIdCreated() {
        return mSharedPref.getInt(IS_LOGIN_ID_CREATED, 0);
    }

    public void setIsLoginIdCreated(int value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(IS_LOGIN_ID_CREATED, value);
        editor.apply();
    }


    public void setIsVideoKyc(int value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(IS_VIDEO_KYC, value);
        editor.apply();
    }

    public int getIsVideoKyc() {
        return mSharedPref.getInt(IS_VIDEO_KYC, 0);
    }


    public void setIsAepsKyc(int value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(IS_AEPS_KYC, value);
        editor.apply();
    }

    public int getIsAepsKyc() {
        return mSharedPref.getInt(IS_AEPS_KYC, 0);
    }


    public void setPopupSee(boolean popup) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(POPUPSEE, popup);
        editor.apply();
    }

    public boolean getPopupSee() {
        return mSharedPref.getBoolean(POPUPSEE, false);
    }


    //notification
    public void setPopup(String popup) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(POPUP, popup);
        editor.apply();
    }

    public String getPopup() {
        return mSharedPref.getString(POPUP, "");
    }

    //////////// token /////////////////////
    public void setToken(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public void deletToken() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(TOKEN);
        editor.apply();
    }

    public String getToken() {
        return mSharedPref.getString(TOKEN, "");
    }

    //kyc_status
    String KYC_STATUS = "kyc_status";

    public int getKYC_STATUS() {
        return mSharedPref.getInt(KYC_STATUS, 0);
    }

    public void setKYC_STATUS(int kyc_status) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(KYC_STATUS, kyc_status);
        editor.apply();
    }

    ///pan number
    String PAN = "pan";

    public void setPan(String pan) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PAN, pan);
        editor.apply();
    }

    public String getPan() {
        return (mSharedPref.getString(PAN, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(PAN, ""));
    }

    ///adhaar number
    String ADHAAR = "adhaar";

    public void setAdhaar(String adhaar) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADHAAR, adhaar);
        editor.apply();
    }

    public String getAdhaar() {
        return (mSharedPref.getString(ADHAAR, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(ADHAAR, ""));
    }

    ///pincode
    String PINCODE = "pincode";

    public void setPINCODE(String pincode) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PINCODE, pincode);
        editor.apply();
    }

    public String getPINCODE() {

        return (mSharedPref.getString(PINCODE, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(PINCODE, ""));
    }

    public void setFather(String father) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(FATHER, father);
        editor.apply();
    }

    public String getFather() {
        return (mSharedPref.getString(FATHER, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(FATHER, ""));
    }

    public void setDob(String dob) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(DOB, dob);
        editor.apply();
    }

    public String getDob() {
        return (mSharedPref.getString(DOB, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(DOB, ""));
    }

    //////////// name /////////////////////
    public void setName(String name) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public void deleteName() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(NAME);
        editor.apply();
    }

    public String getName() {
        return (mSharedPref.getString(NAME, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(NAME, ""));
    }

    //////////// email /////////////////////
    public void setEmail(String email) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public void deleteEmail() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(EMAIL);
        editor.apply();
    }

    public String getEmail() {
        return (mSharedPref.getString(EMAIL, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(EMAIL, ""));
    }

    public void setRoleTitle(String roleTitle) {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ROLE_TITLE, roleTitle);
        editor.apply();
    }

    public String getRoleTitle() {
        return (mSharedPref.getString(ROLE_TITLE, "0.00"));
    }


    //////////// user balance /////////////////////
    public void setUserBalance(String userBalance) {

        String mBalance = "";
        if (userBalance.contains(","))
            mBalance = userBalance.replace(",", "");
        else mBalance = userBalance;

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(USER_BALANCE, mBalance);
        editor.apply();
    }

    public void deleteUserBalance() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(USER_BALANCE);
        editor.apply();
    }

    public String getUserBalance() {
        return (mSharedPref.getString(USER_BALANCE, "0.00"));
    }


    //////////// profile pic /////////////////////
    public void setProfilePic(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PROFILE_PIC, profilePic);
        editor.apply();
    }

    public void deleteProfilePick() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(PROFILE_PIC);
        editor.apply();
    }

    public String getProfilePic() {
        return mSharedPref.getString(PROFILE_PIC, "");
    }

    //////////// pancard pic /////////////////////
    public void setPANCARD_PIC(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(PANCARD_PIC, profilePic);
        editor.apply();
    }

    public String getPANCARD_PIC() {
        return mSharedPref.getString(PANCARD_PIC, "");
    }

    //////////// adhaar pic /////////////////////
    public void setAdhaarPic(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADHAAR_PIC, profilePic);
        editor.apply();
    }

    public String getAdhaarPic() {
        return mSharedPref.getString(ADHAAR_PIC, "");
    }


    //////////// adhaar back pic /////////////////////
    public void setADHAAR_BACK_PIC(String profilePic) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADHAAR_BACK_PIC, profilePic);
        editor.apply();
    }

    public String getADHAAR_BACK_PIC() {
        return mSharedPref.getString(ADHAAR_BACK_PIC, "");
    }


    //////////// mobile /////////////////////
    public void setMobile(String mobile) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(MOBILE, mobile);
        editor.apply();
    }

    public void deleteMobile() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(MOBILE);
        editor.apply();
    }

    public String getMobile() {
        return mSharedPref.getString(MOBILE, "");
    }


    //////////// otp /////////////////////
    public void setOtp(int otp) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(OTP_NUMBER, otp);
        editor.apply();
    }

    public void deleteOtp() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(OTP_NUMBER);
        editor.apply();
    }

    public int getOtp() {
        return mSharedPref.getInt(OTP_NUMBER, 0);
    }


    //////////// shop name /////////////////////
    public void setShopName(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(SHOP_NAME, token);
        editor.apply();
    }

    public void deleteShopName() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(SHOP_NAME);
        editor.apply();
    }

    public String getShopName() {
        return (mSharedPref.getString(SHOP_NAME, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(SHOP_NAME, ""));
    }


    //////////// shop address /////////////////////
    public void setShopAdress(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(SHOP_ADRESS, token);
        editor.apply();
    }

    public void deleteShopAddress() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(SHOP_ADRESS);
        editor.apply();
    }

    public String getShopAdress() {

        return (mSharedPref.getString(SHOP_ADRESS, "").equalsIgnoreCase("null") ? "" : mSharedPref.getString(SHOP_ADRESS, ""));
    }


    //////////// shop address /////////////////////
    public void setAddress(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(ADDRESS, token);
        editor.apply();
    }

    public void deleteAddress() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(ADDRESS);
        editor.apply();
    }

    public String getAddress() {
        return mSharedPref.getString(ADDRESS, "");
    }


    //////////// matm address /////////////////////
    public void setMatmStatus(String status) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(MATM_SERVICE_STATUS, status);
        editor.apply();
    }

    public void deleteMatmStatus() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(MATM_SERVICE_STATUS);
        editor.apply();
    }

    public String getMatmStatus() {
        return mSharedPref.getString(MATM_SERVICE_STATUS, "0");
    }


    //////////// mpos address /////////////////////
    public void setMposStatus(String status) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(MPOS_SERVICE_STATUS, status);
        editor.apply();
    }

    public void deleteMposStatus() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(MPOS_SERVICE_STATUS);
        editor.apply();
    }

    public String getMposStatus() {
        return mSharedPref.getString(MPOS_SERVICE_STATUS, "0");
    }


    //////////// joining date /////////////////////
    public void setJoiningDate(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(JOINING_DATE, token);
        editor.apply();
    }

    public void deleteJoiningDate() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(JOINING_DATE);
        editor.apply();
    }

    public String getJoiningDate() {
        return mSharedPref.getString(JOINING_DATE, "");
    }


    //////////// last update /////////////////////
    public void setLastUpdate(String token) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(LAST_UPDATE, token);
        editor.apply();
    }

    public void deleteLastUpdate() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(LAST_UPDATE);
        editor.apply();
    }

    public String getLastUpdate() {
        return mSharedPref.getString(LAST_UPDATE, "");
    }


    //////////// app start count /////////////////////
    public void setAppStartCount(int appStartCount) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(APP_START_COUNT, appStartCount);
        editor.apply();
    }

    public void deleteAppStartCount() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(APP_START_COUNT);
        editor.apply();
    }

    public int getAppStartCount() {
        return mSharedPref.getInt(APP_START_COUNT, 1);
    }


    public void setLoginPassword(String period) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(LOGIN_PASSWORD, period);
        editor.apply();
    }

    public void deleteLoginPassword() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(LOGIN_PASSWORD);
        editor.apply();
    }

    public String getLoginPassword() {
        return mSharedPref.getString(LOGIN_PASSWORD, "");
    }


    public void setLoginID(String period) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(LOGIN_ID, period);
        editor.apply();
    }

    public void deleteLoginID() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(LOGIN_ID);
        editor.apply();
    }

    public String getLoginID() {
        return mSharedPref.getString(LOGIN_ID, "");
    }


    public void setAutoLogin(boolean autoLogin) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(AUTO_LOGIN, autoLogin);
        editor.apply();
    }

    public void deleteAutoLogin() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(AUTO_LOGIN);
        editor.apply();
    }

    public boolean getAutoLogin() {
        return mSharedPref.getBoolean(AUTO_LOGIN, false);
    }


    public void setNormalUpdateTime(Long value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putLong(APP_NORMAL_UPDATE_TIME, value);
        editor.apply();
    }


    public Long getNormalUpdateTime() {
        return mSharedPref.getLong(APP_NORMAL_UPDATE_TIME, 0L);
    }


    public void setNormalRealUpdateTime(Long value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putLong(APP_NORMAL_REAL_UPDATE_TIME, value);
        editor.apply();
    }


    public Long getNormalRealUpdateTime() {
        return mSharedPref.getLong(APP_NORMAL_REAL_UPDATE_TIME, 0L);
    }


    public void setAepsDriviers(List<AepsDriver> value) {

        String json = new Gson().toJson(value);

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(AEPS_DRIVIERS, json);
        editor.apply();
    }


    public List<AepsDriver> getAepsDriviers() {
        String data = mSharedPref.getString(AEPS_DRIVIERS, "");
        if (data == null || data.equals("")) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(data, new TypeToken<List<AepsDriver>>() {
            }.getType());
        }
    }


}
