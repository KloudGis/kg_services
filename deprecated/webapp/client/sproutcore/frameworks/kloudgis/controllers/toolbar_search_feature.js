// ==========================================================================
// Project:   Kloudgis.toolbarSearchFeatureController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
sc_require('controllers/abstract_search_feature')
sc_require('controllers/toolbar_search')
Kloudgis.toolbarSearchFeatureController = Kloudgis.abstractSearchFeatureController.create(
/** @scope Kloudgis.toolbarSearchFeatureController.prototype */
 {
  
    sceneDidChanged: function() {
        if (Kloudgis.toolbarSearchController.get('activeScene') !== Kloudgis.toolbarSearchController.get('FEATURE_VIEW')) {
            this.set('featuretypeWrapper', NO);
            this.set('content', null);
            this.activateProgress(NO);
        }
    }.observes('Kloudgis.toolbarSearchController.activeScene'),

    pickerShowingDidChanged: function() {
        //console.log('activeToolsDidChanged');
        if (!Kloudgis.toolbarSearchController.get('pickerShowing')) {
            this.set('featuretypeWrapper', NO);
            this.set('content', null);
        }
    }.observes('Kloudgis.toolbarSearchController.pickerShowing'),

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