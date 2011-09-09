// ==========================================================================
// Project:   Kloudgis
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @namespace

  My cool new app.  Describe your application.
  
  @extends SC.Object
*/
Kloudgis = SC.Object.create(
/** @scope Kloudgis.prototype */
{

    NAMESPACE: 'Kloudgis',
    VERSION: '0.1.0',

    store: null,
    context: '/webserver',
	context_client: 'app',
    /*
store: SC.Store.create({ 
  commitRecordsAutomatically: NO
}).from('Kloudgis.Store'),
*/

    unloadRecordType: function(store, recordType) {
        var storeK = store.storeKeysFor(recordType);
        store.unloadRecords(recordType, this.storeKeysToIds(store, storeK), storeK);
    },

    storeKeysToIds: function(store, storeKeys) {
        var i;
        var len = storeKeys.get('length');
        var ids = [];
        for (i = 0; i < len; i++) {
            ids.push(store.idFor(storeKeys.objectAt(i)));
        }
        return ids;
    }
});
