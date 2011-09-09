// ==========================================================================
// Project:   Landanalysis.LimitChartsView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  Charts for Limit placeitem.

  @extends CoreChart.PlaceItemView
*/
Landanalysis.LimitChartsView = CoreChart.PlaceItemChartsView.extend({
	
	childViews: ['featureButton','chart_1','chart_2', 'chart_3', 'arpenteurs_chart', 'note_chart'],

	chart_1: CoreChart.PlaceItemChartView.design({
		featuretypeName: 'Limit',
		chartName: 'road_km',
		layout: {
			left:0,
			width:'0.33',
			bottom: 35,			
		}	   
    }),

	chart_2: CoreChart.PlaceItemChartView.design({
		featuretypeName: 'Limit',
		chartName: 'hydro_km',
		layout: {
			left:'0.33',
			width:'0.33',
			bottom: 35,			
		}	   
    }),

	chart_3: CoreChart.PlaceItemChartView.design({
		featuretypeName: 'Limit',
		chartName: 'lot_chart',
		layout: {
			left:'0.66',
			right:0,
			bottom: 35,			
		}	   
    }),

	arpenteurs_chart: CoreChart.SingleValueChartView.design({
		featuretypeName: 'Limit',
		chartName: 'arpenteurs',
		chartTitleLoc: '_arpenteur_chart',
		chartTitleValLoc: '_arpenteur_val_chart',
		layout: {
			left:10,
			width:150,
			height: 24,
			bottom: 3			
		}	   
    }),

	note_chart: CoreChart.SingleValueChartView.design({
		featuretypeName: 'Limit',
		chartName: 'notes',
		chartTitleLoc: '_note_chart',
		chartTitleValLoc: '_note_val_chart',
		layout: {
			left:175,
			width:150,
			height: 24,
			bottom: 3			
		}	   
    })
});