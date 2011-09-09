// ==========================================================================
// Project:   CoreChart.chartToolbarSearchFeatureController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your Controller Here)

  @extends CoreChart.toolbarSearchFeatureController
*/

sc_require('controllers/chart_toolbar_search')
sc_require('controllers/chart_list')
CoreChart.chartToolbarSearchFeatureController = Kloudgis.abstractSearchFeatureController.create(
/** @scope CoreChart.chartToolbarSearchFeatureController.prototype */
 {

    sceneDidChanged: function() {
        //console.log('sceneDidChanged');
        if (CoreChart.chartToolbarSearchController.get('activeScene') !== CoreChart.chartToolbarSearchController.get('FEATURE_VIEW')) {
            this.set('featuretypeWrapper', NO);
            this.set('content', null);
            this.activateProgress(NO);
        }
    }.observes('CoreChart.chartToolbarSearchController.activeScene'),

    showingDidChanged: function() {
        //console.log('activeToolsDidChanged');
        if (!CoreChart.chartToolbarSearchController.get('pickerShowing')) {
            this.set('featuretypeWrapper', NO);
            this.set('content', null);
        }
    }.observes('CoreChart.chartToolbarSearchController.pickerShowing'),

    selectionDidChanged: function() {
		var select = this.get('selection').get('firstObject');
		if(select){
        	CoreChart.chartListController.addChartForFeature(select);		
		}
    }.observes('selection'),


});