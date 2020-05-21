package example.com.contabilidadpersonal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


public class OperacionesRecycler extends RecyclerViewActivity {
    ArrayList<String> myList = new ArrayList<String>();
    AlmacenOperaciones items = AlmacenOperacionesArray.getInstance(this);
    public String url;
    JSONObject jsonObject2;
    String usuarioValue;
    ArrayList<String> operacionesList = new ArrayList<String>();
    SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutManager(new LinearLayoutManager(this));
        usuarioValue = getToken();
        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("mylist")) {
            myList = getIntent().getExtras().getStringArrayList("mylist");
            for (int i = 0; i < myList.size(); i++) {
                items.guardarOperacion(myList.get(i).toString());
            }
        }
        setAdapter(new AdaptadorOperacionesRecycler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buscador, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getOperaciones(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/

        searchView.setOnSearchClickListener(new SearchView.OnClickListener(){

            @Override
            public void onClick(View view) {
                lanzarBusqueda();
            }
        });


        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getOperaciones();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    class AdaptadorOperacionesRecycler extends RecyclerView.Adapter<OperacionHolder> {

        @Override
        public OperacionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return (new OperacionHolder(getLayoutInflater().inflate(R.layout.elemento_lista, parent, false)));
        }

        @Override
        public void onBindViewHolder(final OperacionHolder holder, final int position) {
            holder.bindModel(items.listaOperaciones(10).get(position));
            holder.eliminarLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contents = holder.Tid.getText().toString();
                    eliminar(contents);
                }
            });
            holder.editLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contents = holder.Tid.getText().toString();
                    editar(contents);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (items.listaOperaciones(10).size());
        }
    }

    public void getOperaciones() {
        url = ((MyApplication) this.getApplication()).getIp() + "/operacion/busqueda/" + usuarioValue;
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

    private void getOperaciones(String s) {
        url = ((MyApplication) this.getApplication()).getIp() + "/operacion/busqueda2/" + usuarioValue + "/" + s;
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

                for (int i = 0; i < cast.length(); i++) {
                    JSONObject operaciones = cast.getJSONObject(i);
                    operacionesList.add(operaciones.toString());
                }
                items.vaciaOperaciones();
                for (int i = 0; i < operacionesList.size(); i++) {
                    items.guardarOperacion(operacionesList.get(i).toString());
                }
                setAdapter(new AdaptadorOperacionesRecycler());
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void eliminar(String id) {
        String url = ((MyApplication) this.getApplication()).getIp() + "/operacion/"+id+"/"+usuarioValue;
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        MostrarManager2 mostrarManager2 = new MostrarManager2();
        StringRequest peticion =
                new StringRequest(Request.Method.DELETE,
                        url,
                        mostrarManager2,
                        mostrarManager2
                );
        colaPeticiones.add(peticion);
    }

    public void editar(String id){
        Intent i = new Intent(this, EditarOperacion.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    public String getToken() {
        String s = ((MyApplication) this.getApplication()).getNombre();
        return s;
    }

    public void lanzarBusqueda(){
        Intent i = new Intent(this, Buscador.class);
        i.putExtra("busqueda", "busqueda");
        startActivity(i);
        this.finish();
    }

}
