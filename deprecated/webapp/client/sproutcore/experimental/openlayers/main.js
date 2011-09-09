// ==========================================================================
// Project:   Openlayers
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Openlayers */

// This is the function that will start your app running.  The default
// implementation will load any fixtures you have created then instantiate
// your controllers and awake the elements on your page.
//
// As you develop your application you will probably want to override this.
// See comments for some pointers on what to do next.
//
Openlayers.main = function main() {

  // Step 1: Instantiate Your Views
  // The default code here will make the mainPane for your application visible
  // on screen.  If you app gets any level of complexity, you will probably 
  // create multiple pages and panes.  

	Openlayers.getPath('mainPage.mainPane.mapView').setupMap();
	Openlayers.getPath('mainPage.mainPane.mapView').addGoogleMapLayer();
	
	//add custom layer
	var layer = new OpenLayers.Layer.WMS(
        "Mun WMS/WFS",
        '/geoserver/wms',		
        {layers: 'topp:lbs_municipality', format: 'image/gif', transparent: true},{
            isBaseLayer: false,
        }
    );
	Openlayers.getPath('mainPage.mainPane.mapView').addLayer(layer);

	//addCustom control
	var control = new OpenLayers.Control.GetFeature({
        protocol: OpenLayers.Protocol.WFS.fromWMSLayer(layer, {
					geometryName : 'geo'
				}),
        box: true,
        hover: true,
        multipleKey: "shiftKey",
        toggleKey: "ctrlKey"
    });
    control.events.register("featureselected", this, function(e) {      
		console.log("Selection: " + e.feature.fid);
    });
	Openlayers.getPath('mainPage.mainPane.mapView').addControl(control);
	control.activate();
	
	//show it on screen
  	Openlayers.getPath('mainPage.mainPane').append() ;
  	
} ;

function main() { Openlayers.main(); }
