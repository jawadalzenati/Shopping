package com.josalla.store.Fragment;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.AddNewCategryActivity;
import com.josalla.store.AddProductActivity;
import com.josalla.store.AddressActivity;
import com.josalla.store.AllUsersActivity;
import com.josalla.store.BalanceActivity;
import com.josalla.store.EditProductActivity;
import com.josalla.store.EranActivity;
import com.josalla.store.FavoriteActivity;
import com.josalla.store.OrdersActivity;
import com.josalla.store.PolicyActivity;
import com.josalla.store.R;
import com.josalla.store.SendNotificationsActivity;
import com.josalla.store.SingInActivity;
import com.josalla.store.SubCategoryActivity;
import com.josalla.store.WithdrawListActivity;
import com.josalla.store.dialogs.AddressDialog;
import com.josalla.store.dialogs.ChangeNumberDialog;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    public TextView tvAddNewCategory, tvAddNewProduct, tvAddEditAddress,
            tvSingOut, tvAllOrder, tvEditDelete, tvMyList, tvPolicy, tvShareApp,
            tvRateApp, tvCallUs, tvAddNumber, tvSendNotification, tvUsers, tvAddSubCategory, tvMyBalance, tvًWithdraw, tvEarn;

    FirebaseAuth auth;
    ImageView ivWhatsApp, ivFacebook, ivCall;
    FirebaseFirestore db;

    AddressDialog addressDialog;



    LinearLayout admin_tools, assist_tools, sub_admin_tools;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View accoutn = inflater.inflate(R.layout.fragment_account, container, false);

        tvAddNewCategory = accoutn.findViewById(R.id.tvAddNewCategory);
        tvAddNewProduct = accoutn.findViewById(R.id.tvAddNewProduct);
        tvAddEditAddress = accoutn.findViewById(R.id.tvAddEditAddress);
        tvSingOut = accoutn.findViewById(R.id.tvSingOut);
        tvAllOrder = accoutn.findViewById(R.id.tvAllOrder);
        tvMyList = accoutn.findViewById(R.id.tvMyList);
        tvPolicy = accoutn.findViewById(R.id.tvPolicy);
        tvShareApp = accoutn.findViewById(R.id.tvShareApp);
        tvRateApp = accoutn.findViewById(R.id.tvRateApp);
        tvCallUs = accoutn.findViewById(R.id.tvCallUs);
        tvAddNumber = accoutn.findViewById(R.id.tvAddNumber);
        ivWhatsApp = accoutn.findViewById(R.id.ivWhatsApp);
        ivFacebook = accoutn.findViewById(R.id.ivFacebook);
        ivCall = accoutn.findViewById(R.id.ivCall);
        tvUsers = accoutn.findViewById(R.id.tvUsers);
        tvMyBalance = accoutn.findViewById(R.id.tvMyBalance);
        tvًWithdraw = accoutn.findViewById(R.id.tvًWithdraw);
        tvEarn = accoutn.findViewById(R.id.tvEarn);

        tvAddSubCategory = accoutn.findViewById(R.id.tvAddSubCategory);


        tvEditDelete = accoutn.findViewById(R.id.tvEditDelete);
        admin_tools = accoutn.findViewById(R.id.admin_tools);
        tvSendNotification = accoutn.findViewById(R.id.tvSendNotification);

        assist_tools = accoutn.findViewById(R.id.assist_tools);
        sub_admin_tools = accoutn.findViewById(R.id.sub_admin_tools);


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        //add new Category
        tvAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNewCategryActivity.class));

            }
        });

        //conect us ------------------------------
        ivWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("contacts").document("contacts")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        Uri uri = Uri.parse("smsto:" + doc.get("whatsapp"));
                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                        i.setPackage("com.whatsapp");
                        startActivity(Intent.createChooser(i, "استفسار"));

                    }
                });
            }
        });

        //--
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("contacts").document("contacts")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + doc.get("mobile")));
                        startActivity(intent);

                    }
                });
            }
        });


        //---

        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                db.collection("contacts").document("contacts")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {

                        String FACEBOOK_URL = "https://www.facebook.com/" + doc.get("facebook");
                        String FACEBOOK_PAGE_ID = "" + doc.get("facebook");
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);

                        PackageManager packageManager = getActivity().getPackageManager();
                        try {
                            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                            if (versionCode >= 3002850) { //newer versions of fb app
                                String facebookUrl = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                                facebookIntent.setData(Uri.parse(facebookUrl));
                                startActivity(facebookIntent);
                            } else { //older versions of fb app
                                String facebookUrl = "fb://page/" + FACEBOOK_PAGE_ID;
                                facebookIntent.setData(Uri.parse(facebookUrl));
                                startActivity(facebookIntent);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            facebookIntent.setData(Uri.parse(FACEBOOK_URL));
                            startActivity(facebookIntent);
                        }


                    }
                });
            }
        });


        //finish add --------------------
        tvRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));

                }

            }
        });

        //-------------------- if account is admin -----------------------------

        db.collection("users")
                .document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if ((doc.get("user_type") + "").equals("admin")) {
                            admin_tools.setVisibility(View.VISIBLE);
                            assist_tools.setVisibility(View.VISIBLE);
                            sub_admin_tools.setVisibility(View.VISIBLE);

                        } else if ((doc.get("user_type") + "").equals("assist")) {
                            admin_tools.setVisibility(View.GONE);
                            assist_tools.setVisibility(View.VISIBLE);
                            sub_admin_tools.setVisibility(View.GONE);

                        } else if ((doc.get("user_type") + "").equals("sub_admin")) {
                            admin_tools.setVisibility(View.GONE);
                            assist_tools.setVisibility(View.VISIBLE);
                            sub_admin_tools.setVisibility(View.VISIBLE);

                        } else {
                            admin_tools.setVisibility(View.GONE);
                            assist_tools.setVisibility(View.GONE);
                            sub_admin_tools.setVisibility(View.GONE);
                        }
                    }
                });


        tvShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareText = "مرحبا .. أنا استخدم متجر شوبنج جو ووجدت كثير من المنتجات التي اعجبتني ";
                String shareTextPart2 = "يمكنك ايضا تحميل التطبيق والاستمتاع به من هنا..";
                String googlePlayLink = "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "FOODYBITE");
                intent.putExtra(Intent.EXTRA_TEXT, shareText + shareTextPart2 + googlePlayLink);
                startActivity(Intent.createChooser(intent, "أختر شيئ"));

            }
        });

        tvAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeNumberDialog changeNumberDialog = new ChangeNumberDialog(getActivity());
                changeNumberDialog.setCancelable(false);
                changeNumberDialog.show();

            }
        });
        // conecte us  with whatsapp
        tvCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                db.collection("contacts").document("contacts")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        Uri uri = Uri.parse("smsto:" + doc.get("whatsapp"));
                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                        i.setPackage("com.whatsapp");
                        startActivity(Intent.createChooser(i, "استفسار"));

                    }
                });


            }
        });

        //add new product
        tvAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));

            }
        });


        // add or edit address

        tvAddEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (auth.getCurrentUser() != null) {


                    db.collection("users").document(auth.getCurrentUser().getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {

                            if ((doc.get("has_address") + "").equals("true")) {

                                addressDialog = new AddressDialog(getActivity(), auth.getCurrentUser().getUid());
                                addressDialog.show();
                            } else {

                                startActivity(new Intent(getActivity(), AddressActivity.class));
                            }
                        }
                    });


                } else {

                    Intent intent = new Intent(getActivity(), SingInActivity.class);
                    intent.putExtra("from", "address");
                    startActivity(intent);
                }

            }
        });

        //--------- edit delete product  -------------------------------------
        tvEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), EditProductActivity.class));


            }
        });

        //---------------------------add new offers -----------------------

        tvًWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WithdrawListActivity.class));
            }
        });

        //-------------sign out from app ---------------------------------------------
        tvSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("تسجيل الخروج")
                        .setMessage("هل تريد تسجيل الخروج")
                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();

                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {


                                auth.signOut();
                                LoginManager.getInstance().logOut();
                                getActivity().finishAffinity();


                            }
                        }).show();


            }
        });

        //---------------------popup -------------------------------------------------

        //----------------------------send notification for all user -----------------
        tvSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SendNotificationsActivity.class));
            }
        });

        //-------------------------- tvPolicy ------------------------
        tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PolicyActivity.class));

            }
        });

        //tvEarn from app
        tvEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser().isAnonymous()) {
                    Toasty.info(getActivity(), "يرجى تسجيل الدخول للمتابعة", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity() , SingInActivity.class));

                } else {
                    startActivity(new Intent(getActivity(), EranActivity.class));
                }
            }
        });


        //-------------------------------------------
        tvMyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FavoriteActivity.class));
            }
        });

        //--------------- show all orders  ---------------------

        tvAllOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OrdersActivity.class));
            }
        });


        // get all users

        tvUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllUsersActivity.class));

            }
        });


        //add sub categoty
        tvAddSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SubCategoryActivity.class));
            }
        });

        // account Balance :رصييد الحساب
        tvMyBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BalanceActivity.class));

            }
        });
        //====================================================

        return accoutn;
    }


    @Override
    public void onResume() {
        super.onResume();


    }
}
