// ==========================================================================
// Project:   Kloudgis.placeItemController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Abstract controller for placeitem featuretype.

  @extends Kloudgis.abstractFeatureController
*/
CoreChart.placeItemController = Kloudgis.abstractFeatureController.extend(
/** @scope Kloudgis.placeItemController.prototype */
{
    featureVisible: YES,
    chartVisible: NO,

    selectionDidChanged: function() {
        var activeFea = this.get('activeFeature');
        sc_super();
        if (!activeFea) {
			//reset to feature view for next selection
			this.set('chartVisible', NO);
			this.set('featureVisible', YES);
		}
    }.observes('selection'),

    showChart: function() {
       	this.set('featureVisible', NO);
        this.set('chartVisible', YES);
    },

    showFeature: function() {
        this.set('featureVisible', YES);
        this.set('chartVisible', NO);
    },

	hasChart: function(){
		return this.get('featuretype').get('hasChart');
	}.property('featuretype').cacheable()

});
