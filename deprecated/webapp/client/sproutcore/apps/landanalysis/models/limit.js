// ==========================================================================
// Project:   Landanalysis.Limit
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document your Model here)

  @extends CoreChart.PlaceItem
  @version 0.1
*/
sc_require('views/limit')
sc_require('views/limit_charts')
Landanalysis.Limit = CoreChart.PlaceItem.extend(
/** @scope Landanalysis.Limit.prototype */
{
    featuretypeName: 'Limit',
    inspectorViewClass: Landanalysis.LimitView,
    chartsViewClass: Landanalysis.LimitChartsView,

    chartNames: ['road_km', 'hydro_km', "lot_chart", "arpenteurs", "notes"],

    //to override
    getChartRecordTypeFor: function(chartName) {
        if (chartName === 'road_km') {
            return 'Landanalysis.Road';
        } else if (chartName === 'hydro_km') {
            return 'Landanalysis.Hydro';
        } else if (chartName === 'arpenteurs') {
            return 'Landanalysis.Arpenteur';
        } else if (chartName === 'lot_chart') {
            return 'Landanalysis.Lot';
        } else if (chartName === 'notes') {
            return 'Landanalysis.Note';
        }
        return NO;
    }
});
