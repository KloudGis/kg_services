// ==========================================================================
// Project:   Kloudgis.featuresController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Controller for the feature list (picker view)

  @extends SC.Object
*/
sc_require('managers/transaction')
 Kloudgis.featureController = SC.ArrayController.create(
/** @scope Kloudgis.featuresController.prototype */
 {

    allowsMultipleSelection: NO,
    featuretype: null,
    iProgressCount: 0,
    iQueryCount: 0,

	_resTemp: null,

    init: function() {
        sc_super();
        Kloudgis.transactionManager.addListener(this);
    },

    //transaction callback - server side complete INSERT
    featureInserted: function(featuretype, storekey) {
        if (!SC.none(this.featuretype) && featuretype === this.get('featuretype')) {
            this.refreshList();
        }
    },

    //transaction callback - server side complete DELETE
    featureDeleted: function(featuretype, storekey) {
        if (!SC.none(this.featuretype) && featuretype === this.get('featuretype')) {
            this.refreshList();
        }
    },

    featuretypeActivated: function(featuretype) {
        this.set('featuretype', featuretype);
		this.invokeLater(function(){
			this.refreshList();
		}, 500);      
    },

    refreshList: function() {
        if (!SC.none(this.get('featuretype')) && Kloudgis.ftToolsController.get('activated')) {
			this.set('content', null);
            this.set('iQueryCount', this.get('iQueryCount') + 1);
            this.set('iProgressCount', 0);
            var featuretype = this.get('featuretype');
            Kloudgis.ftToolsController.activateProgress(YES);
            var recordT = featuretype.get('recordType');
            if (recordT) {
                var query = SC.Query.remote(recordT, {
                    isStreaming: YES,
                    handleMethod: 'fetchIntersectQuery',
                    queryId: this.get('iQueryCount'),
                    envelope: Kloudgis.mapController.getExtentEnv(),
                    fetchCallbackReference: this,
                    fetchStart: this.fetchIntersectStart,
                    fetchEnd: this.fetchIntersectEnd,
                });
                this._resTemp = Kloudgis.store.find(query);
				this.set('content', this._resTemp);				             
            } else {
				Kloudgis.ftToolsController.activateProgress(NO);
                this.set('content', null);
            }
        } else {
            this.set('content', null);
        }
    },

    fetchIntersectStart: function(query) {
        if (query.get('queryId') === this.get('iQueryCount')) {
            this.set('iProgressCount', this.get('iProgressCount') + 1);
            Kloudgis.ftToolsController.activateProgress(YES);
        }
    },

    fetchIntersectEnd: function(query) {
        if (query.get('queryId') === this.get('iQueryCount')) {
            var count = this.get('iProgressCount');
            if (count > 0) {
                this.set('iProgressCount', count - 1);
            }
            if (count <= 1) {
                Kloudgis.ftToolsController.activateProgress(NO);
            }
        }
    },

    mapExtentDidChanged: function() {
        //console.log('mapExtentDidChanged');
        if (!SC.none(this.get('featuretype'))) {
            //console.log('Refresh list for:' + this.get('featuretype').get('name'));
            this.refreshList();
        }
    }.observes('Kloudgis.mapController.mapExtentChanged'),

    sceneDidChanged: function() {
        //console.log('sceneDidChanged');
        if (Kloudgis.ftToolsController.get('activeScene') !== Kloudgis.ftToolsController.get('FEATURE_VIEW')) {
            this.set('featuretype', null);
            this.set('content', null);
        }
        return YES;
    }.observes('Kloudgis.ftToolsController.activeScene'),

    activeToolsDidChanged: function() {
        //console.log('activeToolsDidChanged');
        if (!Kloudgis.ftToolsController.get('activated')) {
            this.set('content', null);
        } else {
            this.refreshList();
        }
    }.observes('Kloudgis.ftToolsController.activated'),

    selectionDidChanged: function() {
        console.log('zooom');
        this.invokeLater('_zoomSelection', 200);
    }.observes('selection'),

    _zoomSelection: function() {
        var _sel = this.get('selection');
        if (_sel) {
            var _fea = _sel.get('firstObject');
            if (_fea) {
                Kloudgis.mapController.zoomHighlightFeature(_fea);
            }
        }
    }
});
