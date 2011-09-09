// ==========================================================================
// Project:   CoreChart.AbstractChartView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
CoreChart.AbstractChartView = SC.View.extend({

    featuretypeName: null,
	controller: function(){
		var ftname = this.get('featuretypeName');
		return Kloudgis.modelManager.getController(ftname);
	}.property('featuretypeName').cacheable(),
    chart_list_item: undefined,
    chartName: 'chart',
    chartInfoCtrl: null,
    chartVisible: NO,

    init: function() {
        sc_super();
        if (this.parentView && this.parentView.getPath('parentView.content')) {
			this.chart_list_item = this.parentView.getPath('parentView.content');
			this.set('chartVisible', YES);   
        } else {
			var ctrl = this.get('controller');
            ctrl.addObserver('selection', this, this.selectionChanged);
            ctrl.addObserver('chartVisible', this, this.chartVisibleChanged);
        }
        this.chartInfoCtrl = SC.ObjectController.create({});
        this.chartInfoCtrl.addObserver('chartChanged', this, this.dataDidChange);
        this.chartInfoCtrl.addObserver('chartProgressVisible', this, this.progressVisibilityChanged);
        this.selectionChanged();

    },

    destroy: function() {
        this.chartInfoCtrl.removeObserver('chartChanged', this, this.dataDidChange);
        this.chartInfoCtrl.removeObserver('chartProgressVisible', this, this.progressVisibilityChanged);
		if (this.chartInfoCtrl.get('content')) {
            this.chartInfoCtrl.get('content').destroy();
        }
        if (this.get('featuretypeName')) {
            var ctrl = this.get('controller');
            ctrl.removeObserver('selection', this, this.selectionChanged);
            ctrl.removeObserver('chartVisible', this, this.chartVisibleChanged);
        }
        sc_super();
    },

    selectionChanged: function() {
        var fea;
		if (this.get('chart_list_item')) {
            fea = this.get('chart_list_item').get('feature');
        }else if (this.get('controller')) {
			var ctrl = this.get('controller');
            fea = ctrl.get('activeFeature');
        } 
        if (fea && fea != null && fea.get('getChartInfo')) {
            var cInfo = fea.getChartInfo(this.get('chartName'));
            if (this.chartInfoCtrl.get('content')) {
                this.chartInfoCtrl.get('content').destroy();
            }
            this.chartInfoCtrl.set('content', cInfo);
        }
        if (this.get('chartVisible') && this.chartInfoCtrl.get('content')) {
            this.chartInfoCtrl.get('content').fetchChart();
        }
    },

    chartVisibleChanged: function() {
        if (!this.get('chart_list_item') && this.get('controller')) {
			var ctrl = this.get('controller');
            var vis = ctrl.get('chartVisible');
            this.set('chartVisible', vis);
            if (vis) {
                this.selectionChanged();
            }
        }
    },

    dataDidChange: function() {
        var info = this.chartInfoCtrl.get('content');
        if (info && this.get('chartVisible')) {
            var data = info.get('chartData');
            var title = info.get('chartTitle');
            if (!SC.none(data)) {
                this.invokeLater('dataDidChangeLater', 500);
            }
        }
    },

	//to override
	progressVisibilityChanged: function() {
	},

	//to override
    dataDidChangeLater: function() {
        
    }

});
