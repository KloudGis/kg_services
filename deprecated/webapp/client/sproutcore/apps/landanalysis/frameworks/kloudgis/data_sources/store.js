// ==========================================================================
// Project:   Kloudgis.Store
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Data Source Here)

  @extends SC.DataSource
*/
Kloudgis.FEATURETYPE_LIST = SC.Query.local(Kloudgis.Featuretype, {
    orderBy: 'label_loc'
});

Kloudgis.ATTRTYPE_LIST = SC.Query.local(Kloudgis.Attrtype, {
    orderBy: 'label_loc'
});

 sc_require('models/featuretype')
 sc_require('models/attrtype')
 sc_require('models/envelope')
 sc_require('managers/transaction')
 Kloudgis.Store = SC.DataSource.extend(
/** @scope Kloudgis.Store.prototype */
 {

    // ..........................................................
    // QUERY SUPPORT
    //
    fetch: function(store, query) {
        console.log("FETCH IN %@".fmt(query.get('handleMethod')));
        if (query === Kloudgis.FEATURETYPE_LIST) {
            console.log("FETCH FEATURETYPES!!");
            SC.Request.getUrl('%@/resources/protected/featuretypes'.fmt(Kloudgis.context)).json().notify(this, 'didFetchFt', store, query).send();
            return YES;
        }
        if (query === Kloudgis.ATTRTYPE_LIST) {
            console.log("FETCH ATTRTYPES!!");
            SC.Request.getUrl('%@/resources/protected/attrtypes'.fmt(Kloudgis.context)).json().notify(this, 'didFetchAt', store, query).send();
            return YES;
        } else if (!SC.none(query.get('recordType')) && !SC.none(query.get('handleMethod'))) {
            var rtype = query.get('recordType');
            if (rtype.prototype.get('handleQuery')) {
                return rtype.prototype.handleQuery(store, query);
            }
        }
        return NO;
    },

    didFetchFt: function(response, store, query) {
        Kloudgis.unloadRecordType(store, Kloudgis.Featuretype);
        if (SC.ok(response)) {
            if (Kloudgis.securityController.securityCheck(response, query)) {
                var body = response.get('body');
                var storeKeys;
                if (body) {
                    storeKeys = store.loadRecords(Kloudgis.Featuretype, body);
                }
                store.dataSourceDidFetchQuery(query);
            }
        } else {
            store.dataSourceDidErrorQuery(query, response);
        }
    },

    didFetchAt: function(response, store, query) {
        Kloudgis.unloadRecordType(store, Kloudgis.Attrtype);
        if (SC.ok(response)) {
            if (Kloudgis.securityController.securityCheck(response, query)) {
                var body = response.get('body');
                var storeKeys;
                if (body) {
                    storeKeys = store.loadRecords(Kloudgis.Attrtype, body);
                }
                store.dataSourceDidFetchQuery(query);
            }
        } else {
            store.dataSourceDidErrorQuery(query, response);
        }
    },

    // ..........................................................
    // RECORD SUPPORT
    //
    retrieveRecord: function(store, storeKey) {
		console.log('retreive record');
        var rtype = store.recordTypeFor(storeKey);
        var id = store.idFor(storeKey);
        if (!SC.none(id) && SC.kindOf(rtype, Kloudgis.Feature)) {
            console.log('retrieve feature, id=' + id);
            var url = rtype.prototype.get('get_url');
            if (!SC.none(url)) {
                url = url.fmt(id);
                SC.Request.getUrl(url).json().notify(this, 'didRetrieveFeature', store, storeKey).send();
                return YES;
            }
        } else if (SC.kindOf(rtype, Kloudgis.Attrtype)) {
            console.log('retrieve attribute, id=' + id);
            var url = '%@/resources/protected/attrtypes/%@'.fmt(Kloudgis.context, id);
            SC.Request.getUrl(url).json().notify(this, 'didRetrieveFeature', store, storeKey).send();
			return YES; 
        } else if (SC.kindOf(rtype, Kloudgis.Priority)) {
            console.log('retrieve priority, id=' + id);
            var url = '%@/resources/protected/priorities/%@'.fmt(Kloudgis.context, id);
            SC.Request.getUrl(url).json().notify(this, 'didRetrieveFeature', store, storeKey).send();
			return YES; 
        }
        return NO;
    },

    didRetrieveFeature: function(response, store, storeKey) {
        if (SC.ok(response)) {
			console.log('retreive record completed - OK');
            if (Kloudgis.securityController.securityCheck(response)) {
                var data = response.get('body');
                if (data) {
                    store.dataSourceDidComplete(storeKey, data);
                } else {
                    store.dataSourceDidComplete(storeKey);
                }
            }
        } else {
			console.log('retreive record completed - ERROR');
            store.dataSourceDidError(storeKey, response);
        }
    },

    createRecord: function(store, storeKey) {
        console.log('createRecord');
        var rtype = store.recordTypeFor(storeKey);
        if (SC.kindOf(rtype, Kloudgis.Feature)) {
            var url = rtype.prototype.get('post_url');
            if (!SC.none(url)) {
                SC.Request.postUrl(url).json().notify(this, this.didCreateFeature, store, storeKey).send(store.readDataHash(storeKey));
            }
            return YES;
        }
        return NO;
    },

    didCreateFeature: function(response, store, storeKey) {
        if (SC.ok(response)) {
			console.log('createRecord completed - OK');
            if (Kloudgis.securityController.securityCheck(response)) {
                var data = response.get('body');
                if (data) {
                    var id = data.guid;
                    store.dataSourceDidComplete(storeKey, data, id);
                } else {
                    store.dataSourceDidComplete(storeKey);
                }
                Kloudgis.transactionManager.featureInserted(store.recordTypeFor(storeKey).prototype.get('featuretype'), storeKey);
            }
        } else {
			console.log('createRecord completed - ERROR');
            store.dataSourceDidError(storeKey, response);
        }
    },

    updateRecord: function(store, storeKey) {
        console.log('update record');
        var rtype = store.recordTypeFor(storeKey);
        var id = store.idFor(storeKey);
        if (!SC.none(id) && SC.kindOf(rtype, Kloudgis.Feature)) {
            var url = rtype.prototype.get('put_url');
            if (url) {
                url = url.fmt(id);
                SC.Request.putUrl(url).json().notify(this, this.didUpdateFeature, store, storeKey).send(store.readDataHash(storeKey));
                return YES;
            }
        }
        return NO;
    },

    didUpdateFeature: function(response, store, storeKey) {
        if (SC.ok(response)) {
			console.log('update record completed - OK');
            if (Kloudgis.securityController.securityCheck(response)) {
                var data = response.get('body');
                if (data) {
                    store.dataSourceDidComplete(storeKey, data);
                } else {
                    store.dataSourceDidComplete(storeKey);
                }
                Kloudgis.transactionManager.featureUpdated(store.recordTypeFor(storeKey).prototype.get('featuretype'), storeKey);
            }
        } else {
			console.log('update record completed - ERROR');
            store.dataSourceDidError(storeKey, response);
        }
    },

    destroyRecord: function(store, storeKey) {
        console.log('destroyRecord');
        var rtype = store.recordTypeFor(storeKey);
        var id = store.idFor(storeKey);
        if (!SC.none(id) && SC.kindOf(rtype, Kloudgis.Feature)) {
            var url = rtype.prototype.get('delete_url');
            if (url) {
                url = url.fmt(id);
                SC.Request.deleteUrl(url).json().notify(this, this.didDestroyFeature, store, storeKey).send();
                return YES;
            }
        }
        return NO;
    },

    didDestroyFeature: function(response, store, storeKey) {		
        if (SC.ok(response)) {
			console.log('destroyRecordCompleted-OK');
            if (Kloudgis.securityController.securityCheck(response)) {
                var ft = store.recordTypeFor(storeKey).prototype.get('featuretype');
                store.dataSourceDidDestroy(storeKey);
                Kloudgis.transactionManager.featureDeleted(ft, storeKey);
            }
        } else {
			console.log('destroyRecordCompleted-ERROR');
            store.dataSourceDidError(storeKey, response);
        }
    }
});
