/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author jnfco
 */
public class AgregarCopiaController implements Initializable {

    @FXML
    private Button buttonGuardar;
    @FXML
    private Button buttonCancelar;
    @FXML
    private BorderPane borderPaneAgregarCopia;
    @FXML
    private Label labelIsbn;
    @FXML
    private Label labelTitulo;
    @FXML
    private Label labelAño;
    @FXML
    private Label labelEdicion;
    @FXML
    private Label labelCodigo;
    @FXML
    private ComboBox<String> comboBoxEstado;
    @FXML
    private ComboBox<String> comboBoxEstante;
    @FXML
    private ComboBox<String> comboBoxNivel;
    
    private String isbn;
    private String titulo;
    private String año;
    private String edicion;
    private String codigo;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        comboBoxEstado.getItems().addAll("Habilitado", "Deshabilitado", "Prestado","En exhibicion");
        System.out.println("Datos: isbn "+isbn+" titulo: "+titulo+" año: "+año);
        labelIsbn.setText(isbn);
        labelTitulo.setText(titulo);
        labelAño.setText(año);
        labelEdicion.setText(edicion);
        labelCodigo.setText(codigo);
        // TODO
    }    

    @FXML
    private void guardarCopia(ActionEvent event) 
    {
        crearCopia();
         ((Stage)borderPaneAgregarCopia.getScene().getWindow()).close();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        
        ((Stage)borderPaneAgregarCopia.getScene().getWindow()).close();
    }
    
    
     private void crearCopia(){
        String estado = comboBoxEstado.getValue().equals("")?"":comboBoxEstado.getValue();
        isbn=isbn.equals("")?"":isbn;
        String codigo=getSaltString().equals("")?"":getSaltString();
        String numeroCopia="2".equals("")?"":"2";
        String  ubicacion= " ".equals("")?"":" ";
        
        
        
       
            
            try {
                this.crearCopiaEnBaseDeDatos(isbn, estado,codigo,numeroCopia , ubicacion);
            } catch (UnsupportedEncodingException ex) {
                System.out.println(ex);
               // Logger.getLogger(CrearLectorController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                System.out.println(ex);
                //Logger.getLogger(CrearLectorController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(AgregarCopiaController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        
        
        
        
    }
    
    
    //TODO: Decodificar JSON
    /**
     * 
     * @param isbn
     * @param autor
     * @param anio
     * @param dewey
     * @param titulo
     * @param edicion
     */
    
    public void crearCopiaEnBaseDeDatos(String isbn,String estado,String codigo,String numeroCopia,String ubicacion) throws MalformedURLException, UnsupportedEncodingException, IOException, JSONException{
    
        System.out.println("datos:"+isbn+estado+codigo+numeroCopia+ubicacion);
    URL url = new URL(Valores.SingletonServidor.getInstancia().getServidor()+"/"+Valores.ValoresEstaticos.crearCopiaPHP);
    Map<String,Object> params = new LinkedHashMap<>();
    params.put("codigo",codigo);
    params.put("isbnlibro",isbn);
    params.put("numerocopia", numeroCopia);
    params.put("estado",estado);
    params.put("ubicacion",ubicacion);
    StringBuilder postData = new StringBuilder();
    for (Map.Entry<String,Object> param : params.entrySet()) {
        if (postData.length() != 0) postData.append('&');
        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
    }

    // Convierte el array, para ser enviendo
    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

    // Conectar al server
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    
    // Configura
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
    conn.setDoOutput(true);
    conn.getOutputStream().write(postDataBytes);

    // Obtiene la respuesta del servidor
    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
    
    String response="";
    System.out.println(in);
    for (int c; (c = in.read()) >= 0;)
       response=response + (char)c;
    
    //Convierte el json enviado (decodigicado)
    JSONObject obj = new JSONObject(response);
    String mensaje = obj.getString("mensaje");
    
    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
    alerta.setTitle("Mensaje");
    alerta.setContentText(mensaje);
    alerta.showAndWait();
   // System.out.println(response);
}
    
    public void setIsbn(String isbn)
    {
        this.isbn=isbn;
    }
    
    public void setTitulo (String titulo)
    {
        this.titulo=titulo;
    }
    
    public void setAño(String año)
    {
        this.año=año;
    }
    
    public void setEdicion(String edicion)
    {
        this.edicion=edicion;
    }
    
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
