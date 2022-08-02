package com.upv.autobusgps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int numRutas = 33; //Numero total de rutas con las que cuenta la aplicación
    int llamadaMetodo; //esta variable sirve para saber de que color poner la línea de la ruta
    int marcadores = 0; //solo se pueden poner 2 marcadores en el mapa

    //clave de googleMapsApi que sirve para poder generar la ruta, Esta clave se encuentra en el PDF del proyecto
    static private String LlaveMapaObtenidaGoogleMapsApi ="AIzaSyCeThucUanCALfMUYskFazmlAD8eoqRHJo";

    //cada ruta cuenta con un arreglo de 25 puntos cada uno con su respectivas latitudes y longitudes
    LatLng ruta1[];
    LatLng ruta2[];
    LatLng ruta3[];
    LatLng ruta4[];
    LatLng ruta6[];
    LatLng ruta7[];
    LatLng ruta8[];
    LatLng ruta10[];
    LatLng ruta11[];
    LatLng ruta12[];
    LatLng ruta13[];
    LatLng ruta14[];
    LatLng ruta15[];
    LatLng ruta16[];
    LatLng ruta17[];
    LatLng ruta18[];
    LatLng ruta19[];
    LatLng ruta20[];
    LatLng ruta21[];
    LatLng ruta22[];
    LatLng ruta23[];
    LatLng ruta24[];
    LatLng ruta25[];
    LatLng ruta26[];
    LatLng ruta27[];
    LatLng ruta28[];
    LatLng ruta29[];
    LatLng ruta30[];
    LatLng ruta31[];
    LatLng ruta32[];
    LatLng ruta34[];
    LatLng ruta35[];
    LatLng ruta51[];

    LatLng Inicial = new LatLng(23.736015, -99.1542473); //Plaza del 15, punto que aparecerá en primer lugar en el mapa
    LatLng Origen, Destino; //variables para obtener los puntos de origen y destino (los dos marcadores)

    public Context mContext;

    String URL[]; //url va a obtener las rutas que se van a graficar de acuerdo a los puntos seleccionados

    Spinner spinnerOpciones; //spinner que mostrará los medios de transporte que se utilizará para ir del origen al destino
    Spinner spinnerRutas; //spinner que mostrará la ruta completa que se haya seleccionado
    TextView rutaAzul, rutaRoja; //aquí se mostrará el nombre de las rutas que se tienen que tomar
    Button buttonBorrar; //borrar los marcadores agregados
    Context CX; //necesario para mostrar un AlertDialog

    //Aquí se guarda el número de ruta y el nombre de la ruta para poder mostrarla en los textView más adelante
    String nomRutas[][] = {{"Ruta-1","Miguel Alemán - Hospital General"},{"Ruta-2","San Luisito - Naco x27"},
            {"Ruta-3","Mariano Matamoros - Coca Cola"}, {"Ruta-4","Las Flores - Revolución verde"},
            {"Ruta-6","Lopez Partillo - Central"},{"Ruta-7","Unidad Modelo - Central"},
            {"Ruta-8","Horacio Terán - Central"}, {"Ruta-10","Sosa - Treviño"},{"Ruta-11","Laborcitas"},
            {"Ruta-12","Blanco - Col. La presita"}, {"Ruta-13","Tamatán - Satélite"},{"Ruta-14","Echeverría - Central"},
            {"Ruta-15","Azteca- Tamatan- Compuertas- Liberal"}, {"Ruta-16","Col. Primavera - UAT - Central"},
            {"Ruta-17","Azteca - Libertad - Cuartel"},{"Ruta-18","San Marcos - Central"},
            {"Ruta-19"," CONALEP - Boulevard - Central"}, {"Ruta-20","Estrella - Central"},
            {"Ruta-21","Col. Vista Hermosa- Col Diana Launra Riojas De Colosio"},{"Ruta-22","Amalia G de Castillo Ledon- Central"},
            {"Ruta-23","Corregidora - Nacozary"},{"Ruta-24","Cumbres-Central"},{"Ruta-25","Estudiantil-Central"},
            {"Ruta-26"," Col. Simon Torre- Central"}, {"Ruta-27","Ej. SAn Jacinto"},
            {"Ruta-28","Ej Manuel Avila Camacho -Victoria"},{"Ruta-29","Cd. Victoria -Ej. El Olivo"},
            {"Ruta-30","Cd. Victoria - La San Juana"}, {"Ruta-31","EJ. Revolución Verde"},
            {"Ruta-32","Mercado - Estación - Tamatán"},{"Ruta-34","Seguro - CONALEP"},
            {"Ruta-35","Portillo - Tecnológico"},{"Ruta-51","Victoria - EJ. La Peña"}};

    Double distancias[][]; //calcular distancias entre todos los puntos de las rutas
    Double tiempos[][]; //y en base a las distancias calcular los tiempos
    int tiempoRetraso = 20; //un tiempo de retraso como consideracion entre paradas y cambios de micro



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Creamos variable de firebase
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Creamos la variable bundle para enviar datos a firebase
        Bundle bundle = new Bundle();
        // Agregamos los datos a enviar a firebase
        bundle.putString("message", "integration complet");
        // Enviamos los datos a firebase
        firebaseAnalytics.logEvent("login",bundle);
        Toast.makeText(getApplicationContext(),"hola, esperando eventos en firebase",Toast.LENGTH_SHORT).show();

        llamadaMetodo = 0;
        CX = this; //necesario para el AlertDialog
        //Double distance = SphericalUtil.computeDistanceBetween(ruta1[0], ruta1[1]);
        distancias = new Double[numRutas][24];
        tiempos = new Double[numRutas][24];

        //LATITUDES Y LONGITUDES DE LOS PUNTOS QUE TIENE CADA RUTA, EL MAXIMO DE PUNTOS QUE PUEDE TENER UNA RUTA ES DE 25
        //RUTA 1 - MIGUEL ALEMAN - HOSPITAL GENERAL
        ruta1 = new LatLng[25];
        ruta1[0] = new LatLng(23.758405, -99.175399);
        ruta1[1] = new LatLng(23.75555, -99.18234);
        ruta1[2] = new LatLng(23.75468, -99.17374);
        ruta1[3] = new LatLng(23.75456, -99.17458);
        ruta1[4] = new LatLng(23.75128, -99.17486);
        ruta1[5] = new LatLng(23.74179, -99.17188);
        ruta1[6] = new LatLng(23.73935, -99.1718);
        ruta1[7] = new LatLng(23.7379, -99.17441);
        ruta1[8] = new LatLng(23.73371, -99.17683);
        ruta1[9] = new LatLng(23.7342, -99.16911);
        ruta1[10] = new LatLng(23.73712, -99.16909);
        ruta1[11] = new LatLng(23.73938, -99.15937);
        ruta1[12] = new LatLng(23.75339, -99.15683);
        ruta1[13] = new LatLng(23.75612, -99.1545);
        ruta1[14] = new LatLng(23.75586, -99.15177);
        ruta1[15] = new LatLng(23.75279, -99.14938);
        ruta1[16] = new LatLng(23.74318, -99.14863);
        ruta1[17] = new LatLng(23.73821, -99.14643);
        ruta1[18] = new LatLng(23.7274, -99.14919);
        ruta1[19] = new LatLng(23.72965, -99.12936);
        ruta1[20] = new LatLng(23.74173, -99.13129);
        ruta1[21] = new LatLng(23.74536, -99.12628);
        ruta1[22] = new LatLng(23.7461, -99.12882);
        ruta1[23] = new LatLng(23.75023, -99.12888);
        ruta1[24] = new LatLng(23.7491, -99.13917);

        //RUTA 2 - SAN LUISITO - NACO X27
        ruta2 = new LatLng[25];
        ruta2[0] = new LatLng(23.76604, -99.15972);
        ruta2[1] = new LatLng(23.7649, -99.15851);
        ruta2[2] = new LatLng(23.76562, -99.15793);
        ruta2[3] = new LatLng(23.76269, -99.15722);
        ruta2[4] = new LatLng(23.76107, -99.15693);
        ruta2[5] = new LatLng(23.76152, -99.15639);
        ruta2[6] = new LatLng(23.76147, -99.15404);
        ruta2[7] = new LatLng(23.76092, -99.15515);
        ruta2[8] = new LatLng(23.75959, -99.1543);
        ruta2[9] = new LatLng(23.75912, -99.14945);
        ruta2[10] = new LatLng(23.75507, -99.15142);
        ruta2[11] = new LatLng(23.75342, -99.15229);
        ruta2[12] = new LatLng(23.75318, -99.15993);
        ruta2[13] = new LatLng(23.75254, -99.15707);
        ruta2[14] = new LatLng(23.75038, -99.15731);
        ruta2[15] = new LatLng(23.74698, -99.16109);
        ruta2[16] = new LatLng(23.7368, -99.16388);
        ruta2[17] = new LatLng(23.73082, -99.16296);
        ruta2[18] = new LatLng(23.72926, -99.15775);
        ruta2[19] = new LatLng(23.72788, -99.14876);
        ruta2[20] = new LatLng(23.75278, -99.0964);
        ruta2[21] = new LatLng(23.76008, -99.106);
        ruta2[22] = new LatLng(23.75751, -99.10928);
        ruta2[23] = new LatLng(23.75432, -99.10666);
        ruta2[24] = new LatLng(23.74758, -99.10047);

        // RUTA 3 - MARIANO MATAMOROS - COCA-COLA
        ruta3 = new LatLng[25];
        ruta3[0] = new LatLng(23.718476,-99.175030);
        ruta3[1] = new LatLng(23.73002, -99.17824);
        ruta3[2] = new LatLng(23.73183, -99.17219);
        ruta3[3] = new LatLng(23.73226, -99.169);
        ruta3[4] = new LatLng(23.72986, -99.16885);
        ruta3[5] = new LatLng(23.72854, -99.16303);
        ruta3[6] = new LatLng(23.72543, -99.16065);
        ruta3[7] = new LatLng(23.72666, -99.15905);
        ruta3[8] = new LatLng(23.72717, -99.15658);
        ruta3[9] = new LatLng(23.72629, -99.15017);
        ruta3[10] = new LatLng(23.72897, -99.13985);
        ruta3[11] = new LatLng(23.73166, -99.13956);
        ruta3[12] = new LatLng(23.73147, -99.13712);
        ruta3[13] = new LatLng(23.73612, -99.13456);
        ruta3[14] = new LatLng(23.73723, -99.13253);
        ruta3[15] = new LatLng(23.74525, -99.13296);
        ruta3[16] = new LatLng(23.74516, -99.13063);
        ruta3[17] = new LatLng(23.75031, -99.13003);
        ruta3[18] = new LatLng(23.76029, -99.11727);
        ruta3[19] = new LatLng(23.7618, -99.11993);
        ruta3[20] = new LatLng(23.75971, -99.12078);
        ruta3[21] = new LatLng(23.76204, -99.12058);
        ruta3[22] = new LatLng(23.76221, -99.12104);
        ruta3[23] = new LatLng(23.76521, -99.12059);
        ruta3[24] = new LatLng(23.752633, -99.099470);

        //RUTA 4 - LAS FLORES - REVOLUCION VERDE
        ruta4 = new LatLng[25];
        ruta4[0] = new LatLng(23.727784, -99.187087);
        ruta4[1] = new LatLng(23.72303, -99.18701);
        ruta4[2] = new LatLng(23.72415, -99.1851);
        ruta4[3] = new LatLng(23.72571, -99.17665);
        ruta4[4] = new LatLng(23.72363, -99.17583);
        ruta4[5] = new LatLng(23.72538, -99.17262);
        ruta4[6] = new LatLng(23.72791, -99.17226);
        ruta4[7] = new LatLng(23.72763, -99.16996);
        ruta4[8] = new LatLng(23.72432, -99.16983);
        ruta4[9] = new LatLng(23.72397, -99.16848);
        ruta4[10] = new LatLng(23.72572, -99.16705);
        ruta4[11] = new LatLng(23.72663, -99.16465);
        ruta4[12] = new LatLng(23.72818, -99.16236);
        ruta4[13] = new LatLng(23.72707, -99.15901);
        ruta4[14] = new LatLng(23.72891, -99.15635);
        ruta4[15] = new LatLng(23.72796, -99.14866);
        ruta4[16] = new LatLng(23.72969, -99.12942);
        ruta4[17] = new LatLng(23.75237, -99.14084);
        ruta4[18] = new LatLng(23.75154, -99.13669);
        ruta4[19] = new LatLng(23.75019, -99.13615);
        ruta4[20] = new LatLng(23.75058, -99.13373);
        ruta4[21] = new LatLng(23.7498, -99.12888);
        ruta4[22] = new LatLng(23.74613, -99.12922);
        ruta4[23] = new LatLng(23.74397, -99.12942);
        ruta4[24] = new LatLng(23.74232, -99.13103);

        //RUTA 6 - LOPEZ PORTILLO - CENTRAL
        ruta6 = new LatLng[25];
        ruta6[0] = new LatLng(23.774876, -99.170543);
        ruta6[1] = new LatLng(23.77645, -99.17109);
        ruta6[2] = new LatLng(23.77936, -99.17011);
        ruta6[3] = new LatLng(23.77854, -99.16979);
        ruta6[4] = new LatLng(23.77562, -99.16896);
        ruta6[5] = new LatLng(23.77485, -99.16805);
        ruta6[6] = new LatLng(23.77455, -99.16405);
        ruta6[7] = new LatLng(23.7736, -99.1689);
        ruta6[8] = new LatLng(23.77229, -99.16185);
        ruta6[9] = new LatLng(23.77146, -99.16316);
        ruta6[10] = new LatLng(23.76488, -99.16403);
        ruta6[11] = new LatLng(23.76399, -99.16397);
        ruta6[12] = new LatLng(23.76293, -99.16432);
        ruta6[13] = new LatLng(23.76122, -99.16627);
        ruta6[14] = new LatLng(23.76116, -99.1653);
        ruta6[15] = new LatLng(23.76048, -99.16288);
        ruta6[16] = new LatLng(23.75965, -99.16538);
        ruta6[17] = new LatLng(23.75316, -99.16325);
        ruta6[18] = new LatLng(23.75222, -99.16788);
        ruta6[19] = new LatLng(23.75218, -99.15486);
        ruta6[20] = new LatLng(23.74029, -99.15337);
        ruta6[21] = new LatLng(23.73446, -99.14495);
        ruta6[22] = new LatLng(23.73323, -99.13715);
        ruta6[23] = new LatLng(23.73527, -99.13663);
        ruta6[24] = new LatLng(23.736220, -99.134961);

        //RUTA 7 - Unidad Modelo - Central
        ruta7 = new LatLng[25];
        ruta7[0] = new LatLng(23.72793, -99.13547);
        ruta7[1] = new LatLng(23.72916, -99.12743);
        ruta7[2] = new LatLng(23.73072, -99.12497);
        ruta7[3] = new LatLng(23.73108, -99.1293);
        ruta7[4] = new LatLng(23.73735, -99.13023);
        ruta7[5] = new LatLng(23.73579, -99.13198);
        ruta7[6] = new LatLng(23.73691, -99.13261);
        ruta7[7] = new LatLng(23.74052, -99.14322);
        ruta7[8] = new LatLng(23.75063, -99.14233);
        ruta7[9] = new LatLng(23.74612, -99.14335);
        ruta7[10] = new LatLng(23.73908, -99.14035);
        ruta7[11] = new LatLng(23.72915, -99.14053);
        ruta7[12] = new LatLng(23.72649, -99.15018);
        ruta7[13] = new LatLng(23.72598, -99.15353);
        ruta7[14] = new LatLng(23.72083, -99.16442);
        ruta7[15] = new LatLng(23.71816, -99.16136);
        ruta7[16] = new LatLng(23.70801, -99.16138);
        ruta7[17] = new LatLng(23.70718, -99.16378);
        ruta7[18] = new LatLng(23.7082, -99.16459);
        ruta7[19] = new LatLng(23.70735, -99.16494);
        ruta7[20] = new LatLng(23.70676, -99.16422);
        ruta7[21] = new LatLng(23.70413, -99.16295);
        ruta7[22] = new LatLng(23.70413, -99.16295);
        ruta7[23] = new LatLng(23.70413, -99.16295);
        ruta7[24] = new LatLng(23.70413, -99.16295);

        //RUTA 8 - HORACIO TERAN - CENTRAL
        ruta8 = new LatLng[25];
        ruta8[0] = new LatLng(23.69729, -99.08938);
        ruta8[1] = new LatLng(23.70201, -99.09386);
        ruta8[2] = new LatLng(23.71891, -99.08608);
        ruta8[3] = new LatLng(23.71894, -99.11157);
        ruta8[4] = new LatLng(23.71587, -99.11207);
        ruta8[5] = new LatLng(23.71948, -99.11581);
        ruta8[6] = new LatLng(23.72013, -99.1308);
        ruta8[7] = new LatLng(23.72091, -99.1314);
        ruta8[8] = new LatLng(23.72096, -99.13199);
        ruta8[9] = new LatLng(23.71793, -99.14558);
        ruta8[10] = new LatLng(23.71921, -99.15151);
        ruta8[11] = new LatLng(23.71967, -99.14693);
        ruta8[12] = new LatLng(23.72952, -99.14411);
        ruta8[13] = new LatLng(23.72974, -99.1295);
        ruta8[14] = new LatLng(23.73769, -99.13174);
        ruta8[15] = new LatLng(23.73774, -99.13287);
        ruta8[16] = new LatLng(23.73847, -99.13358);
        ruta8[17] = new LatLng(23.74047, -99.13243);
        ruta8[18] = new LatLng(23.72932, -99.12977);
        ruta8[19] = new LatLng(23.72898, -99.12771);
        ruta8[20] = new LatLng(23.75194, -99.09595);
        ruta8[21] = new LatLng(23.76405, -99.11357);
        ruta8[22] = new LatLng(23.76176, -99.11274);
        ruta8[23] = new LatLng(23.7471, -99.10002);
        ruta8[24] = new LatLng(23.7471, -99.10002);

        //RUTA 10 - SOSA - TREVIÑO
        ruta10 = new LatLng[25];
        ruta10[0] = new LatLng(23.69878, -99.14206);
        ruta10[1] = new LatLng(23.70081, -99.14327);
        ruta10[2] = new LatLng(23.70297, -99.14132);
        ruta10[3] = new LatLng(23.7064, -99.14126);
        ruta10[4] = new LatLng(23.70726, -99.14215);
        ruta10[5] = new LatLng(23.70433, -99.13243);
        ruta10[6] = new LatLng(23.70894, -99.14317);
        ruta10[7] = new LatLng(23.71178, -99.14256);
        ruta10[8] = new LatLng(23.71285, -99.13853);
        ruta10[9] = new LatLng(23.71384, -99.13818);
        ruta10[10] = new LatLng(23.7165, -99.1328);
        ruta10[11] = new LatLng(23.71785, -99.14411);
        ruta10[12] = new LatLng(23.72091, -99.14374);
        ruta10[13] = new LatLng(23.72088, -99.13514);
        ruta10[14] = new LatLng(23.72168, -99.13413);
        ruta10[15] = new LatLng(23.72488, -99.13252);
        ruta10[16] = new LatLng(23.72502, -99.13507);
        ruta10[17] = new LatLng(23.72555, -99.1446);
        ruta10[18] = new LatLng(23.72575, -99.14072);
        ruta10[19] = new LatLng(23.72687, -99.14456);
        ruta10[20] = new LatLng(23.72951, -99.14407);
        ruta10[21] = new LatLng(23.72964, -99.12932);
        ruta10[22] = new LatLng(23.73644, -99.13279);
        ruta10[23] = new LatLng(23.7417, -99.15054);
        ruta10[24] = new LatLng(23.76224, -99.14818);

        //RUTA 11 - LABORCITAS
        ruta11 = new LatLng[25];
        ruta11[0] = new LatLng(23.81347, -99.145715);
        ruta11[1] = new LatLng(23.81866, -99.1241);
        ruta11[2] = new LatLng(23.75104, -99.14083);
        ruta11[3] = new LatLng(23.72925, -99.12975);
        ruta11[4] = new LatLng(23.72951, -99.14339);
        ruta11[5] = new LatLng(23.72951, -99.14339);
        ruta11[6] = new LatLng(23.72951, -99.14339);
        ruta11[7] = new LatLng(23.72951, -99.14339);
        ruta11[8] = new LatLng(23.72951, -99.14339);
        ruta11[9] = new LatLng(23.72951, -99.14339);
        ruta11[10] = new LatLng(23.72951, -99.14339);
        ruta11[11] = new LatLng(23.72951, -99.14339);
        ruta11[12] = new LatLng(23.72951, -99.14339);
        ruta11[13] = new LatLng(23.72951, -99.14339);
        ruta11[14] = new LatLng(23.72951, -99.14339);
        ruta11[15] = new LatLng(23.72951, -99.14339);
        ruta11[16] = new LatLng(23.72951, -99.14339);
        ruta11[17] = new LatLng(23.72951, -99.14339);
        ruta11[18] = new LatLng(23.72951, -99.14339);
        ruta11[19] = new LatLng(23.72951, -99.14339);
        ruta11[20] = new LatLng(23.72951, -99.14339);
        ruta11[21] = new LatLng(23.72951, -99.14339);
        ruta11[22] = new LatLng(23.72951, -99.14339);
        ruta11[23] = new LatLng(23.72951, -99.14339);
        ruta11[24] = new LatLng(23.72951, -99.14339);


        //RUTA 12 - BLANCO - COL. LA PRESITA
        ruta12 = new LatLng[25];
        ruta12[0] = new LatLng(23.77776, -99.1617);
        ruta12[1] = new LatLng(23.77167, -99.16071);
        ruta12[2] = new LatLng(23.77156, -99.15431);
        ruta12[3] = new LatLng(23.76883, -99.15677);
        ruta12[4] = new LatLng(23.76811, -99.15842);
        ruta12[5] = new LatLng(23.76713, -99.16103);
        ruta12[6] = new LatLng(23.75344, -99.15979);
        ruta12[7] = new LatLng(23.75188, -99.14937);
        ruta12[8] = new LatLng(23.73899, -99.14861);
        ruta12[9] = new LatLng(23.73686, -99.14559);
        ruta12[10] = new LatLng(23.72943, -99.14612);
        ruta12[11] = new LatLng(23.72953, -99.12912);
        ruta12[12] = new LatLng(23.74916, -99.1391);
        ruta12[13] = new LatLng(23.74725, -99.1363);
        ruta12[14] = new LatLng(23.74734, -99.13308);
        ruta12[15] = new LatLng(23.75189, -99.13295);
        ruta12[16] = new LatLng(23.75239, -99.13434);
        ruta12[17] = new LatLng(23.75577, -99.13405);
        ruta12[18] = new LatLng(23.75483, -99.13176);
        ruta12[19] = new LatLng(23.75726, -99.13145);
        ruta12[20] = new LatLng(23.75754, -99.13366);
        ruta12[21] = new LatLng(23.75974, -99.1359);
        ruta12[22] = new LatLng(23.75916, -99.12968);
        ruta12[23] = new LatLng(23.7588, -99.12564);
        ruta12[24] = new LatLng(23.75485, -99.13411);

        //RUTA 13 - TAMATAN - SATELITE
        ruta13 = new LatLng[25];
        ruta13[0] = new LatLng(23.70796, -99.18202);
        ruta13[1] = new LatLng(23.71465, -99.17866);
        ruta13[2] = new LatLng(23.7289, -99.15627);
        ruta13[3] = new LatLng(23.72789, -99.14874);
        ruta13[4] = new LatLng(23.72954, -99.12912);
        ruta13[5] = new LatLng(23.73572, -99.12986);
        ruta13[6] = new LatLng(23.73572, -99.12839);
        ruta13[7] = new LatLng(23.7351, -99.11469);
        ruta13[8] = new LatLng(23.74173, -99.1196);
        ruta13[9] = new LatLng(23.74129, -99.12086);
        ruta13[10] = new LatLng(23.74555, -99.12367);
        ruta13[11] = new LatLng(23.74584, -99.12269);
        ruta13[12] = new LatLng(23.74421, -99.11839);
        ruta13[13] = new LatLng(23.74355, -99.11762);
        ruta13[14] = new LatLng(23.74582, -99.11916);
        ruta13[15] = new LatLng(23.74765, -99.11865);
        ruta13[16] = new LatLng(23.7474, -99.11482);
        ruta13[17] = new LatLng(23.75155, -99.11433);
        ruta13[18] = new LatLng(23.75169, -99.11197);
        ruta13[19] = new LatLng(23.74906, -99.11002);
        ruta13[20] = new LatLng(23.75055, -99.10882);
        ruta13[21] = new LatLng(23.75659, -99.11381);
        ruta13[22] = new LatLng(23.75866, -99.11094);
        ruta13[23] = new LatLng(23.75199, -99.09621);
        ruta13[24] = new LatLng(23.73857, -99.10582);

        //RUTA 14 - ECHEVERRIA - CENTRAL
        ruta14 = new LatLng[25];
        ruta14[0] = new LatLng(23.70925, -99.18648);
        ruta14[1] = new LatLng(23.71081, -99.18953);
        ruta14[2] = new LatLng(23.71177, -99.18888);
        ruta14[3] = new LatLng(23.7096, -99.18492);
        ruta14[4] = new LatLng(23.7089, -99.18433);
        ruta14[5] = new LatLng(23.7089, -99.18345);
        ruta14[6] = new LatLng(23.71358, -99.18094);
        ruta14[7] = new LatLng(23.71291, -99.18023);
        ruta14[8] = new LatLng(23.71263, -99.17971);
        ruta14[9] = new LatLng(23.71448, -99.17843);
        ruta14[10] = new LatLng(23.73501, -99.15556);
        ruta14[11] = new LatLng(23.7335, -99.13686);
        ruta14[12] = new LatLng(23.7363, -99.13586);
        ruta14[13] = new LatLng(23.73773, -99.13373);
        ruta14[14] = new LatLng(23.7375, -99.13225);
        ruta14[15] = new LatLng(23.73972, -99.13342);
        ruta14[16] = new LatLng(23.73639, -99.13047);
        ruta14[17] = new LatLng(23.73618, -99.13484);
        ruta14[18] = new LatLng(23.73618, -99.13484);
        ruta14[19] = new LatLng(23.73618, -99.13484);
        ruta14[20] = new LatLng(23.73618, -99.13484);
        ruta14[21] = new LatLng(23.73618, -99.13484);
        ruta14[22] = new LatLng(23.73618, -99.13484);
        ruta14[23] = new LatLng(23.73618, -99.13484);
        ruta14[24] = new LatLng(23.73618, -99.13484);

        //RUTA 15 - Azteca- Tamatan- Compuertas- Liberal
        ruta15 = new LatLng[25];
        ruta15[0] = new LatLng(23.70527, -99.18331);
        ruta15[1] = new LatLng(23.70655, -99.18258);
        ruta15[2] = new LatLng(23.70636, -99.18171);
        ruta15[3] = new LatLng(23.71016, -99.17957);
        ruta15[4] = new LatLng(23.70984, -99.17853);
        ruta15[5] = new LatLng(23.71058, -99.17641);
        ruta15[6] = new LatLng(23.71342, -99.17283);
        ruta15[7] = new LatLng(23.71768, -99.16786);
        ruta15[8] = new LatLng(23.72378, -99.16047);
        ruta15[9] = new LatLng(23.72892, -99.15638);
        ruta15[10] = new LatLng(23.72809, -99.14851);
        ruta15[11] = new LatLng(23.72798, -99.13558);
        ruta15[12] = new LatLng(23.72966, -99.12934);
        ruta15[13] = new LatLng(23.73574, -99.12983);
        ruta15[14] = new LatLng(23.73759, -99.12301);
        ruta15[15] = new LatLng(23.74019, -99.11829);
        ruta15[16] = new LatLng(23.7366, -99.11547);
        ruta15[17] = new LatLng(23.73919, -99.11142);
        ruta15[18] = new LatLng(23.74119, -99.11242);
        ruta15[19] = new LatLng(23.74383, -99.10626);
        ruta15[20] = new LatLng(23.7427, -99.1053);
        ruta15[21] = new LatLng(23.74461, -99.10175);
        ruta15[22] = new LatLng(23.74672, -99.10237);
        ruta15[23] = new LatLng(23.75426, -99.09764);
        ruta15[24] = new LatLng(23.75426, -99.09764);

        //RUTA 16 - COL. PRIMAVERA - UAT - CENTRAL
        ruta16 = new LatLng[25];
        ruta16[0] = new LatLng(23.75578, -99.09838);
        ruta16[1] = new LatLng(23.75185, -99.09611);
        ruta16[2] = new LatLng(23.72955, -99.12916);
        ruta16[3] = new LatLng(23.73753, -99.13108);
        ruta16[4] = new LatLng(23.73966, -99.13344);
        ruta16[5] = new LatLng(23.73139, -99.13046);
        ruta16[6] = new LatLng(23.72905, -99.14539);
        ruta16[7] = new LatLng(23.72115, -99.1469);
        ruta16[8] = new LatLng(23.71919, -99.15153);
        ruta16[9] = new LatLng(23.71743, -99.14481);
        ruta16[10] = new LatLng(23.71828, -99.12789);
        ruta16[11] = new LatLng(23.71752, -99.12768);
        ruta16[12] = new LatLng(23.71731, -99.12522);
        ruta16[13] = new LatLng(23.71652, -99.12469);
        ruta16[14] = new LatLng(23.71659, -99.12136);
        ruta16[15] = new LatLng(23.71667, -99.11989);
        ruta16[16] = new LatLng(23.71935, -99.11577);
        ruta16[17] = new LatLng(23.71205, -99.11285);
        ruta16[18] = new LatLng(23.71057, -99.11237);
        ruta16[19] = new LatLng(23.70765, -99.12235);
        ruta16[20] = new LatLng(23.71137, -99.13185);
        ruta16[21] = new LatLng(23.71127, -99.13396);
        ruta16[22] = new LatLng(23.71348, -99.13401);
        ruta16[23] = new LatLng(23.71385, -99.13433);
        ruta16[24] = new LatLng(23.71385, -99.13433);

        //RUTA 17 - AZTECA - LIBERTAD - CUARTEL
        ruta17 = new LatLng[25];
        ruta17[0] = new LatLng(23.77988, -99.16759);
        ruta17[1] = new LatLng(23.77826, -99.16593);
        ruta17[2] = new LatLng(23.77414, -99.16563);
        ruta17[3] = new LatLng(23.77353, -99.16857);
        ruta17[4] = new LatLng(23.77246, -99.16186);
        ruta17[5] = new LatLng(23.77191, -99.16317);
        ruta17[6] = new LatLng(23.77195, -99.16634);
        ruta17[7] = new LatLng(23.76995, -99.16518);
        ruta17[8] = new LatLng(23.76297, -99.16809);
        ruta17[9] = new LatLng(23.75315, -99.16325);
        ruta17[10] = new LatLng(23.7526, -99.16515);
        ruta17[11] = new LatLng(23.73889, -99.16377);
        ruta17[12] = new LatLng(23.73719, -99.15375);
        ruta17[13] = new LatLng(23.72865, -99.15443);
        ruta17[14] = new LatLng(23.72786, -99.14878);
        ruta17[15] = new LatLng(23.7296, -99.12917);
        ruta17[16] = new LatLng(23.73786, -99.12989);
        ruta17[17] = new LatLng(23.73968, -99.12415);
        ruta17[18] = new LatLng(23.74212, -99.12455);
        ruta17[19] = new LatLng(23.73893, -99.12081);
        ruta17[20] = new LatLng(23.742, -99.11606);
        ruta17[21] = new LatLng(23.74643, -99.10428);
        ruta17[22] = new LatLng(23.74905, -99.10187);
        ruta17[23] = new LatLng(23.75745, -99.10087);
        ruta17[24] = new LatLng(23.75182, -99.09612);

        //RUTA 18 - SAN MARCOS - CENTRAL
        ruta18 = new LatLng[25];
        ruta18[0] = new LatLng(23.70209, -99.18029);
        ruta18[1] = new LatLng(23.70409, -99.17723);
        ruta18[2] = new LatLng(23.70498, -99.17756);
        ruta18[3] = new LatLng(23.72558, -99.15791);
        ruta18[4] = new LatLng(23.7248, -99.15353);
        ruta18[5] = new LatLng(23.72433, -99.15189);
        ruta18[6] = new LatLng(23.72957, -99.12914);
        ruta18[7] = new LatLng(23.73742, -99.13055);
        ruta18[8] = new LatLng(23.73962, -99.13346);
        ruta18[9] = new LatLng(23.73841, -99.13067);
        ruta18[10] = new LatLng(23.72859, -99.12972);
        ruta18[11] = new LatLng(23.72859, -99.12972);
        ruta18[12] = new LatLng(23.72859, -99.12972);
        ruta18[13] = new LatLng(23.72859, -99.12972);
        ruta18[14] = new LatLng(23.72859, -99.12972);
        ruta18[15] = new LatLng(23.72859, -99.12972);
        ruta18[16] = new LatLng(23.72859, -99.12972);
        ruta18[17] = new LatLng(23.72859, -99.12972);
        ruta18[18] = new LatLng(23.72859, -99.12972);
        ruta18[19] = new LatLng(23.72859, -99.12972);
        ruta18[20] = new LatLng(23.72859, -99.12972);
        ruta18[21] = new LatLng(23.72859, -99.12972);
        ruta18[22] = new LatLng(23.72859, -99.12972);
        ruta18[23] = new LatLng(23.72859, -99.12972);
        ruta18[24] = new LatLng(23.72859, -99.12972);

        //RUTA 19 - CONALEP - BULEVARD - CENTRAL
        ruta19 = new LatLng[25];
        ruta19[0] = new LatLng(23.77903, -99.14631);
        ruta19[1] = new LatLng(23.77122, -99.14743);
        ruta19[2] = new LatLng(23.76708, -99.15205);
        ruta19[3] = new LatLng(23.76684, -99.15585);
        ruta19[4] = new LatLng(23.77155, -99.15406);
        ruta19[5] = new LatLng(23.77144, -99.15597);
        ruta19[6] = new LatLng(23.7703, -99.15643);
        ruta19[7] = new LatLng(23.76796, -99.15841);
        ruta19[8] = new LatLng(23.76753, -99.16128);
        ruta19[9] = new LatLng(23.74734, -99.16068);
        ruta19[10] = new LatLng(23.73098, -99.16243);
        ruta19[11] = new LatLng(23.73038, -99.15763);
        ruta19[12] = new LatLng(23.72906, -99.15752);
        ruta19[13] = new LatLng(23.72789, -99.14875);
        ruta19[14] = new LatLng(23.72955, -99.12912);
        ruta19[15] = new LatLng(23.74135, -99.13172);
        ruta19[16] = new LatLng(23.74292, -99.13082);
        ruta19[17] = new LatLng(23.74395, -99.13163);
        ruta19[18] = new LatLng(23.74694, -99.13217);
        ruta19[19] = new LatLng(23.75188, -99.13292);
        ruta19[20] = new LatLng(23.75224, -99.13436);
        ruta19[21] = new LatLng(23.75439, -99.13395);
        ruta19[22] = new LatLng(23.75463, -99.13175);
        ruta19[23] = new LatLng(23.75714, -99.13288);
        ruta19[24] = new LatLng(23.76069, -99.13011);

        //RUTA 20 - ESTRELLA - CENTRAL
        ruta20 = new LatLng[25];
        ruta20[0] = new LatLng(23.7859, -99.17315);
        ruta20[1] = new LatLng(23.78448, -99.17366);
        ruta20[2] = new LatLng(23.78306, -99.17339);
        ruta20[3] = new LatLng(23.78312, -99.17181);
        ruta20[4] = new LatLng(23.78273, -99.17064);
        ruta20[5] = new LatLng(23.7789, -99.17097);
        ruta20[6] = new LatLng(23.77934, -99.17261);
        ruta20[7] = new LatLng(23.77918, -99.17361);
        ruta20[8] = new LatLng(23.77724, -99.17568);
        ruta20[9] = new LatLng(23.77534, -99.17319);
        ruta20[10] = new LatLng(23.77131, -99.17206);
        ruta20[11] = new LatLng(23.77054, -99.16729);
        ruta20[12] = new LatLng(23.76393, -99.1686);
        ruta20[13] = new LatLng(23.7526, -99.16499);
        ruta20[14] = new LatLng(23.75077, -99.16242);
        ruta20[15] = new LatLng(23.74845, -99.16259);
        ruta20[16] = new LatLng(23.74742, -99.16056);
        ruta20[17] = new LatLng(23.74461, -99.16332);
        ruta20[18] = new LatLng(23.73897, -99.16263);
        ruta20[19] = new LatLng(23.73811, -99.15364);
        ruta20[20] = new LatLng(23.72862, -99.15425);
        ruta20[21] = new LatLng(23.72789, -99.14874);
        ruta20[22] = new LatLng(23.72964, -99.12931);
        ruta20[23] = new LatLng(23.73735, -99.13038);
        ruta20[24] = new LatLng(23.7357, -99.13053);

        //RUTA 21 - Col. Vista Hermosa- Col Diana Launra Riojas De Colosio
        ruta21 = new LatLng[25];
        ruta21[0] = new LatLng(23.73938, -99.10138);
        ruta21[1] = new LatLng(23.73695, -99.0996);
        ruta21[2] = new LatLng(23.73445, -99.10288);
        ruta21[3] = new LatLng(23.73309, -99.10253);
        ruta21[4] = new LatLng(23.73261, -99.10357);
        ruta21[5] = new LatLng(23.73513, -99.10655);
        ruta21[6] = new LatLng(23.73216, -99.11031);
        ruta21[7] = new LatLng(23.72734, -99.10668);
        ruta21[8] = new LatLng(23.7266, -99.10776);
        ruta21[9] = new LatLng(23.73502, -99.11511);
        ruta21[10] = new LatLng(23.73521, -99.11987);
        ruta21[11] = new LatLng(23.73383, -99.12033);
        ruta21[12] = new LatLng(23.73418, -99.13028);
        ruta21[13] = new LatLng(23.73716, -99.13044);
        ruta21[14] = new LatLng(23.7282, -99.13017);
        ruta21[15] = new LatLng(23.72715, -99.14992);
        ruta21[16] = new LatLng(23.7282, -99.15659);
        ruta21[17] = new LatLng(23.73245, -99.15622);
        ruta21[18] = new LatLng(23.73083, -99.15775);
        ruta21[19] = new LatLng(23.73081, -99.1689);
        ruta21[20] = new LatLng(23.73409, -99.16938);
        ruta21[21] = new LatLng(23.7337, -99.17677);
        ruta21[22] = new LatLng(23.73033, -99.18275);
        ruta21[23] = new LatLng(23.73033, -99.18275);
        ruta21[24] = new LatLng(23.73033, -99.18275);

        //RUTA 22 - Amalia G de Castillo Ledon- Central
        ruta22 = new LatLng[25];
        ruta22[0] = new LatLng(23.70436, -99.15825);
        ruta22[1] = new LatLng(23.70616, -99.1604);
        ruta22[2] = new LatLng(23.70696, -99.15878);
        ruta22[3] = new LatLng(23.70537, -99.15736);
        ruta22[4] = new LatLng(23.7058, -99.15543);
        ruta22[5] = new LatLng(23.71086, -99.15684);
        ruta22[6] = new LatLng(23.71398, -99.15634);
        ruta22[7] = new LatLng(23.71943, -99.15775);
        ruta22[8] = new LatLng(23.72035, -99.15632);
        ruta22[9] = new LatLng(23.72056, -99.15273);
        ruta22[10] = new LatLng(23.7216, -99.15298);
        ruta22[11] = new LatLng(23.72521, -99.15485);
        ruta22[12] = new LatLng(23.72493, -99.1535);
        ruta22[13] = new LatLng(23.72419, -99.152);
        ruta22[14] = new LatLng(23.72682, -99.14973);
        ruta22[15] = new LatLng(23.72916, -99.14652);
        ruta22[16] = new LatLng(23.72851, -99.13838);
        ruta22[17] = new LatLng(23.73012, -99.12975);
        ruta22[18] = new LatLng(23.73737, -99.13037);
        ruta22[19] = new LatLng(23.73737, -99.13037);
        ruta22[20] = new LatLng(23.73737, -99.13037);
        ruta22[21] = new LatLng(23.73737, -99.13037);
        ruta22[22] = new LatLng(23.73737, -99.13037);
        ruta22[23] = new LatLng(23.73737, -99.13037);
        ruta22[24] = new LatLng(23.73737, -99.13037);

        //RUTA 23 - CORREGIDORA - NACOZARY
        ruta23 = new LatLng[25];
        ruta23[0] = new LatLng(23.75032, -99.1705);
        ruta23[1] = new LatLng(23.74927, -99.16804);
        ruta23[2] = new LatLng(23.74906, -99.16653);
        ruta23[3] = new LatLng(23.74848, -99.16679);
        ruta23[4] = new LatLng(23.74753, -99.16702);
        ruta23[5] = new LatLng(23.74196, -99.16646);
        ruta23[6] = new LatLng(23.74056, -99.16533);
        ruta23[7] = new LatLng(23.73762, -99.16965);
        ruta23[8] = new LatLng(23.73715, -99.16904);
        ruta23[9] = new LatLng(23.73822, -99.16617);
        ruta23[10] = new LatLng(23.73067, -99.16538);
        ruta23[11] = new LatLng(23.73065, -99.16241);
        ruta23[12] = new LatLng(23.72833, -99.16205);
        ruta23[13] = new LatLng(23.72572, -99.1568);
        ruta23[14] = new LatLng(23.72889, -99.15622);
        ruta23[15] = new LatLng(23.72786, -99.14875);
        ruta23[16] = new LatLng(23.72962, -99.12925);
        ruta23[17] = new LatLng(23.73569, -99.12983);
        ruta23[18] = new LatLng(23.73547, -99.11983);
        ruta23[19] = new LatLng(23.737, -99.11975);
        ruta23[20] = new LatLng(23.73802, -99.12);
        ruta23[21] = new LatLng(23.73896, -99.11922);
        ruta23[22] = new LatLng(23.7391, -99.12127);
        ruta23[23] = new LatLng(23.74584, -99.11812);
        ruta23[24] = new LatLng(23.75625, -99.11705);

        //RUTA 24 - Cumbres-Central
        ruta24 = new LatLng[25];
        ruta24[0] = new LatLng(23.70714, -99.17132);
        ruta24[1] = new LatLng(23.70751, -99.17095);
        ruta24[2] = new LatLng(23.70834, -99.1713);
        ruta24[3] = new LatLng(23.70886, -99.17002);
        ruta24[4] = new LatLng(23.70772, -99.16906);
        ruta24[5] = new LatLng(23.70855, -99.16754);
        ruta24[6] = new LatLng(23.70863, -99.16568);
        ruta24[7] = new LatLng(23.70999, -99.16554);
        ruta24[8] = new LatLng(23.71058, -99.1672);
        ruta24[9] = new LatLng(23.71098, -99.16664);
        ruta24[10] = new LatLng(23.71088, -99.16411);
        ruta24[11] = new LatLng(23.71149, -99.16396);
        ruta24[12] = new LatLng(23.71183, -99.16294);
        ruta24[13] = new LatLng(23.71255, -99.16303);
        ruta24[14] = new LatLng(23.71237, -99.16458);
        ruta24[15] = new LatLng(23.71281, -99.16674);
        ruta24[16] = new LatLng(23.71345, -99.16699);
        ruta24[17] = new LatLng(23.71508, -99.16787);
        ruta24[18] = new LatLng(23.71637, -99.16692);
        ruta24[19] = new LatLng(23.71786, -99.16762);
        ruta24[20] = new LatLng(23.72556, -99.15764);
        ruta24[21] = new LatLng(23.72511, -99.15116);
        ruta24[22] = new LatLng(23.72862, -99.13905);
        ruta24[23] = new LatLng(23.73261, -99.13045);
        ruta24[24] = new LatLng(223.72872, -99.12968);

        //RUTA 25 - Estudiantil-Central
        ruta25 = new LatLng[25];
        ruta25[0] = new LatLng(23.71378, -99.18824);
        ruta25[1] = new LatLng(23.71136, -99.18224);
        ruta25[2] = new LatLng(23.71357, -99.18095);
        ruta25[3] = new LatLng(23.71288, -99.18072);
        ruta25[4] = new LatLng(23.71263, -99.17973);
        ruta25[5] = new LatLng(23.71458, -99.17855);
        ruta25[6] = new LatLng(23.71192, -99.1748);
        ruta25[7] = new LatLng(23.72551, -99.1572);
        ruta25[8] = new LatLng(23.72509, -99.15121);
        ruta25[9] = new LatLng(23.72957, -99.12917);
        ruta25[10] = new LatLng(23.73723, -99.13041);
        ruta25[11] = new LatLng(23.73566, -99.1308);
        ruta25[12] = new LatLng(23.73606, -99.13653);
        ruta25[13] = new LatLng(23.73413, -99.1368);
        ruta25[14] = new LatLng(23.73529, -99.15704);
        ruta25[15] = new LatLng(23.72591, -99.15833);
        ruta25[16] = new LatLng(23.72534, -99.15908);
        ruta25[17] = new LatLng(23.72534, -99.15908);
        ruta25[18] = new LatLng(23.72534, -99.15908);
        ruta25[19] = new LatLng(23.72534, -99.15908);
        ruta25[20] = new LatLng(23.72534, -99.15908);
        ruta25[21] = new LatLng(23.72534, -99.15908);
        ruta25[22] = new LatLng(23.72534, -99.15908);
        ruta25[23] = new LatLng(23.72534, -99.15908);
        ruta25[24] = new LatLng(23.72534, -99.15908);

        //RUTA 26 -  Col. Simon Torre- Central
        ruta26 = new LatLng[25];
        ruta26[0] = new LatLng(23.71535, -99.18116);
        ruta26[1] = new LatLng(23.71655, -99.18231);
        ruta26[2] = new LatLng(23.71731, -99.18627);
        ruta26[3] = new LatLng(23.71825, -99.18627);
        ruta26[4] = new LatLng(23.71852, -99.18656);
        ruta26[5] = new LatLng(23.72086, -99.18616);
        ruta26[6] = new LatLng(23.72137, -99.18587);
        ruta26[7] = new LatLng(23.72106, -99.18185);
        ruta26[8] = new LatLng(23.72053, -99.18161);
        ruta26[9] = new LatLng(23.71672, -99.17659);
        ruta26[10] = new LatLng(23.71926, -99.17226);
        ruta26[11] = new LatLng(23.72165, -99.17254);
        ruta26[12] = new LatLng(23.72146, -99.17135);
        ruta26[13] = new LatLng(23.71986, -99.17131);
        ruta26[14] = new LatLng(23.71921, -99.1721);
        ruta26[15] = new LatLng(23.7155, -99.17043);
        ruta26[16] = new LatLng(23.72159, -99.16311);
        ruta26[17] = new LatLng(23.72548, -99.15704);
        ruta26[18] = new LatLng(23.72405, -99.15217);
        ruta26[19] = new LatLng(23.72952, -99.12908);
        ruta26[20] = new LatLng(23.73735, -99.13028);
        ruta26[21] = new LatLng(23.7358, -99.1305);
        ruta26[22] = new LatLng(23.72837, -99.12989);
        ruta26[23] = new LatLng(23.72837, -99.12989);
        ruta26[24] = new LatLng(23.72837, -99.12989);

        //RUTA 27 - Ej. SAn Jacinto
        ruta27 = new LatLng[25];
        ruta27[0] = new LatLng(23.7287, -99.14445);
        ruta27[1] = new LatLng(23.72738, -99.13753);
        ruta27[2] = new LatLng(23.72747, -99.12899);
        ruta27[3] = new LatLng(23.73054, -99.12984);
        ruta27[4] = new LatLng(23.73399, -99.1303);
        ruta27[5] = new LatLng(23.73774, -99.13006);
        ruta27[6] = new LatLng(23.74028, -99.13172);
        ruta27[7] = new LatLng(23.7446, -99.13516);
        ruta27[8] = new LatLng(23.7491, -99.13878);
        ruta27[9] = new LatLng(23.75743, -99.14087);
        ruta27[10] = new LatLng(23.77267, -99.13766);
        ruta27[11] = new LatLng(23.79233, -99.13293);
        ruta27[12] = new LatLng(23.90917, -99.09596);
        ruta27[13] = new LatLng(23.90917, -99.09596);
        ruta27[14] = new LatLng(23.90917, -99.09596);
        ruta27[15] = new LatLng(23.90917, -99.09596);
        ruta27[16] = new LatLng(23.90917, -99.09596);
        ruta27[17] = new LatLng(23.90917, -99.09596);
        ruta27[18] = new LatLng(23.90917, -99.09596);
        ruta27[19] = new LatLng(23.90917, -99.09596);
        ruta27[20] = new LatLng(23.90917, -99.09596);
        ruta27[21] = new LatLng(23.90917, -99.09596);
        ruta27[22] = new LatLng(23.90917, -99.09596);
        ruta27[23] = new LatLng(23.90917, -99.09596);
        ruta27[24] = new LatLng(23.90917, -99.09596);

        //RUTA 28 - Ej Manuel Avila Camacho -Victoria
        ruta28 = new LatLng[25];
        ruta28[0] = new LatLng(23.72894, -99.14054);
        ruta28[1] = new LatLng(23.72951, -99.12905);
        ruta28[2] = new LatLng(23.72906, -99.12955);
        ruta28[3] = new LatLng(23.7236, -99.12994);
        ruta28[4] = new LatLng(23.71999, -99.13004);
        ruta28[5] = new LatLng(23.71962, -99.12963);
        ruta28[6] = new LatLng(23.71951, -99.12157);
        ruta28[7] = new LatLng(23.71925, -99.10361);
        ruta28[8] = new LatLng(23.7177, -99.00705);
        ruta28[9] = new LatLng(23.71745, -99.0057);
        ruta28[10] = new LatLng(23.71181, -99.00609);
        ruta28[11] = new LatLng(23.71182, -99.0060);
        ruta28[12] = new LatLng(23.71752, -99.00564);
        ruta28[13] = new LatLng(23.71767, -99.00555);
        ruta28[14] = new LatLng(23.71705, -98.99585);
        ruta28[15] = new LatLng(23.67736, -98.98238);
        ruta28[16] = new LatLng(23.67612, -98.98793);
        ruta28[17] = new LatLng(23.67427, -98.99625);
        ruta28[18] = new LatLng(23.67349, -98.99821);
        ruta28[19] = new LatLng(23.67209, -98.99764);
        ruta28[20] = new LatLng(23.67209, -98.99764);
        ruta28[21] = new LatLng(23.67209, -98.99764);
        ruta28[22] = new LatLng(23.67209, -98.99764);
        ruta28[23] = new LatLng(23.67209, -98.99764);
        ruta28[24] = new LatLng(23.67209, -98.99764);

        //RUTA 29 - Cd. Victoria -Ej. El Olivo
        ruta29 = new LatLng[25];
        ruta29[0] = new LatLng(23.72908, -99.14157);
        ruta29[1] = new LatLng(23.72772, -99.13302);
        ruta29[2] = new LatLng(23.7296, -99.12923);
        ruta29[3] = new LatLng(23.7402, -99.13166);
        ruta29[4] = new LatLng(23.74151, -99.13151);
        ruta29[5] = new LatLng(23.75491, -99.12064);
        ruta29[6] = new LatLng(23.76376, -99.11447);
        ruta29[7] = new LatLng(23.78557, -99.09954);
        ruta29[8] = new LatLng(23.78557, -99.09954);
        ruta29[9] = new LatLng(23.78557, -99.09954);
        ruta29[10] = new LatLng(23.78557, -99.09954);
        ruta29[11] = new LatLng(23.78557, -99.09954);
        ruta29[12] = new LatLng(23.78557, -99.09954);
        ruta29[13] = new LatLng(23.78557, -99.09954);
        ruta29[14] = new LatLng(23.78557, -99.09954);
        ruta29[15] = new LatLng(23.78557, -99.09954);
        ruta29[16] = new LatLng(23.78557, -99.09954);
        ruta29[17] = new LatLng(23.78557, -99.09954);
        ruta29[18] = new LatLng(23.78557, -99.09954);
        ruta29[19] = new LatLng(23.78557, -99.09954);
        ruta29[20] = new LatLng(23.78557, -99.09954);
        ruta29[21] = new LatLng(23.78557, -99.09954);
        ruta29[22] = new LatLng(23.78557, -99.09954);
        ruta29[23] = new LatLng(23.78557, -99.09954);
        ruta29[24] = new LatLng(23.78557, -99.09954);

        //RUTA 31 - EJ. REVOLUCION VERDE
        ruta31 = new LatLng[25];
        ruta31[0] = new LatLng(23.72768, -99.12905);
        ruta31[1] = new LatLng(23.7357, -99.12985);
        ruta31[2] = new LatLng(23.73751, -99.12411);
        ruta31[3] = new LatLng(23.74181, -99.11679);
        ruta31[4] = new LatLng(23.73944, -99.1215);
        ruta31[5] = new LatLng(23.74614, -99.12688);
        ruta31[6] = new LatLng(23.79353, -99.09389);
        ruta31[7] = new LatLng(23.77959, -99.07922);
        ruta31[8] = new LatLng(23.80055, -99.05076);
        ruta31[9] = new LatLng(23.78203, -99.03267);
        ruta31[10] = new LatLng(23.78203, -99.03267);
        ruta31[11] = new LatLng(23.78203, -99.03267);
        ruta31[12] = new LatLng(23.78203, -99.03267);
        ruta31[13] = new LatLng(23.78203, -99.03267);
        ruta31[14] = new LatLng(23.78203, -99.03267);
        ruta31[15] = new LatLng(23.78203, -99.03267);
        ruta31[16] = new LatLng(23.78203, -99.03267);
        ruta31[17] = new LatLng(23.78203, -99.03267);
        ruta31[18] = new LatLng(23.78203, -99.03267);
        ruta31[19] = new LatLng(23.78203, -99.03267);
        ruta31[20] = new LatLng(23.78203, -99.03267);
        ruta31[21] = new LatLng(23.78203, -99.03267);
        ruta31[22] = new LatLng(23.78203, -99.03267);
        ruta31[23] = new LatLng(23.78203, -99.03267);
        ruta31[24] = new LatLng(23.78203, -99.03267);

        //RUTA 34 - SEGURO - CONALEP
        ruta34 = new LatLng[25];
        ruta34[0] = new LatLng(23.72131, -99.14591);
        ruta34[1] = new LatLng(23.72283, -99.14541);
        ruta34[2] = new LatLng(23.72953, -99.14412);
        ruta34[3] = new LatLng(23.72932, -99.14154);
        ruta34[4] = new LatLng(23.74028, -99.14103);
        ruta34[5] = new LatLng(23.74162, -99.15054);
        ruta34[6] = new LatLng(23.77116, -99.14718);
        ruta34[7] = new LatLng(23.7414, -99.15326);
        ruta34[8] = new LatLng(23.73675, -99.15361);
        ruta34[9] = new LatLng(23.73557, -99.1449);
        ruta34[10] = new LatLng(23.71924, -99.15149);
        ruta34[11] = new LatLng(23.7178, -99.14411);
        ruta34[12] = new LatLng(23.72005, -99.14414);
        ruta34[13] = new LatLng(23.72017, -99.14683);
        ruta34[14] = new LatLng(23.72017, -99.14683);
        ruta34[15] = new LatLng(23.72017, -99.14683);
        ruta34[16] = new LatLng(23.72017, -99.14683);
        ruta34[17] = new LatLng(23.72017, -99.14683);
        ruta34[18] = new LatLng(23.72017, -99.14683);
        ruta34[19] = new LatLng(23.72017, -99.14683);
        ruta34[20] = new LatLng(23.72017, -99.14683);
        ruta34[21] = new LatLng(23.72017, -99.14683);
        ruta34[22] = new LatLng(23.72017, -99.14683);
        ruta34[23] = new LatLng(23.72017, -99.14683);
        ruta34[24] = new LatLng(23.72017, -99.14683);

        //RUTA 32 - MERCADO - ESTACION - TAMATAN
        ruta32 = new LatLng[25];
        ruta32[0] = new LatLng(23.71125, -99.18225);
        ruta32[1] = new LatLng(23.72572, -99.1568);
        ruta32[2] = new LatLng(23.73502, -99.15564);
        ruta32[3] = new LatLng(23.73337, -99.13687);
        ruta32[4] = new LatLng(23.73633, -99.13632);
        ruta32[5] = new LatLng(23.73607, -99.13284);
        ruta32[6] = new LatLng(23.73785, -99.13221);
        ruta32[7] = new LatLng(23.73959, -99.13346);
        ruta32[8] = new LatLng(23.73587, -99.13259);
        ruta32[9] = new LatLng(23.73587, -99.13259);
        ruta32[10] = new LatLng(23.73587, -99.13259);
        ruta32[11] = new LatLng(23.73587, -99.13259);
        ruta32[12] = new LatLng(23.73587, -99.13259);
        ruta32[13] = new LatLng(23.73587, -99.13259);
        ruta32[14] = new LatLng(23.73587, -99.13259);
        ruta32[15] = new LatLng(23.73587, -99.13259);
        ruta32[16] = new LatLng(23.73587, -99.13259);
        ruta32[17] = new LatLng(23.73587, -99.13259);
        ruta32[18] = new LatLng(23.73587, -99.13259);
        ruta32[19] = new LatLng(23.73587, -99.13259);
        ruta32[20] = new LatLng(23.73587, -99.13259);
        ruta32[21] = new LatLng(23.73587, -99.13259);
        ruta32[22] = new LatLng(23.73587, -99.13259);
        ruta32[23] = new LatLng(23.73587, -99.13259);
        ruta32[24] = new LatLng(23.73587, -99.13259);

        //RUTA 35 - PORTILLO - TECNOLOGICO
        ruta35 = new LatLng[25];
        ruta35[0] = new LatLng(23.74073, -99.13198);
        ruta35[1] = new LatLng(23.74267, -99.13084);
        ruta35[2] = new LatLng(23.74683, -99.1306);
        ruta35[3] = new LatLng(23.74737, -99.13742);
        ruta35[4] = new LatLng(23.74886, -99.13906);
        ruta35[5] = new LatLng(23.72867, -99.12968);
        ruta35[6] = new LatLng(23.72566, -99.15106);
        ruta35[7] = new LatLng(23.72609, -99.15392);
        ruta35[8] = new LatLng(23.7384, -99.1526);
        ruta35[9] = new LatLng(23.73836, -99.1509);
        ruta35[10] = new LatLng(23.74157, -99.15138);
        ruta35[11] = new LatLng(23.74217, -99.15488);
        ruta35[12] = new LatLng(23.75353, -99.15431);
        ruta35[13] = new LatLng(23.75244, -99.16799);
        ruta35[14] = new LatLng(23.75226, -99.16781);
        ruta35[15] = new LatLng(23.75277, -99.16329);
        ruta35[16] = new LatLng(23.7532, -99.16325);
        ruta35[17] = new LatLng(23.75516, -99.16372);
        ruta35[18] = new LatLng(23.7553, -99.16254);
        ruta35[19] = new LatLng(23.76132, -99.16306);
        ruta35[20] = new LatLng(23.76128, -99.16569);
        ruta35[21] = new LatLng(23.76217, -99.16467);
        ruta35[22] = new LatLng(23.7644, -99.16636);
        ruta35[23] = new LatLng(23.76463, -99.1693);
        ruta35[24] = new LatLng(23.76651, -99.17055);

        //RUTA 30 - CD. VICTORIA - LA SAN JUANA
        ruta30 = new LatLng[25];
        ruta30[0] = new LatLng(23.72616, -99.12874);
        ruta30[1] = new LatLng(23.75285, -99.1415);
        ruta30[2] = new LatLng(23.912, -99.11515);
        ruta30[3] = new LatLng(23.91563, -99.13817);
        ruta30[4] = new LatLng(23.92824, -99.13961);
        ruta30[5] = new LatLng(23.92824, -99.13961);
        ruta30[6] = new LatLng(23.92824, -99.13961);
        ruta30[7] = new LatLng(23.92824, -99.13961);
        ruta30[8] = new LatLng(23.92824, -99.13961);
        ruta30[9] = new LatLng(23.92824, -99.13961);
        ruta30[10] = new LatLng(23.92824, -99.13961);
        ruta30[11] = new LatLng(23.92824, -99.13961);
        ruta30[12] = new LatLng(23.92824, -99.13961);
        ruta30[13] = new LatLng(23.92824, -99.13961);
        ruta30[14] = new LatLng(23.92824, -99.13961);
        ruta30[15] = new LatLng(23.92824, -99.13961);
        ruta30[16] = new LatLng(23.92824, -99.13961);
        ruta30[17] = new LatLng(23.92824, -99.13961);
        ruta30[18] = new LatLng(23.92824, -99.13961);
        ruta30[19] = new LatLng(23.92824, -99.13961);
        ruta30[20] = new LatLng(23.92824, -99.13961);
        ruta30[21] = new LatLng(23.92824, -99.13961);
        ruta30[22] = new LatLng(23.92824, -99.13961);
        ruta30[23] = new LatLng(23.92824, -99.13961);
        ruta30[24] = new LatLng(23.92824, -99.13961);

        //RUTA 51 - VICTORIA - EJ. LA PEÑA
        ruta51 = new LatLng[25];
        ruta51[0] = new LatLng(23.73606, -99.13279);
        ruta51[1] = new LatLng(23.73949, -99.13445);
        ruta51[2] = new LatLng(23.74295, -99.15964);
        ruta51[3] = new LatLng(23.77378, -99.16199);
        ruta51[4] = new LatLng(23.77397, -99.16718);
        ruta51[5] = new LatLng(23.79898, -99.17821);
        ruta51[6] = new LatLng(23.81207, -99.1482);
        ruta51[7] = new LatLng(23.82742, -99.16141);
        ruta51[8] = new LatLng(23.84418, -99.1688);
        ruta51[9] = new LatLng(23.84407, -99.17156);
        ruta51[10] = new LatLng(23.90454, -99.22971);
        ruta51[11] = new LatLng(23.90454, -99.22971);
        ruta51[12] = new LatLng(23.90454, -99.22971);
        ruta51[13] = new LatLng(23.90454, -99.22971);
        ruta51[14] = new LatLng(23.90454, -99.22971);
        ruta51[15] = new LatLng(23.90454, -99.22971);
        ruta51[16] = new LatLng(23.90454, -99.22971);
        ruta51[17] = new LatLng(23.90454, -99.22971);
        ruta51[18] = new LatLng(23.90454, -99.22971);
        ruta51[19] = new LatLng(23.90454, -99.22971);
        ruta51[20] = new LatLng(23.90454, -99.22971);
        ruta51[21] = new LatLng(23.90454, -99.22971);
        ruta51[22] = new LatLng(23.90454, -99.22971);
        ruta51[23] = new LatLng(23.90454, -99.22971);
        ruta51[24] = new LatLng(23.90454, -99.22971);

        calcularDistancias(); //se calculan las distancias de todos los puntos

        // Obtiene la ayuda de mapFragment y notifica cuando el mapa esta listo para usuarse
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        mContext = getApplicationContext();

        //TEXT-VIEW------------------------------------------------------------------------------
        rutaAzul = (TextView) findViewById(R.id.azul);
        rutaRoja = (TextView) findViewById(R.id.rojo);

        //SPINNER--------------------------------------------------------------------------------
        spinnerOpciones = (Spinner) findViewById(R.id.opciones);
        String[] opc = {"-Seleccionar-", "Público", "Caminando"}; //opciones que contendrá el spinner

        // Por defecto utiliza una configuración predeterminada
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,opc);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerOpciones.setAdapter(adapter); //se agrega el adapter al elemento spinner

        //SPINNER RUTAS--------------------------------------------------------------------------------
        spinnerRutas = (Spinner) findViewById(R.id.rutas);
        String[] rts = {"-Seleccionar-", "Ruta-1", "Ruta-2","Ruta-3","Ruta-4","Ruta-6","Ruta7","Ruta-8","Ruta-10","Ruta-11",
        "Ruta-12","Ruta-13","Ruta-14","Ruta-15","Ruta-16","Ruta-17","Ruta-18","Ruta-19","Ruta-20","Ruta-21","Ruta-22",
        "Ruta-23","Ruta-24","Ruta-25","Ruta-26","Ruta-27","Ruta-28","Ruta-29","Ruta-30","Ruta-31","Ruta-32","Ruta-34",
        "Ruta-35","Ruta-51"}; //opciones que contendrá el spinner

        // Por defecto utiliza una configuración predeterminada
        ArrayAdapter adapterR = new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,rts);
        adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRutas.setAdapter(adapterR); //se agrega el adapter al elemento spinner

        //se ejecutará cada que se seleccione una opción del spinner
        spinnerOpciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Integer> posRutas = new ArrayList<Integer>(); //guardará el número de ruta a gráficar
                ArrayList<Integer> posRutasK = new ArrayList<Integer>(); //guardará el número de ruta a gráficar
                double tiempoEsti = 0; //tiempo estimado entre los puntos

                //total de rutas
                LatLng rutasAgregadas[][] = {ruta1,ruta2,ruta3,ruta4,ruta6,ruta7,ruta8,ruta10,ruta11,ruta12,ruta13,ruta14,ruta15,
                        ruta16,ruta17,ruta18,ruta19,ruta20,ruta21,ruta22,ruta23,ruta24,ruta25,ruta26,ruta27,ruta28,ruta29,ruta30,
                        ruta31,ruta32,ruta34,ruta35,ruta51};

                //si se seleccionó la opción de viajar en micro se debe comprobar de que exista un origen y un destino
                if(position==1 && Origen!=null && Destino!=null){
                    double res[][] = new double[numRutas][25];

                    //ejecuta la función que trae todas las distancias por cada punto que existe entre el origen dado
                    for(int r=0;r<rutasAgregadas.length;r++){
                        res[r] = puntoEncontrado(rutasAgregadas[r],Origen);
                    }

                    double menor = res[0][0];
                    int pos=0;
                    int posK=0;

                    //conocer cual de esos puntos es el más cercano al punto de origen
                    for(int l=0;l<numRutas;l++){
                        for(int k=0;k<25;k++){
                            if(res[l][k]<menor){
                                menor = res[l][k];
                                pos = l;
                                posK = k;
                            }
                        }
                    }
                    posRutas.add(pos); //se añada al arreglo
                    posRutasK.add(posK); //se añada al arreglo

                    double res2[][] = new double[numRutas][25];
                    //ejecuta la función que trae todas las distancias por cada punto que existe entre el destino dado
                    for (int r = 0; r < rutasAgregadas.length; r++) {
                        res2[r] = puntoEncontrado(rutasAgregadas[r], Destino);
                    }
                    menor = res2[0][0];
                    pos = 0;
                    int posK2=0, posK3=0;
                    //Se crea este arreglo para saber que rutas son las más cercanas al punto de destino
                    ArrayList<Integer> rutasPosibles = new ArrayList<Integer>();
                    ArrayList<Integer> rutasPosiblesK = new ArrayList<Integer>();
                    for (int l = 0; l < numRutas; l++) {
                        for (int k = 0; k < 25; k++) {
                            if(res2[l][k]<0.01){
                                rutasPosibles.add(l);
                                rutasPosiblesK.add(k);
                            }
                            if (res2[l][k] < menor) {
                                menor = res2[l][k];
                                pos = l;
                                posK3 = k;
                            }
                        }
                    }
                    int bandera=0;
                    int auxRuta = 0;
                    //si entre las rutas más cercanas al punto se encuentra la que ya se seleccionó en el origen se enciende una bandera
                    for(int p=0;p<rutasPosibles.size();p++){
                        if(rutasPosibles.get(p)==posRutas.get(0)){
                            auxRuta = rutasPosibles.get(p);
                            posK2 = rutasPosiblesK.get(p);
                            bandera=1;
                        }
                    }
                    //si la bandera es positiva se agrega la misma ruta y sino se agrega la menor que se encontró
                    if(bandera==1){
                        posRutas.add(auxRuta);
                        posRutasK.add(posK2);
                    }else{
                        posRutas.add(pos);
                        posRutasK.add(posK3);
                    }
                    //si la ruta de origen y destino es la misma se elimina del arrayList
                    if(posRutas.get(0)==posRutas.get(1)){
                        posRutas.remove(1);
                    }

                    //esta bloque pone en el textview de acuerdo al color el nombre de la ruta
                    Log.d("Punto",""+posRutasK.get(0));
                    if(posRutas.size()==1){
                        rutaAzul.setText(nomRutas[posRutas.get(0)][0]);
                        tiempoEsti = tiempoEstimado(posRutas.get(0),posRutasK.get(0));
                    }else{
                        rutaAzul.setText(nomRutas[posRutas.get(0)][0]);
                        tiempoEsti = tiempoEstimado(posRutas.get(0),posRutasK.get(0));
                        rutaRoja.setText(nomRutas[posRutas.get(1)][0]);
                        tiempoEsti = tiempoEsti + tiempoEstimado(posRutas.get(1),0);
                    }

                    //URL obtendrá la cadena que utilizará el método encargado de gráficar
                    URL = new String[posRutas.size()];
                    URL = getMapsApiDirectionsUrl(posRutas, posRutasK);
                    for(int h=0;h<URL.length;h++){
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(URL[h]);
                    }

                    //------------------------------Mostrar el camino que se hará caminando----------------------------------
                    llamadaMetodo=2; //poner la linea de diferente color
                    String rutaCaminando; //obtendrá los puntos
                    //al método se le pasa el punto de Origen y el punto más cercano de la ruta como destino
                    rutaCaminando = getMapsApiDrectionsUrlCaminando(Origen,
                            rutasAgregadas[posRutas.get(0)][posRutasK.get(0)]);

                    //se ejecuta el método para graficar la ruta caminando
                    ReadTask downloadTask = new ReadTask();
                    downloadTask.execute(rutaCaminando);

                    int bAux = 0; //saber si son dos rutas o sola una
                    if(posRutas.size()>1){
                        bAux = 1;
                    }

                    llamadaMetodo=1; //la linea se dibuje de diferente color

                    if(bAux==1){
                        //si son dos rutas se manda el destino y el punto más cercano de la segunda ruta
                        rutaCaminando = getMapsApiDrectionsUrlCaminando(Destino,
                                rutasAgregadas[posRutas.get(1)][posRutasK.get(1)]);
                    }else{
                        //si es solo una ruta se manda el Destino y el punto más cercando de la misma ruta
                        rutaCaminando = getMapsApiDrectionsUrlCaminando(Destino,
                                rutasAgregadas[posRutas.get(0)][posRutasK.get(1)]);
                    }

                    //se ejecuta el método para graficarlo
                    downloadTask = new ReadTask();
                    downloadTask.execute(rutaCaminando);

                    //--------------------------------------------------------------------------------------

                    infoRutas(posRutas, tiempoEsti); //se ejecuta el alertDialog con la información de las rutas
                    llamadaMetodo=0;
                }else if(position==2 && Origen!=null && Destino!=null){
                    //si se seleccionó la opción de viajar caminando
                    llamadaMetodo=2;
                    //ruta obtendrá la cadena String con los puntos
                    String ruta;
                    ruta = getMapsApiDrectionsUrlCaminando(Origen,Destino);

                    //se ejecuta el método para graficarlo
                    ReadTask downloadTask = new ReadTask();
                    downloadTask.execute(ruta);
                    llamadaMetodo=0;
                }else if(Origen==null && Destino==null){
                    //si se selecciona una opción y no entra en ninguna de las anteriores y además no hay
                    //origen o destino o ambos se desplegará este Toast
                    Toast.makeText(getApplicationContext(), "Seleccionar Origen y Destino", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //se ejecutará cada que se seleccione una opción del spinner de rutas
        spinnerRutas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.clear(); //y el mapa es limpiado
                if(position!=0) {
                    double tiempoEs = 0;
                    llamadaMetodo = 2;
                    ArrayList<Integer> posRutas = new ArrayList<Integer>(); //guardará el número de ruta a gráficar
                    ArrayList<Integer> posRutasK = new ArrayList<Integer>(); //guardará el número de ruta a gráficar
                    //URL obtendrá la cadena que utilizará el método encargado de gráficar
                    posRutas.add(position - 1);

                    tiempoEs = tiempoEstimado(posRutas.get(0),0);
                    URL = new String[1];
                    URL = getMapsApiDirectionsUrl(posRutas,posRutasK);
                    for (int h = 0; h < URL.length; h++) {
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(URL[h]);
                    }

                    infoRutas(posRutas, tiempoEs); //se ejecuta el alertDialog con la información de las rutas
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //BUTTON-----------------------------------------------------------------------------------
        buttonBorrar = (Button) findViewById(R.id.borrar);

        //Borra los datos que se habían guardado
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se reinician valores
                llamadaMetodo = 0;
                marcadores = 0;
                rutaAzul.setText("");
                rutaRoja.setText("");
                Origen = null;
                Destino = null;
                spinnerOpciones.setSelection(0);
                spinnerRutas.setSelection(0);
                mMap.clear(); //y el mapa es limpiado
            }
        });

        //método para los permisos que necesita la aplicación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }

    }

    //se lanza un alertDialog con la información de las rutas
    public void infoRutas(ArrayList<Integer> rutas, double tiempo){
        AlertDialog.Builder builder = new AlertDialog.Builder(CX);
        //se concatena la información a mostrar
        String tag = "Rutas\n";
        for(int i=0;i<rutas.size();i++){
            tag = tag + nomRutas[rutas.get(i)][0]+": "+nomRutas[rutas.get(i)][1]+"\n";
        }
        tag = tag+"Tiempo Estimado: " + String.format("%.2f",tiempo+tiempoRetraso) +" minutos"+"\n";
        builder.setMessage(""+tag);
        builder.setCancelable(false); //se quita la opción de cancelar
        // Añade el botón de aceptar para cerrar el cuadro de dialogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() { //método de llamada si se selecciona el botón de devuelto
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // Crea el cuadro de dialogo
        AlertDialog dialog = builder.create();
        // Muestra el cuadro de dialogo
        dialog.show();
    }

    //distancias entre todos los puntos de todas las rutas
    public void calcularDistancias(){
        LatLng rutasAgregadas[][] = {ruta1,ruta2,ruta3,ruta4,ruta6,ruta7,ruta8,ruta10,ruta11,ruta12,ruta13,ruta14,ruta15,
                ruta16,ruta17,ruta18,ruta19,ruta20,ruta21,ruta22,ruta23,ruta24,ruta25,ruta26,ruta27,ruta28,ruta29,ruta30,
                ruta31,ruta32,ruta34,ruta35,ruta51};
        for(int i=0;i<numRutas;i++){
            for(int j=0;j<24;j++){
                distancias[i][j] = SphericalUtil.computeDistanceBetween(rutasAgregadas[i][j], rutasAgregadas[i][j+1]);
            }
        }
        calcularTiempos();
    }

    //se calcula el tiempo en base a las distancias y a una velocidad maxima en metros
    public void calcularTiempos(){
        double veloMax = 40;
        double velMetros = veloMax * 5/18;

        Log.d("Tiempo",""+velMetros);

        for(int i=0;i<numRutas;i++){
            for(int j=0;j<24;j++){
                tiempos[i][j] = (distancias[i][j]/velMetros)/60; //tiempo es igual a distancia entre velocidad, seg = min
            }
        }

    }

    //tiempo estimado de la ruta con el punto
    public double tiempoEstimado(int ruta, int punto){
        double tiempoE=0;
        for(int j=punto;j<24;j++){
            tiempoE = tiempoE + tiempos[ruta][j];
        }

        Log.d("Estimado",""+tiempoE);
        return tiempoE;
    }

    //este método calcula la distancia entre todos los puntos que se encuentran registrados por cada ruta y los retorna
    public double[] puntoEncontrado(LatLng ruta[], LatLng punto){
        double distancia[]=new double[ruta.length];
        for(int i=0;i<ruta.length;i++){
            distancia[i] = Math.sqrt(Math.pow((((double) punto.latitude)- ((double) ruta[i].latitude)),2)+Math.pow((((double) punto.longitude)- ((double) ruta[i].longitude)),2));
        }
        return distancia;
    }

    //este método calcula la distancia entre todos los puntos que se encuentran registrados por las dos rutas enviadas y retorna
    //el punto que tiene la menor distancia
    public int puntoEntreDosRutas(LatLng ruta1[], LatLng ruta2[]){
        int punto = 0;
        double distancia[][] = new double[ruta1.length][25];
        for(int i=0;i<ruta1.length;i++){
            for(int j=0;j<ruta2.length;j++){
                distancia[i][j] = Math.sqrt(Math.pow((((double) ruta1[i].latitude)- ((double) ruta2[j].latitude)),2)+Math.pow((((double) ruta1[i].longitude)- ((double) ruta2[j].longitude)),2));
            }
        }

        double menor = distancia[0][0];

        //conocer cual de esos puntos es el más cercano al punto de origen
        for(int l=0;l<ruta1.length;l++){
            for(int k=0;k<25;k++){
                if(distancia[l][k]<menor){
                    menor = distancia[l][k];
                    punto = l;
                }
            }
        }
        return punto;
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }

    // Listener que se dispara al regresar de solicitar permisos...
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
    }


    // Click sobre el TEXTO DE UN MARCADOR (NO SOBRE EL MARCADOR!!)
    public GoogleMap.OnInfoWindowClickListener getInfoWindowClickListener()
    {
        return new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "Marker" + marker.getTitle(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Hacer el ZOOM sobre el punto de inicio
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Inicial, 14));

        // Listener sobre un CLICK sobre el MAPA !!!
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng position)
            {
                marcadores++;
                //debe haber dos marcadores, no más
                if(marcadores<=2){
                    //se obtienen las coordenadas de ese punto
                    LatLng LocalidadX= new LatLng(position.latitude,position.longitude);
                    //si esta condición se cumple significa que el punto seleccionado es el origen e irá de azul, de lo
                    //contrario el punto será el destino e irá de color rojo
                    if(marcadores==1){
                        mMap.addMarker(new MarkerOptions().position(LocalidadX).title("Origen: "+LocalidadX.latitude+""+LocalidadX.longitude).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        Origen = new LatLng(LocalidadX.latitude, LocalidadX.longitude);
                    }else{
                        mMap.addMarker(new MarkerOptions().position(LocalidadX).title("Destino: "+LocalidadX.latitude+""+LocalidadX.longitude).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        Destino = new LatLng(LocalidadX.latitude, LocalidadX.longitude);
                    }
                }
            }
        });

        mMap.setOnInfoWindowClickListener(getInfoWindowClickListener());
    }

    //se crea la cadena cuando el modo de transporte es caminando
    private String getMapsApiDrectionsUrlCaminando(LatLng o, LatLng d){
        String url = "";
        url = "https://maps.googleapis.com/maps/api/directions/json?origin="+o.latitude+","+o.longitude+"&destination="+d.latitude+","+d.longitude+
                "&sensor=false&mode=walking";

        return url;
    }

    //se tiene que saber cuántas y que rutas se van a obtener
    private String[] getMapsApiDirectionsUrl(ArrayList<Integer> posiciones, ArrayList<Integer> puntos) {
        String url[] = new String[numRutas];
        String rutasDibujar[] = new String[posiciones.size()];

        LatLng rutasAgregadas2[][] = {ruta1,ruta2,ruta3,ruta4,ruta6,ruta7,ruta8,ruta10,ruta11,ruta12,ruta13,ruta14,ruta15,
                ruta16,ruta17,ruta18,ruta19,ruta20,ruta21,ruta22,ruta23,ruta24,ruta25,ruta26,ruta27,ruta28,ruta29,ruta30,
                ruta31,ruta32,ruta34,ruta35,ruta51};

        //cada ruta tiene su cadena creada
        url[0] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta1[0].latitude+","+ruta1[0].longitude+"&destination="+ruta1[24].latitude+","+ruta1[24].longitude+
                "&waypoints="
                +
                ruta1[1].latitude+","+ruta1[1].longitude+"|"+"" +
                ruta1[2].latitude+","+ruta1[2].longitude+"|"+"" +
                ruta1[3].latitude+","+ruta1[3].longitude+"|"+"" +
                ruta1[4].latitude+","+ruta1[4].longitude+"|"+"" +
                ruta1[5].latitude+","+ruta1[5].longitude+"|"+"" +
                ruta1[6].latitude+","+ruta1[6].longitude+"|"+"" +
                ruta1[7].latitude+","+ruta1[7].longitude+"|"+"" +
                ruta1[8].latitude+","+ruta1[8].longitude+"|"+"" +
                ruta1[9].latitude+","+ruta1[9].longitude+"|"+"" +
                ruta1[10].latitude+","+ruta1[10].longitude+"|"+"" +
                ruta1[11].latitude+","+ruta1[11].longitude+"|"+"" +
                ruta1[12].latitude+","+ruta1[12].longitude+"|"+"" +
                ruta1[13].latitude+","+ruta1[13].longitude+"|"+"" +
                ruta1[14].latitude+","+ruta1[14].longitude+"|"+"" +
                ruta1[15].latitude+","+ruta1[15].longitude+"|"+"" +
                ruta1[16].latitude+","+ruta1[16].longitude+"|"+"" +
                ruta1[17].latitude+","+ruta1[17].longitude+"|"+"" +
                ruta1[18].latitude+","+ruta1[18].longitude+"|"+"" +
                ruta1[19].latitude+","+ruta1[19].longitude+"|"+"" +
                ruta1[20].latitude+","+ruta1[20].longitude+"|"+"" +
                ruta1[21].latitude+","+ruta1[21].longitude+"|"+"" +
                ruta1[22].latitude+","+ruta1[22].longitude+"|"+"" +
                ruta1[23].latitude+","+ruta1[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[1] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta2[0].latitude+","+ruta2[0].longitude+"&destination="+ruta2[24].latitude+","+ruta2[24].longitude+
                "&waypoints="
                +
                ruta2[1].latitude+","+ruta2[1].longitude+"|"+"" +
                ruta2[2].latitude+","+ruta2[2].longitude+"|"+"" +
                ruta2[3].latitude+","+ruta2[3].longitude+"|"+"" +
                ruta2[4].latitude+","+ruta2[4].longitude+"|"+"" +
                ruta2[5].latitude+","+ruta2[5].longitude+"|"+"" +
                ruta2[6].latitude+","+ruta2[6].longitude+"|"+"" +
                ruta2[7].latitude+","+ruta2[7].longitude+"|"+"" +
                ruta2[8].latitude+","+ruta2[8].longitude+"|"+"" +
                ruta2[9].latitude+","+ruta2[9].longitude+"|"+"" +
                ruta2[10].latitude+","+ruta2[10].longitude+"|"+"" +
                ruta2[11].latitude+","+ruta2[11].longitude+"|"+"" +
                ruta2[12].latitude+","+ruta2[12].longitude+"|"+"" +
                ruta2[13].latitude+","+ruta2[13].longitude+"|"+"" +
                ruta2[14].latitude+","+ruta2[14].longitude+"|"+"" +
                ruta2[15].latitude+","+ruta2[15].longitude+"|"+"" +
                ruta2[16].latitude+","+ruta2[16].longitude+"|"+"" +
                ruta2[17].latitude+","+ruta2[17].longitude+"|"+"" +
                ruta2[18].latitude+","+ruta2[18].longitude+"|"+"" +
                ruta2[19].latitude+","+ruta2[19].longitude+"|"+"" +
                ruta2[20].latitude+","+ruta2[20].longitude+"|"+"" +
                ruta2[21].latitude+","+ruta2[21].longitude+"|"+"" +
                ruta2[22].latitude+","+ruta2[22].longitude+"|"+"" +
                ruta2[23].latitude+","+ruta2[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[2] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta3[0].latitude+","+ruta3[0].longitude+"&destination="+ruta3[24].latitude+","+ruta3[24].longitude+
                "&waypoints="
                +
                ruta3[1].latitude+","+ruta3[1].longitude+"|"+"" +
                ruta3[2].latitude+","+ruta3[2].longitude+"|"+"" +
                ruta3[3].latitude+","+ruta3[3].longitude+"|"+"" +
                ruta3[4].latitude+","+ruta3[4].longitude+"|"+"" +
                ruta3[5].latitude+","+ruta3[5].longitude+"|"+"" +
                ruta3[6].latitude+","+ruta3[6].longitude+"|"+"" +
                ruta3[7].latitude+","+ruta3[7].longitude+"|"+"" +
                ruta3[8].latitude+","+ruta3[8].longitude+"|"+"" +
                ruta3[9].latitude+","+ruta3[9].longitude+"|"+"" +
                ruta3[10].latitude+","+ruta3[10].longitude+"|"+"" +
                ruta3[11].latitude+","+ruta3[11].longitude+"|"+"" +
                ruta3[12].latitude+","+ruta3[12].longitude+"|"+"" +
                ruta3[13].latitude+","+ruta3[13].longitude+"|"+"" +
                ruta3[14].latitude+","+ruta3[14].longitude+"|"+"" +
                ruta3[15].latitude+","+ruta3[15].longitude+"|"+"" +
                ruta3[16].latitude+","+ruta3[16].longitude+"|"+"" +
                ruta3[17].latitude+","+ruta3[17].longitude+"|"+"" +
                ruta3[18].latitude+","+ruta3[18].longitude+"|"+"" +
                ruta3[19].latitude+","+ruta3[19].longitude+"|"+"" +
                ruta3[20].latitude+","+ruta3[20].longitude+"|"+"" +
                ruta3[21].latitude+","+ruta3[21].longitude+"|"+"" +
                ruta3[22].latitude+","+ruta3[22].longitude+"|"+"" +
                ruta3[23].latitude+","+ruta3[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[3] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta4[0].latitude+","+ruta4[0].longitude+"&destination="+ruta4[24].latitude+","+ruta4[24].longitude+
                "&waypoints="
                +
                ruta4[1].latitude+","+ruta4[1].longitude+"|"+"" +
                ruta4[2].latitude+","+ruta4[2].longitude+"|"+"" +
                ruta4[3].latitude+","+ruta4[3].longitude+"|"+"" +
                ruta4[4].latitude+","+ruta4[4].longitude+"|"+"" +
                ruta4[5].latitude+","+ruta4[5].longitude+"|"+"" +
                ruta4[6].latitude+","+ruta4[6].longitude+"|"+"" +
                ruta4[7].latitude+","+ruta4[7].longitude+"|"+"" +
                ruta4[8].latitude+","+ruta4[8].longitude+"|"+"" +
                ruta4[9].latitude+","+ruta4[9].longitude+"|"+"" +
                ruta4[10].latitude+","+ruta4[10].longitude+"|"+"" +
                ruta4[11].latitude+","+ruta4[11].longitude+"|"+"" +
                ruta4[12].latitude+","+ruta4[12].longitude+"|"+"" +
                ruta4[13].latitude+","+ruta4[13].longitude+"|"+"" +
                ruta4[14].latitude+","+ruta4[14].longitude+"|"+"" +
                ruta4[15].latitude+","+ruta4[15].longitude+"|"+"" +
                ruta4[16].latitude+","+ruta4[16].longitude+"|"+"" +
                ruta4[17].latitude+","+ruta4[17].longitude+"|"+"" +
                ruta4[18].latitude+","+ruta4[18].longitude+"|"+"" +
                ruta4[19].latitude+","+ruta4[19].longitude+"|"+"" +
                ruta4[20].latitude+","+ruta4[20].longitude+"|"+"" +
                ruta4[21].latitude+","+ruta4[21].longitude+"|"+"" +
                ruta4[22].latitude+","+ruta4[22].longitude+"|"+"" +
                ruta4[23].latitude+","+ruta4[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[4] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta6[0].latitude+","+ruta6[0].longitude+"&destination="+ruta6[24].latitude+","+ruta6[24].longitude+
                "&waypoints="
                +
                ruta6[1].latitude+","+ruta6[1].longitude+"|"+"" +
                ruta6[2].latitude+","+ruta6[2].longitude+"|"+"" +
                ruta6[3].latitude+","+ruta6[3].longitude+"|"+"" +
                ruta6[4].latitude+","+ruta6[4].longitude+"|"+"" +
                ruta6[5].latitude+","+ruta6[5].longitude+"|"+"" +
                ruta6[6].latitude+","+ruta6[6].longitude+"|"+"" +
                ruta6[7].latitude+","+ruta6[7].longitude+"|"+"" +
                ruta6[8].latitude+","+ruta6[8].longitude+"|"+"" +
                ruta6[9].latitude+","+ruta6[9].longitude+"|"+"" +
                ruta6[10].latitude+","+ruta6[10].longitude+"|"+"" +
                ruta6[11].latitude+","+ruta6[11].longitude+"|"+"" +
                ruta6[12].latitude+","+ruta6[12].longitude+"|"+"" +
                ruta6[13].latitude+","+ruta6[13].longitude+"|"+"" +
                ruta6[14].latitude+","+ruta6[14].longitude+"|"+"" +
                ruta6[15].latitude+","+ruta6[15].longitude+"|"+"" +
                ruta6[16].latitude+","+ruta6[16].longitude+"|"+"" +
                ruta6[17].latitude+","+ruta6[17].longitude+"|"+"" +
                ruta6[18].latitude+","+ruta6[18].longitude+"|"+"" +
                ruta6[19].latitude+","+ruta6[19].longitude+"|"+"" +
                ruta6[20].latitude+","+ruta6[20].longitude+"|"+"" +
                ruta6[21].latitude+","+ruta6[21].longitude+"|"+"" +
                ruta6[22].latitude+","+ruta6[22].longitude+"|"+"" +
                ruta6[23].latitude+","+ruta6[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[5] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta7[0].latitude+","+ruta7[0].longitude+"&destination="+ruta7[21].latitude+","+ruta7[21].longitude+
                "&waypoints="
                +
                ruta7[1].latitude+","+ruta7[1].longitude+"|"+"" +
                ruta7[2].latitude+","+ruta7[2].longitude+"|"+"" +
                ruta7[3].latitude+","+ruta7[3].longitude+"|"+"" +
                ruta7[4].latitude+","+ruta7[4].longitude+"|"+"" +
                ruta7[5].latitude+","+ruta7[5].longitude+"|"+"" +
                ruta7[6].latitude+","+ruta7[6].longitude+"|"+"" +
                ruta7[7].latitude+","+ruta7[7].longitude+"|"+"" +
                ruta7[8].latitude+","+ruta7[8].longitude+"|"+"" +
                ruta7[9].latitude+","+ruta7[9].longitude+"|"+"" +
                ruta7[10].latitude+","+ruta7[10].longitude+"|"+"" +
                ruta7[11].latitude+","+ruta7[11].longitude+"|"+"" +
                ruta7[12].latitude+","+ruta7[12].longitude+"|"+"" +
                ruta7[13].latitude+","+ruta7[13].longitude+"|"+"" +
                ruta7[14].latitude+","+ruta7[14].longitude+"|"+"" +
                ruta7[15].latitude+","+ruta7[15].longitude+"|"+"" +
                ruta7[16].latitude+","+ruta7[16].longitude+"|"+"" +
                ruta7[17].latitude+","+ruta7[17].longitude+"|"+"" +
                ruta7[18].latitude+","+ruta7[18].longitude+"|"+"" +
                ruta7[19].latitude+","+ruta7[19].longitude+"|"+"" +
                ruta7[20].latitude+","+ruta7[20].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[6] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta8[0].latitude+","+ruta8[0].longitude+"&destination="+ruta8[23].latitude+","+ruta8[23].longitude+
                "&waypoints="
                +
                ruta8[1].latitude+","+ruta8[1].longitude+"|"+"" +
                ruta8[2].latitude+","+ruta8[2].longitude+"|"+"" +
                ruta8[3].latitude+","+ruta8[3].longitude+"|"+"" +
                ruta8[4].latitude+","+ruta8[4].longitude+"|"+"" +
                ruta8[5].latitude+","+ruta8[5].longitude+"|"+"" +
                ruta8[6].latitude+","+ruta8[6].longitude+"|"+"" +
                ruta8[7].latitude+","+ruta8[7].longitude+"|"+"" +
                ruta8[8].latitude+","+ruta8[8].longitude+"|"+"" +
                ruta8[9].latitude+","+ruta8[9].longitude+"|"+"" +
                ruta8[10].latitude+","+ruta8[10].longitude+"|"+"" +
                ruta8[11].latitude+","+ruta8[11].longitude+"|"+"" +
                ruta8[12].latitude+","+ruta8[12].longitude+"|"+"" +
                ruta8[13].latitude+","+ruta8[13].longitude+"|"+"" +
                ruta8[14].latitude+","+ruta8[14].longitude+"|"+"" +
                ruta8[15].latitude+","+ruta8[15].longitude+"|"+"" +
                ruta8[16].latitude+","+ruta8[16].longitude+"|"+"" +
                ruta8[17].latitude+","+ruta8[17].longitude+"|"+"" +
                ruta8[18].latitude+","+ruta8[18].longitude+"|"+"" +
                ruta8[19].latitude+","+ruta8[19].longitude+"|"+"" +
                ruta8[20].latitude+","+ruta8[20].longitude+"|"+"" +
                ruta8[21].latitude+","+ruta8[21].longitude+"|"+"" +
                ruta8[22].latitude+","+ruta8[22].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[7] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta10[0].latitude+","+ruta10[0].longitude+"&destination="+ruta10[24].latitude+","+ruta10[24].longitude+
                "&waypoints="
                +
                ruta10[1].latitude+","+ruta10[1].longitude+"|"+"" +
                ruta10[2].latitude+","+ruta10[2].longitude+"|"+"" +
                ruta10[3].latitude+","+ruta10[3].longitude+"|"+"" +
                ruta10[4].latitude+","+ruta10[4].longitude+"|"+"" +
                ruta10[5].latitude+","+ruta10[5].longitude+"|"+"" +
                ruta10[6].latitude+","+ruta10[6].longitude+"|"+"" +
                ruta10[7].latitude+","+ruta10[7].longitude+"|"+"" +
                ruta10[8].latitude+","+ruta10[8].longitude+"|"+"" +
                ruta10[9].latitude+","+ruta10[9].longitude+"|"+"" +
                ruta10[10].latitude+","+ruta10[10].longitude+"|"+"" +
                ruta10[11].latitude+","+ruta10[11].longitude+"|"+"" +
                ruta10[12].latitude+","+ruta10[12].longitude+"|"+"" +
                ruta10[13].latitude+","+ruta10[13].longitude+"|"+"" +
                ruta10[14].latitude+","+ruta10[14].longitude+"|"+"" +
                ruta10[15].latitude+","+ruta10[15].longitude+"|"+"" +
                ruta10[16].latitude+","+ruta10[16].longitude+"|"+"" +
                ruta10[17].latitude+","+ruta10[17].longitude+"|"+"" +
                ruta10[18].latitude+","+ruta10[18].longitude+"|"+"" +
                ruta10[19].latitude+","+ruta10[19].longitude+"|"+"" +
                ruta10[20].latitude+","+ruta10[20].longitude+"|"+"" +
                ruta10[21].latitude+","+ruta10[21].longitude+"|"+"" +
                ruta10[22].latitude+","+ruta10[22].longitude+"|"+"" +
                ruta10[23].latitude+","+ruta10[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[8] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta11[0].latitude+","+ruta11[0].longitude+"&destination="+ruta11[4].latitude+","+ruta11[4].longitude+
                "&waypoints="
                +
                ruta11[1].latitude+","+ruta11[1].longitude+"|"+"" +
                ruta11[2].latitude+","+ruta11[2].longitude+"|"+"" +
                ruta11[3].latitude+","+ruta11[3].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[9] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta12[0].latitude+","+ruta12[0].longitude+"&destination="+ruta12[24].latitude+","+ruta12[24].longitude+
                "&waypoints="
                +
                ruta12[1].latitude+","+ruta12[1].longitude+"|"+"" +
                ruta12[2].latitude+","+ruta12[2].longitude+"|"+"" +
                ruta12[3].latitude+","+ruta12[3].longitude+"|"+"" +
                ruta12[4].latitude+","+ruta12[4].longitude+"|"+"" +
                ruta12[5].latitude+","+ruta12[5].longitude+"|"+"" +
                ruta12[6].latitude+","+ruta12[6].longitude+"|"+"" +
                ruta12[7].latitude+","+ruta12[7].longitude+"|"+"" +
                ruta12[8].latitude+","+ruta12[8].longitude+"|"+"" +
                ruta12[9].latitude+","+ruta12[9].longitude+"|"+"" +
                ruta12[10].latitude+","+ruta12[10].longitude+"|"+"" +
                ruta12[11].latitude+","+ruta12[11].longitude+"|"+"" +
                ruta12[12].latitude+","+ruta12[12].longitude+"|"+"" +
                ruta12[13].latitude+","+ruta12[13].longitude+"|"+"" +
                ruta12[14].latitude+","+ruta12[14].longitude+"|"+"" +
                ruta12[15].latitude+","+ruta12[15].longitude+"|"+"" +
                ruta12[16].latitude+","+ruta12[16].longitude+"|"+"" +
                ruta12[17].latitude+","+ruta12[17].longitude+"|"+"" +
                ruta12[18].latitude+","+ruta12[18].longitude+"|"+"" +
                ruta12[19].latitude+","+ruta12[19].longitude+"|"+"" +
                ruta12[20].latitude+","+ruta12[20].longitude+"|"+"" +
                ruta12[21].latitude+","+ruta12[21].longitude+"|"+"" +
                ruta12[22].latitude+","+ruta12[22].longitude+"|"+"" +
                ruta12[23].latitude+","+ruta12[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[10] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta13[0].latitude+","+ruta13[0].longitude+"&destination="+ruta13[24].latitude+","+ruta13[24].longitude+
                "&waypoints="
                +
                ruta13[1].latitude+","+ruta13[1].longitude+"|"+"" +
                ruta13[2].latitude+","+ruta13[2].longitude+"|"+"" +
                ruta13[3].latitude+","+ruta13[3].longitude+"|"+"" +
                ruta13[4].latitude+","+ruta13[4].longitude+"|"+"" +
                ruta13[5].latitude+","+ruta13[5].longitude+"|"+"" +
                ruta13[6].latitude+","+ruta13[6].longitude+"|"+"" +
                ruta13[7].latitude+","+ruta13[7].longitude+"|"+"" +
                ruta13[8].latitude+","+ruta13[8].longitude+"|"+"" +
                ruta13[9].latitude+","+ruta13[9].longitude+"|"+"" +
                ruta13[10].latitude+","+ruta13[10].longitude+"|"+"" +
                ruta13[11].latitude+","+ruta13[11].longitude+"|"+"" +
                ruta13[12].latitude+","+ruta13[12].longitude+"|"+"" +
                ruta13[13].latitude+","+ruta13[13].longitude+"|"+"" +
                ruta13[14].latitude+","+ruta13[14].longitude+"|"+"" +
                ruta13[15].latitude+","+ruta13[15].longitude+"|"+"" +
                ruta13[16].latitude+","+ruta13[16].longitude+"|"+"" +
                ruta13[17].latitude+","+ruta13[17].longitude+"|"+"" +
                ruta13[18].latitude+","+ruta13[18].longitude+"|"+"" +
                ruta13[19].latitude+","+ruta13[19].longitude+"|"+"" +
                ruta13[20].latitude+","+ruta13[20].longitude+"|"+"" +
                ruta13[21].latitude+","+ruta13[21].longitude+"|"+"" +
                ruta13[22].latitude+","+ruta13[22].longitude+"|"+"" +
                ruta13[23].latitude+","+ruta13[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[11] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta14[0].latitude+","+ruta14[0].longitude+"&destination="+ruta14[17].latitude+","+ruta14[17].longitude+
                "&waypoints="
                +
                ruta14[1].latitude+","+ruta14[1].longitude+"|"+"" +
                ruta14[2].latitude+","+ruta14[2].longitude+"|"+"" +
                ruta14[3].latitude+","+ruta14[3].longitude+"|"+"" +
                ruta14[4].latitude+","+ruta14[4].longitude+"|"+"" +
                ruta14[5].latitude+","+ruta14[5].longitude+"|"+"" +
                ruta14[6].latitude+","+ruta14[6].longitude+"|"+"" +
                ruta14[7].latitude+","+ruta14[7].longitude+"|"+"" +
                ruta14[8].latitude+","+ruta14[8].longitude+"|"+"" +
                ruta14[9].latitude+","+ruta14[9].longitude+"|"+"" +
                ruta14[10].latitude+","+ruta14[10].longitude+"|"+"" +
                ruta14[11].latitude+","+ruta14[11].longitude+"|"+"" +
                ruta14[12].latitude+","+ruta14[12].longitude+"|"+"" +
                ruta14[13].latitude+","+ruta14[13].longitude+"|"+"" +
                ruta14[14].latitude+","+ruta14[14].longitude+"|"+"" +
                ruta14[15].latitude+","+ruta14[15].longitude+"|"+"" +
                ruta14[16].latitude+","+ruta14[16].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[12] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta15[0].latitude+","+ruta15[0].longitude+"&destination="+ruta15[23].latitude+","+ruta15[23].longitude+
                "&waypoints="
                +
                ruta15[1].latitude+","+ruta15[1].longitude+"|"+"" +
                ruta15[2].latitude+","+ruta15[2].longitude+"|"+"" +
                ruta15[3].latitude+","+ruta15[3].longitude+"|"+"" +
                ruta15[4].latitude+","+ruta15[4].longitude+"|"+"" +
                ruta15[5].latitude+","+ruta15[5].longitude+"|"+"" +
                ruta15[6].latitude+","+ruta15[6].longitude+"|"+"" +
                ruta15[7].latitude+","+ruta15[7].longitude+"|"+"" +
                ruta15[8].latitude+","+ruta15[8].longitude+"|"+"" +
                ruta15[9].latitude+","+ruta15[9].longitude+"|"+"" +
                ruta15[10].latitude+","+ruta15[10].longitude+"|"+"" +
                ruta15[11].latitude+","+ruta15[11].longitude+"|"+"" +
                ruta15[12].latitude+","+ruta15[12].longitude+"|"+"" +
                ruta15[13].latitude+","+ruta15[13].longitude+"|"+"" +
                ruta15[14].latitude+","+ruta15[14].longitude+"|"+"" +
                ruta15[15].latitude+","+ruta15[15].longitude+"|"+"" +
                ruta15[16].latitude+","+ruta15[16].longitude+"|"+"" +
                ruta15[17].latitude+","+ruta15[17].longitude+"|"+"" +
                ruta15[18].latitude+","+ruta15[18].longitude+"|"+"" +
                ruta15[19].latitude+","+ruta15[19].longitude+"|"+"" +
                ruta15[20].latitude+","+ruta15[20].longitude+"|"+"" +
                ruta15[21].latitude+","+ruta15[21].longitude+"|"+"" +
                ruta15[22].latitude+","+ruta15[22].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;


        url[13] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta16[0].latitude+","+ruta16[0].longitude+"&destination="+ruta16[23].latitude+","+ruta16[23].longitude+
                "&waypoints="
                +
                ruta16[1].latitude+","+ruta16[1].longitude+"|"+"" +
                ruta16[2].latitude+","+ruta16[2].longitude+"|"+"" +
                ruta16[3].latitude+","+ruta16[3].longitude+"|"+"" +
                ruta16[4].latitude+","+ruta16[4].longitude+"|"+"" +
                ruta16[5].latitude+","+ruta16[5].longitude+"|"+"" +
                ruta16[6].latitude+","+ruta16[6].longitude+"|"+"" +
                ruta16[7].latitude+","+ruta16[7].longitude+"|"+"" +
                ruta16[8].latitude+","+ruta16[8].longitude+"|"+"" +
                ruta16[9].latitude+","+ruta16[9].longitude+"|"+"" +
                ruta16[10].latitude+","+ruta16[10].longitude+"|"+"" +
                ruta16[11].latitude+","+ruta16[11].longitude+"|"+"" +
                ruta16[12].latitude+","+ruta16[12].longitude+"|"+"" +
                ruta16[13].latitude+","+ruta16[13].longitude+"|"+"" +
                ruta16[14].latitude+","+ruta16[14].longitude+"|"+"" +
                ruta16[15].latitude+","+ruta16[15].longitude+"|"+"" +
                ruta16[16].latitude+","+ruta16[16].longitude+"|"+"" +
                ruta16[17].latitude+","+ruta16[17].longitude+"|"+"" +
                ruta16[18].latitude+","+ruta16[18].longitude+"|"+"" +
                ruta16[19].latitude+","+ruta16[19].longitude+"|"+"" +
                ruta16[20].latitude+","+ruta16[20].longitude+"|"+"" +
                ruta16[21].latitude+","+ruta16[21].longitude+"|"+"" +
                ruta16[22].latitude+","+ruta16[22].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[14] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta17[0].latitude+","+ruta17[0].longitude+"&destination="+ruta17[24].latitude+","+ruta17[24].longitude+
                "&waypoints="
                +
                ruta17[1].latitude+","+ruta17[1].longitude+"|"+"" +
                ruta17[2].latitude+","+ruta17[2].longitude+"|"+"" +
                ruta17[3].latitude+","+ruta17[3].longitude+"|"+"" +
                ruta17[4].latitude+","+ruta17[4].longitude+"|"+"" +
                ruta17[5].latitude+","+ruta17[5].longitude+"|"+"" +
                ruta17[6].latitude+","+ruta17[6].longitude+"|"+"" +
                ruta17[7].latitude+","+ruta17[7].longitude+"|"+"" +
                ruta17[8].latitude+","+ruta17[8].longitude+"|"+"" +
                ruta17[9].latitude+","+ruta17[9].longitude+"|"+"" +
                ruta17[10].latitude+","+ruta17[10].longitude+"|"+"" +
                ruta17[11].latitude+","+ruta17[11].longitude+"|"+"" +
                ruta17[12].latitude+","+ruta17[12].longitude+"|"+"" +
                ruta17[13].latitude+","+ruta17[13].longitude+"|"+"" +
                ruta17[14].latitude+","+ruta17[14].longitude+"|"+"" +
                ruta17[15].latitude+","+ruta17[15].longitude+"|"+"" +
                ruta17[16].latitude+","+ruta17[16].longitude+"|"+"" +
                ruta17[17].latitude+","+ruta17[17].longitude+"|"+"" +
                ruta17[18].latitude+","+ruta17[18].longitude+"|"+"" +
                ruta17[19].latitude+","+ruta17[19].longitude+"|"+"" +
                ruta17[20].latitude+","+ruta17[20].longitude+"|"+"" +
                ruta17[21].latitude+","+ruta17[21].longitude+"|"+"" +
                ruta17[22].latitude+","+ruta17[22].longitude+"|"+"" +
                ruta17[23].latitude+","+ruta17[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[15] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta18[0].latitude+","+ruta18[0].longitude+"&destination="+ruta18[10].latitude+","+ruta18[10].longitude+
                "&waypoints="
                +
                ruta18[1].latitude+","+ruta18[1].longitude+"|"+"" +
                ruta18[2].latitude+","+ruta18[2].longitude+"|"+"" +
                ruta18[3].latitude+","+ruta18[3].longitude+"|"+"" +
                ruta18[4].latitude+","+ruta18[4].longitude+"|"+"" +
                ruta18[5].latitude+","+ruta18[5].longitude+"|"+"" +
                ruta18[6].latitude+","+ruta18[6].longitude+"|"+"" +
                ruta18[7].latitude+","+ruta18[7].longitude+"|"+"" +
                ruta18[8].latitude+","+ruta18[8].longitude+"|"+"" +
                ruta18[9].latitude+","+ruta18[9].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[16] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta19[0].latitude+","+ruta19[0].longitude+"&destination="+ruta19[24].latitude+","+ruta19[24].longitude+
                "&waypoints="
                +
                ruta19[1].latitude+","+ruta19[1].longitude+"|"+"" +
                ruta19[2].latitude+","+ruta19[2].longitude+"|"+"" +
                ruta19[3].latitude+","+ruta19[3].longitude+"|"+"" +
                ruta19[4].latitude+","+ruta19[4].longitude+"|"+"" +
                ruta19[5].latitude+","+ruta19[5].longitude+"|"+"" +
                ruta19[6].latitude+","+ruta19[6].longitude+"|"+"" +
                ruta19[7].latitude+","+ruta19[7].longitude+"|"+"" +
                ruta19[8].latitude+","+ruta19[8].longitude+"|"+"" +
                ruta19[9].latitude+","+ruta19[9].longitude+"|"+"" +
                ruta19[10].latitude+","+ruta19[10].longitude+"|"+"" +
                ruta19[11].latitude+","+ruta19[11].longitude+"|"+"" +
                ruta19[12].latitude+","+ruta19[12].longitude+"|"+"" +
                ruta19[13].latitude+","+ruta19[13].longitude+"|"+"" +
                ruta19[14].latitude+","+ruta19[14].longitude+"|"+"" +
                ruta19[15].latitude+","+ruta19[15].longitude+"|"+"" +
                ruta19[16].latitude+","+ruta19[16].longitude+"|"+"" +
                ruta19[17].latitude+","+ruta19[17].longitude+"|"+"" +
                ruta19[18].latitude+","+ruta19[18].longitude+"|"+"" +
                ruta19[19].latitude+","+ruta19[19].longitude+"|"+"" +
                ruta19[20].latitude+","+ruta19[20].longitude+"|"+"" +
                ruta19[21].latitude+","+ruta19[21].longitude+"|"+"" +
                ruta19[22].latitude+","+ruta19[22].longitude+"|"+"" +
                ruta19[23].latitude+","+ruta19[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[17] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta20[0].latitude+","+ruta20[0].longitude+"&destination="+ruta20[24].latitude+","+ruta20[24].longitude+
                "&waypoints="
                +
                ruta20[1].latitude+","+ruta20[1].longitude+"|"+"" +
                ruta20[2].latitude+","+ruta20[2].longitude+"|"+"" +
                ruta20[3].latitude+","+ruta20[3].longitude+"|"+"" +
                ruta20[4].latitude+","+ruta20[4].longitude+"|"+"" +
                ruta20[5].latitude+","+ruta20[5].longitude+"|"+"" +
                ruta20[6].latitude+","+ruta20[6].longitude+"|"+"" +
                ruta20[7].latitude+","+ruta20[7].longitude+"|"+"" +
                ruta20[8].latitude+","+ruta20[8].longitude+"|"+"" +
                ruta20[9].latitude+","+ruta20[9].longitude+"|"+"" +
                ruta20[10].latitude+","+ruta20[10].longitude+"|"+"" +
                ruta20[11].latitude+","+ruta20[11].longitude+"|"+"" +
                ruta20[12].latitude+","+ruta20[12].longitude+"|"+"" +
                ruta20[13].latitude+","+ruta20[13].longitude+"|"+"" +
                ruta20[14].latitude+","+ruta20[14].longitude+"|"+"" +
                ruta20[15].latitude+","+ruta20[15].longitude+"|"+"" +
                ruta20[16].latitude+","+ruta20[16].longitude+"|"+"" +
                ruta20[17].latitude+","+ruta20[17].longitude+"|"+"" +
                ruta20[18].latitude+","+ruta20[18].longitude+"|"+"" +
                ruta20[19].latitude+","+ruta20[19].longitude+"|"+"" +
                ruta20[20].latitude+","+ruta20[20].longitude+"|"+"" +
                ruta20[21].latitude+","+ruta20[21].longitude+"|"+"" +
                ruta20[22].latitude+","+ruta20[22].longitude+"|"+"" +
                ruta20[23].latitude+","+ruta20[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[18] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta21[0].latitude+","+ruta21[0].longitude+"&destination="+ruta21[22].latitude+","+ruta21[22].longitude+
                "&waypoints="
                +
                ruta21[1].latitude+","+ruta21[1].longitude+"|"+"" +
                ruta21[2].latitude+","+ruta21[2].longitude+"|"+"" +
                ruta21[3].latitude+","+ruta21[3].longitude+"|"+"" +
                ruta21[4].latitude+","+ruta21[4].longitude+"|"+"" +
                ruta21[5].latitude+","+ruta21[5].longitude+"|"+"" +
                ruta21[6].latitude+","+ruta21[6].longitude+"|"+"" +
                ruta21[7].latitude+","+ruta21[7].longitude+"|"+"" +
                ruta21[8].latitude+","+ruta21[8].longitude+"|"+"" +
                ruta21[9].latitude+","+ruta21[9].longitude+"|"+"" +
                ruta21[10].latitude+","+ruta21[10].longitude+"|"+"" +
                ruta21[11].latitude+","+ruta21[11].longitude+"|"+"" +
                ruta21[12].latitude+","+ruta21[12].longitude+"|"+"" +
                ruta21[13].latitude+","+ruta21[13].longitude+"|"+"" +
                ruta21[14].latitude+","+ruta21[14].longitude+"|"+"" +
                ruta21[15].latitude+","+ruta21[15].longitude+"|"+"" +
                ruta21[16].latitude+","+ruta21[16].longitude+"|"+"" +
                ruta21[17].latitude+","+ruta21[17].longitude+"|"+"" +
                ruta21[18].latitude+","+ruta21[18].longitude+"|"+"" +
                ruta21[19].latitude+","+ruta21[19].longitude+"|"+"" +
                ruta21[20].latitude+","+ruta21[20].longitude+"|"+"" +
                ruta21[21].latitude+","+ruta21[21].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[19] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta22[0].latitude+","+ruta22[0].longitude+"&destination="+ruta22[18].latitude+","+ruta22[18].longitude+
                "&waypoints="
                +
                ruta22[1].latitude+","+ruta22[1].longitude+"|"+"" +
                ruta22[2].latitude+","+ruta22[2].longitude+"|"+"" +
                ruta22[3].latitude+","+ruta22[3].longitude+"|"+"" +
                ruta22[4].latitude+","+ruta22[4].longitude+"|"+"" +
                ruta22[5].latitude+","+ruta22[5].longitude+"|"+"" +
                ruta22[6].latitude+","+ruta22[6].longitude+"|"+"" +
                ruta22[7].latitude+","+ruta22[7].longitude+"|"+"" +
                ruta22[8].latitude+","+ruta22[8].longitude+"|"+"" +
                ruta22[9].latitude+","+ruta22[9].longitude+"|"+"" +
                ruta22[10].latitude+","+ruta22[10].longitude+"|"+"" +
                ruta22[11].latitude+","+ruta22[11].longitude+"|"+"" +
                ruta22[12].latitude+","+ruta22[12].longitude+"|"+"" +
                ruta22[13].latitude+","+ruta22[13].longitude+"|"+"" +
                ruta22[14].latitude+","+ruta22[14].longitude+"|"+"" +
                ruta22[15].latitude+","+ruta22[15].longitude+"|"+"" +
                ruta22[16].latitude+","+ruta22[16].longitude+"|"+"" +
                ruta22[17].latitude+","+ruta22[17].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[20] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta23[0].latitude+","+ruta23[0].longitude+"&destination="+ruta23[24].latitude+","+ruta23[24].longitude+
                "&waypoints="
                +
                ruta23[1].latitude+","+ruta23[1].longitude+"|"+"" +
                ruta23[2].latitude+","+ruta23[2].longitude+"|"+"" +
                ruta23[3].latitude+","+ruta23[3].longitude+"|"+"" +
                ruta23[4].latitude+","+ruta23[4].longitude+"|"+"" +
                ruta23[5].latitude+","+ruta23[5].longitude+"|"+"" +
                ruta23[6].latitude+","+ruta23[6].longitude+"|"+"" +
                ruta23[7].latitude+","+ruta23[7].longitude+"|"+"" +
                ruta23[8].latitude+","+ruta23[8].longitude+"|"+"" +
                ruta23[9].latitude+","+ruta23[9].longitude+"|"+"" +
                ruta23[10].latitude+","+ruta23[10].longitude+"|"+"" +
                ruta23[11].latitude+","+ruta23[11].longitude+"|"+"" +
                ruta23[12].latitude+","+ruta23[12].longitude+"|"+"" +
                ruta23[13].latitude+","+ruta23[13].longitude+"|"+"" +
                ruta23[14].latitude+","+ruta23[14].longitude+"|"+"" +
                ruta23[15].latitude+","+ruta23[15].longitude+"|"+"" +
                ruta23[16].latitude+","+ruta23[16].longitude+"|"+"" +
                ruta23[17].latitude+","+ruta23[17].longitude+"|"+"" +
                ruta23[18].latitude+","+ruta23[18].longitude+"|"+"" +
                ruta23[19].latitude+","+ruta23[19].longitude+"|"+"" +
                ruta23[20].latitude+","+ruta23[20].longitude+"|"+"" +
                ruta23[21].latitude+","+ruta23[21].longitude+"|"+"" +
                ruta23[22].latitude+","+ruta23[22].longitude+"|"+"" +
                ruta23[23].latitude+","+ruta23[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[21] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta24[0].latitude+","+ruta24[0].longitude+"&destination="+ruta24[24].latitude+","+ruta24[24].longitude+
                "&waypoints="
                +
                ruta24[1].latitude+","+ruta24[1].longitude+"|"+"" +
                ruta24[2].latitude+","+ruta24[2].longitude+"|"+"" +
                ruta24[3].latitude+","+ruta24[3].longitude+"|"+"" +
                ruta24[4].latitude+","+ruta24[4].longitude+"|"+"" +
                ruta24[5].latitude+","+ruta24[5].longitude+"|"+"" +
                ruta24[6].latitude+","+ruta24[6].longitude+"|"+"" +
                ruta24[7].latitude+","+ruta24[7].longitude+"|"+"" +
                ruta24[8].latitude+","+ruta24[8].longitude+"|"+"" +
                ruta24[9].latitude+","+ruta24[9].longitude+"|"+"" +
                ruta24[10].latitude+","+ruta24[10].longitude+"|"+"" +
                ruta24[11].latitude+","+ruta24[11].longitude+"|"+"" +
                ruta24[12].latitude+","+ruta24[12].longitude+"|"+"" +
                ruta24[13].latitude+","+ruta24[13].longitude+"|"+"" +
                ruta24[14].latitude+","+ruta24[14].longitude+"|"+"" +
                ruta24[15].latitude+","+ruta24[15].longitude+"|"+"" +
                ruta24[16].latitude+","+ruta24[16].longitude+"|"+"" +
                ruta24[17].latitude+","+ruta24[17].longitude+"|"+"" +
                ruta24[18].latitude+","+ruta24[18].longitude+"|"+"" +
                ruta24[19].latitude+","+ruta24[19].longitude+"|"+"" +
                ruta24[20].latitude+","+ruta24[20].longitude+"|"+"" +
                ruta24[21].latitude+","+ruta24[21].longitude+"|"+"" +
                ruta24[22].latitude+","+ruta24[22].longitude+"|"+"" +
                ruta24[23].latitude+","+ruta24[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[22] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta25[0].latitude+","+ruta25[0].longitude+"&destination="+ruta25[17].latitude+","+ruta25[17].longitude+
                "&waypoints="
                +
                ruta25[1].latitude+","+ruta25[1].longitude+"|"+"" +
                ruta25[2].latitude+","+ruta25[2].longitude+"|"+"" +
                ruta25[3].latitude+","+ruta25[3].longitude+"|"+"" +
                ruta25[4].latitude+","+ruta25[4].longitude+"|"+"" +
                ruta25[5].latitude+","+ruta25[5].longitude+"|"+"" +
                ruta25[6].latitude+","+ruta25[6].longitude+"|"+"" +
                ruta25[7].latitude+","+ruta25[7].longitude+"|"+"" +
                ruta25[8].latitude+","+ruta25[8].longitude+"|"+"" +
                ruta25[9].latitude+","+ruta25[9].longitude+"|"+"" +
                ruta25[10].latitude+","+ruta25[10].longitude+"|"+"" +
                ruta25[11].latitude+","+ruta25[11].longitude+"|"+"" +
                ruta25[12].latitude+","+ruta25[12].longitude+"|"+"" +
                ruta25[13].latitude+","+ruta25[13].longitude+"|"+"" +
                ruta25[14].latitude+","+ruta25[14].longitude+"|"+"" +
                ruta25[15].latitude+","+ruta25[15].longitude+"|"+"" +
                ruta25[16].latitude+","+ruta25[16].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[23] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta26[0].latitude+","+ruta26[0].longitude+"&destination="+ruta26[22].latitude+","+ruta26[22].longitude+
                "&waypoints="
                +
                ruta26[1].latitude+","+ruta26[1].longitude+"|"+"" +
                ruta26[2].latitude+","+ruta26[2].longitude+"|"+"" +
                ruta26[3].latitude+","+ruta26[3].longitude+"|"+"" +
                ruta26[4].latitude+","+ruta26[4].longitude+"|"+"" +
                ruta26[5].latitude+","+ruta26[5].longitude+"|"+"" +
                ruta26[6].latitude+","+ruta26[6].longitude+"|"+"" +
                ruta26[7].latitude+","+ruta26[7].longitude+"|"+"" +
                ruta26[8].latitude+","+ruta26[8].longitude+"|"+"" +
                ruta26[9].latitude+","+ruta26[9].longitude+"|"+"" +
                ruta26[10].latitude+","+ruta26[10].longitude+"|"+"" +
                ruta26[11].latitude+","+ruta26[11].longitude+"|"+"" +
                ruta26[12].latitude+","+ruta26[12].longitude+"|"+"" +
                ruta26[13].latitude+","+ruta26[13].longitude+"|"+"" +
                ruta26[14].latitude+","+ruta26[14].longitude+"|"+"" +
                ruta26[15].latitude+","+ruta26[15].longitude+"|"+"" +
                ruta26[16].latitude+","+ruta26[16].longitude+"|"+"" +
                ruta26[17].latitude+","+ruta26[17].longitude+"|"+"" +
                ruta26[18].latitude+","+ruta26[18].longitude+"|"+"" +
                ruta26[19].latitude+","+ruta26[19].longitude+"|"+"" +
                ruta26[20].latitude+","+ruta26[20].longitude+"|"+"" +
                ruta26[21].latitude+","+ruta26[21].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;


        url[24] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta27[0].latitude+","+ruta27[0].longitude+"&destination="+ruta27[12].latitude+","+ruta27[12].longitude+
                "&waypoints="
                +
                ruta27[1].latitude+","+ruta27[1].longitude+"|"+"" +
                ruta27[2].latitude+","+ruta27[2].longitude+"|"+"" +
                ruta27[3].latitude+","+ruta27[3].longitude+"|"+"" +
                ruta27[4].latitude+","+ruta2[4].longitude+"|"+"" +
                ruta27[5].latitude+","+ruta27[5].longitude+"|"+"" +
                ruta27[6].latitude+","+ruta27[6].longitude+"|"+"" +
                ruta27[7].latitude+","+ruta27[7].longitude+"|"+"" +
                ruta27[8].latitude+","+ruta27[8].longitude+"|"+"" +
                ruta27[9].latitude+","+ruta27[9].longitude+"|"+"" +
                ruta27[10].latitude+","+ruta27[10].longitude+"|"+"" +
                ruta27[11].latitude+","+ruta27[11].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[25] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta28[0].latitude+","+ruta28[0].longitude+"&destination="+ruta28[19].latitude+","+ruta28[19].longitude+
                "&waypoints="
                +
                ruta28[1].latitude+","+ruta28[1].longitude+"|"+"" +
                ruta28[2].latitude+","+ruta28[2].longitude+"|"+"" +
                ruta28[3].latitude+","+ruta28[3].longitude+"|"+"" +
                ruta28[4].latitude+","+ruta28[4].longitude+"|"+"" +
                ruta28[5].latitude+","+ruta28[5].longitude+"|"+"" +
                ruta28[6].latitude+","+ruta28[6].longitude+"|"+"" +
                ruta28[7].latitude+","+ruta28[7].longitude+"|"+"" +
                ruta28[8].latitude+","+ruta28[8].longitude+"|"+"" +
                ruta28[9].latitude+","+ruta28[9].longitude+"|"+"" +
                ruta28[10].latitude+","+ruta28[10].longitude+"|"+"" +
                ruta28[11].latitude+","+ruta28[11].longitude+"|"+"" +
                ruta28[12].latitude+","+ruta28[12].longitude+"|"+"" +
                ruta28[13].latitude+","+ruta28[13].longitude+"|"+"" +
                ruta28[14].latitude+","+ruta28[14].longitude+"|"+"" +
                ruta28[15].latitude+","+ruta28[15].longitude+"|"+"" +
                ruta28[16].latitude+","+ruta28[16].longitude+"|"+"" +
                ruta28[17].latitude+","+ruta28[17].longitude+"|"+"" +
                ruta28[18].latitude+","+ruta28[18].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[26] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta29[0].latitude+","+ruta29[0].longitude+"&destination="+ruta29[7].latitude+","+ruta29[7].longitude+
                "&waypoints="
                +
                ruta29[1].latitude+","+ruta29[1].longitude+"|"+"" +
                ruta29[2].latitude+","+ruta29[2].longitude+"|"+"" +
                ruta29[3].latitude+","+ruta29[3].longitude+"|"+"" +
                ruta29[4].latitude+","+ruta29[4].longitude+"|"+"" +
                ruta29[5].latitude+","+ruta29[5].longitude+"|"+"" +
                ruta29[6].latitude+","+ruta29[6].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;


        url[27] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta30[0].latitude+","+ruta30[0].longitude+"&destination="+ruta30[4].latitude+","+ruta30[4].longitude+
                "&waypoints="
                +
                ruta30[1].latitude+","+ruta30[1].longitude+"|"+"" +
                ruta30[2].latitude+","+ruta30[2].longitude+"|"+"" +
                ruta30[3].latitude+","+ruta30[3].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;


        url[28] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta31[0].latitude+","+ruta31[0].longitude+"&destination="+ruta31[9].latitude+","+ruta31[9].longitude+
                "&waypoints="
                +
                ruta31[1].latitude+","+ruta31[1].longitude+"|"+"" +
                ruta31[2].latitude+","+ruta31[2].longitude+"|"+"" +
                ruta31[3].latitude+","+ruta31[3].longitude+"|"+"" +
                ruta31[4].latitude+","+ruta31[4].longitude+"|"+"" +
                ruta31[5].latitude+","+ruta31[5].longitude+"|"+"" +
                ruta31[6].latitude+","+ruta31[6].longitude+"|"+"" +
                ruta31[7].latitude+","+ruta31[7].longitude+"|"+"" +
                ruta31[8].latitude+","+ruta31[8].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[29] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta32[0].latitude+","+ruta32[0].longitude+"&destination="+ruta32[8].latitude+","+ruta32[8].longitude+
                "&waypoints="
                +
                ruta32[1].latitude+","+ruta32[1].longitude+"|"+"" +
                ruta32[2].latitude+","+ruta32[2].longitude+"|"+"" +
                ruta32[3].latitude+","+ruta32[3].longitude+"|"+"" +
                ruta32[4].latitude+","+ruta32[4].longitude+"|"+"" +
                ruta32[5].latitude+","+ruta32[5].longitude+"|"+"" +
                ruta32[6].latitude+","+ruta32[6].longitude+"|"+"" +
                ruta32[7].latitude+","+ruta32[7].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        url[30] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta34[0].latitude+","+ruta34[0].longitude+"&destination="+ruta34[13].latitude+","+ruta34[13].longitude+
                "&waypoints="
                +
                ruta34[1].latitude+","+ruta34[1].longitude+"|"+"" +
                ruta34[2].latitude+","+ruta34[2].longitude+"|"+"" +
                ruta34[3].latitude+","+ruta34[3].longitude+"|"+"" +
                ruta34[4].latitude+","+ruta34[4].longitude+"|"+"" +
                ruta34[5].latitude+","+ruta34[5].longitude+"|"+"" +
                ruta34[6].latitude+","+ruta34[6].longitude+"|"+"" +
                ruta34[7].latitude+","+ruta34[7].longitude+"|"+"" +
                ruta34[8].latitude+","+ruta34[8].longitude+"|"+"" +
                ruta34[9].latitude+","+ruta34[9].longitude+"|"+"" +
                ruta34[10].latitude+","+ruta34[10].longitude+"|"+"" +
                ruta34[11].latitude+","+ruta34[11].longitude+"|"+"" +
                ruta34[12].latitude+","+ruta34[12].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;


        url[31] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta35[0].latitude+","+ruta35[0].longitude+"&destination="+ruta35[24].latitude+","+ruta35[24].longitude+
                "&waypoints="
                +
                ruta35[1].latitude+","+ruta35[1].longitude+"|"+"" +
                ruta35[2].latitude+","+ruta35[2].longitude+"|"+"" +
                ruta35[3].latitude+","+ruta35[3].longitude+"|"+"" +
                ruta35[4].latitude+","+ruta35[4].longitude+"|"+"" +
                ruta35[5].latitude+","+ruta35[5].longitude+"|"+"" +
                ruta35[6].latitude+","+ruta35[6].longitude+"|"+"" +
                ruta35[7].latitude+","+ruta35[7].longitude+"|"+"" +
                ruta35[8].latitude+","+ruta35[8].longitude+"|"+"" +
                ruta35[9].latitude+","+ruta35[9].longitude+"|"+"" +
                ruta35[10].latitude+","+ruta35[10].longitude+"|"+"" +
                ruta35[11].latitude+","+ruta35[11].longitude+"|"+"" +
                ruta35[12].latitude+","+ruta35[12].longitude+"|"+"" +
                ruta35[13].latitude+","+ruta35[13].longitude+"|"+"" +
                ruta35[14].latitude+","+ruta35[14].longitude+"|"+"" +
                ruta35[15].latitude+","+ruta35[15].longitude+"|"+"" +
                ruta35[16].latitude+","+ruta35[16].longitude+"|"+"" +
                ruta35[17].latitude+","+ruta35[17].longitude+"|"+"" +
                ruta35[18].latitude+","+ruta35[18].longitude+"|"+"" +
                ruta35[19].latitude+","+ruta35[19].longitude+"|"+"" +
                ruta35[20].latitude+","+ruta35[20].longitude+"|"+"" +
                ruta35[21].latitude+","+ruta35[21].longitude+"|"+"" +
                ruta35[22].latitude+","+ruta35[22].longitude+"|"+"" +
                ruta35[23].latitude+","+ruta35[23].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;


        url[32] = "https://maps.googleapis.com/maps/api/directions/json?origin="+ruta51[0].latitude+","+ruta51[0].longitude+"&destination="+ruta51[10].latitude+","+ruta51[10].longitude+
                "&waypoints="
                +
                ruta51[1].latitude+","+ruta51[1].longitude+"|"+"" +
                ruta51[2].latitude+","+ruta51[2].longitude+"|"+"" +
                ruta51[3].latitude+","+ruta51[3].longitude+"|"+"" +
                ruta51[4].latitude+","+ruta51[4].longitude+"|"+"" +
                ruta51[5].latitude+","+ruta51[5].longitude+"|"+"" +
                ruta51[6].latitude+","+ruta51[6].longitude+"|"+"" +
                ruta51[7].latitude+","+ruta51[7].longitude+"|"+"" +
                ruta51[8].latitude+","+ruta51[8].longitude+"|"+"" +
                ruta51[9].latitude+","+ruta51[9].longitude+
                "&sensor=true," +"MA&key="+LlaveMapaObtenidaGoogleMapsApi;

        if(puntos.size()!=0) {
            for (int p = 0; p < posiciones.size(); p++) {
                //Log.d("puntos.get(0)", ""+puntos.get(0));
                //Log.d("puntos.get(1)", ""+puntos.get(1));
                if (posiciones.size() == 1) {
                    //se crea el string con los puntos desde el punto mas cercano al origen y hasta el punto más cercano al destino
                    rutasDibujar[p] = "https://maps.googleapis.com/maps/api/directions/json?origin=" + rutasAgregadas2[posiciones.get(p)][puntos.get(0)].latitude + ","
                            + rutasAgregadas2[posiciones.get(p)][puntos.get(0)].longitude + "&destination=" + rutasAgregadas2[posiciones.get(p)][puntos.get(1)].latitude
                            + "," + rutasAgregadas2[posiciones.get(p)][puntos.get(1)].longitude + "&waypoints=";
                    for (int p1 = puntos.get(0)+1; p1 < puntos.get(1); p1++) {
                        if (p1 == puntos.get(1)-1) {
                            rutasDibujar[p] = rutasDibujar[p] + rutasAgregadas2[posiciones.get(p)][p1].latitude + ","
                                    + rutasAgregadas2[posiciones.get(p)][p1].longitude + "&sensor=true,"
                                    + "MA&key=" + LlaveMapaObtenidaGoogleMapsApi;
                        } else {
                            rutasDibujar[p] = rutasDibujar[p] + rutasAgregadas2[posiciones.get(p)][p1].latitude + ","
                                    + rutasAgregadas2[posiciones.get(p)][p1].longitude + "|" + "";
                        }
                    }
                    Log.d("Rutas dibujar[p]", ""+rutasDibujar[p]);
                }else if(posiciones.size() == 2) {
                    //se calculan los puntos de intersección entre ambas rutas
                    int punto1 = puntoEntreDosRutas(rutasAgregadas2[posiciones.get(0)], rutasAgregadas2[posiciones.get(1)]);
                    int punto2 = puntoEntreDosRutas(rutasAgregadas2[posiciones.get(1)], rutasAgregadas2[posiciones.get(0)]);
                    //Log.d("Punto1..", ""+punto1);
                    //Log.d("Punto2..", ""+punto2);
                    mMap.addMarker(new MarkerOptions().position(rutasAgregadas2[posiciones.get(1)][punto2]).title("Intersección").
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    if (p == 0) {
                        //se crea el string entre el punto más cercano al origen y el punto más cercano a la otra ruta
                        rutasDibujar[p] = "https://maps.googleapis.com/maps/api/directions/json?origin=" + rutasAgregadas2[posiciones.get(p)][puntos.get(0)].latitude + ","
                                + rutasAgregadas2[posiciones.get(p)][puntos.get(0)].longitude + "&destination=" + rutasAgregadas2[posiciones.get(p)][punto1].latitude
                                + "," + rutasAgregadas2[posiciones.get(p)][punto1].longitude + "&waypoints=";
                        for (int p1 = puntos.get(0)+1; p1 <= punto1; p1++) {
                            if (p1 == punto1) {
                                rutasDibujar[p] = rutasDibujar[p] + rutasAgregadas2[posiciones.get(p)][p1].latitude + ","
                                        + rutasAgregadas2[posiciones.get(p)][p1].longitude + "&sensor=true,"
                                        + "MA&key=" + LlaveMapaObtenidaGoogleMapsApi;
                            } else {
                                rutasDibujar[p] = rutasDibujar[p] + rutasAgregadas2[posiciones.get(p)][p1].latitude + ","
                                        + rutasAgregadas2[posiciones.get(p)][p1].longitude + "|" + "";
                            }
                        }
                    } else {
                        //se crea el string entre el punto más cercano a la otra ruta hasta el punto de destino
                        rutasDibujar[p] = "https://maps.googleapis.com/maps/api/directions/json?origin=" + rutasAgregadas2[posiciones.get(p)][punto2].latitude + ","
                                + rutasAgregadas2[posiciones.get(p)][punto2].longitude + "&destination=" + rutasAgregadas2[posiciones.get(p)][puntos.get(1)].latitude
                                + "," + rutasAgregadas2[posiciones.get(p)][puntos.get(1)].longitude + "&waypoints=";
                        for (int p1 = punto2; p1 <= puntos.get(1); p1++) {
                            if (p1 == puntos.get(1)) {
                                rutasDibujar[p] = rutasDibujar[p] + rutasAgregadas2[posiciones.get(p)][p1].latitude + ","
                                        + rutasAgregadas2[posiciones.get(p)][p1].longitude + "&sensor=true,"
                                        + "MA&key=" + LlaveMapaObtenidaGoogleMapsApi;
                            } else {
                                rutasDibujar[p] = rutasDibujar[p] + rutasAgregadas2[posiciones.get(p)][p1].latitude + ","
                                        + rutasAgregadas2[posiciones.get(p)][p1].longitude + "|" + "";
                            }
                        }
                    }
                }
            }
        }else{
            for (int p = 0; p < posiciones.size(); p++) {
                rutasDibujar[p] = url[posiciones.get(p)]; //si solo se selecciona una ruta
            }
        }

        return rutasDibujar; //se retornan la cadena de la o las rutas a dibujar
    }

    //se lee la cadena de la ruta que se le envía
    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";

                try {
                    HttpConnection http = new HttpConnection();
                    data = http.readUrl(url[0]);
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        //se escribe en el json
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        //se lee el json y se gráfican las líneas
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            if (routes.size()>0) {
                llamadaMetodo++;
                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                        double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(4);
                    //color con el que se dibujará la línea
                    if(llamadaMetodo==1){
                        polyLineOptions.color(Color.BLUE);
                    }else if(llamadaMetodo==2){
                        polyLineOptions.color(Color.RED);
                    }else{
                        polyLineOptions.color(Color.BLACK);
                    }
                }

                mMap.addPolyline(polyLineOptions);
            } else
                Toast.makeText(getApplicationContext(), "NO HAY DATOS DE LA RUTA", Toast.LENGTH_SHORT).show();

        }
    }
}
