// ==========================================================================
// Project:   Kloudgis.layerController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Controller for the map layers

  @extends SC.Object
*/
Kloudgis.layerController = SC.ArrayController.create(
/** @scope Kloudgis.layerController.prototype */
{

    allowsMultipleSelection: NO,

    googleMapType: google.maps.MapTypeId.ROADMAP,
    googleLayer: null,
    osmLayer: null,

    GOOGLE: 'google',
    OSM: 'osm',

    googleActive: NO,

    //boolean
    layersLoading: NO,
    //counter
    loadCounter: 0,

    addLayer: function(layer) {
        if (!this.get('content')) {
            this.set('content', []);
        }
        this.addObject(layer);
        Kloudgis.mapController.addLayer(layer);
        layer.events.register('loadstart', this, this.increaseLoadCounter);
        layer.events.register('loadend', this, this.decreaseLoadCounter);
    },

    increaseLoadCounter: function(event) {
        this.incrementProperty('loadCounter');
        this.setIfChanged('layersLoading', YES);
        //console.log(event.object.name + ' started');
    },

    decreaseLoadCounter: function(event) {
        this.decrementProperty('loadCounter');
        if(this.get('loadCounter') === 0){
			this.setIfChanged('layersLoading', NO);
		}
    },

    addGoogleMapLayer: function(layerName, type) {
        if (!layerName) {
            layerName = 'Google';
        }
        //type:
        //google.maps.MapTypeId.TERRAIN
        //google.maps.MapTypeId.HYBRID
        //google.maps.MapTypeId.SATELLITE			
        var google = new OpenLayers.Layer.Google(layerName, {
            type: type,
            numZoomLevels: 22
        });
        this.addLayer(google);
        this.set('googleLayer', google);
        return google;
    },

    addOpenStreetMapLayer: function(layerName) {
        if (!layerName) {
            layerName = 'OSM';
        }
        var osm = new OpenLayers.Layer.OSM(layerName);
        this.addLayer(osm);
        this.set('osmLayer', osm);
        return osm;
    },

    googleMapTypeDidChanged: function() {
        if (this.googleLayer) {
            this.googleLayer.type = this.googleMapType;
            this.googleLayer.redraw(true);
        }
    }.observes('googleMapType'),

    googleActive: function() {
        return this.get('baseLayer') === this.GOOGLE;
    }.property('baseLayer'),

    baseLayer: function(key, value) {
        //setter
        if (value !== undefined) {
            if (value === this.GOOGLE) {
                var glayer = this.get('googleLayer');
                if (glayer) {
                    Kloudgis.mapController.getMap().setBaseLayer(glayer);
                }
            } else if (value === this.OSM) {
                var osmlayer = this.get('osmLayer');
                if (osmlayer) {
                    Kloudgis.mapController.getMap().setBaseLayer(osmlayer);
                }
            }
        }
        var bl = Kloudgis.mapController.getMap().baseLayer;
        if (bl) {
            if (bl === this.get('googleLayer')) {
                return this.GOOGLE;
            } else if (bl === this.get('osmLayer')) {
                return this.OSM;
            }
        }
        return NO;
    }.property(),

    refreshLayerByTableName: function(tablename) {
        var len = this.get('length');
        var i = 0;
        var counter = 0;
        for (i = 0; i < len; i++) {
            var layer = this.objectAt(i);
            var table = layer.tablename;
            if (tablename === table) {
                layer.redraw(true); //force redraw from scratch
                counter++;
            }
        }
        return counter;
    },

    refreshLayerByFeaturetype: function(featuretype) {
        var tableFt = featuretype.get('table_name');
        return this.refreshLayerByTableName(tableFt);
    },

    isVisibleLayer: function(tablename) {
        var len = this.get('length');
        var i = 0;
        for (i = 0; i < len; i++) {
            var layer = this.objectAt(i);
            var table = layer.tablename;
            if (tablename === table) {
                if (layer.visibility) {
                    return YES;
                }
            }
        }
        return NO;
    },

    setVisibleLayer: function(tablename, vis) {
        var len = this.get('length');
        var i = 0;
        for (i = 0; i < len; i++) {
            var layer = this.objectAt(i);
            var table = layer.tablename;
            if (tablename === table) {
                layer.setVisibility(vis);
            }
        }
        return YES;
    },

    hasLayer: function(tablename) {
        return this.getLayersForTable(tablename).length > 0;
    },

    getLayersForTable: function(tablename) {
        var matchLayers = [];
        var len = this.get('length');
        var i = 0;
        for (i = 0; i < len; i++) {
            var layer = this.objectAt(i);
            var table = layer.tablename;
            if (tablename === table) {
                matchLayers.push(layer);
            }
        }
        return matchLayers;
    },

    getFeatureOLFromFeaturetype: function(fids, featuretype, callback, callbackMethod) {
        var layers = this.getLayersForTable(featuretype.get('table_name'));
        if (layers.length > 0) {
            this.getFeatureOLFromLayer(fids, layers.objectAt(0), callback, callbackMethod);
            return YES;
        }
        return NO;
    },

    getFeatureOLFromLayer: function(fids, layer, callback, callbackMethod) {
        if (SC.typeOf(fids) !== SC.T_ARRAY) {
            fids = [fids];
        }
        var proto = OpenLayers.Protocol.WFS.fromWMSLayer(layer, {
            srsName: "EPSG:4326"
        });
        var response = proto.read({
            maxFeatures: fids.length,
            filter: new OpenLayers.Filter.FeatureId({
                fids: fids
            }),
            callback: callbackMethod,
            scope: callback
        });
        return YES;
    }

});
