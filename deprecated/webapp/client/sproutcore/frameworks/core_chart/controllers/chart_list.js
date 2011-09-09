// ==========================================================================
// Project:   CoreChart.chartListController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your Controller Here)

  @extends SC.ArrayController
*/
CoreChart.chartListController = SC.ArrayController.create({

    addChartForFeature: function(feature) {
        if (!this.get('content')) {
            this.set('content', []);
        }
        if (feature.get('getChartInfo')) {
            var item = CoreChart.ChartListItem.create({
                feature: feature
            });
            this.addObject(item);
            this.selectObject(item);
        }
    },

    removeAllChart: function() {
        CoreChart.chartListController.forEach(function(item) {
            item.destroy();
        });
        CoreChart.chartListController.set('content', []);
    }
});
