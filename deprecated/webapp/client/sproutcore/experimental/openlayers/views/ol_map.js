/** @class Openlayers.OLMapView

  Sproutcore view to display a map from OpenLayers.

  @extends SC.View
*/
Openlayers.OLMapView = SC.View.extend(
/** @scope Openlayers.OLMapView.prototype */
{
    map: null,
    startCenterLonLat: new OpenLayers.LonLat( - 71, 54),
    startCenterLevel: 3,

    /**
	*	get the openlayers map reference
	*/
    getMap: function() {
        return map;
    },

    setupMap: function() {
        OpenLayers.ImgPath = 'http://openlayers.org/api/img/';
        OpenLayers.theme = 'http://openlayers.org/api/theme';
        this.map = new OpenLayers.Map({
            controls: [
            new OpenLayers.Control.PanZoom(), new OpenLayers.Control.Navigation(), new OpenLayers.Control.LayerSwitcher(), new OpenLayers.Control.MousePosition()],
            projection: new OpenLayers.Projection("EPSG:900913"),
            //for controls like MousePosition
            displayProjection: new OpenLayers.Projection("EPSG:4326"),
            numZoomLevels: 18,
            units: "m",
            maxResolution: 156543.0339,
            maxExtent: new OpenLayers.Bounds( - 20037508, -20037508, 20037508, 20037508.34)
        });
    },

    addLayer: function(layer) {
        if (this.map) {
            this.map.addLayer(layer);
        }
    },

    addGoogleMapLayer: function(layerName, type) {
        if (!layerName) {
            layerName = 'Google';
        }
        if (!type) {
            //G_SATELLITE_MAP,G_NORMAL_MAP,G_HYBRID_MAP
            type = G_NORMAL_MAP;
        }
        var google = new OpenLayers.Layer.Google(layerName, {
            type: type,
            sphericalMercator: true
        });
        this.addLayer(google);
        return google;
    },

    addControl: function(control) {
        if (this.map) {
            this.map.addControl(control);
        }
    },

    didAppendToDocument: function() {
        this.addMapToDocument();
    },

    addMapToDocument: function() {
        var mapCanvasId = this.get("layerId");
        if (this.map) {
            this.map.render(mapCanvasId);
            if (this.startCenterLonLat) {
                var center = this.startCenterLonLat.clone();
                center.transform(new OpenLayers.Projection("EPSG:4326"), this.map.getProjectionObject());
                this.map.setCenter(center, this.startCenterLevel);
            }
        } else {
            console.log("Map was'nt init!  Call setup() method.");
        }
    }

});
