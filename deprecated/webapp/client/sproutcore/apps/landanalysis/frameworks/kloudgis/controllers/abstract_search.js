// ==========================================================================
// Project:   Kloudgis.abstractSearchController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends SC.ArrayController
*/
Kloudgis.abstractSearchController = SC.ArrayController.extend(
/** @scope Kloudgis.abstractSearchController.prototype */
{
	searchableQuery: SC.Query.local(Kloudgis.Featuretype, {
	    conditions: "searchable = true",
	    orderBy: 'label_loc'
	}),
    FT_VIEW: 'ftViewSearch',
    FEATURE_VIEW: 'featureViewSearch',
    activeScene: null,

    search: "",
    allowsMultipleSelection: NO,
    queryInprogress: 0,
    searchCounter: 0,
    searchTimer: null,

    pickerShowing: NO,

    activeLabel: "",
    labelVisible: YES,
    btnLeftVisible: NO,
    progressVisible: NO,

    getSelectedFeaturetypeWrapper: function() {
        var _sel = this.get('selection');
        if (!SC.none(_sel)) {
            var _ft = _sel.get('firstObject');
            return _ft;
        }
        return NO;
    },

    //*** SCENES
    activateFeatures: function() {
        this.invokeLater('_lateActiveFeatures', 200);
    },

    _lateActiveFeatures: function() {
        this.set('activeScene', this.FEATURE_VIEW);
        this.set('btnLeftVisible', YES);
    },

    activateFeaturetypes: function() {
        this.set('activeScene', this.FT_VIEW);
        this.activateProgress(NO);
        this.set('btnLeftVisible', NO);
    },

    //**** Progress
    activateProgress: function(bStart) {
        this.set('progressVisible', bStart);
    },

    buttonLeftPressed: function() {
        this.activateFeaturetypes();
    },

    searchChanged: function() {
        if (this.searchTimer) {
            this.searchTimer.invalidate();
        }
        this.searchTimer = SC.Timer.schedule({
            target: this,
            action: 'timerFired',
            interval: 500,
            repeats: NO
        });
    }.observes('search'),

    timerFired: function() {
        console.log("Search asked:" + this.search);
        this.refreshList();
    },

    refreshList: function() {
        var search_string = this.get('search');
        this.set('activeLabel', search_string);
        this.set('content', []);
        this.searchCounter++;
        //reset
        if (!SC.empty(search_string)) {
            this.set('queryInprogress', 0);
            var fts = this._getFeaturetypes();
            var len = fts.get('length');
            this.activateProgress(YES);
            var i = 0;
            for (i = 0; i < len; i++) {
                var featuretype = fts.objectAt(i);
                var recordT = featuretype.get('recordType');
                if (!SC.none(recordT)) {
                    var query = SC.Query.remote(recordT, {
                        handleMethod: 'fetchSearchCountQuery',
                        search_string: search_string,
                        searchCounter: this.searchCounter,
                        callback: this,
                        callbackMethod: this.fetchSearchCountDone
                    });
                    Kloudgis.store.find(query);
                    this.incrementProperty('queryInprogress');
                }
            }
            if (this.queryInprogress === 0) {
                this.activateProgress(NO);
            }
			return YES;
        } else {
            this.activateProgress(NO);
            this.closePicker();
			return NO;
        }
    },

	_getFeaturetypes: function(){
		var fts = Kloudgis.store.find(Kloudgis.FEATURETYPE_LIST);
		return fts.find(this.searchableQuery);
	},

    closePicker: function() {
        this.set('pickerShowing', NO);
        var len = this.get('length');
        var i;
        for (i = 0; i < len; i++) {
            this.objectAt(i).destroy();
        }
        this.set('content', null);
    }
});
