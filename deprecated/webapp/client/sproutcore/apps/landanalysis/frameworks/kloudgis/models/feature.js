// ==========================================================================
// Project:   Kloudgis.Feature
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Base class for feature records.

  @extends SC.Record
  @version 0.1
*/
sc_require('models/featuretype')
sc_require('models/attrtype')
sc_require('models/envelope')
Kloudgis.Feature = SC.Record.extend(
/** @scope Kloudgis.Feature.prototype */
{
	
	init: function(){
		sc_super();
		this._kvo_cache = null;
	},
	
    //basic urls
    post_url: function() {
        return '%@/resources/protected/features/%@'.fmt(Kloudgis.context, this.get('featuretypeName'));
    }.property('featuretypeName').cacheable(),

    get_url: function() {
        return this.get('post_url') + "/%@";
    }.property('post_url').cacheable(),

    put_url: function() {
        return this.get('get_url');
    }.property('get_url').cacheable(),

    delete_url: function() {
        return this.get('get_url');
    }.property('get_url').cacheable(),

	sparsearrayBlock: 50,

    /*****************************/
    /**  QUERY HANDLING **/
    handleQuery: function(store, query) {
        var handler = query.get('handleMethod');
        if (handler && this.get(handler)) {
            if (query.get('isStreaming')) {
                store.loadQueryResults(query, SC.SparseArray.create({
                    delegate: this,
                    store: store,
                    query: query,
                    rangeWindowSize: this.get('sparsearrayBlock')
                }));
                return YES;
            } else {
                return this.get(handler).call(this, store, query);
            }
        }
        return NO;
    },

    //SPARSE ARRAY DELEGATE METHODS
    //streaming support
    sparseArrayDidRequestLength: function(sparseArray) {
        return this.sparseArrayDidRequestRange(sparseArray, {
            start: 0,
            length: this.get('sparsearrayBlock')
        },
        YES);
    },
    sparseArrayDidRequestRange: function(sparseArray, range, requestLength) {
        var query = sparseArray.get('query');
        var handler = query.get('handleMethod');
        if (!SC.none(query) && !SC.none(handler) && this.get(handler)) {
            this.notifyFetchStart(query);
            return this.get(handler).call(this, sparseArray, range, requestLength);
        }
        return NO;
    },

    /* STANDARD QUERIES */
    //stream INTERSECT features
    fetchIntersectQuery: function(sparseArray, range, requestLength) {
        var query = sparseArray.get('query');
        var count = requestLength === YES;
        var env = query.get('envelope');
        if (!SC.none(env)) {
            var url = '%@/resources/protected/features/%@/intersects?lowLat=%@&lowLon=%@&hiLat=%@&hiLon=%@&start=%@&length=%@&count=%@'.fmt(Kloudgis.context, this.get('featuretypeName'), env.lowLat, env.lowLon, env.hiLat, env.hiLon, range.start, range.length, count);
            SC.Request.getUrl(url).json().notify(this, this.didFetchFeatures, sparseArray.get('store'), {
                array: sparseArray,
                start: range.start,
                length: range.length
            }).send();
            return YES;
        }
    },
    //stream SEARCH features
    fetchSearchQuery: function(sparseArray, range, requestLength) {
        var query = sparseArray.get('query');
        var count = requestLength === YES;
        var search = query.get('search_string');
        if (!SC.empty(search)) {
            var url = '%@/resources/protected/features/%@/search?searchstring=%@&start=%@&length=%@&count=%@'.fmt(Kloudgis.context, this.get('featuretypeName'), search, range.start, range.length, count);
            SC.Request.getUrl(url).json().notify(this, this.didFetchFeatures, sparseArray.get('store'), {
                array: sparseArray,
                start: range.start,
                length: range.length
            }).send();
            return YES;
        }
    },
    //FETCH GEO
    fetchGeoQuery: function(store, query) {
        var id = query.get('id');
        if (!SC.none(id)) {
            var url = '%@/resources/protected/features/%@/%@/defaultGeometry'.fmt(Kloudgis.context, this.get('featuretypeName'), id);
            var response = SC.Request.getUrl(url).json().notify(this, 'didFetchGeoQuery', store, query).send();
			var callback = query.get('callback');
			if( callback && query.get('callbackResponse')){
				query.get('callbackResponse').call(callback, response);
			}
            return YES;
        }
        return NO;
    },
    //FETCH SEARCH COUNT
    fetchSearchCountQuery: function(store, query) {
        var search = query.get('search_string');
        var url = '%@/resources/protected/features/%@/count_search?searchstring=%@'.fmt(Kloudgis.context, this.get('featuretypeName'), search);
        SC.Request.getUrl(url).json().notify(this, 'didFetchSearchCountQuery', store, query).send();
        return YES;
    },

    /*****************************/
    /* STANDARD QUERIES CALLBACK */
    //FETCH FEATURE STREAMING
    didFetchFeatures: function(response, store, params) {
        var sparseArray = params.array,
        start = params.start,
        length = params.length;
        var query = sparseArray.get('query');
        var recordType = query.get('resultRecordType');
        if (SC.none(recordType)) {
            recordType = query.get('recordType');
        }
        var storeKeys;
        if (SC.ok(response)) {
            if (Kloudgis.securityController.securityCheck(response, query)) {
                var body = response.get('body');
                if (body) {
                    //***important*** Flush cache in the prototype if any because it corrupt the records created
                    recordType.prototype._kvo_cache = null;
                    storeKeys = store.loadRecords(recordType, body.list);
                }
                if (!SC.none(body.count)) {
                    sparseArray.provideLength(body.count);
                }
                sparseArray.provideObjectsInRange({
                    start: start,
                    length: length
                },
                storeKeys);
                sparseArray.rangeRequestCompleted(start);
            }
        } else {
            store.dataSourceDidErrorQuery(query, response);
        }
        this.notifyFetchEnd(query);
    },
    //FETCH GEO
    didFetchGeoQuery: function(response, store, query) {
        var wkt = null;
        if (SC.ok(response)) {
            if (Kloudgis.securityController.securityCheck(response, query)) {
                var body = response.get('body');
                if (body) {
                    wkt = body.wkt;
                }
                store.dataSourceDidFetchQuery(query);
            }
        } else {
            store.dataSourceDidErrorQuery(query, response);
        }
        if (query.get('callback') && query.get('callbackMethod')) {
            query.get('callbackMethod').call(query.get('callback'), wkt);
        }
    },

    //FETCH SEARCH COUNT
    didFetchSearchCountQuery: function(response, store, query) {
        var count = 0;
        if (SC.ok(response)) {
            if (Kloudgis.securityController.securityCheck(response, query)) {
                var body = response.get('body');
                if (body) {
                    count = body;
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

    notifyFetchStart: function(query) {
        if (query.get('fetchStart') && query.get('fetchCallbackReference')) {
            query.get('fetchStart').call(query.get('fetchCallbackReference'), query);
        }
    },

    notifyFetchEnd: function(query) {
        if (query.get('fetchEnd') && query.get('fetchCallbackReference')) {
            query.get('fetchEnd').call(query.get('fetchCallbackReference'), query);
        }
    },

    /** END QUERY HANDLING **/
    /*****************************/

    boundswkt: SC.Record.attr(String),
	newgeowkt: SC.Record.attr(String),
	//to override
	featuretypeName: null,
	//to override - default Kloudgis.abstractFeatureController
	controllerClassName: null,
	
	
	featuretype: function() {
        return Kloudgis.modelManager.getFeaturetype(this.get('featuretypeName'));
    }.property('featuretypeName').cacheable(),

	featureController: function() {
        return Kloudgis.modelManager.getController(this.get('featuretypeName'));
    }.property('featuretypeName'),

	table_name: function() {
        var ft = this.get('featuretype');
        if (ft) {
            return ft.get('table_name');
        }
    }.property('featuretype').cacheable(),


    boundsLonLat: function() {
        var geo = this.get('boundswkt');
        if (SC.none(geo) && !SC.none(this.get('newgeowkt'))) {
            geo = this.get('newgeowkt');
        }
        if (!SC.none(geo)) {
            var ol_geo = OpenLayers.Geometry.fromWKT(geo);
            if (ol_geo) {
                return ol_geo.getBounds();
            }
        }
    }.property('boundswkt', 'newgeowkt').cacheable(),

    fetchDefaultGeometry: function(callback, callbackMethod, callbackResponse) {
        if (this.get('newgeowkt')) {
            callbackMethod.call(callback, this.get('newgeowkt'));
        } else {
            var ft = this.get('featuretype');
            if (ft) {
                var recordT = ft.get('recordType');
                if (recordT) {
                    var query = SC.Query.remote(recordT, {
                        handleMethod: 'fetchGeoQuery',
                        id: this.get('id'),
                        callback: callback,
                        callbackMethod: callbackMethod,
						callbackResponse: callbackResponse
                    });
                    Kloudgis.store.find(query);
                }
            }
        }
    },

    label: function() {
        return this.get('id');
    }.property('id').cacheable(),

    labelInfo: function() {
        return this.get('labelInspector');
    }.property('labelInspector'),

    labelInspector: function() {
        var ft = this.get('featuretype');
        if (ft) {
            return ft.get('label_loc') + " (" + this.get('id') + ")";
        } else {
            return '';
        }
    }.property('featuretype','label_loc' ,'id')

});
