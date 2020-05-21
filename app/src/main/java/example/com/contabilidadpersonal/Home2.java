package example.com.contabilidadpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Home2 extends AppCompatActivity {

    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    public String url;
    JSONObject jsonObject, jsonObject2;
    String usuarioValue;
    ArrayList<String> operacionesList = new ArrayList<String>();
    String intent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home2);
        usuarioValue = getToken();
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.navview);
        View v = navView.getHeaderView(0);
        TextView headerUser = (TextView ) v.findViewById(R.id.usuMenu);
        String upperString = getToken().substring(0,1).toUpperCase() + getToken().substring(1);
        headerUser.setText(upperString);
        drawerLayout.openDrawer(Gravity.LEFT);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;

                        switch (menuItem.getItemId()) {
                            case R.id.menu_seccion_1:
                                lanzarRealizarOperacion();
                                break;
                            case R.id.menu_seccion_2:
                                lanzarBusqueda();
                                break;
                            case R.id.menu_seccion_3:
                                lanzarGraficos();
                                break;
                            case R.id.menu_seccion_4:
                                lanzarCerrarSesion();
                                break;
                        }

                        if(fragmentTransaction) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void lanzarRealizarOperacion(){
        Intent i = new Intent(this, RealizarOperacion.class);
        startActivity(i);
    }

    public void lanzarBusqueda(){
        intent = "busqueda";
        getOperaciones();

    }

    public void lanzarGraficos(){
        intent = "graficos";
        getOperaciones();
    }

    public void lanzarCerrarSesion(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
    }

    private void getOperaciones() {
        url =((MyApplication) this.getApplication()).getIp() + "/operacion/busqueda/" + usuarioValue;
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        MostrarManager2 mostrarManager2 = new MostrarManager2();
        StringRequest peticion =
                new StringRequest(Request.Method.GET,
                        url,
                        mostrarManager2,
                        mostrarManager2
                );
        colaPeticiones.add(peticion);
    }

    class MostrarManager2 implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response) {
            try {
                operacionesList.clear();
                jsonObject2 = new JSONObject(response);
                JSONArray cast = jsonObject2.getJSONArray("operaciones");

                for (int i=0; i<cast.length(); i++) {
                    JSONObject operaciones = cast.getJSONObject(i);
                    operacionesList.add(operaciones.toString());
                }
                if(intent.equals("graficos")){
                    Intent i = new Intent(getApplication(), Graficos.class);
                    i.putExtra("mylist", operacionesList);
                    startActivity(i);
                }
                if(intent.equals("busqueda")){
                    Intent i = new Intent(getApplication(), OperacionesRecycler.class);
                    i.putExtra("mylist", operacionesList);
                    startActivity(i);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public String getToken(){
        String s = ((MyApplication) this.getApplication()).getNombre();
        return s;
    }
}