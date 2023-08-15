package barcellos.zamborlini.joao_pedro.galeirapublica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

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
}