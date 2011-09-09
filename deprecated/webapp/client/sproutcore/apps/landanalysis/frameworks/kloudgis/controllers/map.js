// ==========================================================================
// Project:   Kloudgis.mapController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
sc_require('models/envelope')
Kloudgis.mapController = SC.ObjectController.create(
/** @scope Kloudgis.mapController.prototype */
{
    mapExtentChanged: 0,
    //layer to display the temp highlight
    tempHighlightLayer: null,
    markersLayer: null,
    hlIcon: null,
    //layer to show Selection
    selectionLayer: null,
    //current selection feature (cache for the layer)
    selectionFeature: null,

    _selectionGeoReponse: null,
    _highlightGeoReponse: null,

    _locIcon: null,
    _locMarker: null,

    addLayer: function(layer) {
        this.getMap().addLayer(layer);
        if (this.selectionLayer) {
            this.getMap().raiseLayer(this.selectionLayer, 3);
        }
        if (this.tempHighlightLayer) {
            this.getMap().raiseLayer(this.tempHighlightLayer, 3);
        }
        if (this.markersLayer) {
            this.getMap().raiseLayer(this.markersLayer, 3);
        }
    },

    contentDidChanged: function() {
        var mapview = this.get('content');
        if (mapview) {
            mapview.setupMap();
            mapview.getMap().projection = new OpenLayers.Projection("EPSG:900913");
            //selection
            var sLayer = new OpenLayers.Layer.Vector("Selection", {
                styleMap: new OpenLayers.Style(OpenLayers.Feature.Vector.style["select"]),
                isBaseLayer: false,
                displayInLayerSwitcher: false,
            });
            this.set('selectionLayer', sLayer);
            //highlight
            var hlLayer = new OpenLayers.Layer.Vector("Highlight", {
                /*styleMap: new OpenLayers.Style({
                    fillColor: "#EEFF33",
                    fillOpacity: 0.3,
                    strokeColor: "#E4DF4F",
                    strokeOpacity: 0.8,
                    graphicZIndex: 2
                }),*/
                isBaseLayer: false,
                displayInLayerSwitcher: false,
            });
            this.set('tempHighlightLayer', hlLayer);
            //highlight - markers
            var markersLayer = new OpenLayers.Layer.Markers("Markers", {
                isBaseLayer: false,
                displayInLayerSwitcher: false,
            });
            this.set('markersLayer', markersLayer);
            //bind to map
            mapview.addLayer(sLayer);
            mapview.addLayer(hlLayer);
            mapview.addLayer(markersLayer);
            //map move listener (pan + zoom)
            this.getMap().events.register('moveend', this, this.extentChanged);
        }
    }.observes('content'),

    highlightSelectionFeature: function(feature) {
        if (this.selectionFeature !== feature) {
            if (this._selectionGeoReponse) {
                this._selectionGeoReponse.cancel();
            }
            this.clearHighlightSelectionFeatures();
            feature.fetchDefaultGeometry(this, this.highlightSelectionFeatureGeometryWKT, this.selectionGeoResponseCreated);
            this.set('selectionFeature', feature);
        }
    },

    selectionGeoResponseCreated: function(response) {
        this._selectionGeoReponse = response;
    },

    highlightSelectionFeatureGeometryWKT: function(wkt) {
        if (wkt) {
            var ol_geo = OpenLayers.Geometry.fromWKT(wkt);
            if (ol_geo) {
                this.highlightSelectionFeatureGeometry(ol_geo.transform(this.getLonLatProj(), this.getMap().getProjectionObject()));
            }
        }
    },

    highlightSelectionFeatureGeometry: function(geom, kfeature) {
        if (kfeature) {
            this.set('selectionFeature', kfeature);
        }
        //console.log('hl selection for:');
        //console.log(geom);
        var featureVector = new OpenLayers.Feature.Vector(geom);
        if (this.selectionLayer) {
            this.selectionLayer.addFeatures(featureVector);
        }
    },

    clearHighlightSelectionFeatures: function() {
        if (this.selectionLayer) {
            this.selectionLayer.removeAllFeatures();
            this.set('selectionFeature', null);
        }
        this.clearTempHighlight();
    },

    refreshSelectionHighlight: function() {
        if (!SC.none(this.selectionFeature)) {
            this.selectionLayer.removeAllFeatures();
            this.selectionFeature.fetchDefaultGeometry(this, this.highlightSelectionFeatureGeometryWKT, this.selectionGeoResponseCreated);
        }
    },

    tempHighlightFeature: function(feature) {
        this.clearTempHighlight();
        if (this._highlightGeoReponse) {
            this._highlightGeoReponse.cancel();
        }
        feature.fetchDefaultGeometry(this, this.tempHighlightFeatureGeometryWKT, this.highlightGeoResponseCreated);
    },

    highlightGeoResponseCreated: function(response) {
        this._highlightGeoReponse = response;
    },

    tempHighlightFeatureGeometryWKT: function(wkt) {
        if (wkt) {
            var ol_geo = OpenLayers.Geometry.fromWKT(wkt);
            if (ol_geo) {
                this.tempHighlightFeatureGeometry(ol_geo.transform(this.getLonLatProj(), this.getMap().getProjectionObject()));
            }
        }
    },

    tempHighlightFeatureGeometry: function(geom) {
        if (SC.none(this.hlIcon)) {
            this.hlIcon = new OpenLayers.Icon(static_url('images/map/marker.png'), new OpenLayers.Size(35, 30), new OpenLayers.Pixel( - 17, -27));
        }
        if (geom.CLASS_NAME === 'OpenLayers.Geometry.Point') {
            if (this.markersLayer) {
                this.markersLayer.addMarker(new OpenLayers.Marker(new OpenLayers.LonLat(geom.x, geom.y), this.hlIcon));
            }
        }
        else {
            var featureVector = new OpenLayers.Feature.Vector(geom);
            if (this.tempHighlightLayer) {
                this.tempHighlightLayer.addFeatures(featureVector);
            }
            if (this.markersLayer) {
                var centroid = geom.getCentroid();
                this.markersLayer.addMarker(new OpenLayers.Marker(new OpenLayers.LonLat(centroid.x, centroid.y), this.hlIcon));
            }
        }
    },

    clearTempHighlight: function() {
        if (this.tempHighlightLayer) {
            this.tempHighlightLayer.removeAllFeatures();
        }
        if (this.markersLayer) {
            this.markersLayer.clearMarkers();
        }
    },

    extentChanged: function(e) {
        //console.log('map moved: ' + this.mapExtentChanged);
        //console.log(e);
        var changed = this.mapExtentChanged + 1;
        this.set('mapExtentChanged', changed);
    },

    getMap: function() {
        var ol_mapview = this.get('content');
        if (ol_mapview) {
            return ol_mapview.getMap();
        }
        return NO;
    },

    getLonLatProj: function() {
        if (this.get('content')) {
            return this.get('content').get('lonlatProj');
        }
    },

    getExtentEnv: function() {
        var map = this.getMap();
        if (map) {
            var extentOL = map.getExtent();
            if (extentOL) {
                var proj = map.getProjectionObject();
                var lonlatProj = this.getLonLatProj();
                var extLonLat = extentOL.transform(proj, lonlatProj);
                var env = Kloudgis.Envelope.create({});
                env.set('lowLon', extLonLat.left);
                env.set('lowLat', extLonLat.bottom);
                env.set('hiLon', extLonLat.right);
                env.set('hiLat', extLonLat.top);
                return env;
            }
        }
        return NO;
    },

    getScale: function() {
        var map = this.getMap();
        if (map) {
            return map.getScale();
        }
        return NO;
    },

    zoomHighlightFeature: function(feature) {
        var bounds = feature.get('boundsLonLat');
        if (bounds) {
            this.zoomExtentLonLat(bounds);
            this.tempHighlightFeature(feature);
        }
    },

    zoomExtentLonLat: function(bounds) {
        var map = this.getMap();
        if (map && bounds) {
            var boundsSrid = bounds.clone().transform(this.getLonLatProj(), map.getProjectionObject());
            this.zoomExtent(boundsSrid);
        }
    },

    //zoom the openlayers bounds parameter
    zoomExtent: function(bounds) {
        var map = this.getMap();
        if (map && bounds) {
            var zoom = map.getZoomForExtent(bounds, true)
            var center = bounds.getCenterLonLat();
            map.setCenter(center, Math.min(zoom - 1, 16));
            return YES;
        }
        return NO;
    },

    //GEO LOCALISATION
    currentPosition: null,

    userPosition: function() {
        var pos = this.get('currentPosition');
        if (!SC.none(pos)) {
            var lon = pos.coords.longitude;
            var lat = pos.coords.latitude;
            var lonLat = new OpenLayers.LonLat(lon, lat);
            var coordinate = lonLat.transform(this.getLonLatProj(), this.getMap().getProjectionObject());
            return coordinate;
        }
    }.property('currentPosition').cacheable(),

    userPositionBounds: function() {
        var pos = this.get('currentPosition');
        if (!SC.none(pos)) {
            
			//meters
            var radius = pos.coords.accuracy;
			var posMeters = this.get('userPosition');
			var lon = posMeters.lon;
            var lat = posMeters.lat;
            var bbox = new OpenLayers.Bounds(lon - radius, lat - radius, lon + radius, lat + radius);
            return bbox;
        }
    }.property('currentPosition').cacheable(),

    currentPositionError: null,

    isZoomToUserPosition: function() {
        if (navigator.geolocation) {
            return YES;
        }
        return NO;
    }.property(),

    zoomUserPosition: function() {
        this.fetchUserPosition(this.zoomPositionCallback, this.userPositionErrorCallback);
    },

    fetchUserPosition: function(sucessCallback, errorCallback, options) {
        if (!options) {
            options = {
				//use GPS if possible
				enableHighAccuracy:true
			};
        }
        navigator.geolocation.getCurrentPosition(sucessCallback, errorCallback, options);
    },

    zoomPositionCallback: function(position) {
        Kloudgis.mapController.set('currentPosition', position);
        Kloudgis.mapController.zoomCurrentPosition();
    },

    zoomCurrentPosition: function() {
        var bounds = this.get('userPositionBounds');
        if (!SC.none(bounds)) {
            this.zoomExtent(bounds);
            if (this.markersLayer) {
                var coord = this.get('userPosition');
                if (!SC.none(this._locMarker)) {
                    this.markersLayer.removeMarker(this._locMarker);
                }
                if (SC.none(this._locIcon)) {
                    this._locIcon = new OpenLayers.Icon(static_url('images/map/loc_marker.png'), new OpenLayers.Size(32, 32), new OpenLayers.Pixel( - 4, -28));
                }
                this._locMarker = new OpenLayers.Marker(new OpenLayers.LonLat(coord.lon, coord.lat), this._locIcon);
                this.markersLayer.addMarker(this._locMarker);
            }
        }
    },

    userPositionErrorCallback: function(error) {
        switch (error.code) {
        case 0:
            console.log("There was an error while retrieving your location: " + error.message);
            break;
        case 1:
            console.log("The user didn't accept to provide the location: ");
            break;
        case 2:
            console.log("The browser was unable to determine your location: " + error.message);
            break;
        case 3:
            console.log("The browser timed out before retrieving the location.");
            break;
        }
        Kloudgis.mapController.set('currentPositionError', error);
    }

    //END GEOLOCALISATION
});
