// ==========================================================================
// Project:   CoreChart.PlaceItemChartView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
sc_require('views/gv')
sc_require('views/abstract_chart')
CoreChart.PlaceItemChartView = CoreChart.AbstractChartView.extend({

    childViews: 'chartView progressView dropListLabel noDataLabel errDataLabel'.w(),

    progressVisibilityChanged: function() {
        var contrl = this.chartInfoCtrl;
		this.noDataLabel.setIfChanged('isVisible', NO);
		this.errDataLabel.setIfChanged('isVisible', NO);
        if (contrl && contrl !== null) {            
            var vis = contrl.get('chartProgressVisible');
            this.progressView.set('isVisible', vis);
            this.progressView.set('isRunning', vis);
        }
    },

    dataDidChangeLater: function() {
        var info = this.chartInfoCtrl.get('content');
        if (info && this.get('chartVisible')) {
            var data = info.get('chartData');
            if (data === "_error") {
                this.errDataLabel.setIfChanged('isVisible', YES);
				this.chartView.drawPieData(data, info.get('chartTitle'));
            } else {
                this.errDataLabel.setIfChanged('isVisible', NO);
                this.chartView.drawPieData(data, info.get('chartTitle'));
                if (SC.none(data) || !data.rows || data.rows.length === 0) {
                    this.noDataLabel.setIfChanged('isVisible', YES);
                } else {
                    this.noDataLabel.setIfChanged('isVisible', NO);
                }
            }
        }
    },

    chartView: CoreChart.gvView.design({

        layout: {
            top: 0,
            left: 0,
            right: 0,
            bottom: 0
        },
        init: function() {
            sc_super();
            this.addSelectionListener(this.selectionHandler);
        },
        selectionHandler: function() {
            console.log('chart selection made');
            var rowCols = this.getSelection();
            var mainView = this.parentGV.parentView;
            for (var i = 0; i < rowCols.length; i++) {
                var item = rowCols[i];
                if (item.row >= 0) {
                    var chartInfo = mainView.get('chartInfoCtrl').get('content');
                    var data = chartInfo.get('chartData');
                    if (!SC.none(data) && data.rows && data.rows.length >= item.row) {
                        var row = data.rows[item.row];
                        if (!SC.none(row) && row.c && row.c.length >= 0) {
                            var val = row.c[0];
                            if (!SC.none(val) && val.v) {
                                //value
                                var itemValue = val.v;
                                var itemLabel = itemValue;
                                if (val.f) {
                                    //formatted value
                                    itemLabel = val.f;
                                }
                                chartInfo.chartSelectionChanged(itemValue, itemLabel, mainView.dropListLabel);
                            }
                        }
                    }
                    this.setSelection({});
                    break;
                }
            }
        }
    }),

    //progress bar while fetching
    progressView: SC.ProgressView.design({

        layout: {
            centerX: 0,
            centerY: 0,
            width: 100,
            height: 20
        },

        isIndeterminate: YES,
        isVisible: NO
    }),

    //anchor for the selection dialog
    dropListLabel: SC.LabelView.design({
        layout: {
            centerX: 0,
            bottom: '0.25',
            width: 50,
            height: 20
        },
        value: ''
    }),

    //label to show when there is no data to render in the pie chart
    noDataLabel: SC.LabelView.design({
        layout: {
            top: '0.5',
            left: 0,
            right: 0,
            height: 20
        },
        classNames: 'label-centered label-gray'.w(),
        isVisible: NO,
        //use css
        textAlign: null,
        value: '_noDataChart'.loc()
    }),

 //label to show when there is no data to render in the pie chart
    errDataLabel: SC.LabelView.design({
        layout: {
            top: '0.5',
            left: 0,
            right: 0,
            height: 20
        },
        classNames: 'label-centered label-error'.w(),
        isVisible: NO,
        //use css
        textAlign: null,
        value: '_errDataChart'.loc()
    })
});
