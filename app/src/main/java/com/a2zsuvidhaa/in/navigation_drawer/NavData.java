package com.a2zsuvidhaa.in.navigation_drawer;

import android.content.Context;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.R;

import java.util.ArrayList;


public class NavData {


    public static ArrayList<NavMenuModel> getMenuNavigasi(Context context) {
        ArrayList<NavMenuModel> menu = new ArrayList<>();

        menu.add(new NavMenuModel("Home", R.drawable.icon_home));
        if (AppPreference.getInstance(context).getRollId() == 5) {
            menu.add(new NavMenuModel("Dashboard", R.drawable.icon_dashboard));

        }


        if (AppPreference.getInstance(context).getRollId() == 5) {
            menu.add(new NavMenuModel("Reports", R.drawable.icon_report,
                    new ArrayList<NavMenuModel.SubMenuModel>() {{
                        add(new NavMenuModel.SubMenuModel("Ledger Report"));
                        add(new NavMenuModel.SubMenuModel("AEPS Report"));
                        add(new NavMenuModel.SubMenuModel("Matm/Mpos Report"));
                        //add(new NavMenuModel.SubMenuModel("Usage Report"));
                        //if (SharedPref.getInstance(context).getRollId() != 1)
                        //    add(new NavMenuModel.SubMenuModel("Wallet Load"));
                        // add(new NavMenuModel.SubMenuModel("Account Statement"));
                    }}));
        }

        if (AppPreference.getInstance(context).getRollId() == 4 || AppPreference.getInstance(context).getRollId() == 3) {
            menu.add(new NavMenuModel("Reports", R.drawable.icon_report,
                    new ArrayList<NavMenuModel.SubMenuModel>() {{
                        add(new NavMenuModel.SubMenuModel("Network Ledger"));
                        add(new NavMenuModel.SubMenuModel("Network Recharge"));
                        add(new NavMenuModel.SubMenuModel("Account Statement"));
                    }}));
        }
        if (AppPreference.getInstance(context).getRollId() == 5) {
            menu.add(new NavMenuModel("Funds", R.drawable.icon_dmt,
                    new ArrayList<NavMenuModel.SubMenuModel>() {{
                        add(new NavMenuModel.SubMenuModel("Fund Request"));
                        add(new NavMenuModel.SubMenuModel("R2R Transfer"));
                        add(new NavMenuModel.SubMenuModel("Fund Report"));
                        add(new NavMenuModel.SubMenuModel("DT Report"));
                    }}));
        }
        if (AppPreference.getInstance(context).getRollId() == 4) {
            menu.add(new NavMenuModel("Members", R.drawable.icon_user,
                    new ArrayList<NavMenuModel.SubMenuModel>() {{
                        add(new NavMenuModel.SubMenuModel("Retailer List"));
                        add(new NavMenuModel.SubMenuModel("Create User"));
                    }}));
        }

        if (AppPreference.getInstance(context).getRollId() == 3) {
            menu.add(new NavMenuModel("Members", R.drawable.icon_user,
                    new ArrayList<NavMenuModel.SubMenuModel>() {{
                        add(new NavMenuModel.SubMenuModel("Retailer List"));
                        add(new NavMenuModel.SubMenuModel("Distributor List"));
                        add(new NavMenuModel.SubMenuModel("Create User"));
                    }}));
        }

        if (AppPreference.getInstance(context).getRollId() == 4 || AppPreference.getInstance(context).getRollId() == 3)
            menu.add(new NavMenuModel("Payment", R.drawable.icon_dmt,
                    new ArrayList<NavMenuModel.SubMenuModel>() {{
                        if (AppPreference.getInstance(context).getRollId() == 3)
                            add(new NavMenuModel.SubMenuModel("Distributor Fund Transfer"));
                        add(new NavMenuModel.SubMenuModel("Retailer Fund Transfer"));
                        add(new NavMenuModel.SubMenuModel("Agent Request View"));
                        add(new NavMenuModel.SubMenuModel("Payment Report"));
                        add(new NavMenuModel.SubMenuModel("Fund Transfer Report"));
                        //  add(new NavMenuModel.SubMenuModel("Purchase Balance"));
                    }}));


        menu.add(new NavMenuModel("Change Password", R.drawable.icon_change_password));

        if (AppPreference.getInstance(context).getRollId() == 5 ||
                AppPreference.getInstance(context).getRollId() == 4 ||
                AppPreference.getInstance(context).getRollId() == 3)
            menu.add(new NavMenuModel("Change Pin", R.drawable.icon_pin));


        if (AppPreference.getInstance(context).getRollId() == 4) {

            menu.add(new NavMenuModel("Fund Request", R.drawable.icon_dmt));
        }

   /*     if (SharedPref.getInstance(context).getRollId() == 1) {
            menu.add(new NavMenuModel("Api Balance", R.drawable.icon_dmt));
            menu.add(new NavMenuModel("Service Management", R.drawable.icon_reset));
            menu.add(new NavMenuModel("Update News", R.drawable.icon_news));
        }*/

        if (AppPreference.getInstance(context).getRollId() == 5) {
            menu.add(new NavMenuModel("View Complain", R.drawable.icon_complain));
            menu.add(new NavMenuModel("My Commission", R.drawable.money_bag));
            //  menu.add(new NavMenuModel("Bank Detail", R.drawable.icon_bank));
            menu.add(new NavMenuModel("Aeps Settlement", R.drawable.icon_bank));
        }

        if (AppPreference.getInstance(context).getRollId() == 5) {
            menu.add(new NavMenuModel("Aadhaar Kyc", R.drawable.fingerprint));
            menu.add(new NavMenuModel("Document Kyc", R.drawable.upload));
            menu.add(new NavMenuModel("Matm Service", R.drawable.atm));
          //  menu.add(new NavMenuModel("RICTC Agreement", R.drawable.train));

        }
        menu.add(new NavMenuModel("User Agreement", R.drawable.icon_report));
        menu.add(new NavMenuModel("Logout", R.drawable.icon_logout));

        return menu;
    }
}
