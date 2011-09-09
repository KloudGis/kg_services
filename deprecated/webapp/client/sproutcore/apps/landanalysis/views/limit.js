// ==========================================================================
// Project:   Landanalysis.LimitView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  Inspector View for Limit FeatureType
  Extend PlaceItemView to set the controller, the size and the content (featureview)

  @extends CoreChart.PlaceItemView
*/
sc_require('views/limit_charts')
Landanalysis.LimitView = CoreChart.PlaceItemView.extend(
/** @scope Landanalysis.LimitView.prototype */ {

	featuretypeName: 'Limit',
	
	chartSize: {
        width: 525,
        height: 275
    },

	featureView: CoreChart.PlaceItemContainerView.design({		
		featuretypeName: 'Limit',
				
		charts: Landanalysis.LimitChartsView.design({	
		})
	})

});
