// ==========================================================================
// Project:   Useradmin.Store
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

/** @class

  (Document Your Data Source Here)

  @extends SC.DataSource
*/

Useradmin.USERS_QUERY = SC.Query.local(Useradmin.User, {
    filter: '',
    orderBy: 'label'
});

sc_require('models/user')
Useradmin.Store = SC.DataSource.extend(
/** @scope Useradmin.Store.prototype */
{

    // ..........................................................
    // QUERY SUPPORT
    // 
    fetch: function(store, query) {

        if (query === Useradmin.USERS_QUERY) {
            console.log("FETCH USERS!!");
            var filter = query.get('filter');
            if (SC.none(filter)) {
                filter = '';
            }
            SC.Request.getUrl('%@/resources/admin/users?filter=%@'.fmt(Useradmin.context_server, filter)).json().notify(this, 'didFetchUsers', store, query).send();
            return YES;
        } else {
            console.log('ignoring fetch request with query: %@'.fmt(query));
        }

        // call store.dataSourceDidFetchQuery(query) when done.
        return NO; // return YES if you handled the query
    },

    didFetchUsers: function(response, store, query) {
        var storeK = store.storeKeysFor(Useradmin.User);
        store.unloadRecords(undefined, undefined, storeK);
        if (SC.ok(response)) {
            var body = response.get('body');
            var storeKeys;
            if (body) {
                storeKeys = store.loadRecords(Useradmin.User, body);
            }
            store.dataSourceDidFetchQuery(query);
        } else {
            store.dataSourceDidErrorQuery(query, response);
        }
    },

    // ..........................................................
    // RECORD SUPPORT
    // 
    retrieveRecord: function(store, storeKey) {

         SC.Request.getUrl(Useradmin.context_server + '/resources/admin/users/%@'.fmt(store.idFor(storeKey))).json().notify(this, 'didRetrieve', store, storeKey).send();
        // call store.dataSourceDidComplete(storeKey) when done.
        return NO; // return YES if you handled the storeKey
    },

	

    createRecord: function(store, storeKey) {
        console.log("Create User");
        SC.Request.postUrl(Useradmin.context_server + '/resources/admin/users').json().notify(this, this.didCreate, store, storeKey).send(store.readDataHash(storeKey));
        // call store.dataSourceDidComplete(storeKey) when done.
        return YES; // return YES if you handled the storeKey
    },

    didCreate: function(response, store, storeKey) {
        if (SC.ok(response)) {
            var data = response.get('body');
            console.log("Create OK");
            // console.log(data);
            if (data) {
                var id = data.guid;
                store.dataSourceDidComplete(storeKey, data, id);
            } else {
                store.dataSourceDidComplete(storeKey);
            }
        } else {
            store.dataSourceDidError(storeKey, response);
        }
    },

    updateRecord: function(store, storeKey) {
        SC.Request.putUrl(Useradmin.context_server + '/resources/admin/users/%@'.fmt(store.idFor(storeKey))).json().notify(this, this.didUpdate, store, storeKey).send(store.readDataHash(storeKey));
        return YES; // return YES if you handled the storeKey
    },

	didUpdate: function(response, store, storeKey) {
            if (SC.ok(response)) {
                var data = response.get('body');
                if (data) {
                    store.dataSourceDidComplete(storeKey, data);
                } else {
                    store.dataSourceDidComplete(storeKey);
                }
            } else {
                store.dataSourceDidError(storeKey);
            }
    },

    destroyRecord: function(store, storeKey) {
        SC.Request.deleteUrl(Useradmin.context_server + '/resources/admin/users/' + store.idFor(storeKey)).set('isJSON', YES).json().notify(this, this.didDestroy, store, storeKey).send();
        // call store.dataSourceDidDestroy(storeKey) when done
        return YES; // return YES if you handled the storeKey
    },

    didDestroy: function(response, store, storeKey) {
        if (SC.ok(response)) {
            store.dataSourceDidDestroy(storeKey);
        } else {
            store.dataSourceDidError(storeKey, response);
        }
    }

});
