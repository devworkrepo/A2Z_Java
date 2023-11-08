package com.a2zsuvidhaa.in.activity.kyc;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

public class UserData {

    private transient final String USER_DATA_SP = "userDataSP";
    private transient final String USER_DATA_JSON = "userDataJson";
    private transient SharedPreferences sharedPref;
    private transient SharedPreferences.Editor sPrefEditor;
    private transient Context context;

    /* NOTE:
     *
     * Data will be saved in json format in sharedPreferences
     *
     * When we create object of this class then the json should be passed
     * in the constructor
     *
     * To access data or set data, use getters or setters
     *
     * after setting data call object.updateData;
     * so that the data gets updated in the sharedPreferences
     *
     * */

    /* NOTE: TO add more parameters
     *  Add them globally and then add them in
     *  METHOD -> setUserDataFromJson()
     *  METHOD -> initializeAllStrings()
     *  {above is necessary, else nullPointerException}
     *
     *  UPDATE -> toString override Method (this for us)*/

    // user information that is given in aadhaar kyc document
    private String name, birthDate, emailSha, gender, mobileSha, fatherName,
            country, district, house, landmark, location, pinCode,
            state, street, subDistrict, vtc, photoByteCode,
            email, mobile, password, aadharReferenceId, walletBalance;

    // information which is given in login response
    private String status, roleId, id, token, userBalance,
            otpNumber, message, shopName, address, shop_address,
            joiningDate, lastUpdate, panCardPicture, profilePicture,
            stateId, appStartCount, loginPassword, changePin,
            aadhaarCardNo, panCardNo, aadhaarFrontPic, aadharBackPic,
            popup, kycStatus;

    private boolean isAutoLoginEnabled, isLoggedInAlready, isKycVerified, isShowPopup;

    // constructor
    public UserData(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(USER_DATA_SP, Context.MODE_PRIVATE);
        sPrefEditor = sharedPref.edit();
        sPrefEditor.apply();

        initializeAllStrings(); // to prevent null exceptions

        String jsonString = sharedPref.getString(USER_DATA_JSON, "");
        // if there is no data then do nothing
        assert jsonString != null;
        if (!jsonString.equals("")) {

            Gson gson = new Gson();
            UserData userData = gson.fromJson(jsonString, UserData.class);
            setUserDataFromJson(userData);
            // now we have assigned previous (old) data to this object
        } else {
            Toast.makeText(context, "NO DATA FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    // for updating userDataInSP
    public void applyUpdate() {

        /* TO UPDATE DATA FOLLOW THESE STEPS
         * 1. Create the object (created by old json automatically)
         * 2. Set the data you want to change
         * 3. Now call updateData from the object in which data changed
         * 4. We will save the data from that object in SharedPref
         * 5. HOW? updated strings gets replaced and others will be there as it is
         *  */

        sharedPref = context.getSharedPreferences(USER_DATA_SP, Context.MODE_PRIVATE);
        sPrefEditor = sharedPref.edit();
        sPrefEditor.putString(USER_DATA_JSON, new Gson().toJson(this));
        sPrefEditor.apply();

    }

    // internal used method
    private void setUserDataFromJson(UserData userData) {
        this.name = userData.name;
        this.birthDate = userData.birthDate;
        this.emailSha = userData.emailSha;
        this.gender = userData.gender;
        this.mobileSha = userData.mobileSha;
        this.fatherName = userData.fatherName;
        this.country = userData.country;
        this.district = userData.district;
        this.house = userData.house;
        this.landmark = userData.landmark;
        this.location = userData.location;
        this.pinCode = userData.pinCode;
        this.state = userData.state;
        this.street = userData.street;
        this.subDistrict = userData.subDistrict;
        this.vtc = userData.vtc;
        this.photoByteCode = userData.photoByteCode;
        this.email = userData.email;
        this.mobile = userData.mobile;
        this.password = userData.password;
        this.isLoggedInAlready = userData.isLoggedInAlready;
        this.aadharReferenceId = userData.aadharReferenceId;
        this.isKycVerified = userData.isKycVerified;
        this.walletBalance = userData.walletBalance;

        this.status = userData.status;
        this.roleId = userData.roleId;
        this.id = userData.id;
        this.token = userData.token;
        this.userBalance = userData.userBalance;
        this.otpNumber = userData.otpNumber;
        this.message = userData.message;
        this.shopName = userData.shopName;
        this.address = userData.address;
        this.shop_address = userData.shop_address;
        this.joiningDate = userData.joiningDate;
        this.lastUpdate = userData.lastUpdate;
        this.panCardPicture = userData.panCardPicture;
        this.profilePicture = userData.profilePicture;
        this.stateId = userData.stateId;
        this.appStartCount = userData.appStartCount;
        this.loginPassword = userData.loginPassword;
        this.changePin = userData.changePin;
        this.aadhaarCardNo = userData.aadhaarCardNo;
        this.panCardNo = userData.panCardNo;
        this.aadhaarFrontPic = userData.aadhaarFrontPic;
        this.aadharBackPic = userData.aadharBackPic;
        this.popup = userData.popup;
        this.kycStatus = userData.kycStatus;
        this.isShowPopup = userData.isShowPopup;
        this.isAutoLoginEnabled = userData.isAutoLoginEnabled;
    }

    public String getChangePin() {
        return changePin;
    }

    public void setChangePin(String changePin) {
        this.changePin = changePin;
    }

    // internal used method
    private void initializeAllStrings() {
        // To prevent null pointer exceptions we are assigning values

        String initialValue = "";
        String initialBalance = "0.00";

        this.name = initialValue;
        this.birthDate = initialValue;
        this.emailSha = initialValue;
        this.gender = initialValue;
        this.mobileSha = initialValue;
        this.fatherName = initialValue;
        this.country = initialValue;
        this.district = initialValue;
        this.house = initialValue;
        this.landmark = initialValue;
        this.location = initialValue;
        this.pinCode = initialValue;
        this.state = initialValue;
        this.street = initialValue;
        this.subDistrict = initialValue;
        this.vtc = initialValue;
        this.photoByteCode = initialValue;
        this.email = initialValue;
        this.mobile = initialValue;
        this.password = initialValue;
        this.aadharReferenceId = initialValue;
        this.walletBalance = initialBalance;
        this.status = initialValue;
        this.roleId = initialValue;
        this.id = initialValue;
        this.token = initialValue;
        this.userBalance = initialValue;
        this.otpNumber = initialValue;
        this.message = initialValue;
        this.shopName = initialValue;
        this.address = initialValue;
        this.shop_address = initialValue;
        this.joiningDate = initialValue;
        this.lastUpdate = initialValue;
        this.panCardPicture = initialValue;
        this.profilePicture = initialValue;
        this.stateId = initialValue;
        this.appStartCount = initialValue;
        this.loginPassword = initialValue;
        this.changePin = initialValue;
        this.panCardNo = initialValue;
        this.aadhaarFrontPic = initialValue;
        this.aadharBackPic = initialValue;
        this.popup = initialValue;
        this.kycStatus = initialValue;
        this.aadhaarCardNo = initialValue;

        this.isKycVerified = false;
        this.isLoggedInAlready = false;
        this.isAutoLoginEnabled = false;
        this.isShowPopup = false;

    }

    public void eraseData() {
        initializeAllStrings();
        applyUpdate();
    }

    @Override
    public String toString() {
        return "UserData{" +
                "USER_DATA_SP='" + USER_DATA_SP + '\'' +
                ", USER_DATA_JSON='" + USER_DATA_JSON + '\'' +
                ", sharedPref=" + sharedPref +
                ", sPrefEditor=" + sPrefEditor +
                ", context=" + context +
                ", name='" + name + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", emailSha='" + emailSha + '\'' +
                ", gender='" + gender + '\'' +
                ", mobileSha='" + mobileSha + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", country='" + country + '\'' +
                ", district='" + district + '\'' +
                ", house='" + house + '\'' +
                ", landmark='" + landmark + '\'' +
                ", location='" + location + '\'' +
                ", pinCode='" + pinCode + '\'' +
                ", state='" + state + '\'' +
                ", street='" + street + '\'' +
                ", subDistrict='" + subDistrict + '\'' +
                ", vtc='" + vtc + '\'' +
                ", photoByteCode='" + photoByteCode + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", aadharReferenceId='" + aadharReferenceId + '\'' +
                ", isLoggedInAlready='" + isLoggedInAlready + '\'' +
                '}';
    }

    // GETTERS AND SETTERS BELOW

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmailSha() {
        return emailSha;
    }

    public void setEmailSha(String emailSha) {
        this.emailSha = emailSha;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileSha() {
        return mobileSha;
    }

    public void setMobileSha(String mobileSha) {
        this.mobileSha = mobileSha;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getVtc() {
        return vtc;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }

    public String getPhotoByteCode() {
        return photoByteCode;
    }

    public void setPhotoByteCode(String photoByteCode) {
        this.photoByteCode = photoByteCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsLoggedInAlready() {
        return isLoggedInAlready;
    }

    public void setIsLoggedInAlready(boolean isLoggedInAlready) {
        this.isLoggedInAlready = isLoggedInAlready;
    }

    public String getAadharReferenceId() {
        return aadharReferenceId;
    }

    public void setAadharReferenceId(String aadharReferenceId) {
        this.aadharReferenceId = aadharReferenceId;
    }

    public boolean getIsKycVerified() {
        return isKycVerified;
    }

    public void setIsKycVerified(boolean isKycVerified) {
        this.isKycVerified = isKycVerified;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public boolean isLoggedInAlready() {
        return isLoggedInAlready;
    }

    public void setLoggedInAlready(boolean loggedInAlready) {
        isLoggedInAlready = loggedInAlready;
    }

    public boolean isKycVerified() {
        return isKycVerified;
    }

    public void setKycVerified(boolean kycVerified) {
        isKycVerified = kycVerified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(String userBalance) {
        this.userBalance = userBalance;
    }

    public String getOtpNumber() {
        return otpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        this.otpNumber = otpNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getPanCardPicture() {
        return panCardPicture;
    }

    public void setPanCardPicture(String panCardPicture) {
        this.panCardPicture = panCardPicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getAppStartCount() {
        return appStartCount;
    }

    public void setAppStartCount(String appStartCount) {
        this.appStartCount = appStartCount;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public boolean isAutoLoginEnabled() {
        return isAutoLoginEnabled;
    }

    public void setAutoLoginEnabled(boolean autoLoginEnabled) {
        this.isAutoLoginEnabled = autoLoginEnabled;
    }

    public String getAadhaarCardNo() {
        return aadhaarCardNo;
    }

    public void setAadhaarCardNo(String aadhaarCardNo) {
        this.aadhaarCardNo = aadhaarCardNo;
    }

    public String getPanCardNo() {
        return panCardNo;
    }

    public void setPanCardNo(String panCardNo) {
        this.panCardNo = panCardNo;
    }

    public String getAadhaarFrontPic() {
        return aadhaarFrontPic;
    }

    public void setAadhaarFrontPic(String aadhaarFrontPic) {
        this.aadhaarFrontPic = aadhaarFrontPic;
    }

    public String getAadharBackPic() {
        return aadharBackPic;
    }

    public void setAadharBackPic(String aadharBackPic) {
        this.aadharBackPic = aadharBackPic;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public boolean isShowPopup() {
        return isShowPopup;
    }

    public void setShowPopup(boolean showPopup) {
        this.isShowPopup = showPopup;
    }
}
