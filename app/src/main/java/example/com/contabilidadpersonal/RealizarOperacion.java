package example.com.contabilidadpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealizarOperacion extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    String tipoValue, conceptoValue, cantidadValue, usuarioValue, predefinidoValue;
    EditText cantidad;
    AutoCompleteTextView concepto;
    Spinner tipo;
    RadioButton predefinido;
    public String url;
    JSONObject jsonObject, jsonObject2;
    List<String> conceptos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realizar_operacion);
        usuarioValue = getToken();
        getConceptos();
        Button buttonGuardaOperacion = (Button) findViewById(R.id.buttonGuardaOperacion);
        buttonGuardaOperacion.setOnClickListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo=(Spinner) findViewById(R.id.spinnerTipo);
        tipo.setAdapter(adapter);
        cantidad=(EditText)findViewById(R.id.editTextCantidad);
        concepto=(AutoCompleteTextView)findViewById(R.id.editTextConcepto);
        predefinido=(RadioButton)findViewById(R.id.radioButtonPredefinido);
        cantidad.addTextChangedListener(this);
        concepto.addTextChangedListener(this);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,conceptos);
        concepto.setAdapter(adapter2);
        concepto.setThreshold(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGuardaOperacion:
                tipoValue = tipo.getSelectedItem().toString();
                if(tipoValue.equals("Ingreso")||tipoValue.equals("Ingrés")||tipoValue.equals("Entry")){tipoValue="Ingreso";}
                if(tipoValue.equals("Gasto")||tipoValue.equals("Despesa")||tipoValue.equals("Spending")){tipoValue="Gasto";}
                conceptoValue = concepto.getText().toString();
                cantidadValue = cantidad.getText().toString();
                if(predefinido.isChecked()){
                    predefinidoValue = "true";
                }else{
                    predefinidoValue = "false";
                }
                guardarOperacion();
                break;
        }
    }

    private void guardarOperacion() {
        if(validacion()){
            url =((MyApplication) this.getApplication()).getIp() + "/operacion";
            RequestQueue colaPeticiones = Volley.newRequestQueue(this);
            MostrarManager mostrarManager = new MostrarManager();
            StringRequest peticion =
                    new StringRequest(Request.Method.POST,
                            url,
                            mostrarManager,
                            mostrarManager){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String, String> parametros = new HashMap<String, String>();
                            parametros.put("fecha", getDate());
                            parametros.put("tipo", tipoValue);
                            parametros.put("concepto", conceptoValue);
                            parametros.put("cantidad", cantidadValue);
                            parametros.put("usuario", usuarioValue);
                            parametros.put("predefinido", predefinidoValue);
                            return parametros;
                        }
                    };
            colaPeticiones.add(peticion);
        }
    }

    class MostrarManager implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), (getString(R.string.error_servicio)), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response) {
            try {
                jsonObject = new JSONObject(response);
                lanzarHome();

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), (getString(R.string.error_servicio)), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void getConceptos() {
        url =((MyApplication) this.getApplication()).getIp() + "/operacion/" + usuarioValue;
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
            Toast.makeText(getApplicationContext(), (getString(R.string.error_servicio)), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response) {
            try {
                jsonObject2 = new JSONObject(response);
                JSONArray cast = jsonObject2.getJSONArray("concepto");
                for (int i=0; i<cast.length(); i++) {
                    JSONObject concepto = cast.getJSONObject(i);
                    String nombreConcepto = concepto.getString("concepto");
                    if (!conceptos.contains(nombreConcepto)) {
                        conceptos.add(nombreConcepto);
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), (getString(R.string.error_servicio)), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public boolean validacion(){
        if (cantidad.getText().toString().trim().equalsIgnoreCase("")) {
            cantidad.setError(getString(R.string.error_vacio));
            return false;
        }
        if (concepto.getText().toString().trim().equalsIgnoreCase("")) {
            concepto.setError(getString(R.string.error_vacio));
            return false;
        }
        return true;
    }

    public String getToken(){
        String s = ((MyApplication) this.getApplication()).getNombre();
        return s;
    }

    public String getDate() {
        try {
            DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker2);

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
        return null;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s)  {
        if (cantidad.getText().toString().length() <= 0)
            cantidad.setError(getString(R.string.error_vacio));
        else
            cantidad.setError(null);

        if (concepto.getText().toString().length() <= 0)
            concepto.setError(getString(R.string.error_vacio));
        else
            concepto.setError(null);
    }

    public void lanzarHome() {
        Intent i = new Intent(this, Home2.class);
        startActivity(i);
    }
}