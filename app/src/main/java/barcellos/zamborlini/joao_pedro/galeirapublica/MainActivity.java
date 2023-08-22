package barcellos.zamborlini.joao_pedro.galeirapublica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    static int RESULT_REQUEST_PERMISSION = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pegando o ViewModel
        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        // Pegando o btNav
        bottomNavigationView = findViewById(R.id.btNav);
        // Setando o ouvidor pra ver qual item foi selecionado
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Setando o item selecionado
                vm.setNavigationOpSelected(item.getItemId());
                switch (item.getItemId()){
                    // Se o id for de grid
                    case R.id.gridViewOp:
                        // Pegando o GridFragment
                        GridViewFragment gridViewFragment = GridViewFragment.newInstance();
                        // Setando o Fragment
                        setFragment(gridViewFragment);
                        break;
                    // Se o id for de grid
                    case R.id.listViewOp:
                        // Pegando o ListFragment
                        ListViewFragment listViewFragment = ListViewFragment.newInstance();
                        // Setando o Fragment
                        setFragment(listViewFragment);
                        break;
                }
                return true;
            }
        });
    }
    protected void onResume(){
        super.onResume();
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        checkForPermissions(permissions);
    }

    void setFragment(Fragment fragment){
        // Pegando o gerenciador de fragmentos e iniciando uma transação
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Mudando o fragmento
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        // Confirmando a transação
        fragmentTransaction.commit();
    }

    public void checkForPermissions(List<String> permissions){
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions){
            // Percorrendo a lista de permissões e verificando se não foi confirmada
            if((!hasPermission(permission))){
                // Colocando a permissão não confirmada na lista com outras não confirmadas
                permissionsNotGranted.add(permission);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsNotGranted.size() > 0) {
                // Requisitando as permissões não confirmadas
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            } else {
                MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
                // Pegando o item selecionado e colocando na bottonNav, assim fazendo o fragmento mudar
                int navigationOpSelected = vm.getNavigationOpSelected();
                bottomNavigationView.setSelectedItemId(navigationOpSelected);
            }
        }
    }
    private boolean hasPermission(String permission){
        // Verificando se uma permissão já foi permitida
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION){
            for(String permission : permissions){
                // Verificando se cada permissão foi confirmada
                if(!hasPermission(permission)){
                    // Colocando a permissão não confirmada na lista com outras não confirmadas
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    // Informando ao usuário que a permissão é necessária para o funcionamento do app
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa " +
                            "app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Requisitando novamente as permissões não confirmadas
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }else {
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
            // Pegando o item selecionado e colocando na bottonNav, assim fazendo o fragmento mudar
            int navigationOpSelected = vm.getNavigationOpSelected();
            bottomNavigationView.setSelectedItemId(navigationOpSelected);
        }
    }
}