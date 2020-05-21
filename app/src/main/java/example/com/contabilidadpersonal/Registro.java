package example.com.contabilidadpersonal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Registro  extends AppCompatActivity implements View.OnClickListener {
    public String nombreValue, usuarioValue, passwordValue;
    public EditText nombre, usuario, password;
    public String url;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        Button registro = (Button) findViewById(R.id.buttonRegistrarse);
        registro.setOnClickListener(this);
        nombre=(EditText)findViewById(R.id.editNombre);
        usuario=(EditText)findViewById(R.id.editLogin);
        password=(EditText)findViewById(R.id.editPassword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegistrarse:
                nombreValue = nombre.getText().toString();
                usuarioValue = usuario.getText().toString();
                passwordValue = password.getText().toString();
                registro();
                break;
        }
    }

    private void registro() {
        if(validacion()){
            url =((MyApplication) this.getApplication()).getIp() + "/usuario";
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
                            parametros.put("nombre", nombreValue);
                            parametros.put("usuario", usuarioValue);
                            parametros.put("password", passwordValue);
                            return parametros;
                        }
                    };
            colaPeticiones.add(peticion);
        }
    }

    class MostrarManager implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_usuario), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response) {
            try {
                jsonObject = new JSONObject(response);
                lanzarLogin();

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_servicio), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public boolean validacion(){
        if (nombre.getText().toString().trim().equalsIgnoreCase("")) {
            nombre.setError(getString(R.string.error_vacio));
            return false;
        }
        if (usuario.getText().toString().trim().equalsIgnoreCase("")) {
            usuario.setError(getString(R.string.error_vacio));
            return false;
        }
        if (password.getText().toString().trim().equalsIgnoreCase("")) {
            password.setError(getString(R.string.error_vacio));
            return false;
        }
        return true;
    }

    public void lanzarLogin() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}

