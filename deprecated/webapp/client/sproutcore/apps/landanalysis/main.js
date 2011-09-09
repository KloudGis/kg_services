// ==========================================================================
// Project:   Landanalysis
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

// This is the function that will start your app running.  The default
// implementation will load any fixtures you have created then instantiate
// your controllers and awake the elements on your page.
//
// As you develop your application you will probably want to override this.
// See comments for some pointers on what to do next.
//
Landanalysis.main = function main() {

    //error!
    /*SC.ExceptionHandler.handleException = function(exception) {
	  	console.log('Error!!' + exception);
	}*/

    //SC.LOG_OBSERVERS = YES;
    SC.routes.add(':pageName', Landanalysis.routes, 'gotoRoute');
    SC.routes.add(':', Landanalysis.routes, 'gotoRoute');


    Kloudgis.mapController.set('content', Landanalysis.getPath('mainPage.mainPane.mapView'));
    Kloudgis.mapController.content.set('startCenterLonLat', new OpenLayers.LonLat( - 73.2, 45.5));
    Kloudgis.mapController.content.set('startZoomLevel', 12);

    var note = new OpenLayers.Layer.WMS("Notes", "/geoserver/wms", {
        layers: 'cite:note',
        format: 'image/png',
        transparent: true,
        tiled: true,
        srs: 'EPSG:4326'
    },
    {
        isBaseLayer: false,
        geometryAttr: 'geom',
        buffer: 1,
        displayOutsideMaxExtent: true,
        tablename: 'note',
        visibility: true
    });

    var road = new OpenLayers.Layer.WMS("Roads", "/geoserver/gwc/service/wms", {
        layers: 'cite:rue',
        format: 'image/png',
        transparent: true,
        tiled: true,
        srs: 'EPSG:4326'
    },
    {
        isBaseLayer: false,
        geometryAttr: 'geo',
        buffer: 0,
        displayOutsideMaxExtent: false,
        tablename: 'rue',
        visibility: false
    });

    var hydro = new OpenLayers.Layer.WMS("Hydro", "/geoserver/gwc/service/wms", {
        layers: 'cite:hydro',
        format: 'image/png',
        transparent: true,
        tiled: true,
        srs: 'EPSG:4326'
    },
    {
        isBaseLayer: false,
        geometryAttr: 'geo',
        buffer: 0,
        displayOutsideMaxExtent: false,
        tablename: 'hydro',
        visibility: false
    });

    var limit = new OpenLayers.Layer.WMS("Limits", "/geoserver/gwc/service/wms", {
        layers: 'cite:limit_items',
        format: 'image/png',
        transparent: true,
        tiled: true,
        srs: 'EPSG:4326'
    },
    {
        isBaseLayer: false,
        geometryAttr: 'geo',
        buffer: 1,
        displayOutsideMaxExtent: false,
        tablename: 'limit_items',
        visibility: true,
		//transitionEffect: 'resize'
    });

	var lot = new OpenLayers.Layer.WMS("Lots", "/geoserver/gwc/service/wms", {
        layers: 'cite:lot',
        format: 'image/png',
        transparent: true,
        tiled: true,
        srs: 'EPSG:4326'
    },
    {
        isBaseLayer: false,
        geometryAttr: 'geo',
        buffer: 1,
        displayOutsideMaxExtent: false,
        tablename: 'lot',
        visibility: true
    });

	var arpenteur = new OpenLayers.Layer.WMS("Lots", "/geoserver/gwc/service/wms", {
        layers: 'cite:arpenteur',
        format: 'image/png',
        transparent: true,
        tiled: true,
        srs: 'EPSG:4326'
    },
    {
        isBaseLayer: false,
        geometryAttr: 'geo',
        buffer: 1,
        displayOutsideMaxExtent: false,
        tablename: 'arpenteur',
        visibility: false
    });

    Kloudgis.layerController.addGoogleMapLayer("Google Street");
    Kloudgis.layerController.addOpenStreetMapLayer("Open Street");

    Kloudgis.layerController.addLayer(limit);
	Kloudgis.layerController.addLayer(lot);
    Kloudgis.layerController.addLayer(hydro);
    Kloudgis.layerController.addLayer(road);
	Kloudgis.layerController.addLayer(arpenteur);
    Kloudgis.layerController.addLayer(note);


    Kloudgis.mapControlController.set('wmsLayersSelection', [note, arpenteur, limit]);
    Kloudgis.mapControlController.addWMSSelectionControl();
    Kloudgis.mapControlController.addDrawFeatureControls();
    Kloudgis.mapControlController.set('activeTool', 'selection');    

    //set the framework store
    Kloudgis.store = Landanalysis.store;
    Kloudgis.set('context', Landanalysis.get('context'));
	Kloudgis.set('context_client', Landanalysis.get('context_client'));

	Kloudgis.modelManager.loadModel();
	Kloudgis.modelManager.onFeaturetypesLoaded(null, function(){
		var ctrl = Kloudgis.modelManager.getController('Note');
		if(!SC.none(ctrl) && ctrl.get('handlePointAndLineCreation')){
			ctrl.handlePointAndLineCreation();
		}
	});
    //logged user
    Kloudgis.securityController.updateLoggedUser();

};

function main() {
    Landanalysis.main();
}
