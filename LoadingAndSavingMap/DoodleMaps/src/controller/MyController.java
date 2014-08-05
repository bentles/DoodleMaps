package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import application.Main;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class MyController implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	
	//Instantiate References to components located in FXML File (Layout.fxml)
	@FXML private Button btnPlot;
	@FXML private TextField txtStart;
	@FXML private TextField txtEnd;
	@FXML private WebView wbView1;
	@FXML private Button btnSave;
	@FXML private WebView wbView2;
	//Create a Global Variable to store the user's current directions in order to save them to a .html file
	private String directions;
	
	
	// Retrieves Users proposed Starting point and destination and loads .html file to display route on screen
	public void PlotDirections(ActionEvent event){
		
		// Initial document (will be put into a file to look prettier)
		directions = "<!DOCTYPE html>"+
	    		"<html>"+
	    		 " <head>"+
	    		  "  <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">"+
	    		   " <meta charset=\"utf-8\">"+
	    		    "<title>Draggable directions</title>"+
	    		    "<style>"+
	    		     " html, body, #map-canvas {"+
	    		      "  height: 100%;"+
	    		       " margin: 0px;"+
	    		        "padding: 0px"+
	    		      "}"+
	    		  "  </style>"+
	    		"    <script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyDeSWWyq34KWp3EHRb3JOwcCH71aZvnWxM\"></script>"+
	    		 "   <script>"+

	    		"var rendererOptions = {"+
	    		"  draggable: true"+
	    		"};"+
	    		"var directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);;"+
	    		"var directionsService = new google.maps.DirectionsService();"+
	    		"var map;"+

	    		"var pe = new google.maps.LatLng(-33.959311, 25.618076);"+

	    		"function initialize() {"+

	    		 " var mapOptions = {"+
	    		 "   zoom: 7,"+
	    		 "   center: pe"+
	    		 " };"+
	    		 " map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);"+
	    		 " directionsDisplay.setMap(map);"+
	    		 " directionsDisplay.setPanel(document.getElementById('directionsPanel'));"+

	    		"  google.maps.event.addListener(directionsDisplay, 'directions_changed', function() {"+
	    		"    computeTotalDistance(directionsDisplay.getDirections());"+
	    		"  });"+

	    		"  calcRoute();"+
	    		"}"+

	    		"function calcRoute() {"+

	    		 " var request = {"+
	    		 "   origin: '"
	    		 +txtStart.getText()+"',"+                      ///
	    		 "   destination: '"							///  Inserting Users Start and End points
	    		 +txtEnd.getText()+"',"+						///
	    		 "   travelMode: google.maps.TravelMode.DRIVING"+
	    		 " };"+
	    		 " directionsService.route(request, function(response, status) {"+
	    		 "   if (status == google.maps.DirectionsStatus.OK) {"+
	    		 "     directionsDisplay.setDirections(response);"+
	    		 "   }"+
	    		 " });"+
	    		"}"+

	    		"function computeTotalDistance(result) {"+
	    		"  var total = 0;"+
	    		"  var myroute = result.routes[0];"+
	    		"  for (var i = 0; i < myroute.legs.length; i++) {"+
	    		"    total += myroute.legs[i].distance.value;"+
	    		"  }"+
	    		"  total = total / 1000.0;"+
	    		"  document.getElementById('total').innerHTML = total + ' km';"+
	    		"}"+

	    		"google.maps.event.addDomListener(window, 'load', initialize);"+

	    		"    </script>"+
	    		"  </head>"+
	    		"  <body>"+
	    		"    <div id=\"map-canvas\" style=\"float:left;width:65%; height:100%\"></div>"+
	    		"    <div id=\"directionsPanel\" style=\"float:right;width:35%;height:50%\">"+
	    		"    <p>Total Distance: <span id=\"total\"></span></p>"+
	    		"    </div>"+
	    		"  </body>"+
	    		"</html>";
		
		// Writing the newly composed .html file to a temp file to be loaded into the webview
		try {
			PrintWriter pw = new PrintWriter("tempFile.html");
			pw.write(directions);
			pw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Name of the file in working directory
        String html = "tempFile.html";
        
        //Creating the URI of where the file is located
        URI uri = java.nio.file.Paths.get(html).toAbsolutePath().toUri();
        
        //Loading the temp .html file via the WebView's Engine
        wbView1.getEngine().load(uri.toString());
		
	}
	
	// Method to save the file where desired (for development purpose only - will be set location in final system)
	public void SaveDirections(ActionEvent event){
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Location to Save");
		File file = fc.showSaveDialog(Main.primStage);
		
		File NewFile = new File(file+".html");
		
		try {
			PrintWriter pw = new PrintWriter(NewFile);
			pw.write(directions);                        /// Using the Global variable - Couldn't find an effective way of extracting the .html from the WebView
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Method to load desired map that has been saved in .html form (Will also be modified for final system)
	public void LoadMap(ActionEvent event){
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Select File");

		File file = fc.showOpenDialog(Main.primStage);
		
		if (file != null){
			URI uri = java.nio.file.Paths.get(file.getAbsolutePath()).toAbsolutePath().toUri();
			wbView2.getEngine().load(uri.toString());
		}

        
	}
	
}
