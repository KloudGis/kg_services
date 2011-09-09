// ==========================================================================
// Project:   Landanalysis.HydroView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document Your View Here)

  @extends CityChartsView.PlaceItemView
*/
Landanalysis.HydroView = CoreChart.PlaceItemView.extend(
/** @scope Landanalysis.HydroView.prototype */
{

    featuretypeName: 'Hydro',

	featureSize: {
        width: 400,
        height: 225
    },

    init: function() {
        sc_super();
        this.featureView.set('layout', this.getPath('featureLayout'));
    },

    featureView: CoreChart.PlaceItemFeatureView.design({
        featuretypeName: 'Hydro'		
    })

});
