// ==========================================================================
// Project:   Kloudgis.abstractSearchFeatureController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/

Kloudgis.abstractSearchFeatureController = SC.ArrayController.extend(
/** @scope Kloudgis.abstractSearchFeatureController.prototype */
{
    allowsMultipleSelection: NO,
    featuretype: NO,
    progressVisible: NO,

    iQueryCount: 0,
    iProgressCount: 0,

    featuretypeActivated: function(featuretypeWrapper) {
        this.set('featuretypeWrapper', featuretypeWrapper);
        this.refreshList();
    },

    //**** Progress
    activateProgress: function(bStart) {
        this.set('progressVisible', bStart);
    },

    refreshList: function() {
        if (!SC.none(this.get('featuretypeWrapper'))) {
            var featuretypeW = this.get('featuretypeWrapper');
            this.incrementProperty('iQueryCount');
            this.set('iProgressCount', 0);
            var recordT = featuretypeW.get('recordType');
            if (!SC.none(recordT)) {
                this.activateProgress(YES);
                var query = SC.Query.remote(recordT, {
                    isStreaming: YES,
                    handleMethod: 'fetchSearchQuery',
                    queryId: this.get('iQueryCount'),
                    search_string: featuretypeW.get('search_string'),
                    fetchCallbackReference: this,
                    fetchStart: this.fetchSearchStart,
                    fetchEnd: this.fetchSearchEnd,
                });
                var res = Kloudgis.store.find(query);
                this.set('content', res);
            } else {
                this.set('content', null);
            }
        } else {
            this.set('content', null);
        }
    },

    fetchSearchStart: function(query) {
        if (query.get('queryId') === this.get('iQueryCount')) {
            this.set('iProgressCount', this.get('iProgressCount') + 1);
            this.activateProgress(YES);
        }
    },

    fetchSearchEnd: function(query) {
        if (query.get('queryId') === this.get('iQueryCount')) {
            var count = this.get('iProgressCount');
            if (count > 0) {
                this.set('iProgressCount', count - 1);
            }
            if (count <= 1) {
                this.activateProgress(NO);
            }
        }
    }

});
