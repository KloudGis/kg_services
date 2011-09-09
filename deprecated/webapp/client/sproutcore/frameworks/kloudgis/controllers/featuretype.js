// ==========================================================================
// Project:   Kloudgis.featuretypesController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Controller for for the featuretype list (picker view).

  @extends SC.Object
*/
Kloudgis.featuretypeController = SC.ArrayController.create(
/** @scope Kloudgis.featuretypesController.prototype */
{

    allowsMultipleSelection: NO,
	geoQuery: SC.Query.local(Kloudgis.Featuretype, {
	    conditions: "hasGeometry = true",
	    orderBy: 'label_loc'
	}),

    editVisibility: function(value) {
        if (!SC.none(this.get('content'))) {
            var len = this.get('length');
            var i;
            for (i = 0; i < len; i++) {
                this.objectAt(i).set('isLayerEditVisibility', value);
            }
        }
    },

    selectionDidChanged: function() {
        var select = this.getSelectedFeaturetype();
        if (!SC.none(select)) {
            if (select.get('isLayerEditVisibility')) {
                select.setVisibleLayer(!select.get('isVisibleLayer'));
                this.invokeLater('deselectAll', 500);
            } else {
                Kloudgis.featureController.featuretypeActivated(select);
                this.invokeLater('_activateFeatures', 300);
				this.invokeLater('deselectAll', 500);
            }
        }
    }.observes('selection'),

    deselectAll: function() {
        this.deselectObjects(this.get('arrangedObjects'));
    },

    getSelectedFeaturetype: function() {
        var _sel = this.get('selection');
        if (!SC.none(_sel)) {
            var _ft = _sel.get('firstObject');
            return _ft;
        }
        return NO;
    },

    _activateFeatures: function() {
        Kloudgis.ftToolsController.activateFeatures();
    },

    sceneDidChanged: function() {
        if (Kloudgis.ftToolsController.get('activeScene') === Kloudgis.ftToolsController.get('FT_VIEW')) {
            this.loadFeaturetypes();
        }
    }.observes('Kloudgis.ftToolsController.activeScene'),

    activateDidChanged: function() {
        if (Kloudgis.ftToolsController.get('activated')) {
            if (Kloudgis.ftToolsController.get('activeScene') === Kloudgis.ftToolsController.get('FT_VIEW')) {
                this.loadFeaturetypes();
            }
        } else {
            this.set('content', null);		
        }
    }.observes('Kloudgis.ftToolsController.activated'),

    loadFeaturetypes: function() {
        if (SC.none(this.get('content')) || this.get('content').get('length') == 0) {
            var fts = Kloudgis.store.find(Kloudgis.FEATURETYPE_LIST);
			var gfts = fts.find(this.geoQuery);
            this.set('content', gfts);
        }
    }

});
