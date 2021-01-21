package com.automatodev.loa.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AdressCallback;
import com.automatodev.loa.controller.callback.AnnouncementCallback;
import com.automatodev.loa.controller.callback.OfferCallback;
import com.automatodev.loa.controller.callback.UserCallback;
import com.automatodev.loa.controller.service.CityService;
import com.automatodev.loa.controller.service.ItemService;
import com.automatodev.loa.controller.service.OfferService;
import com.automatodev.loa.controller.service.UserService;
import com.automatodev.loa.databinding.ActivityMainBinding;
import com.automatodev.loa.model.entity.CategoryEntity;
import com.automatodev.loa.model.entity.CityEntity;
import com.automatodev.loa.model.entity.CountryEntity;
import com.automatodev.loa.model.entity.ImageEntity;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.model.entity.OfferEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.model.firebase.Authentication;
import com.automatodev.loa.view.adapter.AdapterOffer;
import com.automatodev.loa.view.adapter.ItemAdapter;
import com.automatodev.loa.view.adapter.SpinnerCustomAdapter;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.extras.PreferencesApplication;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.melnykov.fab.ScrollDirectionListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
//teste
@SuppressLint("RestrictedApi")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static ActivityMainBinding binding;
    public static boolean statusView;
    private List<ItemEntity> itemEntities;
    private ItemAdapter itemAdapter;
    private UserEntity userEntity;
    private PreferencesApplication pApplication;
    private ComponentListener componentListener;
    private Authentication auth;
    private EditText edtSubject_layoutSend;
    private EditText edtMessage_layoutSend;
    private BottomSheetDialog bottomSheetDialog;
    private ItemEntity itemFilter;
    private boolean shimmer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        super.onCreate(savedInstanceState);
        //Area para instanciação  / configuração de elementos e objetos
        componentListener = new ComponentListener(this);
        pApplication = new PreferencesApplication(this);
        itemEntities = new ArrayList<>();
        itemFilter = new ItemEntity();
        auth = new Authentication();
        binding.btnGoMain.setOnClickListener(this);
        binding.btnProfileMain.setOnClickListener(this);
        binding.btnLancesMain.setOnClickListener(this);
        binding.btnMyAnnouncementMain.setOnClickListener(this);
        binding.btnFavouritesMain.setOnClickListener(this);
        binding.btnProblemMain.setOnClickListener(this);
        binding.btnFeedbackMain.setOnClickListener(this);
        binding.btnAboutMain.setOnClickListener(this);
        binding.btnLogoutMain.setOnClickListener(this);
        binding.btnFilterMain.setOnClickListener(this);
        binding.btnClearMain.setOnClickListener(this);
        binding.fabNewMain.setOnClickListener(this);
        binding.swipeRefreshMain.setColorSchemeResources(R.color.colorPrimaryDark2);

        binding.fabNewMain.attachToRecyclerView(binding.recyclerItemsMain, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                binding.fabNewMain.show();
            }

            @Override
            public void onScrollUp() {
                binding.fabNewMain.hide();
            }
        });

        itemAdapter = new ItemAdapter(this, null, 0, binding.relativeResourcesMain);
        binding.recyclerItemsMain.setAdapter(itemAdapter);

        binding.swipeRefreshMain.setOnRefreshListener(this::getAnnounces);

        binding.edtSearchMain.setOnEditorActionListener((v2, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchItems();

                View viewKey = getCurrentFocus();
                if (viewKey != null) {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(viewKey.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        verifyUser();
        //  getAnnounces();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGo_main:
                if (!AccountActivity.status)
                    startActivity(new Intent(this, AccountActivity.class));
                break;
            case R.id.btnProfile_main:
                if (userEntity != null && !ProfileActivity.status) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btnFilter_main:
                openFilter();
                break;
            case R.id.btnLances_main:
                viewOffers();
                break;
            case R.id.btnMyAnnouncement_main:
                if (!OwnerActivity.status)
                    startActivity(new Intent(this, OwnerActivity.class));
                break;
            case R.id.btnFavourites_main:
                if (!FavouriteActivity.status)
                    startActivity(new Intent(this, FavouriteActivity.class));
                break;
            case R.id.btnProblem_main:
                showBottomSendEmail();
                break;
            case R.id.btnFeedback_main:
                feedback();
                break;
            case R.id.btnAbout_main:
                if (!AboutActivity.status)
                    startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.btnLogout_main:
                logoffUser();
                break;
            case R.id.btnClear_main:
                if (!binding.edtSearchMain.getText().toString().trim().isEmpty()) {
                    binding.edtSearchMain.setText("");
                    getAnnounces();
                }
                break;
            case R.id.fabNew_main:
                actMainNew();
                break;

        }
    }

    public boolean verifyConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnectedOrConnecting();
    }

    public void actMainNew() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userEntity = pApplication.getUserPrefs();
            if (verifyConnection() && userEntity != null && !NewActivity.status) {
                Intent i = new Intent(this, NewActivity.class);
                i.putExtra("user", userEntity);
                startActivity(i);
            } else
                Snackbar.make(binding.relativeParentMain,
                        "Ops! Ha algo errado na sua solcitação\nVerifique sua conexão e reinicie o aplicativo", Snackbar.LENGTH_LONG).show();
        } else {
            if (!AboutActivity.status)
                startActivity(new Intent(this, AboutActivity.class));

        }

    }

    private void searchItems() {
        if (!binding.edtSearchMain.getText().toString().isEmpty()) {
            Map<String, String> mapInit = getInitialPrefs();
            ItemEntity itemFilter = new ItemEntity();
            itemFilter.setTitle(binding.edtSearchMain.getText().toString());
            itemFilter.setUf(mapInit.get("uf"));
            itemFilter.setCity(mapInit.get("city"));
            getFilterResultSet(itemFilter, 's');

        }
    }

    public void logoffUser() {
        if (auth.getUser() != null) {
            final AlertDialog alerta = new AlertDialog.Builder(this).create();
            View v = getLayoutInflater().inflate(R.layout.layout_dialog, null);
            Button btnOk_dialog = v.findViewById(R.id.btnOk_dialog);
            Button btnNo_dialog = v.findViewById(R.id.btnNo_dialog);
            btnOk_dialog.setText(R.string.txtBtnYesDialog);
            btnNo_dialog.setText(R.string.txtBtnCancelDialog);
            TextView txtTitle_dialog = v.findViewById(R.id.txtTitle_dialog);
            TextView txtDialog_dialog = v.findViewById(R.id.txtDialog_dialog);
            txtTitle_dialog.setText(R.string.titleDialogLogoff);
            txtDialog_dialog.setText(R.string.textDialogLogoff);
            btnOk_dialog.setOnClickListener(viewOnClick -> {
                auth.userSingOut();
                verifyUser();
                alerta.dismiss();
            });
            btnNo_dialog.setOnClickListener(viewBtnNo -> {
                alerta.dismiss();
            });
            alerta.setView(v);
            alerta.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alerta.show();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statusView) {
            verifyUser();
            statusView = false;
        }
    }

    public Map<String, String> getInitialPrefs() {
        Map<String, String> ref = new HashMap<>();
        SharedPreferences initialData = this.getSharedPreferences("initData", Context.MODE_PRIVATE);
        ref.put("uf", initialData.getString("uf", ""));
        ref.put("city", initialData.getString("city", ""));
        return ref;
    }

    public void getAnnounces() {
        binding.swipeRefreshMain.setRefreshing(true);
        binding.relativeResourcesMain.setVisibility(View.GONE);
        componentListener.fadeComponents(binding.recyclerItemsMain, "alpha", 1f, 0f, 250, true);
        pApplication = new PreferencesApplication(this);
        try {
            UserEntity u = pApplication.getUserPrefs();
            Map<String, String> initData = getInitialPrefs();
            ItemService aService = new ItemService();
            aService.getAnnouncementAll(u != null ? u.getUf() : initData.get("uf"), u != null ? u.getCity() : initData.get("city"), new AnnouncementCallback() {
                @Override
                public void onResponse(Response<ItemEntity> response) {
                }

                @Override
                public void onFailure(Throwable t) {
                    if (itemAdapter != null) {
                        itemEntities.clear();
                        itemAdapter.notifyDataSetChanged();
                    }
                    binding.imgResourcesMain.setImageResource(R.drawable.ic_undraw_notify_re_65on);
                    binding.txtTitleResourcesMain.setText(getResources().getString(R.string.titleErrorConnection));
                    binding.txtMessageResourcesMain.setText(getResources().getString(R.string.messageErrorConnectionMain));
                    componentListener.fadeComponents(binding.relativeResourcesMain, "alpha", 0f, 1f, 250, false);
                    binding.relativeResourcesMain.setVisibility(View.VISIBLE);
                    Log.e("logx","log: "+t.getMessage());

                }


                @Override
                public void onResponseAll(Response<List<ItemEntity>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        binding.imgResourcesMain.setImageResource(R.drawable.ic_undraw_note_list_re_r4u9);
                        binding.txtTitleResourcesMain.setText(getResources().getString(R.string.titleNoContentMain));
                        binding.txtMessageResourcesMain.setText(getResources().getString(R.string.messageNoContentMain));
                        itemEntities.clear();
                        itemEntities.addAll(response.body());
                        showDataMain(itemEntities);
                        binding.recyclerItemsMain.setVisibility(View.VISIBLE);
                        binding.swipeRefreshMain.setRefreshing(false);

                    } else {
                        binding.relativeResourcesMain.setVisibility(View.VISIBLE);
                        try {
                            JSONObject objectError = new JSONObject(response.errorBody().string());
                            Snackbar.make(binding.relativeParentMain, "Houve um problema ao carregar os itens: \n" + objectError.getString("title"),
                                    Snackbar.LENGTH_LONG).show();
                        } catch (IOException | JSONException e) {
                            Snackbar.make(binding.relativeParentMain, "Error desconhecido: \n" + e.getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                }
            });

        } catch (Exception e) {
            Log.e("logx", "EGetAnnounces: " + e.getMessage());
            Toast.makeText(this, "Tivemos um problema ao obter os dados. " +
                    "\nPor favor reinicie o app e tente novamente", Toast.LENGTH_SHORT).show();
        }

    }


    //Metodo que ira verificar se o usuario esta logado, ira fazer um select e trazer os dados do usuario caso esteja logado
    private void verifyUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.fabNewMain.setVisibility(View.GONE);
            binding.fabNewMain.setImageResource(R.drawable.fab_add);
            binding.relativeNoLoginMain.setVisibility(View.GONE);
            binding.relativeResourcesMain.setVisibility(View.GONE);
            binding.shimmerMain.startShimmer();
            componentListener.fadeComponents(binding.shimmerMain, "alpha", 0f, 1f, 400, false);
            binding.shimmerMain.setVisibility(View.VISIBLE);
            loadUser(user.getUid());
        } else {

            componentListener.fadeComponents(binding.fabNewMain, "alpha", 0f, 1f, 400, false);
            if (binding.fabNewMain.getVisibility() == View.GONE)
                binding.fabNewMain.setVisibility(View.VISIBLE);
            binding.fabNewMain.setImageResource(R.drawable.ic_baseline_help_24);
            binding.relativeResourcesMain.setVisibility(View.GONE);
            binding.relativeNoLoginMain.setVisibility(View.VISIBLE);
            binding.horizontalScrollMain.setVisibility(View.GONE);
            binding.shimmerMain.setVisibility(View.GONE);
            userEntity = null;
            getAnnounces();
        }
    }

    public void loadUser(String uid) {
        binding.swipeRefreshMain.setRefreshing(true);
        binding.horizontalScrollMain.scrollTo(0, 100);
        userEntity = pApplication.getUserPrefs();
        if (userEntity != null && userEntity.getUid().equals(uid)) {
            binding.shimmerMain.stopShimmer();
            componentListener.fadeComponents(binding.shimmerMain, "alpha", 1f, 0f, 400, true);
            componentListener.fadeComponents(binding.horizontalScrollMain, "alpha", 0f, 1f, 400, false);
            binding.horizontalScrollMain.setVisibility(View.VISIBLE);
            componentListener.fadeComponents(binding.fabNewMain, "alpha", 0f, 1f, 400, false);
            binding.fabNewMain.setVisibility(View.VISIBLE);
            getAnnounces();

        } else {
            shimmer = true;
            componentListener.fadeComponents(binding.recyclerItemsMain, "alpha", 1f, 0f, 250, true);
            UserService uService = new UserService();
            try {
                uService.getUserByUid(uid, new UserCallback() {
                    @Override
                    public void onResponse(Response<UserEntity> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userEntity = response.body();
                            pApplication.setUserPrefs(userEntity);
                            binding.shimmerMain.stopShimmer();
                            componentListener.fadeComponents(binding.shimmerMain, "alpha", 1f, 0f, 400, true);
                            componentListener.fadeComponents(binding.horizontalScrollMain, "alpha", 0f, 1f, 400, false);
                            binding.horizontalScrollMain.setVisibility(View.VISIBLE);
                            componentListener.fadeComponents(binding.fabNewMain, "alpha", 0f, 1f, 400, false);
                            binding.fabNewMain.setVisibility(View.VISIBLE);
                            getAnnounces();
                            shimmer = false;
                        } else {
                            if (response.code() == 404) {
                                alertUserNotFound();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
            } catch (Exception e) {
                Log.e("logx", "ELoadUser: " + e.getMessage());
                Toast.makeText(this, "Tivemos um problema ao obter os dados do usuario. " +
                        "\nPor favor reinicie o app e tente novamente", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void showDataMain(List<ItemEntity> itens) {
        componentListener.fadeComponents(binding.recyclerItemsMain, "alpha", 0f, 1f, 800, false);
        binding.recyclerItemsMain.hasFixedSize();
        binding.recyclerItemsMain.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));

        itemAdapter.setItemEntityList(itens);
        itemAdapter.notifyDataSetChanged();

        if (itens.size() == 0) {
            componentListener.fadeComponents(binding.relativeResourcesMain, "alpha", 0f, 1f, 250, false);
            binding.relativeResourcesMain.setVisibility(View.VISIBLE);
        }

        itemAdapter.setOnClickListener(new ItemAdapter.OnClickListener() {
            @Override
            public void onDeleteClick(int position) {
            }

            @Override
            public void onItemClick(int position) {
                List<ImageEntity> images = new ArrayList<>(itens.get(position).getImages());
                if (!DetailsActivity.status) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("announcement", itens.get(position));
                    intent.putExtra("images", (Serializable) images);
                    if (userEntity != null)
                        intent.putExtra("user", userEntity);
                    startActivity(intent);
                }

            }
        });

    }

    public void openFilter() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottom = getLayoutInflater().inflate(R.layout.layout_filter_bottom, null);
        Spinner spinCountry_filterBottom = bottom.findViewById(R.id.spinCountry_filterBottom);
        SearchableSpinner spinCity_filterBottom = bottom.findViewById(R.id.spinCity_filterBottom);
        Spinner spinCategory_filterBottom = bottom.findViewById(R.id.spinCategory_filterBottom);
        Spinner spinnerDatePub_filterBottom = bottom.findViewById(R.id.spinnerDatePub_filterBottom);
        Button btnFilter_filterBottom = bottom.findViewById(R.id.btnFilter_filterBottom);
        RadioGroup situationGroup_filterBottom = bottom.findViewById(R.id.situationGroup_filterBottom);
        CheckBox chkCatEnableCat_filterBottom = bottom.findViewById(R.id.chkCatEnableCat_filterBottom);
        CheckBox chkEnableCity_filterBottom = bottom.findViewById(R.id.chkEnableCity_filterBottom);
        RelativeLayout relativeProgressCity_filterBottom = bottom.findViewById(R.id.relativeProgressCity_filterBottom);
        inflateSpinnerUf(relativeProgressCity_filterBottom, spinCountry_filterBottom, spinCity_filterBottom, itemFilter);
        inflateSpinnerCategory(spinCategory_filterBottom, itemFilter);
        inflateSpinnerDate(spinnerDatePub_filterBottom);
        bottomSheetDialog.setContentView(bottom);
        bottomSheetDialog.show();
        btnFilter_filterBottom.setOnClickListener(v2 -> {
            switch (situationGroup_filterBottom.getCheckedRadioButtonId()) {
                case R.id.rbNew_filterBottom:
                    itemFilter.setSituation("Novo");
                    break;
                case R.id.rbUsed_filterBottom:
                    itemFilter.setSituation("Usado");
                    break;
            }
            if (!chkEnableCity_filterBottom.isChecked())
                itemFilter.setCity(null);
            if (!chkCatEnableCat_filterBottom.isChecked())
                itemFilter.setCategory(null);
            getFilterResultSet(itemFilter,
                    spinnerDatePub_filterBottom.getSelectedItem().toString().equals("Mais recentes") ? 's' : 'r');
            bottomSheetDialog.dismiss();
        });
    }

    public void inflateSpinnerUf(RelativeLayout layout, Spinner spinUf, SearchableSpinner spinCity, ItemEntity itemFilter) {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.states,
                android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinUf.setAdapter(arrayAdapter);
        if (itemFilter.getUf() != null) {
            for (int i = 0; i < CountryEntity.populateUf().size(); i++) {
                if (CountryEntity.populateUf().get(i).getSigla().equals(itemFilter.getUf())) {
                    spinUf.setSelection(i);
                }
            }
        } else if (userEntity != null) {
            for (int i = 0; i < CountryEntity.populateUf().size(); i++) {
                if (CountryEntity.populateUf().get(i).getSigla().equals(userEntity.getUf())) {
                    spinUf.setSelection(i);
                }
            }
        } else {
            Map<String, String> ref = getInitialPrefs();
            for (int i = 0; i < Arrays.asList(getResources().getStringArray(R.array.states)).size(); i++) {
                if (ref.get("uf").equals(Arrays.asList(getResources().getStringArray(R.array.states)).get(i)))
                    spinUf.setSelection(i);
            }
        }
        spinUf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCityCloud(layout, spinCity, CountryEntity.populateUf().get(position).getId(), itemFilter);
                itemFilter.setUf(spinUf.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getCityCloud(RelativeLayout layout, SearchableSpinner spinCity, int id, ItemEntity itemFilter) {
        layout.setVisibility(View.VISIBLE);
        CityService a = new CityService();
        try {
            a.getCities(id, new AdressCallback() {
                @Override
                public void onResponse(Response<List<CityEntity>> response) {
                    List<CityEntity> cityEntities = response.body();
                    layout.setVisibility(View.GONE);
                    inflateSpinnerCity(spinCity, cityEntities, itemFilter);
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        } catch (Exception e) {
            Log.e("logx", "EGetCityCloud: " + e.getMessage());
            Toast.makeText(this, "Tivemos um problema ao obter os dados do IBGE. " +
                    "\nPor favor reinicie o app e tente novamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void inflateSpinnerCity(SearchableSpinner spinCity, List<CityEntity> cityEntityList, ItemEntity itemFilter) {
        List<String> cities = new ArrayList<>();
        cityEntityList.forEach(c -> cities.add(c.getNome()));

        spinCity.setTitle("Selecione uma cidade");
        spinCity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,cities ));
        if (itemFilter.getCity() != null) {
            for (int i = 0; i < cityEntityList.size(); i++) {
                if (cityEntityList.get(i).getNome().equals(itemFilter.getCity()))
                    spinCity.setSelection(i);
            }

        } else if (userEntity != null) {
            for (int i = 0; i < cityEntityList.size(); i++) {
                if (userEntity.getCity().equals(cityEntityList.get(i).getNome()))
                    spinCity.setSelection(i);
            }
        } else {
            Map<String, String> ref = getInitialPrefs();
            for (int i = 0; i < cityEntityList.size(); i++) {
                if (ref.get("city").equals(cityEntityList.get(i).getNome()))
                    spinCity.setSelection(i);
            }

        }
        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemFilter.setCity(cityEntityList.get(position).getNome());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void inflateSpinnerCategory(Spinner spinCategory, ItemEntity itemFilter) {
        SpinnerCustomAdapter customAdapter = new SpinnerCustomAdapter(this, CategoryEntity.getCategory());
        spinCategory.setAdapter(customAdapter);
        if (itemFilter.getCategory() != null) {
            for (int i = 0; i < CategoryEntity.getCategory().size(); i++) {
                if (CategoryEntity.getCategory().get(i).getOption().equals(itemFilter.getCategory()))
                    spinCategory.setSelection(i);
            }
        } else if (userEntity != null) {
            for (int i = 0; i < CategoryEntity.getCategory().size(); i++) {
                if (CategoryEntity.getCategory().get(i).getOption().equals(userEntity.getCatDefault()))
                    spinCategory.setSelection(i);
            }
        } else
            spinCategory.setSelection(0);
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemFilter.setCategory(CategoryEntity.getCategory().get(position).getOption());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void inflateSpinnerDate(Spinner spinnerDate) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dateFilter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapter);
        spinnerDate.setSelection(0);
    }

    public void getFilterResultSet(ItemEntity itemEntity, char order) {
        binding.swipeRefreshMain.setRefreshing(true);
        binding.relativeResourcesMain.setVisibility(View.GONE);
        componentListener.fadeComponents(binding.recyclerItemsMain, "alpha", 1f, 0f, 250, true);
        ItemService aService = new ItemService();
        Map<String, String> map = new HashMap<>();
        map.put("statusItem", "ativo");
        if (!binding.edtSearchMain.getText().toString().isEmpty())
            map.put("title", binding.edtSearchMain.getText().toString());
        if (itemEntity.getUf() != null)
            map.put("uf", itemEntity.getUf());
        if (itemEntity.getCity() != null)
            map.put("city", itemEntity.getCity());
        if (itemEntity.getSituation() != null)
            map.put("situation", itemEntity.getSituation());
        if (itemEntity.getCategory() != null)
            map.put("category", itemEntity.getCategory());
        try {
            aService.getResultSet(map, new AnnouncementCallback() {
                @Override
                public void onResponse(Response<ItemEntity> response) {
                }

                @Override
                public void onFailure(Throwable t) {
                    if (itemAdapter != null) {
                        itemEntities.clear();
                        itemAdapter.notifyDataSetChanged();
                    }
                    binding.imgResourcesMain.setImageResource(R.drawable.ic_undraw_notify_re_65on);
                    binding.txtTitleResourcesMain.setText(getResources().getString(R.string.titleErrorConnection));
                    binding.txtMessageResourcesMain.setText(getResources().getString(R.string.messageErrorConnectionMain));
                    componentListener.fadeComponents(binding.relativeResourcesMain, "alpha", 0f, 1f, 250, false);
                    binding.relativeResourcesMain.setVisibility(View.VISIBLE);
                }

                @Override
                public void onResponseAll(Response<List<ItemEntity>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        itemEntities.clear();
                        itemEntities.addAll(response.body());
                        binding.imgResourcesMain.setImageResource(R.drawable.ic_undraw_note_list_re_r4u9);
                        binding.txtTitleResourcesMain.setText(getResources().getString(R.string.titleNoContentMain));
                        binding.txtMessageResourcesMain.setText(getResources().getString(R.string.messageNoContentMain));
                        binding.relativeResourcesMain.setVisibility(View.GONE);
                        if (order == 'r')
                            Collections.sort(itemEntities);
                        else {
                            Collections.sort(itemEntities);
                            Collections.reverse(itemEntities);
                        }
                        showDataMain(itemEntities);
                        binding.recyclerItemsMain.setVisibility(View.VISIBLE);
                        binding.swipeRefreshMain.setRefreshing(false);
                    } else {
                        try {
                            JSONObject objectError = new JSONObject(response.errorBody().string());
                            Snackbar.make(binding.relativeParentMain, "Erro ao buscar os dados: \n" + objectError.getString("title"), Snackbar.LENGTH_LONG).show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(binding.relativeParentMain, "Erro não identificado: \n" + e.getMessage(), Snackbar.LENGTH_LONG).show();

                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("logx", "EGetFilterResultSet: " + e.getMessage());
            Toast.makeText(this, "Tivemos um problema ao aplicar o filtro. " +
                    "\nPor favor reinicie o app e tente novamente", Toast.LENGTH_SHORT).show();
        }

    }

    public void showBottomSendEmail() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_send_email, null);
        edtSubject_layoutSend = viewDialog.findViewById(R.id.edtSubject_layoutSend);
        edtMessage_layoutSend = viewDialog.findViewById(R.id.edtMessage_layoutSend);
        Button btnSend_layoutSend = viewDialog.findViewById(R.id.btnSend_layoutSend);
        edtSubject_layoutSend.requestFocus();
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();
        btnSend_layoutSend.setOnClickListener(v -> sendEmail());

    }

    private void sendEmail() {
        String subject = edtSubject_layoutSend.getText().toString();
        String message = edtMessage_layoutSend.getText().toString();
        String[] toList = {"aol.automatodev@gmail.com"};
        if (subject.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "O email requer um titule e uma mensagem!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, toList);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        try {
            startActivity(Intent.createChooser(intent, "Escolha seu cliente de e-mail"));
            Toast.makeText(this, "Obriagdo pelo seu parecer!", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "Não ha nenhum cliente de e-mail instalado.", Toast.LENGTH_SHORT).show();
        }

    }

    public void feedback() {
        final String appPackageName = this.getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    public void viewOffers() {
        OfferService oService = new OfferService();
        final AlertDialog dialogOffers = new AlertDialog.Builder(this).create();
        dialogOffers.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View viewOffers = getLayoutInflater().inflate(R.layout.layout_dialog_offer, null);
        final RecyclerView recycler_dialogOffer = viewOffers.findViewById(R.id.recycler_dialogOffer);
        final SwipeRefreshLayout swipeRefresh_dialogOffer = viewOffers.findViewById(R.id.swipeRefresh_dialogOffer);
        final RelativeLayout relativeResources_dialogOffer = viewOffers.findViewById(R.id.relativeResources_dialogOffer);
        swipeRefresh_dialogOffer.setColorSchemeResources(R.color.colorPrimary2);
        recycler_dialogOffer.setHasFixedSize(true);
        recycler_dialogOffer.setLayoutManager(new LinearLayoutManager(this));
        swipeRefresh_dialogOffer.setRefreshing(true);

        swipeRefresh_dialogOffer.setOnRefreshListener(() -> fetchOffers(oService, recycler_dialogOffer, swipeRefresh_dialogOffer, relativeResources_dialogOffer));

        fetchOffers(oService, recycler_dialogOffer, swipeRefresh_dialogOffer, relativeResources_dialogOffer);
        dialogOffers.setView(viewOffers);
        dialogOffers.show();

    }

    private void fetchOffers(OfferService oService, RecyclerView recycler_dialogOffer, SwipeRefreshLayout swipeRefresh_dialogOffer, RelativeLayout relativeResources_dialogOffer) {
        oService.getAllByUser(userEntity.getIdUser(), new OfferCallback() {
            @Override
            public void onResponse(Response<List<OfferEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdapterOffer adapterOffer = new AdapterOffer(response.body(), "user", MainActivity.this, relativeResources_dialogOffer);
                    recycler_dialogOffer.setAdapter(adapterOffer);
                    swipeRefresh_dialogOffer.setRefreshing(false);
                    componentListener.fadeComponents(recycler_dialogOffer, "alpha", 0f, 1f, 800, false);
                    adapterOffer.setOnItemClickListener(new AdapterOffer.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            callNumber(response.body().get(position).getItem().getPhone());
                        }

                        @Override
                        public void onItemDeleteClick(int position) {
                            adapterOffer.notifyItemRemoved(position);
                            oService.deleteOffer(response.body().get(position).getIdOffer(), new OfferCallback() {
                                @Override
                                public void onResponse(Response<List<OfferEntity>> response) {
                                }

                                @Override
                                public void onResponseId(Response<Long> response) {
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                }
                            });
                            response.body().remove(position);

                        }
                    });
                } else {
                    swipeRefresh_dialogOffer.setRefreshing(false);
                    try {
                        JSONObject errorObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(MainActivity.this, "Tivemos um problema ao processar sua requisição!\nErro: " + errorObject.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Tivemos um problema ao processar sua requisição! \nError: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onResponseId(Response<Long> response) {
            }

            @Override
            public void onFailure(Throwable t) {
                swipeRefresh_dialogOffer.setRefreshing(false);
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "Tivemos um problema ao processar sua requisição! \nVerifique sua conexão e tente novamentw", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callNumber(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    public void alertUserNotFound() {
        AlertDialog alerta = new AlertDialog.Builder(this).create();
        alerta.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog, null);
        TextView txtTitle_dialog = view.findViewById(R.id.txtTitle_dialog);
        TextView txtDialog_dialog = view.findViewById(R.id.txtDialog_dialog);
        Button btnNo_dialog = view.findViewById(R.id.btnNo_dialog);
        Button btnOk_dialog = view.findViewById(R.id.btnOk_dialog);
        txtTitle_dialog.setText(R.string.str_nocad_login_google_title);
        txtDialog_dialog.setText(R.string.str_nocad_login_google_message);
        btnNo_dialog.setText(R.string.str_nocad_login_google_exit);
        btnOk_dialog.setText(R.string.str_nocad_login_google_go);
        btnOk_dialog.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            alerta.dismiss();
        });
        btnNo_dialog.setOnClickListener(v2 -> {
            finishAffinity();
        });


        alerta.setCancelable(false);
        alerta.setView(view);
        alerta.show();


    }

}