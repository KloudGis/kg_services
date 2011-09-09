
Googlemaps.GMapView = SC.View.extend({

  didAppendToDocument: function(){
	 this.initMap();
  },	

  addOverlay: function (overlay) {
    this.get("mapObject").addOverlay(overlay);
  },

  addPlacemark: function (point) {
    var placemark = this.createPlacemark(point);
    this.addOverlay(placemark);
  },

  createPlacemark: function (point) {
    var latitude = point.get("latitude"),
        longitude = point.get("longitude"),
        gLatLng = new GLatLng(latitude, longitude),
        gMarker = new GMarker(gLatLng);
    return gMarker;
  },

  initMap: function () {
    var mapCanvasId = this.get("layerId");

    // initialize the map
    if (GBrowserIsCompatible()) {
      var map = new GMap2(document.getElementById(mapCanvasId));
      this.set("mapObject", map);
      this.setCenterToSFBay();
      this.setUIToDefault();
      var mapView = this;
      GEvent.addListener(map, "click", function (overlay, gLatLng) {
        // this this may be the map object and not the map view
        mapView.gMapClickDidOccur(overlay, gLatLng);
      });
    }
  },

  gMapClickDidOccur: function (context, gLatLng) {
    if (context) {
      this.mapClickDidOccurWithOverlay(context);
    } else {
      this.mapClickDidOccurWithoutOverlay(gLatLng);
    }
  },

  mapClickDidOccurWithOverlay: function (overlay) {
    PolygonTool.gMapController.mapClickDidOccurWithOverlay(overlay);
  },

  mapClickDidOccurWithoutOverlay: function (gLatLng) {
    PolygonTool.gMapController.mapClickDidOccurWithoutOverlay(gLatLng);
  },

  setCenterToSFBay: function () {
    this.setCenter(37.79, -122.39, 11);
  },

  setCenter: function (latitude, longitude, zoom) {
    var point = new GLatLng(latitude, longitude);
    this.get("mapObject").setCenter(point, zoom);
  },

  setUIToDefault: function () {
    this.get("mapObject").setUIToDefault();
  }

});
