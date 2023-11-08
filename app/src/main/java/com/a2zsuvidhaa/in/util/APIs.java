package com.a2zsuvidhaa.in.util;

import android.util.Log;

public class APIs {
    public static String PDF_BASE_URL = "https://partners.a2zsuvidhaa.com/mini_statement_doc/";
    public static String LOGIN_URL;
    public static String USER_KYC_COMPLETE;
    public static String USER_E_KYC;
    public static String SEND_OTP;
    public static String VERIFY_OTP;
    public static String RESEND_OTP_AES;
    public static String VERIFY_MOBILE_EMAIL;
    public static String VALIDATE_MOBILE_EMAIL;
    public static String VERIFY_KYC;
    public static String MY_NOTIFICATION;
    public static String UPDATE_USER_NAME;
    public static String GENERATE_QRCODE_URL;
    public static String SIGNIN_URL;
    public static String VERIFY_DEVICE;
    public static String WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP;
    public static String GET_RECHARGE_PROVIDER;
    public static String LEDGER_REPORT;
    public static String DIRECT_FUND_TRANSFER;
    public static String USAGE_REPORT;
    public static String SLIP_REPORT;
    public static String ACCOUNT_STATEMENT_REPORT;
    public static String NETWORK_RECHARGE_D;
    public static String FUND_REQUEST_BANK_LIST;
    public static String FUND_REQUEST_SAVE;
    public static String GET_RETAILER_DETAILS;
    public static String VERIFY_PIN;
    public static String FUND_TRANSFER_R2R;
    public static String VIEW_COMPLAIN;
    public static String GET_BANK_DETAILS;
    public static String RETAILER_DASHBOARD;
    public static String FUND_REPORT;
    public static String GET_TRANSACTION_DETAILS;
    public static String UPDATE_TRANSACTION;
    public static String COMPLAIN;
    public static String RECHARGE;
    public static String VIEW_ELECTRICITY_BILL;
    public static String ELECTRICITY_RECHARGE;
    public static String VERIFY_NUMBER_DMT1;
    public static String SENDER_REGISTRATION_DMT1;
    public static String SENDER_VERIFICATION_DMT1;
    public static String GET_BANK_LIST_DMT;
    public static String BANK_ACCOUNT_INFO_DMT;
    public static String CHECK_STATUS_VERI;
    public static String ADD_BENEFICIARY_DMT1;
    public static String DELETE_BENEFICIARY_DMT1;
    public static String DELETE_BENEFICIARY_DDMT1;
    public static String USER_KYC_DETAILS;
    public static String USER_DETAIL_DATA;

    public static String ELECTRICITY2_RECHARGE;

    public static String VERIFY_NUMBER_A2ZWallet;
    public static String ACCOUNT_SEARCH;
    public static String VERIFY_NUMBER_OTP_A2ZWallet;
    public static String REGISTER_REMITTER_A2ZWallet;
    public static String GET_BENElIST_A2ZWallet;
    public static String DELETE_BENEFICIARY_A2ZWallet;
    public static String DELETE_BENEFICIARY_OTP_A2ZWallet;
    public static String GET_BANK_LIST_A2ZWallet;
    public static String BENEFICIARY_ADD_A2ZWallet;
    public static String GET_REM_TRANSACTIONS;
    public static String GET_AGENT_CHARGE_AMT_A2ZWallet;
    public static String TRANSFER_MONEY_A2ZWallet;
    public static String GET_AGENT_CHARGE_AMT_DMT;
    public static String TRANSFER_MONEY_DMT1;
    public static String CHECK_BANK_DOWN;
    public static String CHANGE_PASSWROD;
    public static String GENERATE_NEW_PIN;
    public static String GET_GENERATED_PIN;
    public static String DIST_FUND_LIST;
    public static String FUND_TRANSFER;
    public static String FURN_RETURN;
    public static String AGENT_REQUEST_VIEW;
    public static String GET_LOADCASH_RECORD;
    public static String AGENT_REQUEST_APPROVE;
    public static String RETAILER_FUND_TRANSFER;
    public static String PAYMENT_REPORT;
    public static String FUND_TRANSFER_REPORT;
    public static String CHECK_IMPS_STATUS;
    public static String GET_RETAILER_COMMISSION;
    public static String GET_DISTRIBUTOR_COMMISSION;
    public static String GET_ADMIN_COMMISSION;
    public static String GET_NEWS;
    public static String UPDATE_NEWS;
    public static String FORGET_PASSWORD;
    public static String STORE_PASSWORD;
    public static String API_BALANCE;
    public static String SERVICE_MANAGEMENT;
    public static String UPDATE_SERVICE_MANAGEMENT;
    public static String GET_MEMBER;
    public static String USER_DEACTIVATE;
    public static String PURCHASE_BALANCE;
    public static String ADMIN_DASHBOARD;
    public static String GET_STATES;
    public static String GET_CITY;
    public static String CREATE_RETAILER;
    public static String UPDATE_RETAILER;
    public static String PAN_VERIFY;

    public static String PDF_URL;
    public static String AEPS_SETTLEMENT;
    public static String GET_MY_BANK_DETAILS;
    public static String ADD_BANK_DETAILS;
    public static String UPDATE_BANK_DETAILS;
    public static String GET_AEPS_BANK_LIST;
    public static String AEPS_TRANSACTION;
    public static String AEPS_TRANSACTION_THREE;
    public static String AEPS_TRANSACTION_ONE;
    public static String AEPS_THREE_CHECK_STATUS;
    public static String AEPS_THREE_CHECK_STATUS_FROM_BANK;
    public static String MATM_DIRECT_CHECK_STATUS;
    public static String ADHAR_TRANSACTION;


    public static String MOBILE_VERIFICATION_DMT2;
    public static String REGISTER_REMITTER_DMT2;
    public static String REMITTER_VERIFICATION_DMT2;
    public static String DMT_REMITTER_REGISTRATION_RESEND_OTP;
    public static String ADD_BENEFICIARY_DMT2;
    public static String DELETE_BENEFICIARY_DMT2;
    public static String CONFIRM_BENEFICIARY_DELETE_DMT2;
    public static String MONEY_TRANSACTION_DMT2;


    public static String GET_SPECIAL_OFFER;
    public static String GET_DTH_INFO;

    public static String VERIFY_PG_TRANSACTION;
    public static String GET_PAYMENT_DETAILS;
    public static String MAKE_PG_TRANSACTION;
    public static String UPDATE_PG_TRANSACTION;

    public static String REFRESH_PAGE;


    //NEW BBPS
    public static String BBPSONE;
    public static String BBPSTWO;
    public static String BBPSONESLIP;
    public static String BBPSTWOSLIP;


    /*tags*/
    public static String USER_TAG;
    public static String TOKEN_TAG;
    public static String PASSWORD_TAG;
    public static String ENCRYPTED_KEY;

    public static String DOWNLOAD_RECEIPT;
    public static String DOWNLOAD_NEW_RECEIPT;
    public static String DOWNLOAD_RECEIPT_AEPS_THREE;
    public static String USER_BALANCE;


    public static String SETTLEMENT_ADD_BANK_INFO;
    public static String SETTLEMENT_ADD_BANK;
    public static String AEPS_SETTLEMENT_UPLOAD_DOCS;

    public static String AEPS_REPORT;
    public static String MATM_REPORT;
    public static String AEPS_COMMISSION_CHARGE;

    /*constructor*/
    public APIs(String s1, String s2, String s3, String s4, String s5, String s6) {
        Log.e("s1", s1 + "" + s2 + s3 + s4 + s5 + s6);
        USER_TAG = s2;
        TOKEN_TAG = s3;
        PASSWORD_TAG = s4;
        ENCRYPTED_KEY = s6 + s2 + (s4.substring(0, s4.length() - 2));
        ENCRYPTED_KEY = ENCRYPTED_KEY.toUpperCase();

        USER_DETAIL_DATA = s1 + "user/details";
        SIGNIN_URL = s1 + "storesignup_mob";//sign  in_url
        LOGIN_URL = s1 + "agentLogin";//login_url
        VERIFY_DEVICE = s1 + "verify-device";//login_url
        WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP = s1 + "resend-device-otp";
        VERIFY_MOBILE_EMAIL = s1 + "user/send-verification-otp";
        VALIDATE_MOBILE_EMAIL = s1 + "user/validate-verification-otp";

        GENERATE_QRCODE_URL = s1 + "generate-qr-code";
        MY_NOTIFICATION = s1 + "get-notification";

        //api for a2z
        GET_RECHARGE_PROVIDER = s1 + "get-recharge-provider";
        LEDGER_REPORT = s1 + "all_recharge_report";
        DIRECT_FUND_TRANSFER = s1 + "get-direct-fund-transfer";
        USAGE_REPORT = s1 + "summary-report";
        SLIP_REPORT = s1 + "report-slip";
        ACCOUNT_STATEMENT_REPORT = s1 + "get-account-statement";
        NETWORK_RECHARGE_D = s1 + "get-recharge-report";
        FUND_REQUEST_BANK_LIST = s1 + "get-bank-list";
        FUND_REQUEST_SAVE = s1 + "fund-request-save";
        GET_RETAILER_DETAILS = s1 + "get-retailer-detail";
        VERIFY_PIN = s1 + "verify-pin";
        FUND_TRANSFER_R2R = s1 + "fund-transfer_r2r";
        VIEW_COMPLAIN = s1 + "complain-request-view";
        GET_BANK_DETAILS = s1 + "get-bank-details";
        RETAILER_DASHBOARD = s1 + "get-retailer-dashboard";


        //reports
        //public static final String LEDGER_REPORT=s1+"ledger-report";
        FUND_REPORT = s1 + "fund-report";
        GET_TRANSACTION_DETAILS = s1 + "get-transation-details";
        UPDATE_TRANSACTION = s1 + "update-transaction";
        COMPLAIN = s1 + "store_complain_req";

        //mobile recharge
        RECHARGE = s1 + "make-recharge";
        ELECTRICITY_RECHARGE = s1 + "make-electricity-recharge";
        VIEW_ELECTRICITY_BILL = s1 + "fetch";//-bill-amount";

        //BBPS 1 bill pay
        BBPSONE = s1 + "bill-payment-one";
        BBPSONESLIP = s1 + "bbps-one-preslip";
        //BBPS2 Bills
        BBPSTWO = s1 + "bill-payment-two";
        BBPSTWOSLIP = s1 + "bbps-two-preslip";

        //dmt1
        VERIFY_NUMBER_DMT1 = s1 + "money/mobile-verification";
        SENDER_REGISTRATION_DMT1 = s1 + "money/sender-registration";
        SENDER_VERIFICATION_DMT1 = s1 + "money/sender-verification";
        GET_BANK_LIST_DMT = s1 + "money/get-bankList";
        BANK_ACCOUNT_INFO_DMT = s1 + "money/account-name-info";
        CHECK_STATUS_VERI = s1 + "check-status";
        ADD_BENEFICIARY_DMT1 = s1 + "money/add_beneficiary";
        DELETE_BENEFICIARY_DMT1 = s1 + "money/delete-beneficiary";//1 for request otp
        DELETE_BENEFICIARY_DDMT1 = s1 + "money/delete-beneficiary-otp";//2 for delete

        //A2ZWallet
        ACCOUNT_SEARCH = s1 + "a2zplus/account-search";
        VERIFY_NUMBER_A2ZWallet = s1 + "a2zplus/mobile-verification";//"premium/mobile-verification";
        VERIFY_NUMBER_OTP_A2ZWallet = s1 + "a2zplus/mobile-verification-otp";//"premium/mobile-verification-otp";
        REGISTER_REMITTER_A2ZWallet = s1 + "a2zplus/remitter-registration";//"premium/remitter-registration";
        GET_BENElIST_A2ZWallet = s1 + "a2zplus/get-beniList";// "premium/get-beniList";
        DELETE_BENEFICIARY_A2ZWallet = s1 + "a2zplus/delete-beneficiary";//"premium/delete-beneficiary";
        DELETE_BENEFICIARY_OTP_A2ZWallet = s1 + "a2zplus/delete-beneficiary-otp";//"premium/delete-beneficiary-otp";
        GET_BANK_LIST_A2ZWallet = s1 + "premium/get-bank-list";
        BENEFICIARY_ADD_A2ZWallet = s1 + "a2zplus/bene-add";//"premium/bene-add";
        GET_REM_TRANSACTIONS = s1 + "get-mobile-transaction-report";//

        //transacation
        GET_AGENT_CHARGE_AMT_A2ZWallet = s1 + "money/get-agent-charge-amt";
        TRANSFER_MONEY_A2ZWallet = s1 + "money-transfer-a2z-plus-wallet";//"money-transfer-a2z-wallet";

        GET_AGENT_CHARGE_AMT_DMT = s1 + "money/get-agent-charge-amt";
        TRANSFER_MONEY_DMT1 = s1 + "money-transfer-dmt1";

        CHECK_BANK_DOWN = s1 + "money/check-bank-down";


        //change password
        CHANGE_PASSWROD = s1 + "change-password";
        //generate new pin
        GENERATE_NEW_PIN = s1 + "generate-new-pin";
        GET_GENERATED_PIN = s1 + "get-generated-pin";

        //admin fund transfer
        DIST_FUND_LIST = s1 + "distfundlist";
        FUND_TRANSFER = s1 + "fund-transafer";
        FURN_RETURN = s1 + "fund-return";
        AGENT_REQUEST_VIEW = s1 + "agent-request-view";
        GET_LOADCASH_RECORD = s1 + "get-loadcash-record";
        AGENT_REQUEST_APPROVE = s1 + "agent-request-approve";

        //retailer fund transfer
        RETAILER_FUND_TRANSFER = s1 + "retailer-fund-transfer";
        //payment report
        PAYMENT_REPORT = s1 + "payment-report";
        FUND_TRANSFER_REPORT = s1 + "fund-transfer-report";
        //check imps status
        CHECK_IMPS_STATUS = s1 + "get-imps-status";
        //get commission
        GET_RETAILER_COMMISSION = s1 + "view-retailer-commission";
        GET_DISTRIBUTOR_COMMISSION = s1 + "view-distributor-commission";
        GET_ADMIN_COMMISSION = s1 + "view-admin-commission";
        //getNews
        GET_NEWS = s1 + "get-news";
        UPDATE_NEWS = s1 + "update-news";
        //forget Password
        FORGET_PASSWORD = s1 + "forget-password";
        STORE_PASSWORD = s1 + "store-password";

        //api balance
        API_BALANCE = s1 + "api-balance";
        //service management
        SERVICE_MANAGEMENT = s1 + "get-active-services";
        UPDATE_SERVICE_MANAGEMENT = s1 + "make-active-inactive-services";

        //member
        GET_MEMBER = s1 + "get-members";
        USER_DEACTIVATE = s1 + "user-deactivate";
        PURCHASE_BALANCE = s1 + "purchase-balance";


        //admin dashboard
        ADMIN_DASHBOARD = s1 + "admin-dashboard";
        GET_STATES = s1 + "get-state-list";
        GET_CITY = s1 + "get-city-list";
        CREATE_RETAILER = s1 + "create-retailer";
        UPDATE_RETAILER = s1 + "update-profile";
        PAN_VERIFY = s1 + "pan-verification";


        //instant pay
        MOBILE_VERIFICATION_DMT2 = s1 + "instant/mobile-verification";
        REGISTER_REMITTER_DMT2 = s1 + "instant/register-remitter";
        REMITTER_VERIFICATION_DMT2 = s1 + "instant/remitter-verification";
        DMT_REMITTER_REGISTRATION_RESEND_OTP = s1 + "instant/otp-remitter-verification-resend";
        ADD_BENEFICIARY_DMT2 = s1 + "instant/add-beneficiary";
        DELETE_BENEFICIARY_DMT2 = s1 + "instant/delete_beneficiary";
        CONFIRM_BENEFICIARY_DELETE_DMT2 = s1 + "instant/confirm-bene-delete";
        MONEY_TRANSACTION_DMT2 = s1 + "dmt-two/transaction";

        //Aeps
        PDF_URL = s1 + "receipt-pdf";//login_url
        AEPS_SETTLEMENT = s1 + "a2z-settlement-request";
        GET_MY_BANK_DETAILS = s1 + "get-my-bank-details";
        ADD_BANK_DETAILS = s1 + "add-bank-details";
        UPDATE_BANK_DETAILS = s1 + "update-bank-details";
        GET_AEPS_BANK_LIST = s1 + "aeps/bank/list";
        AEPS_TRANSACTION = s1 + "aeps-transaction";
        AEPS_TRANSACTION_THREE = s1 + "aeps/three-new";
        AEPS_TRANSACTION_ONE = s1 + "aeps/one-new";
        AEPS_THREE_CHECK_STATUS = s1 + "aeps/three/table-check-status";
        AEPS_THREE_CHECK_STATUS_FROM_BANK = s1 + "aeps/three/checkstatus";
        MATM_DIRECT_CHECK_STATUS = s1 + "matm/direct/checkstatus";
        ADHAR_TRANSACTION = s1 + "aeps-ap-transaction";
        USER_KYC_DETAILS = s1 + "aeps/user-kyc-details";
        UPDATE_USER_NAME = s1 + "aeps/update-kyc-name";
        SEND_OTP = s1 + "aeps/kyc/send-otp";
        VERIFY_OTP = s1 + "aeps/kyc/otp/validate";
        RESEND_OTP_AES = s1 + "aeps/kyc/resend-otp";
        VERIFY_KYC = s1 + "aeps/kyc/verify-user";
        USER_KYC_COMPLETE = s1 + "user/update/kyc-status";
        USER_E_KYC = s1 + "user/e-kyc";
        //offer
        GET_DTH_INFO = s1 + "get-dth-customer-info";
        GET_SPECIAL_OFFER = s1 + "get-special-offer";
        REFRESH_PAGE = s1 + "page-refresh";

        //Payment Gateway
        VERIFY_PG_TRANSACTION = s1 + "verify-pg-otp";
        GET_PAYMENT_DETAILS = s1 + "get-payment-details";
        MAKE_PG_TRANSACTION = s1 + "send-atom-transaction";
        UPDATE_PG_TRANSACTION = s1 + "update-pg-transaction";

        DOWNLOAD_RECEIPT = s1 + "slip";
        DOWNLOAD_NEW_RECEIPT = s1 + "slip/new";
        DOWNLOAD_RECEIPT_AEPS_THREE = s1 + "aeps/report/slip";
        USER_BALANCE = s1 + "user/balance";

        SETTLEMENT_ADD_BANK_INFO = s1 + "aeps/settlement-bank";
        SETTLEMENT_ADD_BANK = s1+"aeps/add-bank-details";
        AEPS_SETTLEMENT_UPLOAD_DOCS = s1+"aeps/settlement/bank/document";
        AEPS_REPORT = s1+"aeps/report";
        MATM_REPORT = s1+"matm/report";
        AEPS_COMMISSION_CHARGE = s1+"aeps/transaction/charge-commission";
    }
}
