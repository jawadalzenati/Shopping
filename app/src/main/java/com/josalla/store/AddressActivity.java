package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.josalla.store.utils.ToolUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class AddressActivity extends AppCompatActivity {

    Button btnAddAddressManual, btnAddAddress, btnAdd;
    EditText txtFirstName, txtLastName, txtPhoneNumber, txtOtherPhoneNumber, txtZipCode, txtState, txtZipCodeLocation, txtId, txtPersonId;
    ImageView ivBack;
    static String latitudel = "", longitude = "", location_address;
    int REQUEST_CODE_CHECK_SETTINGS = 100;
    LocationRequest mLocationRequest;

    ConstraintLayout showLocation;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    LinearLayout clAddManual;
    ConstraintLayout whyId;

    TextView txtCountry, txtRegion, txtStreet, txtDetails, tvCancel, tvWhyId, btnOk, tvWhyId2;

    static boolean location_detect = false, IsJordan = false;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    static boolean address_active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress);


        btnAddAddressManual = findViewById(R.id.btnAddAddressManual);
        ivBack = findViewById(R.id.ivBack);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        btnAdd = findViewById(R.id.btnAdd);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtOtherPhoneNumber = findViewById(R.id.txtOtherPhoneNumber);
        showLocation = findViewById(R.id.showLocation);
        clAddManual = findViewById(R.id.clAddManual);
        txtZipCode = findViewById(R.id.txtZipCode);

        txtCountry = findViewById(R.id.txtCountry);
        txtRegion = findViewById(R.id.txtRegion);
        txtStreet = findViewById(R.id.txtStreet);
        txtDetails = findViewById(R.id.txtDetails);
        txtState = findViewById(R.id.txtState);
        tvCancel = findViewById(R.id.tvCancel);

        tvWhyId = findViewById(R.id.tvWhyId);

        txtId = findViewById(R.id.txtId);
        whyId = findViewById(R.id.whyId);
        btnOk = findViewById(R.id.btnOk);

        tvWhyId2 = findViewById(R.id.tvWhyId2);


        txtZipCodeLocation = findViewById(R.id.txtZipCodeLocation);
        txtPersonId = findViewById(R.id.txtPersonId);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        btnAddAddressManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clAddManual.setVisibility(View.VISIBLE);
                address_active = true;
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clAddManual.setVisibility(View.GONE);
                address_active = false;
            }
        });

        //-------------------------------------------------------------------------
        tvWhyId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whyId.setVisibility(View.VISIBLE);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whyId.setVisibility(View.GONE);
                tvWhyId2.setVisibility(View.GONE);
            }
        });

        tvWhyId2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whyId.setVisibility(View.VISIBLE);

            }
        });

        //--------------------------------- get user address -----------------------------------------

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = txtFirstName.getText().toString() + " " + txtLastName.getText().toString() + "";
                String phone = txtPhoneNumber.getText().toString() + "<>" + txtOtherPhoneNumber.getText().toString() + "";
                String location = latitudel + "<>" + longitude + "";
                String phone1 = txtPhoneNumber.getText().toString();
                String phone2 = txtOtherPhoneNumber.getText().toString();
                String address = "الدولة: " + txtCountry.getText() +
                        " /المنطقة: " + txtRegion.getText() +
                        " /الشارع: " + txtStreet.getText() +
                        " /المحافظة: " + txtState.getText() +
                        " /تفاصيل اضافية: " + txtDetails.getText() +
                        " /الرمز البريدي: " + txtZipCode.getText() +
                        " /الرقم القومي: " + txtPersonId.getText();


                String person_id = txtId.getText() + "";
                String zipCode_locations = txtZipCodeLocation.getText() + "";
                String personid_manual = txtPersonId.getText() + "";
                String country = txtCountry.getText() + "";


                if (address_active) {
                    boolean jordan = (country.contains("الأردن") || country.contains("الاردن") || country.contains("اردن") || country.contains("أردن") || country.contains("Jordan"));

                    if ((jordan) && personid_manual.length() != 10) {
                        if (personid_manual.length() != 10) {
                            txtPersonId.setError("رقم خاطئ!");
                        } else {
                            txtPersonId.setError("مطلوب!");
                        }
                        Toasty.error(AddressActivity.this, "الرجاء التحقق من جميع الحقول", Toast.LENGTH_SHORT).show();

                    } else if (txtRegion.getText().length() == 0 || txtStreet.getText().length() == 0 || txtState.getText().length() == 0 || txtZipCode.getText().length() == 0 || txtCountry.getText().length() == 0) {
                        Toasty.error(AddressActivity.this, "جميع الحقول مطلوبة", Toast.LENGTH_SHORT).show();
                        if (txtZipCode.getText().length() == 0) {
                            txtZipCode.setError("مطلوب!");
                        }

                    } else if (name.length() < 2 || !isValidPhone(phone1) || (phone2.length() > 0 && !isValidPhone(phone2))) {
                        if (name.length() < 2) {
                            txtFirstName.setError("مطلوب");
                            txtLastName.setError("مطلوب");
                        }
                        if (phone.length() < 2) {
                            txtPhoneNumber.setError("مطلوب");
                        }
                        if (!isValidPhone(phone1)) {
                            txtPhoneNumber.setError("رقم غير صالح");
                        }
                        if (phone2.length() > 0 && !isValidPhone(phone2)) {
                            txtOtherPhoneNumber.setError("رقم غير صالح");
                        }

                    } else {

                        ToolUtils.showDialog(AddressActivity.this, "جاري تحديث البيانات");

                        if (jordan) {
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .update("user_address", address);
                        } else {
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .update("user_address", address.replace(" /الرقم القومي: ", ""));
                        }


                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("user_location", location);

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("user_name", name);

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("user_phone", phone);

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("has_address", true);

                        ToolUtils.hideDialog();


                        Toasty.success(AddressActivity.this, "تم اضافة العنوان", Toast.LENGTH_SHORT).show();
                        finish();

                    }

                } else {

                    if ((location.length() < 3 && !address_active) || (IsJordan && person_id.length() == 0) || (zipCode_locations.length() == 0)) {
                        if (IsJordan && person_id.length() == 0) {
                            txtId.setError("مطلوب");
                        } else if (zipCode_locations.length() == 0) {
                            txtZipCodeLocation.setError("مطلوب!");
                        } else {
                            Toasty.error(AddressActivity.this, "يجب على الاقل ادخال عنوان واحد", Toast.LENGTH_SHORT).show();
                        }
                    } else if (name.length() < 2 || !isValidPhone(phone1) || (phone2.length() > 0 && !isValidPhone(phone2))) {
                        if (name.length() < 2) {
                            txtFirstName.setError("مطلوب");
                            txtLastName.setError("مطلوب");
                        }
                        if (phone.length() < 2) {
                            txtPhoneNumber.setError("مطلوب");
                        }
                        if (!isValidPhone(phone1)) {
                            txtPhoneNumber.setError("رقم غير صالح");
                        }
                        if (phone2.length() > 0 && !isValidPhone(phone2)) {
                            txtOtherPhoneNumber.setError("رقم غير صالح");
                        }

                    } else {
                        if (IsJordan) {
                            address = location_address + " /الرمز البريدي: " + txtZipCodeLocation.getText() + " / الرقم القومي: " + txtId.getText();
                        } else {
                            address = location_address + " /الرمز البريدي: " + txtZipCodeLocation.getText();

                        }

                        ToolUtils.showDialog(AddressActivity.this, "جاري تحديث البيانات");
                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("user_address", address);

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("user_location", location);

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("user_name", name);

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("user_phone", phone);

                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("has_address", true);

                        ToolUtils.hideDialog();


                        Toasty.success(AddressActivity.this, "تم اضافة العنوان", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                }


            }
        });

        //---------------------------------------------- show current location ------------------------------
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = latitudel + "<>" + longitude + "";
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });


        //to get current location
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(AddressActivity.this)
                        .withPermissions(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted


                                if (checkGPSSetting()) {
                                    startLocationUpdates();
                                } else {
                                    enableLocationSettings();
                                }
                                // do you work now


                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    // permission is denied permenantly, navigate user to app settings
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();

            }
        });


        //------------- finish ------------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }


    protected void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(0)
                .setFastestInterval(0)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    // startUpdatingLocation(...);
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(AddressActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_CHECK_SETTINGS == requestCode) {
            if (Activity.RESULT_OK == resultCode) {

                startLocationUpdates();

            } else {
                Toasty.error(this, "الرجاء تفعيل اعدادات الموقع", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkGPSSetting() {
        boolean state;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            state = false;
        } else {
            state = true;
        }

        return state;
    }

    private void startLocationUpdates() {
        location_detect = false;
        // Create the location request to start receiving updates
        ToolUtils.showDialog(AddressActivity.this, "جاري تحديد الموقع");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(AddressActivity.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here

                        Location location = locationResult.getLastLocation();
                        String msg = location.getLatitude() + "," + location.getLongitude();
                        if (msg.length() > 1 && !location_detect) {
                            location_detect = true;
                            // to get address form locations
                            try {
                                GetAddressFromLocation(location.getLatitude(), location.getLongitude());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ToolUtils.hideDialog();


                        }
                        Log.e("MAS", location.getLatitude() + " : " + location.getLongitude());

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }

                },
                Looper.myLooper());

    }

    public boolean isValidPhone(String phone) {
        boolean check = false;
        if (Pattern.matches("^0[0-9]{9}$", phone) ||
                Pattern.matches("^00[0-9]{12}$", phone) || Pattern.matches("^\\+[0-9]{12}$", phone)) {
            check = true;
        }

        return check;
    }


    public void GetAddressFromLocation(double lat, double log) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        Log.e("MAS_Locations", "start");
        addresses = geocoder.getFromLocation(lat, log, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if (addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(0) + ""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();

            latitudel = lat + "";
            longitude = log + "";
            location_address = "العنوان: " + address;

            Toasty.success(AddressActivity.this, "تم تحديد الموقع", Toast.LENGTH_SHORT).show();
            showLocation.setVisibility(View.VISIBLE);
            txtZipCodeLocation.setVisibility(View.VISIBLE);

            if (country.contains("Jordan") || country.contains("الأردن")) {
                IsJordan = true;
                txtId.setVisibility(View.VISIBLE);
                tvWhyId2.setVisibility(View.VISIBLE);
                Log.e("MAS_location", address + "");
            }


        } else {
            Toasty.error(this, "عنوان غير معرف الرجاء اضافة العنوان بشكل يدوي", Toast.LENGTH_SHORT).show();
            clAddManual.setVisibility(View.VISIBLE);
            address_active = true;
            ToolUtils.hideDialog();

        }

    }


}
