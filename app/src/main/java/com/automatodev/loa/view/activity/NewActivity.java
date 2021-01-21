package com.automatodev.loa.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AdressCallback;
import com.automatodev.loa.controller.callback.AnnouncementCallback;
import com.automatodev.loa.controller.service.CityService;
import com.automatodev.loa.controller.service.ItemService;
import com.automatodev.loa.databinding.ActivityNewBinding;
import com.automatodev.loa.model.entity.CategoryEntity;
import com.automatodev.loa.model.entity.CityEntity;
import com.automatodev.loa.model.entity.CountryEntity;
import com.automatodev.loa.model.entity.ImageEntity;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.view.adapter.SpinnerCustomAdapter;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.components.DialogUtils;
import com.automatodev.loa.view.extras.RequestPermission;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iceteck.silicompressorr.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import lombok.SneakyThrows;
import retrofit2.Response;

public class NewActivity extends AppCompatActivity{

    private int countProgressSave;
    private int countProgressDelete;
    public static boolean status;
    private String[] permissionsList = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    private String provider;
    private ImageView[] images = new ImageView[6];
    private TextView txtProgress;
    private TextView txtTitle;
    private ActivityNewBinding binding;
    private ComponentListener cListener;
    private UserEntity user;
    private List<ImageEntity> imageList;
    private List<ImageEntity> oldImages;
    private List<CityEntity> lc;
    private Uri uri;
    private Uri uri1;
    private Uri uri2;
    private Uri uri3;
    private Uri uri4;
    private Uri uri5;
    private Uri uri6;
    private DialogUtils dialogUtils;
    private ItemEntity itemEntity;
    private FusedLocationProviderClient fLocation;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        //Area para instanciação de elementos e objetosE
        fLocation = LocationServices.getFusedLocationProviderClient(this);
        cListener = new ComponentListener(this);
        user = new UserEntity();
        itemEntity = new ItemEntity();
        imageList = new ArrayList<>();
        oldImages = new ArrayList<>();
        //#######################
        images[0] = binding.imgOneNew;
        images[1] = binding.imgTwoNew;
        images[2] = binding.imgTreeNew;
        images[3] = binding.imgFourNew;
        images[4] = binding.imgFiveNew;
        images[5] = binding.imgSixNew;
        //Area para chamada de metodos ao iniciar a activity
        getData();

    }

    //No onStart é atribuido true variavel de verificação da instancia de telas
    @Override
    protected void onStart() {
        super.onStart();
        status = true;
    }

    //No onStop é atribuido false a variavel de verificação da instancia de telas
    @Override
    protected void onStop() {
        super.onStop();
        status = false;
    }

    //Metodo que retorna para a ctivity main fechando a activity de favoritos
    public void actNewMain(View view) {
        NavUtils.navigateUpFromSameTask(NewActivity.this);
    }

    //Metodo que traz todos as cidades  com base no codigo da UF passada no parametro
    public void getCityCloud(int id, String city) {
        CityService cityService = new CityService();
        binding.relativeCityNew.setVisibility(View.VISIBLE);
        lc = new ArrayList<>();
        cityService.getCities(id, new AdressCallback() {
            @Override
            public void onResponse(Response<List<CityEntity>> response) {
                if (response.body() != null) {
                    lc = response.body();
                    binding.relativeCityNew.setVisibility(View.GONE);
                    populeSpinnerCity(lc, city);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(binding.relativeParentNew,
                        "Verifique sua conexão!", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    //Metodo que popula o spinner contendo as cidades setando a cidade do usuario como default
    public void populeSpinnerCity(List<CityEntity> cityList, String city) {
        List<String> cities = new ArrayList<>();
        cityList.forEach(c -> cities.add(c.getNome()));
        binding.spinnerNew.setTitle("Selecione uma cidade");
        binding.spinnerNew.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,cities));
        for (int i = 0; i < cities.size(); i++) {
            if (cityList.get(i).getNome().equals(city))
                binding.spinnerNew.setSelection(i);
        }
    }

    //Metodo que popula o spinner contendo as categorias recebendo uma posição como parametro
    public void populeSpinnerCategory(int position) {
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(NewActivity.this, CategoryEntity.getCategory());
        binding.spinCategoryNew.setAdapter(adapter);
        binding.spinCategoryNew.setSelection(position);
    }

    @SneakyThrows
    public Uri resizeImage(Uri uri) {
        File file = new File(FileUtils.getPath(this, uri));
        try {
            file = new Compressor(this).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }
    //Metodo sobreposto que capta a resposta (requestCode) ao escolher a imagem,
    // e direciona para o imageView correto com base no requestCode passado

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            uri = data.getData();
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    if (imageList.size() >= 1 && !oldImages.contains(imageList.get(0)))
                        oldImages.add(imageList.get(0));
                    Glide.with(this).load(uri)
                            .into(binding.imgOneNew);
                    new Thread() {
                        @Override
                        public void run() {
                            uri1 = resizeImage(uri);
                        }
                    }.start();
                    break;
                case 2:
                    if (imageList.size() >= 2 && !oldImages.contains(imageList.get(1)))
                        oldImages.add(imageList.get(1));
                    Glide.with(this).load(uri)
                            .into(binding.imgTwoNew);
                    new Thread() {
                        @Override
                        public void run() {
                            uri2 = resizeImage(uri);
                        }
                    }.start();
                    break;
                case 3:
                    if (imageList.size() >= 3 && !oldImages.contains(imageList.get(2))) {
                        oldImages.add(imageList.get(2));
                    }
                    Glide.with(this).load(uri)
                            .into(binding.imgTreeNew);
                    new Thread() {
                        @Override
                        public void run() {
                            uri3 = resizeImage(uri);
                        }
                    }.start();
                    break;
                case 4:
                    if (imageList.size() >= 4 && !oldImages.contains(imageList.get(3))) {
                        oldImages.add(imageList.get(3));
                    }
                    Glide.with(this).load(uri)
                            .into(binding.imgFourNew);
                    new Thread() {
                        @Override
                        public void run() {
                            uri4 = resizeImage(uri);
                        }
                    }.start();
                    break;
                case 5:
                    if (imageList.size() >= 5 && !oldImages.contains(imageList.get(4)))
                        oldImages.add(imageList.get(4));
                    Glide.with(this).load(uri)
                            .into(binding.imgFiveNew);
                    new Thread() {
                        @Override
                        public void run() {
                            uri5 = resizeImage(uri);
                        }
                    }.start();
                    break;
                case 6:
                    if (imageList.size() >= 6 && !oldImages.contains(imageList.get(5)))
                        oldImages.add(imageList.get(5));
                    Glide.with(this).load(uri)
                            .into(binding.imgSixNew);
                    new Thread() {
                        @Override
                        public void run() {
                            uri6 = resizeImage(uri);
                        }
                    }.start();
                    break;
            }
        }
    }

    //Metodo para escolher a foto apartir da biblioteca, no qual o metodo identifica qual imageView foi
    // tocado e chama o startActivityForResult passando um requestCode especifico para cada imageView
    public void pickLib(View view) {
        RequestPermission requestPermissions = new RequestPermission(this, permissionsList);
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (requestPermissions.verifyPermissonSingle(permission)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            switch (view.getId()) {
                case R.id.imgOne_new:
                    startActivityForResult(intent, 1);
                    break;
                case R.id.imgTwo_new:
                    startActivityForResult(intent, 2);
                    break;
                case R.id.imgTree_new:
                    startActivityForResult(intent, 3);
                    break;
                case R.id.imgFour_new:
                    startActivityForResult(intent, 4);
                    break;
                case R.id.imgFive_new:
                    startActivityForResult(intent, 5);
                    break;
                case R.id.imgSix_new:
                    startActivityForResult(intent, 6);
                    break;
            }

        }
    }

    //Metodo sobreposto que verifica as permissoes e dispara um alerta caso a permissao seja negada
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                Snackbar.make(binding.relativeParentNew, "Você precisa dessa permissão!", Snackbar.LENGTH_LONG).show();
             else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (provider == null || provider.length() == 0){
                    dialog.dismiss();
                    dialogLocation();
                }
                else
                    startProcessSave(binding.relativeParentNew);
            }
        }
    }

    //Metodo que valida os edits se estao vazias ou no caso da edit para o valor do item, se é igual a "0,00"
    public boolean validateFields() {
        int count = 0;
        EditText[] fields = new EditText[4];
        fields[0] = binding.edtTitleNew;
        fields[1] = binding.edtDescriptionNew;
        fields[2] = binding.edtPriceNew;
        fields[3] = binding.edtPhoneNew;
        for (EditText e : fields) {
            if (e.getText().toString().trim().isEmpty() || e.getText().toString().equals("R$ 0,00")) {
                e.setBackgroundResource(R.drawable.bg_edt_global_error);
                cListener.onTextListener(e);
                count++;
            }
        }
        return count != 0;
    }

    //Metodo que recupera os dados passados pelo getExtras, tanto para edição quanto para inserção de um novo item
    private void getData() {
        //O bundle recebe os dados passados na intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //Se bundle nao for nulo, recupera os dados com base nas keys
            user = bundle.getParcelable("user");
            itemEntity = bundle.getParcelable("announcement");
            //###############
            //Se o item for diferente de nulo, signifca que um item foi enviado para
            // a tela de edição, entao o fluxo seguira para a edição de  um item ja existente
            if (itemEntity != null) {
                imageList = (List<ImageEntity>) bundle.getSerializable("images");
                binding.lblTitleWindowNew.setText("Edição");
                binding.btnSaveNew.setText("Salvar");
                binding.edtTitleNew.setText(itemEntity.getTitle());
                binding.edtPhoneNew.setText(itemEntity.getPhone());
                binding.edtPriceNew.setText(String.valueOf(itemEntity.getPrice() * 10.00));
                binding.edtDescriptionNew.setText(itemEntity.getDescription());
                if (itemEntity.getSituation().equals("Novo"))
                    binding.rdNewNew.setChecked(true);
                else
                    binding.rdUsedNew.setChecked(true);
                //###############
                int i = 0;
                for (ImageEntity image : imageList) {
                    Glide.with(NewActivity.this).load(image.getUrlImage())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(images[i]);
                    i++;
                }
                //###############
                for (CountryEntity c : CountryEntity.populateUf()) {
                    if (itemEntity.getUf().equals(c.getSigla())) {
                        getCityCloud(c.getId(), itemEntity.getCity());
                    }
                }
                //###############
                i = 0;
                for (CategoryEntity c : CategoryEntity.getCategory()) {
                    if (c.getOption().equals(itemEntity.getCategory())) {
                        populeSpinnerCategory(i);
                    }
                    i++;
                }
                return;
            }
            //###############
            // Se o usuario nao for nulo nesta validação,
            //significa que eu nao tenho um item a ser editado, ou seja um novo anuncio sera criado
            if (user != null) {
                int i = 0;
                itemEntity = new ItemEntity();
                binding.edtPhoneNew.setText(user.getPhone());
                for (CategoryEntity c : CategoryEntity.getCategory()) {
                    if (user.getCatDefault().equals(c.getOption())) {
                        populeSpinnerCategory(i);
                        break;
                    }
                    i++;
                }
                //#################
                for (CountryEntity c : CountryEntity.populateUf()) {
                    if (user.getUf().equals(c.getSigla())) {
                        getCityCloud(c.getId(), user.getCity());
                        break;
                    }
                }
            }
        }
    }

    //Metodo que inicia o processo de salvamento tanto para incluir um novo item quanto para atualizar um ja existente
    //O metodo recupera os dados e valida, e so depois da inicio as transações no servidor
    public void startProcessSave(View view) {
        List<Uri> uriList = new ArrayList<>();
        uriList.clear();
        Uri[] uris = {uri1, uri2, uri3, uri4, uri5, uri6};
        for (Uri u : uris)
            if (u != null)
                uriList.add(u);
        //################
        dialogUtils = new DialogUtils(this);
        dialog = dialogUtils.dialogProgress("upload");
        View v = dialogUtils.getView();
        txtProgress = v.findViewById(R.id.txtMessage_dialog_progress);
        txtTitle = v.findViewById(R.id.titleProgress_layout);
        txtTitle.setText("Cuidando de alguns assuntos...");
        //###############
        if (verifyImageView() && itemEntity.getIdAnnouncement() == null) {
            Snackbar.make(binding.relativeParentNew
                    , "Necessário escolher ao menos 3 imagens para o anúncio!", Snackbar.LENGTH_LONG).show();
            return;
        }
        //###############
        if (validateFields()) {
            Snackbar.make(binding.relativeParentNew
                    , "Existem campos que precisam ser preenchidos!", Snackbar.LENGTH_LONG).show();
            return;
        }
        //###############
        if (binding.edtDescriptionNew.getText().length() < 20) {
            binding.edtDescriptionNew.setBackgroundResource(R.drawable.bg_edt_global_error);
            cListener.onTextListener(binding.edtDescriptionNew);
            Snackbar.make(binding.relativeParentNew, "A descrição precisa ter pelo menos 20 caracteres!", Snackbar.LENGTH_LONG).show();
            return;
        }
        //###############
        if (binding.edtPhoneNew.getText().length() < 14) {
            binding.edtPhoneNew.setBackgroundResource(R.drawable.bg_edt_global_error);
            cListener.onTextListener(binding.edtPhoneNew);
            Snackbar.make(binding.relativeParentNew, "Telefone deve seguir o padrao de 9 digitos!", Snackbar.LENGTH_LONG).show();
            return;
        }
        //###############

        //###############
        if (lc.size() == 0)
            itemEntity.setCity(user.getCity());
        else
            itemEntity.setCity(lc.get(binding.spinnerNew.getSelectedItemPosition()).getNome());
        //###############
        switch (binding.rdSituationNew.getCheckedRadioButtonId()) {
            case R.id.rdNew_new:
                itemEntity.setSituation(binding.rdNewNew.getText().toString());
                break;
            case R.id.rdUsed_new:
                itemEntity.setSituation(binding.rdUsedNew.getText().toString());
                break;
        }
        //###############
        itemEntity.setUserEntity(new UserEntity(user.getIdUser()));
        itemEntity.setUf(user.getUf());
        itemEntity.setPhone(binding.edtPhoneNew.getText().toString());
        //Por se tratar de  um maskEdit o valor do item é capturado pelo getRwawValuew, porem para o obter o valor correto a ser gravado é necessario dividir por 100.00
        itemEntity.setPrice(Double.parseDouble(String.valueOf(binding.edtPriceNew.getRawValue() / 100.00)));
        itemEntity.setTitle(binding.edtTitleNew.getText().toString());
        itemEntity.setDescription(binding.edtDescriptionNew.getText().toString());
        itemEntity.setCategory(CategoryEntity.getCategory().get(binding.spinCategoryNew.getSelectedItemPosition()).getOption());
        //###############
        if (itemEntity.getUid() == null)
            itemEntity.setUid(UUID.randomUUID().toString());
        //###############
        dialog.show();
        provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            dialog.dismiss();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else if (provider == null || provider.length() == 0) {
            dialog.dismiss();
            dialogLocation();
        } else {
            LocationRequest locationRequest = new LocationRequest();
             locationRequest.setInterval(1000);
             locationRequest.setFastestInterval(3000);
             locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
             LocationServices.getFusedLocationProviderClient(this)
                     .requestLocationUpdates(locationRequest,
                             new LocationCallback(){

                         @Override
                                 public void onLocationResult(LocationResult locationResult){
                                super.onLocationResult(locationResult);
                             LocationServices.getFusedLocationProviderClient(NewActivity.this).removeLocationUpdates(this);
                             if (locationResult != null && locationResult.getLocations().size() >0){
                                 int latestLocation = locationResult.getLocations().size() -1;
                                 double lat, lang;
                                 lat = locationResult.getLocations().get(latestLocation).getLatitude();
                                 lang = locationResult.getLocations().get(latestLocation).getLongitude();
                                 itemEntity.setLang(lang);
                                 itemEntity.setLat(lat);
                                 //Chama o metodo que verificara se ha imagens antigas a serem deletadas, se houver, deleta e da inicio ao salvamento em nuvem
                                 //se nao houver, apenas da inicio ao salvamento em nuvem
                                 //Ao mesmo tampo que faz uma validação se o tipo de transação sera 's' de save ou 'u' de update com base no id do item ser nulo ou nao
                                 deleteOldImages(itemEntity.getUid(), uriList, itemEntity.getIdAnnouncement() == null ? 's' : 'u', oldImages);
                             } else{
                                 dialogUtils.dialogStatus(binding.relativeParentNew, "Algo deu errado!",
                                         "Não foi possivel obter sua localização.\nDesative e ative a localização, e tente novamente"
                                         , "error.json", true);
                             }
                         }
             }, Looper.getMainLooper());
        }

    }

    //Metodo que deleta as antigas imagens, caso haja imagens para deletar
    //Quando o usuario atualiza um item e escolhe uma nova imagem, a imagem antiga (url, name, id) sao enviadaas para uma lista que sera usada para
    //deletar esses itens na nuvem, se essa lista contiver dados, faz a remoção e chama o metodo de salvamento, se nao, apenas chama o metodo de salvamento
    public void deleteOldImages(String dir, List<Uri> uList, char type, List<ImageEntity> images) {
        countProgressDelete = 0;
        if (images.size() != 0) {
            try {
                txtTitle.setText("Removendo dados antigos");
                for (ImageEntity i : images) {
                    StorageReference itemStorage = FirebaseStorage.getInstance().getReference("announcement")
                            .child(user.getEmail())
                            .child(itemEntity.getUid())
                            .child(i.getName());
                    itemStorage.delete().addOnSuccessListener(avoid -> {
                        if (countProgressDelete + 1 == images.size()) {
                            saveInCloud(dir, uList, type);
                        }
                        countProgressDelete++;

                    }).addOnFailureListener(e -> Log.e("logx", "Error " + e.getMessage()));
                }
            } catch (Exception e) {
                Log.e("logx", "EDeleteOldImages: " + e.getMessage());
                dialogUtils.dialogStatus(binding.relativeParentNew, "Algo deu errado!",
                        "Seus dados tropecaram no caminho e nao pudemos concluir sua publicação.\nPor favor tente novamente."
                        , "error.json", true);
            }

        } else
            saveInCloud(dir, uList, type);

    }

    //Metodo que ira salvar os dados em nuvem, recebe um diretorio(email do usuario), uma lista de uris referente as imagens da galeria e o tipo de operaçaõ 's' ou 'u'
    public void saveInCloud(String dir, List<Uri> uList, char type) {
        List<ImageEntity> newImages = new ArrayList<>();
        countProgressSave = 0;
        txtTitle.setText("Cuidando de alguns assuntos...");
        //#################
        //Se a lista de uri for maior que 0 significa que eu tenho imagens a enviar
        //é feito entao um for dentro dessa lista de uri, e para cada item é criada uma referencia no storage com um nome randomico
        //e o processo para cada item entao é iniciado
        if (uList.size() > 0) {
            for (Uri u : uList) {
                String uuid = UUID.randomUUID().toString();
                StorageReference itemStorage = FirebaseStorage.getInstance().getReference("announcement")
                        .child(user.getEmail())
                        .child(dir)
                        .child(uuid);
                //#################
                itemStorage.putFile(u).addOnCompleteListener(task -> itemStorage.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            txtTitle.setText("Enviando arquivos");
                            if (countProgressSave + 1 == uList.size()) {
                                //no sucesso do ultimo item, atualiza o processo e verifica que tipo de operação devera ser feita 's' ou 'u'
                                txtProgress.setText((countProgressSave + 1) + " / " + uList.size());
                                newImages.add(new ImageEntity(null, uri.toString(), uuid));
                                //Se for 's' o item sera salvo com as novas, seta a lista de imagens no item, e chama o metodo salvar passando o item como parametro
                                if (type == 's') {
                                    itemEntity.setImages(newImages);
                                    saveItem(itemEntity, newImages);
                                } else {
                                    //Se for 'u', é necessario comparar as imagens que foram substituidas e estao na lista de imagens antigas
                                    //se houver correspendencia entre a lista original e lista de imagens antigas, é substituido na posição exata da lista original
                                    //o id da imagem antiga, a url da nova imagem e o nome dela tambem, ao mesmo tempo que remove da lista de imagens novas a imagem que foi alterada
                                    //ao mesmo tempo que adiciona no item correspondente em newImages o id da imagem afim de comparação no proximo passo
                                    for (int i = 0; i < imageList.size(); i++) {
                                        for (int j = 0; j < oldImages.size(); j++) {
                                            if (imageList.get(i).getIdImage().equals(oldImages.get(j).getIdImage())) {
                                                imageList.get(i).setUrlImage(newImages.get(j).getUrlImage());
                                                imageList.get(i).setName(newImages.get(j).getName());
                                                newImages.get(j).setIdImage(imageList.get(i).getIdImage());

                                            }
                                        }
                                    }
                                    //Verifica sem na imageList ha um correspondente em newImages, se tiver, deleta esse correspondente de new Images
                                    for (ImageEntity i : imageList) {
                                        if (newImages.contains(i))
                                            newImages.remove(i);
                                    }
                                    //Se a lista de novas imagens ainda contiver itens mesmo apos a deleção no passo acima, significa que novas imagens foram inseridas alem de modificadas.
                                    //Sendo assim é adicionado o restante dos dados contidos em newImages dentro de imageList
                                    if (newImages.size() != 0) {
                                        imageList.addAll(newImages);
                                    }
                                    //Atribui imageList no item
                                    itemEntity.setImages(imageList);
                                    //E por fim chama o metodo de atualizar passando como parametro o id do item e o item em si.
                                    updateItem(itemEntity, itemEntity.getIdAnnouncement());
                                }
                            } else {
                                //Para cada item, é mostrado o progresso no textView do dialog
                                //ao mesmo tempo que adiciona o link obtido na lista de imagens com o nome gerado randomicamente e o link
                                txtProgress.setText((countProgressSave + 1) + " / " + uList.size());
                                newImages.add(new ImageEntity(null, uri.toString(), uuid));
                            }
                            countProgressSave++;
                        }));
            }
        } else {
            //Se nao houve alteração nas imagens, apenas na descrição,
            //Atribui imageList no item e chama o metodo de update
            itemEntity.setImages(imageList);
            updateItem(itemEntity, itemEntity.getIdAnnouncement());
        }
    }

    //Meodo que salva um item, recebendo como parametro uma lista de novas imagens, pois caso haja erro no banco de dados, um rollback é chamado desfazendo as alterações no storage
    public void saveItem(ItemEntity item, List<ImageEntity> newImages) {
        ItemService iService = new ItemService();
        txtTitle.setText("Salvando anúncio...");
        iService.postAnnouncement(item, new AnnouncementCallback() {
            @Override
            public void onResponse(Response<ItemEntity> response) {
                dialogUtils.dialogStatus(binding.relativeParentNew, "Tudo certo!",
                        "Sucesso!", "success.json", false);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                            OwnerActivity.update = true;
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("logx", "Error:" + t.getMessage());
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                            rollBackImages(item.getUserEntity().getEmail(), item.getUid(), newImages);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void onResponseAll(Response<List<ItemEntity>> response) {
            }
        });
    }

    //Meodo que salva um item, recebendo como parametro um item e um id para atualização
    //TODO Ainda deve ser implementado um rollback para caso de merda no upload da ediçao, deletar as imagens antigas
    private void updateItem(ItemEntity itemEntity, Long idAnnouncement) {
        ItemService iService = new ItemService();
        iService.updateAnnouncement(idAnnouncement, itemEntity, new AnnouncementCallback() {
            @Override
            public void onResponse(Response<ItemEntity> response) {
                dialogUtils.dialogStatus(binding.relativeParentNew, "Tudo certo!",
                        "Sucesso!", "success.json", false);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(1500);
                            OwnerActivity.update = true;
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void onFailure(Throwable t) {
                dialogUtils.dialogStatus(binding.relativeParentNew, "Algo deu errado!",
                        "Houve um erro ao processar sua solicitação!\nContate o administrador caso necessário!"
                        , "error.json", true);

            }

            @Override
            public void onResponseAll(Response<List<ItemEntity>> response) {
            }
        });
    }

    //Metodo que valida se ha ao menos 3 imagens escolhidas, que é o minimo de imagens aceitas.
    public boolean verifyImageView() {
        int count = 0;
        for (ImageView i : images) {
            if (i.getDrawable() == null)
                count++;
        }
        return count > 3;
    }

    //Metodo supinpa de rollback que deleta as imagens tendo como base a referencia no storage
    //E se caso o cara queira dar uma de espertinho, ainda tem o rollback do rollback no onFailure
    public void rollBackImages(String path, String ref, List<ImageEntity> images) {
        countProgressDelete = 0;
        if (images.size() != 0) {
            for (ImageEntity i : images) {
                StorageReference itemStorage = FirebaseStorage.getInstance().getReference("announcement")
                        .child(path)
                        .child(ref)
                        .child(i.getName());
                itemStorage.delete().addOnSuccessListener(avoid -> {
                    if (countProgressDelete + 1 == images.size()) {
                        dialogUtils.dialogStatus(binding.relativeParentNew, "Anúncio não publicado!",
                                "Houve um erro ao processar sua solicitação!\nContate o administrador caso necessário!"
                                , "error.json", true);
                    }
                    countProgressDelete++;
                }).addOnFailureListener(t -> {
                    Log.e("logx", "Error: " + t.getMessage());
                    dialogUtils.dialogStatus(binding.relativeParentNew, "Anúncio não publicado!",
                            "Houve um erro ao processar sua solicitação!\nContate o administrador caso necessário!"
                            , "error.json", true);
                });
            }

        }
    }

    //Metodo que cria um dialog caso a localização nao esteja ativada / autorizada para o aplicativo, tendo a opção de levar o usuario direto para a tela de configurações de localização
    public void dialogLocation() {
        AlertDialog alerta = new AlertDialog.Builder(this).create();
        View v = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null);
        alerta.setView(v);
        alerta.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView txtTitle_dialog = v.findViewById(R.id.txtTitle_dialog);
        TextView txtDialog_dialog = v.findViewById(R.id.txtDialog_dialog);
        txtTitle_dialog.setText(R.string.titleDialogLocation);
        txtDialog_dialog.setText(R.string.textDialogLocation);
        Button btnOk_dialog = v.findViewById(R.id.btnOk_dialog);
        Button btnNo_dialog = v.findViewById(R.id.btnNo_dialog);
        btnOk_dialog.setText(R.string.txtBtnYesDialogLocation);
        btnNo_dialog.setText(R.string.txtBtnNoDialogLocation);
        alerta.setCancelable(false);
        alerta.show();
        btnNo_dialog.setOnClickListener(v1 -> {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            alerta.dismiss();
        });
        btnOk_dialog.setOnClickListener(v2 -> alerta.dismiss());

    }


    public void clearFields(View view) {
        Snackbar.make(view, "Calma! Ainda estamos preparando tudo para você :D", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}

