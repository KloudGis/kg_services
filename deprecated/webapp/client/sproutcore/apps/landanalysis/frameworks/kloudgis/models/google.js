// ==========================================================================
// Project:   Kloudgis.Google
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Base class for feature records.

  @extends SC.Record
  @version 0.1
*/
sc_require('models/feature')
 Kloudgis.Google = Kloudgis.Feature.extend(
/** @scope Kloudgis.Google.prototype */
 {
    types: SC.Record.attr(Array),
    formatted_address: SC.Record.attr(String),
    geometry: SC.Record.attr(Object),

    post_url: null,
    get_url: null,
    put_url: null,
    delete_url: null,

    fetchSearchQuery: function(sparseArray, range, requestLength) {
        var query = sparseArray.get('query');
        var count = requestLength === YES;
        var search = query.get('search_string');
        var url = '/maps/api/geocode/json?address=%@&sensor=true'.fmt(query.get('search_string'));
        SC.Request.getUrl(url).json().notify(this, this.didFetchFeatures, sparseArray.get('store'), {
            array: sparseArray,
            start: range.start,
            length: range.length
        }).send();
        return YES;
    },

    fetchSearchCountQuery: function(store, query) {
        var url = '/maps/api/geocode/json?address=%@&sensor=true'.fmt(query.get('search_string'));
        SC.Request.getUrl(url).json().notify(this, 'didFetchSearchCountQuery', store, query).send();
        return YES;
    },

    didFetchFeatures: function(response, store, params) {
        var sparseArray = params.array;
        Kloudgis.unloadRecordType(store, Kloudgis.Google);
        var query = sparseArray.get('query');
        if (SC.ok(response)) {
            var body = response.get('body');
            var results = [];
            var storeKeys = [];
            if (body) {
                if (body.status) {
                    var len = body.results.length;
                    var i;
                    var ids = [];
                    for (i = 0; i < len; i++) {
                        ids.push(i + 1000);
                    }
                    Kloudgis.Google.prototype._kvo_cache = null;
                    storeKeys = store.loadRecords(Kloudgis.Google, body.results, ids);
                    for (i = 0; i < len; i++) {
                        results.push(store.find(Kloudgis.Google, ids[i]));
                    }
                }
            }
            sparseArray.provideLength(results.length);
            sparseArray.provideObjectsInRange({
                start: 0,
                length: results.length
            },
            storeKeys);
            sparseArray.rangeRequestCompleted(0);
        } else {
            store.dataSourceDidErrorQuery(query, response);
        }
        this.notifyFetchEnd(query);
    },

    //FETCH SEARCH COUNT
    didFetchSearchCountQuery: function(response, store, query) {
        var count = 0;
        if (SC.ok(response)) {
            if (Kloudgis.securityController.securityCheck(response, query)) {
                var body = response.get('body');
                if (body) {
                    if (body.status) {
                        count = body.results.length;
                    }
                }
                store.dataSourceDidFetchQuery(query);
            }
        } else {
            store.dataSourceDidErrorQuery(query, response);
        }
        if (query.get('callback') && query.get('callbackMethod')) {
            query.get('callbackMethod').call(query.get('callback'), query, count);
        }
    },

    //END OF QUERY SUPPORT
    label: function() {
        return this.get('formatted_address');
    }.property('formatted_address').cacheable(),

    location: function() {
        var geo = this.get('geometry');
        if (geo && geo.location) {
            return {
                lat: geo.location.lat,
                lon: geo.location.lng,
            };
        }
        return NO;
    }.property('geometry').cacheable(),

    viewport: function() {
        var geo = this.get('geometry');
        if (geo && geo.viewport) {
            return {
                min: {
                    lat: geo.viewport.southwest.lat,
                    lon: geo.viewport.southwest.lng,
                },
                max: {
                    lat: geo.viewport.northeast.lat,
                    lon: geo.viewport.northeast.lng,
                }
            };
        }
        return NO;
    }.property('geometry').cacheable(),

    boundsLonLat: function() {
        var geo = this.get('viewport');
        if (geo) {
            var bounds = new OpenLayers.Bounds();
            bounds.extend(new OpenLayers.LonLat(geo.min.lon, geo.min.lat));
            bounds.extend(new OpenLayers.LonLat(geo.max.lon, geo.max.lat));
            return bounds;
        }
    }.property('viewport').cacheable(),

    fetchDefaultGeometry: function(callback, callbackMethod) {
        var loc = this.get('location');
        if (loc && callback && callbackMethod) {
            callbackMethod.call(callback, "POINT(%@ %@)".fmt(loc.lon, loc.lat));
        }
        return NO;
    },

    featuretypeName: 'Google'

});
