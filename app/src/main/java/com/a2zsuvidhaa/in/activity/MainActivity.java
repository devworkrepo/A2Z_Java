package com.a2zsuvidhaa.in.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.BuildConfig;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.activity.aeps.AepsSettlementActivityKT;
import com.a2zsuvidhaa.in.activity.fund_request.FundRequestActivity;
import com.a2zsuvidhaa.in.activity.login.LoginActivity;
import com.a2zsuvidhaa.in.activity.login_id.create.CreateLoginIdActivity;
import com.a2zsuvidhaa.in.activity.register.RegistrationActivity;
import com.a2zsuvidhaa.in.firebase.NormalAppUpdateInfo;
import com.a2zsuvidhaa.in.fragment.AdminApiBalanceFragment;
import com.a2zsuvidhaa.in.fragment.AdminDashboardFragment;
import com.a2zsuvidhaa.in.fragment.AgentRequestViewFragment2;
import com.a2zsuvidhaa.in.fragment.BankDetailFragment;
import com.a2zsuvidhaa.in.fragment.ChangePasswordFragment;
import com.a2zsuvidhaa.in.fragment.ChangePinFragment;
import com.a2zsuvidhaa.in.fragment.ComplainViewFragment;
import com.a2zsuvidhaa.in.fragment.FundReportFragment;
import com.a2zsuvidhaa.in.fragment.FundTransferFragment;
import com.a2zsuvidhaa.in.fragment.MatmReportFragment;
import com.a2zsuvidhaa.in.fragment.MemberFragment;
import com.a2zsuvidhaa.in.fragment.PaymentFundReportFragment;
import com.a2zsuvidhaa.in.fragment.ProfileFragment;
import com.a2zsuvidhaa.in.fragment.PurchaseBalanceFragment;
import com.a2zsuvidhaa.in.fragment.R2RTransferFragment;
import com.a2zsuvidhaa.in.fragment.ReportAepsFragment;
import com.a2zsuvidhaa.in.fragment.ReportFragment;
import com.a2zsuvidhaa.in.fragment.RetailerDashboardFragment;
import com.a2zsuvidhaa.in.fragment.ServiceManagementFragment;
import com.a2zsuvidhaa.in.fragment.UpdateNewsFragment;
import com.a2zsuvidhaa.in.fragment.ViewCommissionFragment;
import com.a2zsuvidhaa.in.fragment.addhar_kyc.AadhaarKycFragment;
import com.a2zsuvidhaa.in.fragment.agreement.AgreementFragment;
import com.a2zsuvidhaa.in.fragment.document_kyc.FragmentDocumentKyc;
import com.a2zsuvidhaa.in.fragment.home.HomeFragment;
import com.a2zsuvidhaa.in.fragment.matm_service.MatmServiceTabFragment;
import com.a2zsuvidhaa.in.fragment.report.LedgerReportFragment;
import com.a2zsuvidhaa.in.fragment.sale.SaleHomeFragment;
import com.a2zsuvidhaa.in.listener.KycRequiredListener;
import com.a2zsuvidhaa.in.listener.OnSaleItemClickListener;
import com.a2zsuvidhaa.in.navigation_drawer.NavData;
import com.a2zsuvidhaa.in.navigation_drawer.NavMenuAdapter;
import com.a2zsuvidhaa.in.navigation_drawer.NavMenuModel;
import com.a2zsuvidhaa.in.navigation_drawer.SubTitle;
import com.a2zsuvidhaa.in.navigation_drawer.TitleMenu;
import com.a2zsuvidhaa.in.util.AppConstants;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;
import com.a2zsuvidhaa.in.util.SessionManager;
import com.a2zsuvidhaa.in.util.dialogs.Dialogs;
import com.a2zsuvidhaa.in.util.dialogs.UpdateDialog;
import com.a2zsuvidhaa.in.util.enums.PaymentReportType;
import com.a2zsuvidhaa.in.util.enums.ReportTypes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import de.hdodenhof.circleimageview.CircleImageView;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity
        implements NavMenuAdapter.MenuItemClickListener, KycRequiredListener, OnSaleItemClickListener {

    @Inject
    NormalAppUpdateInfo normalAppUpdateInfo;

    @Inject
    AppPreference appPreference;


    private DrawerLayout drawer;
    ArrayList<NavMenuModel> menu;
    TextView tv_user_name;
    TextView tv_role;
    NavMenuAdapter adapter;
    CircleImageView iv_user_profile;
    Toolbar toolbar;
    private boolean shouldExit = true;

    private TextView tv_currentVersion;


    public static int mode_pos = 0;


    private OnFileImagePickerListener listener;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        String title = "A2Z Suvidha";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setupBottomNavMenu();

        setNavigationDrawerMenu();
        setupHeader();

        checkForUpdate();


        //if(SharedPref.getInstance(MainActivity.this).getPopupSee()==false)
        //showPopUpNotification();
    }

    @Override
    public void onBackPressed() {
        if (AppPreference.getInstance(MainActivity.this).getCHANGEPIN() == 2) {
            shouldExit = false;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (shouldExit)
                super.onBackPressed();
            else {
                shouldExit = true;
                toolbar.setTitle("Dashboard");


                if (appPreference.getRollId() == 5
                        || appPreference.getRollId() == 22
                        || appPreference.getRollId() == 23
                        || appPreference.getRollId() == 24
                ) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomNavigationView.setSelectedItemId(R.id.menu_home);
                } else if (appPreference.getRollId() == 4 || appPreference.getRollId() == 3) {
                    bottomNavigationView.setVisibility(View.GONE);
                    setupHomeFragmentSelected();
                }
            }
        }
    }


    private void checkForUpdate() {


        normalAppUpdateInfo.checkForNormalUpdate(getPackageName(), (force, enable, delay) -> {


            if (enable) UpdateDialog.INSTANCE.showUpdate(this, force, () -> {

                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                                "https://play.google.com/store/apps/details?id="
                                        + BuildConfig.APPLICATION_ID
                        )
                );
                startActivity(intent);
                return null;
            });


            return null;
        });













        

     /*   tv_currentVersion = findViewById(R.id.tv_currentVersion);
        tv_currentVersion.setText("Version : "+MyApplication.currentAppVersion);

        new AppVersionInfo2(getPackageName()).execute();
        AppVersionInfo2.setupListener(version -> {
            if(version!=null && !version.isEmpty()){
                try {
                    Log.e("version","="+version);
                    float playVersion = Float.parseFloat(version);
                    float currentVersion = Float.parseFloat(MyApplication.currentAppVersion);

                    Log.e("currentVersion","="+playVersion+" "+currentVersion);
                    if(playVersion>currentVersion){
                        showUpdateDialog(currentVersion, playVersion);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    MakeToast.show(this,"Failed to fetch update info!");
                }
            }
        });*/
    }


    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.icon_logout));
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.confirm_logout);
        builder.setPositiveButton(R.string.logout, (dialog, which) -> {
            SessionManager sessionManager = new SessionManager(MainActivity.this);
            sessionManager.clear();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            AppPreference.getInstance(this).deleteAutoLogin();
            AppPreference.getInstance(this).deleteLoginPassword();
            startActivity(intent);
            this.finish();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        AutoLogoutManager.cancelTimer();
        if (AutoLogoutManager.isSessionTimeout) {
            AutoLogoutManager.logout(this);
        }

        if (appPreference.getIsLoginIdCreated() == 1) return;

        Dialogs.INSTANCE.createLoginID(this, () -> {
            Intent intent = new Intent(this, CreateLoginIdActivity.class);
            startActivityForResult(intent,1001);
            return null;
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (AutoLogoutManager.isAppInBackground(this)) {
            AutoLogoutManager.startUserSession();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupHeader() {

        tv_user_name = findViewById(R.id.tv_username);
        tv_role = findViewById(R.id.tv_role);
        iv_user_profile = findViewById(R.id.iv_user_profile);
        tv_user_name.setText(AppPreference.getInstance(this).getName());
        tv_role.setText("Role Id: " + String.valueOf(AppPreference.getInstance(this).getId()));

        Picasso.get()
                .load(AppPreference.getInstance(this).getProfilePic())
                .resize(50, 50)
                .centerCrop()
                .placeholder(R.drawable.icon_no_image)
                .error(R.drawable.icon_no_image)
                .into(iv_user_profile);


        iv_user_profile.setOnClickListener(view -> {
            toolbar.setTitle("User Profile");
            shouldExit = false;
            setFragment(ProfileFragment.newInstance());
            drawer.closeDrawer(GravityCompat.START);
        });


    }


    private void setNavigationDrawerMenu() {

        adapter = new NavMenuAdapter(this, getMenuList(), this);
        RecyclerView navMenuDrawer = (RecyclerView) findViewById(R.id.main_nav_menu_recyclerview);
        navMenuDrawer.setAdapter(adapter);
        navMenuDrawer.setLayoutManager(new LinearLayoutManager(this));
        navMenuDrawer.setAdapter(adapter);
        adapter.selectedItemParent = menu.get(0).menuTitle;
        onMenuItemClick(adapter.selectedItemParent);
        adapter.notifyDataSetChanged();
    }


    private List<TitleMenu> getMenuList() {
        List<TitleMenu> list = new ArrayList<>();

        menu = NavData.getMenuNavigasi(this);
        for (int i = 0; i < menu.size(); i++) {
            ArrayList<SubTitle> subMenu = new ArrayList<>();
            if (menu.get(i).subMenu.size() > 0) {
                for (int j = 0; j < menu.get(i).subMenu.size(); j++) {
                    subMenu.add(new SubTitle(menu.get(i).subMenu.get(j).subMenuTitle));
                }
            }

            list.add(new TitleMenu(menu.get(i).menuTitle, subMenu, menu.get(i).menuIconDrawable));
        }
        return list;
    }

    public static int spos = 0;

    @Override
    public void onMenuItemClick(String itemString) {
        if (AppPreference.getInstance(MainActivity.this).getCHANGEPIN() == 1) {

            toolbar.setTitle("Change Pin");
            //shouldExit = true;
            setFragment(ChangePinFragment.newInstance());
        } else {

            bottomNavigationView.setVisibility(View.GONE);

            for (int i = 0; i < menu.size(); i++) {
                if (itemString.equals(menu.get(i).menuTitle)) {

                    if (itemString.equalsIgnoreCase("Home")) {
                        setupHomeFragmentSelected();
                    } else if (itemString.equalsIgnoreCase("Dashboard")) {
                        toolbar.setTitle("Dashboard");
                        shouldExit = false;
                        setFragment(RetailerDashboardFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("Aadhaar Kyc")) {
                        toolbar.setTitle("Aadhaar Kyc");
                        shouldExit = false;
                        setFragment(AadhaarKycFragment.Companion.newInstance(null));
                    } else if (itemString.equalsIgnoreCase("Document Kyc")) {
                        toolbar.setTitle("Document Kyc");
                        shouldExit = false;
                        setFragment(FragmentDocumentKyc.Companion.newInstance("main_activity", null));
                    } else if (itemString.equalsIgnoreCase("Matm Service")) {
                        toolbar.setTitle("Micro Atm Service");
                        shouldExit = false;
                        setFragment(MatmServiceTabFragment.Companion.newInstance());
                    }
                    else if (itemString.equalsIgnoreCase("User Agreement")) {
                        toolbar.setTitle("User Agreement");
                        shouldExit = false;
                        setFragment(AgreementFragment.Companion.newInstance(0));
                    }
                    else if (itemString.equalsIgnoreCase("RICTC Agreement")) {
                        toolbar.setTitle("RICTC Agreement");
                        shouldExit = false;
                        setFragment(AgreementFragment.Companion.newInstance(1));
                    } else if (itemString.equalsIgnoreCase("Logout")) {
                        logout();
                    } else if (itemString.equalsIgnoreCase("Change Password")) {
                        toolbar.setTitle("Change Password");
                        shouldExit = false;
                        setFragment(ChangePasswordFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("Change Pin")) {
                        toolbar.setTitle("Change Pin");
                        shouldExit = false;
                        setFragment(ChangePinFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("Api Balance")) {
                        toolbar.setTitle("Api Balance");
                        shouldExit = false;
                        setFragment(AdminApiBalanceFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("Service Management")) {
                        toolbar.setTitle("Service Management");
                        shouldExit = false;
                        setFragment(ServiceManagementFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("Update News")) {
                        toolbar.setTitle("Update News");
                        shouldExit = false;
                        setFragment(UpdateNewsFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("View Complain")) {
                        toolbar.setTitle("View Complain");
                        shouldExit = false;
                        setFragment(ComplainViewFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("My Commission")) {

                        if (toolbar.getTitle() == "Dashboard"
                                || toolbar.getTitle() == "Ledger Report"
                                || toolbar.getTitle() == "Aeps Report") {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                        Intent intent = new Intent(this, MyCommissionActivity.class);
                        startActivity(intent);
                    } else if (itemString.equalsIgnoreCase("Bank Detail")) {
                        spos = 0;
                        toolbar.setTitle("Bank Detail");
                        shouldExit = false;
                        setFragment(BankDetailFragment.newInstance());
                    } else if (itemString.equalsIgnoreCase("Aeps Settlement")) {
                        Intent intent = new Intent(this, AepsSettlementActivityKT.class);
                        startActivity(intent);
                    } else if (itemString.equalsIgnoreCase("Fund Request")) {

                        Intent intent = new Intent(this, FundRequestActivity.class);
                        intent.putExtra(AppConstants.ORIGIN, "distributor");
                        startActivity(intent);

                   /* Intent intent = new Intent(this, FundRequestActivity.class);
                    intent.putExtra("type","1");
                    startActivity(intent);*/
                    }

                    if (appPreference.getRollId() == 4 || appPreference.getRollId() == 3)
                        bottomNavigationView.setVisibility(View.GONE);
                    break;
                } else {
                    for (int j = 0; j < menu.get(i).subMenu.size(); j++) {
                        if (itemString.equals(menu.get(i).subMenu.get(j).subMenuTitle)) {

                            //-----reports retailers
                            if (itemString.equalsIgnoreCase("Ledger Report")) {
                                toolbar.setTitle("Ledger Report");
                                shouldExit = false;
                                bottomNavigationView.setVisibility(View.VISIBLE);
                                bottomNavigationView.setSelectedItemId(R.id.menu_ledger_report);
                                setFragment(LedgerReportFragment.Companion.newInstance());
                            } else if (itemString.equalsIgnoreCase("AEPS Report")) {

                                toolbar.setTitle("AEPS Report");
                                shouldExit = false;
                           /* bottomNavigationView.setVisibility(View.VISIBLE);
                            bottomNavigationView.setSelectedItemId(R.id.menu_aeps_report);
*/
                                setFragment(ReportAepsFragment.Companion.newInstance());
                            } else if (itemString.equalsIgnoreCase("Matm/Mpos Report")) {

                                toolbar.setTitle("Matm/Mpos Report");
                                shouldExit = false;
                                setFragment(MatmReportFragment.Companion.newInstance());
                            } else if (itemString.equalsIgnoreCase("Usage Report")) {
                                toolbar.setTitle("Usage Report");
                                shouldExit = false;
                                setFragment(ReportFragment.newInstance(ReportTypes.USAGE_REPORT));
                            }
                            //-----reports distributor
                            else if (itemString.equalsIgnoreCase("Network Ledger")) {
                                toolbar.setTitle("Network Ledger");
                                shouldExit = false;
                                setFragment(ReportFragment.newInstance(ReportTypes.LEDGER_REPORT));
                            } else if (itemString.equalsIgnoreCase("Network Recharge")) {
                                toolbar.setTitle("Network Recharge");
                                shouldExit = false;
                                setFragment(ReportFragment.newInstance(ReportTypes.NETWORK_RECHARGE));
                            } else if (itemString.equalsIgnoreCase("Account Statement")) {
                                toolbar.setTitle("Account Statement");
                                shouldExit = false;
                                setFragment(ReportFragment.newInstance(ReportTypes.ACCOUNT_STATEMENT));
                            } else if (itemString.equalsIgnoreCase("View Commission")) {
                                toolbar.setTitle("View Commission");
                                shouldExit = false;
                                setFragment(ViewCommissionFragment.newInstance());
                            } else if (itemString.equalsIgnoreCase("Fund Report")) {
                                toolbar.setTitle("Fund Report");
                                shouldExit = false;
                                setFragment(FundReportFragment.newInstance(ReportTypes.FUND_REPORT));
                            } else if (itemString.equalsIgnoreCase("DT Report")) {
                                toolbar.setTitle("DT Report");
                                shouldExit = false;
                                setFragment(FundReportFragment.newInstance(ReportTypes.DT_FUND_REPORT));
                            } else if (itemString.equalsIgnoreCase("Distributor Fund Transfer")) {
                                toolbar.setTitle("Distributor Fund Transfer");
                                shouldExit = false;
                                setFragment(FundTransferFragment.newInstance(FundTransferFragment.DISTRIBUTOR));
                            } else if (itemString.equalsIgnoreCase("Retailer Fund Transfer")) {
                                toolbar.setTitle("Retailer Fund Transfer");
                                shouldExit = false;
                                setFragment(FundTransferFragment.newInstance(FundTransferFragment.RETAILER));
                            } else if (itemString.equalsIgnoreCase("Agent Request View")) {
                                toolbar.setTitle("Agent Request View");
                                shouldExit = false;
                                setFragment(AgentRequestViewFragment2.Companion.newInstance());
                            } else if (itemString.equalsIgnoreCase("Payment Report")) {
                                toolbar.setTitle("Payment Report");
                                shouldExit = false;
                                setFragment(PaymentFundReportFragment.newInstance(PaymentReportType.PAYMENT_REPORT));
                            } else if (itemString.equalsIgnoreCase("Fund Transfer Report")) {
                                toolbar.setTitle("Fund Transfer Report");
                                shouldExit = false;
                                setFragment(PaymentFundReportFragment.newInstance(PaymentReportType.FUND_TRANSFER_REPORT));
                            } else if (itemString.equalsIgnoreCase("Retailer List")) {
                                toolbar.setTitle("Retailers");
                                shouldExit = false;
                                setFragment(MemberFragment.newInstance("", ""));
                            } else if (itemString.equalsIgnoreCase("Distributor List")) {
                                toolbar.setTitle("Distributors");
                                shouldExit = false;
                                setFragment(MemberFragment.newInstance("Distributor", ""));
                            } else if (itemString.equalsIgnoreCase("Purchase Balance")) {
                                toolbar.setTitle("Purchase Balance");
                                shouldExit = false;
                                setFragment(PurchaseBalanceFragment.newInstance());
                            } else if (itemString.equalsIgnoreCase("R2R Transfer")) {
                                toolbar.setTitle("R2R Transfer");
                                shouldExit = false;
                                setFragment(R2RTransferFragment.newInstance());
                            } else if (itemString.equalsIgnoreCase("Fund Request")) {

                                Intent intent = new Intent(this, FundRequestActivity.class);
                                intent.putExtra(AppConstants.ORIGIN, "distributor");
                                startActivity(intent);

                         /*   Intent intent = new Intent(this, FundRequestActivity.class);
                            startActivity(intent);*/
                            } else if (itemString.equalsIgnoreCase("Create User")) {
                                Intent intent = new Intent(this, RegistrationActivity.class);
                                intent.putExtra(AppConstants.IS_SELF_REGISTRATION, false);
                                startActivity(intent);
                            }


                            if (appPreference.getRollId() == 4 || appPreference.getRollId() == 3)
                                bottomNavigationView.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
            }
        }
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void setupHomeFragmentSelected() {
        shouldExit = true;


        if (appPreference.getRollId() == 5) bottomNavigationView.setVisibility(View.VISIBLE);
        if (AppPreference.getInstance(this).getRollId() == 1) {
            toolbar.setTitle("Admin Dashboard");
            setFragment(AdminDashboardFragment.newInstance());
        } else if (AppPreference.getInstance(this).getRollId() == 4
                || AppPreference.getInstance(this).getRollId() == 3) {
            toolbar.setTitle("Agent Request View");
            setFragment(AgentRequestViewFragment2.Companion.newInstance());

        } else if (
                AppPreference.getInstance(this).getRollId() == 22
                        || AppPreference.getInstance(this).getRollId() == 23
                        || AppPreference.getInstance(this).getRollId() == 24
        ) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            toolbar.setTitle("Dashboard");
            setFragment(SaleHomeFragment.Companion.newInstance());

        } else {
            toolbar.setTitle("Dashboard");
            setFragment(HomeFragment.Companion.newInstance());
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, fragment).commit();
    }

    public void replaceFragment(Fragment fragment) {
        toolbar.setTitle("Bank Details");
        shouldExit = false;
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            recreate();
            return true;
        } else if (id == R.id.notification) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showPopUpNotification() {
        AppPreference appPreference = AppPreference.getInstance(this);
        appPreference.setPopupSee(true);
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        RelativeLayout linearLayout = dialog.findViewById(R.id.ll_dialogMainLayout);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView popup = dialog.findViewById(R.id.popup_img);
        //http://prod.excelonestopsolution.com/user-uploaded-files/my_note_img1234/
        try {
            Picasso.get().load("https://partners.a2zsuvidhaa.com/user-uploaded-files/my_note_img1234/" + appPreference.getPopup()).error(R.drawable.logo_small).into(popup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView btn_close = dialog.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(view -> dialog.dismiss());


        dialog.show();
    }

    private void showUpdateDialog(float currentVersion, float updateVersion) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        LinearLayout linearLayout = dialog.findViewById(R.id.ll_dialogMainLayout);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_currentVersion = dialog.findViewById(R.id.tv_currentVersion);
        TextView tv_updateVersion = dialog.findViewById(R.id.tv_updateVersion);
        tv_currentVersion.setText("Current Version : " + currentVersion);
        tv_updateVersion.setText("Update Version : " + updateVersion);


        Button btn_update = dialog.findViewById(R.id.btn_update);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(view -> dialog.dismiss());

        btn_update.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
            startActivity(intent);
            //dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (listener != null)
            listener.onFileImagePick(requestCode, resultCode, data);

    }

    public void setOnDataListener(OnFileImagePickerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAadhaarKycRequired() {
        toolbar.setTitle("Aadhaar Kyc");
        shouldExit = false;
        setFragment(AadhaarKycFragment.Companion.newInstance(null));
    }

    @Override
    public void onDocumentKycRequired() {
        toolbar.setTitle("Document Kyc");
        shouldExit = false;
        setFragment(FragmentDocumentKyc.Companion.newInstance("main_activity", null));
    }


    @Override
    public void onMPOSService() {
        toolbar.setTitle("Micro Atm Service");
        shouldExit = false;
        setFragment(MatmServiceTabFragment.Companion.newInstance());
    }

    @Override
    public void onMATMService() {
        toolbar.setTitle("Micro Atm Service");
        shouldExit = false;
        setFragment(MatmServiceTabFragment.Companion.newInstance());
    }

    @Override
    public void onSaleItemClick(@NonNull String searchFor) {
        toolbar.setTitle(searchFor);
        shouldExit = false;
        setFragment(MemberFragment.newInstance("", searchFor));
        bottomNavigationView.setVisibility(View.GONE);
    }


    public interface OnFileImagePickerListener {
        public void onFileImagePick(int requestCode, int resultCode, Intent data);
    }


    private void setupBottomNavMenu() {


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.menu_home) {
                setupHomeFragmentSelected();
            } else if (item.getItemId() == R.id.menu_ledger_report) {
                shouldExit = false;
                toolbar.setTitle("Ledger Report");
                setFragment(LedgerReportFragment.Companion.newInstance());
            } /*else if (item.getItemId() == R.id.menu_aeps_report) {
                shouldExit = false;
                toolbar.setTitle("Aeps Report");
                setFragment(ReportAepsFragment.Companion.newInstance());
            }*/

            return true;
        });

        if (appPreference.getRollId() == 4 || appPreference.getRollId() == 3)
            bottomNavigationView.setVisibility(View.GONE);
    }

}
