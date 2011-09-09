// ==========================================================================
// Project:   CoreChart.PlaceItemChartView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
sc_require('views/abstract_chart')
 CoreChart.SingleValueChartView = CoreChart.AbstractChartView.extend({

    childViews: 'buttonView progressView'.w(),
	chartTitleLoc: "",
    chartTitleValLoc: '%@',

    progressVisibilityChanged: function() {
        var contrl = this.chartInfoCtrl;
        if (contrl && contrl !== null) {
            var vis = contrl.get('chartProgressVisible');
            this.progressView.set('isVisible', vis);
        }
    },

    dataDidChangeLater: function() {
        var info = this.chartInfoCtrl.get('content');
        if (info && this.get('chartVisible')) {
            var data = info.get('chartData');			
            if (SC.none(data)) {
                this.buttonView.set('title', '');
            } else {
                this.buttonView.set('title', this.get('chartTitleValLoc').loc(data));
            }
        }
    },

    buttonView: SC.ButtonView.design({
        layout: {
			height:24
        },
        title: '',
		action: function(){
			var chartInfo = this.parentView.get('chartInfoCtrl').get('content');
            chartInfo.chartSelectionChanged(null,this.parentView.get('chartTitleLoc').loc(), this);
		}
    }),

    //infinite progress anim
    progressView: Kloudgis.ProgressLoopView.design({
		theme: 'gray',
        layout: {
            width: 16,
            height: 16,
            centerY: 0,
            right: 5
        }
    })
});