/*
package com.a2zsuvidhaa.in.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.KYCApproval;
import com.a2zsuvidhaa.in.SharedPref;
import com.a2zsuvidhaa.in.activity.AddMoneyActivity;
import com.a2zsuvidhaa.in.activity.AdhaarPay.AdhaarActivity;
import com.a2zsuvidhaa.in.activity.CreateRetailerActivity;
import com.a2zsuvidhaa.in.activity.MainActivity;
import com.a2zsuvidhaa.in.activity.aeps.MiniStatementActivity;
import com.a2zsuvidhaa.in.activity.dmt1.DMT1Activity;
import com.a2zsuvidhaa.in.activity.a2z_wallet.A2ZWalletActivity;
import com.a2zsuvidhaa.in.activity.dmt2.DMT2Activity;
import com.a2zsuvidhaa.in.activity.login.LoginActivity;
import com.a2zsuvidhaa.in.activity.recharge.ProviderActivity;
import com.a2zsuvidhaa.in.activity.aeps.AepsActivity;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AppUitls;
import com.a2zsuvidhaa.in.util.Enums.ProviderType;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.RefreshPage;
import com.a2zsuvidhaa.in.util.SessionManager;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class A2ZSuvidhaHomeFragment extends Fragment implements View.OnClickListener {

    public static final String PROVIDER_TYPE = "provider_type";
    private static final String TAG = "HomeFragment";

    LinearLayout tv_kyc;

    SessionManager sessionManager;
    public A2ZSuvidhaHomeFragment() {

    }

    private TextView tv_bankDown;
    private TextView tv_news;

    public static A2ZSuvidhaHomeFragment newInstance() {
        A2ZSuvidhaHomeFragment fragment = new A2ZSuvidhaHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_a2zsuvidha_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);

    }

    private void initView(View view) {

        sessionManager=new SessionManager(getActivity());
        CircleImageView cv_broadband = view.findViewById(R.id.cv_broadband);
        CircleImageView cv_dth = view.findViewById(R.id.cv_dth);
        CircleImageView cv_electricity = view.findViewById(R.id.cv_electricity);
        CircleImageView cv_electricity2 = view.findViewById(R.id.cv_electricity2);
        CircleImageView cv_gas = view.findViewById(R.id.cv_gas);
        CircleImageView cv_insurance = view.findViewById(R.id.cv_insurance);
        CircleImageView cv_data_card = view.findViewById(R.id.cv_data_card);
        CircleImageView cv_mobile = view.findViewById(R.id.cv_mobile);
        CircleImageView cv_water = view.findViewById(R.id.cv_water);
        CircleImageView cv_dmt1 = view.findViewById(R.id.cv_dmt1);
        CircleImageView cv_a2z_wallet = view.findViewById(R.id.cv_a2z_wallet);
        CircleImageView cv_aeps = view.findViewById(R.id.cv_aeps);
        CircleImageView cv_adhaar = view.findViewById(R.id.cv_adhaar);
        CircleImageView cv_postpaid = view.findViewById(R.id.ic_postpaid);
        CircleImageView cv_landline = view.findViewById(R.id.cv_landline);
        CircleImageView cv_dmt2 = view.findViewById(R.id.cv_dmt2);
        tv_kyc = view.findViewById(R.id.tv_kyc);

        ImageView cv_fasttag = view.findViewById(R.id.cv_fasttag);
        ImageView cv_loan = view.findViewById(R.id.cv_loan);
        Button add=view.findViewById(R.id.btn_addmoney);
        if (sharedPref.getKYC()==1){
            tv_kyc.setVisibility(View.GONE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                */
/*String upi_str = "upi://pay?pa=excelone@icici&pn=Excel Stop&tr=132"+"&am=1.00"+"&cu=INR&mc=5411&tn=By Rahul";
                Intent intent = new Intent();
                intent.setData(Uri.parse(upi_str));
                Intent chooser = Intent.createChooser(intent, "Pay with...");
                startActivityForResult(chooser, 1, null);*//*

                MainActivity.spos=0;
                ((MainActivity)getActivity()).replaceFragment(BankDetailFragment.newInstance());
            }
        });

        tv_bankDown = view.findViewById(R.id.tv_bankDown);
        tv_news = view.findViewById(R.id.tv_news);

        cv_broadband.setOnClickListener(this);
        cv_dth.setOnClickListener(this);
        cv_electricity.setOnClickListener(this);
        cv_electricity2.setOnClickListener(this);
        cv_gas.setOnClickListener(this);
        cv_insurance.setOnClickListener(this);
        cv_data_card.setOnClickListener(this);
        cv_mobile.setOnClickListener(this);
        cv_water.setOnClickListener(this);
        cv_dmt1.setOnClickListener(this);
        cv_a2z_wallet.setOnClickListener(this);
        cv_postpaid.setOnClickListener(this);
        cv_landline.setOnClickListener(this);
        cv_aeps.setOnClickListener(this);
        cv_adhaar.setOnClickListener(this);
        cv_dmt2.setOnClickListener(this);
        cv_fasttag.setOnClickListener(this);
        cv_loan.setOnClickListener(this);
        tv_kyc.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPref.getKYC()==1){
            tv_kyc.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        RefreshPage.getData(getActivity());
        RefreshPage.setBankDownNewsBalanceListener((isRefresh, balance, retailerNews, distributorNews, bankDownList) -> {
            if (isRefresh) {

                tv_news.setSelected(true);
                tv_news.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tv_news.setSingleLine(true);

                tv_bankDown.setSelected(true);
                tv_bankDown.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tv_bankDown.setSingleLine(true);

                tv_news.setText("Welcome To A2Z family , सुविधा चाहिए तो A2Z के यहाँ आइये");
                if (!retailerNews.equalsIgnoreCase("null"))
                    tv_news.setText(retailerNews);
                tv_bankDown.setText(bankDownList + "  || Helpline Number 09251133333");

                listener.onBalanceUpdate(balance);
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("fragment response pg","="+requestCode);
        if (requestCode == 1)
        {
            System.out.println("---------INSIDE-------");
            if (data != null)
            {
                String message = data.getStringExtra("status");
                String[] resKey = data.getStringArrayExtra("responseKeyArray");
                String[] resValue = data.getStringArrayExtra("responseValueArray");
                if(resKey!=null && resValue!=null)
                {
                    for(int i=0; i<resKey.length; i++)
                        System.out.println("  "+i+" resKey : "+resKey[i]+" resValue : "+resValue[i]);
                }

                //  Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                System.out.println("RECEIVED BACK--->" + message);
            }

        }
    }

    private void showKycDialog()
    {
        Dialog dialog = AppDialogs.a2zdialogMessage(getActivity(),"Please Update Your KYC.");
        TextView ok = dialog.findViewById(R.id.btn_ok);
        TextView cancel = dialog.findViewById(R.id.btn_cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateRetailerActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showKycDialog2()
    {
        Dialog dialog = AppDialogs.a2zdialogMessage(getActivity(),"Please Update Your KYC.");
        TextView ok = dialog.findViewById(R.id.btn_ok);
        TextView cancel = dialog.findViewById(R.id.btn_cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), KYCApproval.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    SharedPref sharedPref = SharedPref.getInstance(getActivity());
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cv_broadband: {

                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.BROADBAND);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_dth: {
                sessionManager.addString("bbps","1");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.DTH);
                requireActivity().startActivity(intent);
                break;
            }
            case R.id.cv_loan:
            {
                sessionManager.addString("bbps","2");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.LOAN_REPAYMENT);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_electricity: {
                sessionManager.addString("bbps","2");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.ELECTRICITY);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_gas:
                {
                sessionManager.addString("bbps","2");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.GAS);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
                case R.id.cv_insurance: {
                sessionManager.addString("bbps","2");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.INSURANCE);
                Objects.requireNonNull(getActivity()).startActivity(intent);

                break;
            }
            case R.id.cv_landline: {
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.LANDLINE);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.ic_postpaid: {
                sessionManager.addString("bbps","2");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.POSTPAID);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            */
/* Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.INSURANCE);
                Objects.requireNonNull(getActivity()).startActivity(intent);*//*

            case R.id.tv_kyc: {

                Intent intent = new Intent(getActivity(), KYCApproval.class);
                */
/*intent.putExtra(PROVIDER_TYPE, ProviderType.DATA_CARD);*//*

                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_mobile: {
                sessionManager.addString("bbps","1");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.MOBILE_PREPAID);
                Objects.requireNonNull(getActivity()).startActivity(intent);

                break;
            }
            case R.id.cv_fasttag: {
                sessionManager.addString("bbps","2");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.FASTTAG);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_water: {
                sessionManager.addString("bbps","2");
                Intent intent = new Intent(getActivity(), ProviderActivity.class);
                intent.putExtra(PROVIDER_TYPE, ProviderType.WATER);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_dmt1: {
                Intent intent = new Intent(getActivity(), DMT1Activity.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_a2z_wallet: {
                if(sharedPref.getKYC_STATUS()==0)
                {
                    showKycDialog();
                    Toast.makeText(getActivity(), "Your Kyc is Pending.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), A2ZWalletActivity.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            case R.id.cv_aeps: {
                if(sharedPref.getKYC()==0)
                { showKycDialog2();
                    Toast.makeText(getActivity(), "Your Kyc is Pending.", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Intent intent = new Intent(getActivity(), AepsActivity.class);
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                }

                break;
            }
            case R.id.cv_adhaar: {
                if(sharedPref.getKYC()==0)
                {
                    Toast.makeText(getActivity(), "Your Kyc is Pending.", Toast.LENGTH_SHORT).show();
                    showKycDialog2();
                    return;
                }else {
                    Intent intent = new Intent(getActivity(), AdhaarActivity.class);
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                }

                break;
            }
            case R.id.cv_dmt2: {
                if(sharedPref.getKYC_STATUS()==0)
                {
                    showKycDialog();
                    Toast.makeText(getActivity(), "Your Kyc is Pending.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), DMT2Activity.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }

    private static UpdateBalanceListener listener;
    public static void setBalanceListener(UpdateBalanceListener listener){
        A2ZSuvidhaHomeFragment.listener = listener;
    }
    public interface UpdateBalanceListener{
        void onBalanceUpdate(String balance);
    }
}
*/
