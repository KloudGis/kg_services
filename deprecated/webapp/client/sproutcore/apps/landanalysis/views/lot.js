// ==========================================================================
// Project:   Landanalysis.LotView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document Your View Here)

  @extends CityChartsView.PlaceItemView
*/
Landanalysis.LotView = CoreChart.PlaceItemView.extend(
/** @scope Landanalysis.LotView.prototype */ {

 	featuretypeName: 'Lot',

	featureSize: {
        width: 400,
        height: 225
    },

    init: function() {
        sc_super();
        this.featureView.set('layout', this.getPath('featureLayout'));
    },

    featureView: CoreChart.PlaceItemFeatureView.design({
        featuretypeName: 'Lot'
    })

});