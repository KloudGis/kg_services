// ==========================================================================
// Project:   Kloudgis.ChartInfo
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

	This contains the informations about a chart.   The feature it comes from, the chart name, the title...

  @extends SC.Object
  @version 0.1
*/
CoreChart.ChartInfo = SC.Object.extend({

    //chart's feature  - need to be set
    feature: undefined,
    //chart name - need to be set
    chartName: undefined,
    //items type inside the chart (for selection) - need to be set to activate the selection
    chartRecordType: undefined,

    //data to build chart
    chartData: null,
    //chart's title
    chartTitle: '',
    //flag to trigger chart refresh
    chartChanged: 1,
    //to show the progress bar during the loading
    chartProgressVisible: NO,

    _response: null,

    destroy: function() {
        sc_super();
        if (this._response) {
            //console.log('cancelling request for: ' + this.feature.get('id'));
            this._response.cancel();
        }
		if (this._delayTimer) {
            this._delayTimer.invalidate();
			this._delayTimer = undefined;
        }
        this.feature = undefined;
        this.chartName = undefined;
        this.chartRecordType = undefined;
        this.chartData = undefined;
        this.chartTitle = undefined;
        this.chartChanged = undefined;
        this.chartProgressVisible = undefined;
    },

    fetchChart: function() {
        if (this._delayTimer) {
            this._delayTimer.invalidate();
        }
        this._delayTimer = SC.Timer.schedule({
            target: this,
            action: 'timerFired',
            interval: 500,
            repeats: NO
        });

    },

    timerFired: function() {
        if (!this.get('isDestroyed')) {
			console.log('fetch chart %@ for %@'.fmt(this.chartName, this.get('feature').get('id')));
            var rType = this.get('feature').get('store').recordTypeFor(this.get('feature').get('storeKey'));
            if (rType && rType.prototype.fetchChart) {
                this.set('chartProgressVisible', YES);
                this._response = rType.prototype.fetchChart(this.get('feature'), this.get('chartName'), this, this.didFetchChart);
            }
        }
    },

    didFetchChart: function(success, data, title) {
        if (success) {
            this.setData(this.get('chartName'), data, title);
        } else {
			this.setData(this.get('chartName'), "_error", title);
            this.set('chartProgressVisible', NO);
        }
    },

    setData: function(chartName, data, title) {
        this.set('chartData', data);
        this.set('chartTitle', title);
        this.incrementProperty('chartChanged');
        this.set('chartProgressVisible', NO);
    },

    chartSelectionChanged: function(item, label, anchor) {
        var ft = this.get('feature').get('featuretype');
        var recT = this.get('chartRecordType');
        if (ft && recT) {
            var recordT = ft.get('recordType');
            var recordTChart = SC.objectForPropertyPath(recT);
            var query = SC.Query.remote(recordT, {
                isStreaming: YES,
                handleMethod: 'fetchChartQuery',
                resultRecordType: recordTChart,
                item: item,
                id: this.get('feature').get('id'),
                chartType: this.get('chartName') + '_query',
                label: label
            });
            CoreChart.chartSelectionController.showResult(query, anchor);
        }
    }

});
