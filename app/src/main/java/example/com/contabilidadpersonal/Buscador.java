package example.com.contabilidadpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class Buscador extends AppCompatActivity implements View.OnClickListener{
    public String url;
    JSONObject jsonObject2;
    ArrayList<String> operacionesList = new ArrayList<String>();
    public String usuarioValue, conceptoValue, cantidadMinValue, cantidadMaxValue, tipoValue;
    public EditText  concepto, cantidadMin, cantidadMax;
    public RadioButton semana, mes, rango;
    public LinearLayout layout_fecha;
    public Spinner tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscador);
        Button buttonBuscar = (Button) findViewById(R.id.buttonBusqueda);
        buttonBuscar.setOnClickListener(this);
        usuarioValue = getToken();
        concepto = (EditText) this.findViewById(R.id.editConcepto);
        cantidadMin = (EditText) this.findViewById(R.id.editCantidadMin);
        cantidadMax = (EditText) this.findViewById(R.id.editCantidadMax);
        semana = (RadioButton) this.findViewById(R.id.radio_semana);
        mes = (RadioButton) this.findViewById(R.id.radio_mes);
        rango = (RadioButton) this.findViewById(R.id.radio_rango);
        layout_fecha = (LinearLayout) this.findViewById(R.id.layout_fecha);
        semana.setOnClickListener(this);
        mes.setOnClickListener(this);
        rango.setOnClickListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos2, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo=(Spinner) findViewById(R.id.spinnerTipo2);
        tipo.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBusqueda:
                conceptoValue = concepto.getText().toString();
                cantidadMinValue = cantidadMin.getText().toString();
                cantidadMaxValue = cantidadMax.getText().toString();
                tipoValue = tipo.getSelectedItem().toString();
                if(tipoValue.equals("Ingreso")||tipoValue.equals("Ingrés")||tipoValue.equals("Entry")){tipoValue="Ingreso";}
                if(tipoValue.equals("Gasto")||tipoValue.equals("Despesa")||tipoValue.equals("Spending")){tipoValue="Gasto";}
                if(tipoValue.equals("Gasto / Ingreso")||tipoValue.equals("Despesa / Ingrés")||tipoValue.equals("Spending / Entry")){tipoValue="";}
                busqueda();
                break;
            case R.id.radio_semana:
                layout_fecha.setVisibility(View.GONE);
                break;
            case R.id.radio_mes:
                layout_fecha.setVisibility(View.GONE);
                break;
            case R.id.radio_rango:
                layout_fecha.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void busqueda() {
        url =((MyApplication) this.getApplication()).getIp() + "/operacion/busquedaPersonal";
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        MostrarManager2 mostrarManager2 = new MostrarManager2();
        StringRequest peticion =
                new StringRequest(Request.Method.POST,
                        url,
                        mostrarManager2,
                        mostrarManager2){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("concepto", conceptoValue);
                        parametros.put("tipo", tipoValue);
                        parametros.put("user", usuarioValue);
                        parametros.put("cantidadMin", cantidadMinValue);
                        parametros.put("cantidadMax", cantidadMaxValue);
                        parametros.put("fechaMin", getDate());
                        parametros.put("fechaMax", getDate2());
                        return parametros;
                    }
                };
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
                lanzarDatos();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public String getDate() {
        if(rango.isChecked()) {
            try {
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePickermin);

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                String formatedDate = sdf.format(calendar.getTime());

                return formatedDate;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            if(semana.isChecked()) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, -7);
                Date semana = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formatedDate = sdf.format(semana.getTime());
                return formatedDate;
            }
            else{
                if(mes.isChecked()){
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_YEAR, -30);
                    Date semana = cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String formatedDate = sdf.format(semana.getTime());
                    return formatedDate;
                }
            }
        }


        return null;
    }

    public String getDate2() {
        if(rango.isChecked()){
            try {
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePickermax);

                int day  = datePicker.getDayOfMonth();
                int month= datePicker.getMonth();
                int year = datePicker.getYear();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                String formatedDate = sdf.format(calendar.getTime());

                return formatedDate;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            Date semana = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formatedDate = sdf.format(semana.getTime());
            return formatedDate;
        }

        return null;
    }

    public void lanzarDatos(){
        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("busqueda")) {
            Intent i = new Intent(getApplication(), OperacionesRecycler.class);
            i.putExtra("mylist", operacionesList);
            startActivity(i);
            this.finish();
        }
        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("graficos")) {
            Intent i = new Intent(getApplication(), Graficos.class);
            i.putExtra("mylist", operacionesList);
            startActivity(i);
            this.finish();
        }
    }

    public String getToken(){
        String s = ((MyApplication) this.getApplication()).getNombre();
        return s;
    }
}

