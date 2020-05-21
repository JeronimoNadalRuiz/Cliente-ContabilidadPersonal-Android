package example.com.contabilidadpersonal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener, TextWatcher {
    public String usuarioValue, passwordValue;
    public EditText usuario, password;
    public String url;
    JSONObject jsonObject, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView registro = (TextView) findViewById(R.id.registro);
        registro.setOnClickListener(this);
        Button iniciaSesion = (Button) findViewById(R.id.buttonIniciarSesion);
        iniciaSesion.setOnClickListener(this);
        usuario = (EditText) this.findViewById(R.id.editLogin);
        password = (EditText) this.findViewById(R.id.editPassword);
        usuario.addTextChangedListener(this);
        password.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registro:
                lanzarRegistro();
                break;
            case R.id.buttonIniciarSesion:
                usuarioValue = usuario.getText().toString();
                passwordValue = password.getText().toString();
                login();
                break;
        }
    }

    private void login() {
        if(validacion()){
            url =((MyApplication) this.getApplication()).getIp() + "/login/" + usuarioValue + "/" + passwordValue;
            RequestQueue colaPeticiones = Volley.newRequestQueue(this);
            MostrarManager mostrarManager = new MostrarManager();
            StringRequest peticion =
                    new StringRequest(Request.Method.GET,
                            url,
                            mostrarManager,
                            mostrarManager
                    );
            colaPeticiones.add(peticion);
        }
    }

    class MostrarManager implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), R.string.error_incorrecto, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response) {
            try {
                jsonObject = new JSONObject(response);
                user = jsonObject.getJSONObject("user");
                String nombreUsuario = user.getString("usuario");
                setToken(nombreUsuario);
                lanzarHome();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), (getString(R.string.error_servicio)), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void lanzarRegistro() {
        Intent i = new Intent(this, Registro.class);
        startActivity(i);
    }

    public void lanzarHome() {
        Intent i = new Intent(this, Home2.class);
        startActivity(i);
        this.finish();
    }

    public void setToken(String nombreUsuario) {
        ((MyApplication) this.getApplication()).setNombre(nombreUsuario);
    }

    public boolean validacion(){
        if (usuario.getText().toString().trim().equalsIgnoreCase("")) {
            usuario.setError((getString(R.string.error_vacio)));
            return false;
        }
        if (password.getText().toString().trim().equalsIgnoreCase("")) {
            password.setError((getString(R.string.error_vacio)));
            return false;
        }
        return true;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s)  {
        if (usuario.getText().toString().length() <= 0)
            usuario.setError((getString(R.string.error_vacio)));
        else
            usuario.setError(null);

        if (password.getText().toString().length() <= 0)
            password.setError((getString(R.string.error_vacio)));
        else
            password.setError(null);
    }
}