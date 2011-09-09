// ==========================================================================
// Project:   CoreChart.PlaceItemContainerView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  The main view for PlaceItem.  Contain both the featureview and the chart view.

  @extends  CoreChart.PlaceItemContainerView
*/
sc_require('views/place_item_feature')
sc_require('views/place_item_charts')
CoreChart.PlaceItemContainerView = SC.View.extend({
	
	featuretypeName: null,
	controller: function(){
		var ftname = this.get('featuretypeName');
		return Kloudgis.modelManager.getController(ftname);
	}.property('featuretypeName').cacheable(),
	
	init: function() {   
		sc_super();
        this.set('layout', this.getPath('parentView.featureLayout'));	
		var ftname = this.get('featuretypeName');
		var ctrl = Kloudgis.modelManager.getController(ftname);
		this.feature.bind('isVisible', SC.Binding.from(".featureVisible", ctrl));
		this.charts.bind('isVisible', SC.Binding.from(".chartVisible", ctrl));
    },

	classNames:['inspector-feature-view'],
	childViews: 'feature charts'.w(),	
	
    feature: CoreChart.PlaceItemFeatureView.design({						
    }),
    charts: SC.LabelView.design({
		value: 'NO CHART!'
	})
});