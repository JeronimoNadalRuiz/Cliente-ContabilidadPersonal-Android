package example.com.contabilidadpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

import java.io.Serializable;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements View.OnClickListener{
    public String url;
    JSONObject jsonObject, jsonObject2;
    String usuarioValue;
    ArrayList<String> operacionesList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        usuarioValue = getToken();

        LinearLayout layoutRealizarOperacion = (LinearLayout) findViewById(R.id.LayoutRealizarOperacion);
        layoutRealizarOperacion.setOnClickListener(this);

        LinearLayout LayoutBusqueda = (LinearLayout) findViewById(R.id.LayoutBusqueda);
        LayoutBusqueda.setOnClickListener(this);

        LinearLayout LayoutGraficos = (LinearLayout) findViewById(R.id.LayoutGraficos);
        LayoutGraficos.setOnClickListener(this);

        LinearLayout LayoutCerrarSesion = (LinearLayout) findViewById(R.id.LayoutCerrarSesion);
        LayoutCerrarSesion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LayoutRealizarOperacion:
                lanzarRealizarOperacion();
                break;
            case R.id.LayoutBusqueda:
                lanzarBusqueda();
                break;
            case R.id.LayoutGraficos:
                lanzarGraficos();
                break;
            case R.id.LayoutCerrarSesion:
                lanzarCerrarSesion();
                break;
        }
    }

    public void lanzarRealizarOperacion(){
        Intent i = new Intent(this, RealizarOperacion.class);
        startActivity(i);
    }

    public void lanzarBusqueda(){
        getOperaciones();
        Intent i = new Intent(this, OperacionesRecycler.class);
        i.putExtra("mylist", operacionesList);
        startActivity(i);
    }

    public void lanzarGraficos(){

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
            Toast.makeText(getApplicationContext(), "Error accediendo al servicio", Toast.LENGTH_LONG).show();
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
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error accediendo al servicio", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public String getToken(){
        String s = ((MyApplication) this.getApplication()).getNombre();
        return s;
    }
}
