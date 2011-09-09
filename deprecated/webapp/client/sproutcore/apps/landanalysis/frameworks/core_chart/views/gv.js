// ==========================================================================
/** @class

  (Document Your View Here)

  @extends SC.View
*/
CoreChart.gvView = SC.View.extend({

    chart: null,
    chartLayerId: 'nullid',
    lateListeners: [],
    //google data table
    dataChart: null,
    title: '',

    init: function() {
        sc_super();
        this.set('lateListeners', []);
    },

    destroy: function() {
        sc_super();
        if (this.chart) {
            google.visualization.events.removeAllListeners(this.chart);
        }
        this.chart = null;
        this.lateListeners = null;
        this.dataChart = null;
        this.title = null;
		CoreChart.set(this.get('layerId'), null);
    },

    didAppendToDocument: function() {
        if (!this.chart) {
            console.log('creating the chart!');
            var element = document.getElementById(this.get("layerId"));
            if (element) {
                this.chart = new google.visualization.PieChart(element);
                this.chart.parentGV = this;
                this.addLateListeners();
            }
            this.chartLayerId = this.get("layerId");
        }
    },

    drawPieData: function(rawData, title) {
        console.log('DRAW PIE!!');
        if (this.get('isDestroyed')) {
            return NO;
        }
		
        if (SC.none(rawData) || !rawData.rows || rawData.rows.length === 0) {
            this.setIfChanged('isVisible', NO);
        } else {
			var data = new google.visualization.DataTable(rawData);
            this.setIfChanged('isVisible', YES);
            var elem = document.getElementById(this.get("chartLayerId"));
            if (this.chart && elem) {
                this.set('title', '');
                this.chart.draw(data, {
                    title: title,
                    legend: 'bottom'
                });
            } else if (elem) {
                this.chart = new google.visualization.PieChart(elem);
                this.chart.parentGV = this;
                this.addLateListeners();
                this.set('title', '');
                this.chart.draw(data, {
                    title: title,
                    legend: 'bottom'
                });
            }
        }
    },

    addSelectionListener: function(listen) {
        if (this.chart) {
			var listener = listen;
			var chart = this.chart;
            google.visualization.events.addListener(this.chart, 'select', function(){
					listener.call(chart);
			});
        } else {
            this.lateListeners.push(listen);
        }
    },

    getSelection: function() {
        if (this.chart) {
            return this.chart.getSelection();
        }
    },

    addLateListeners: function() {
        if (this.chart) {
            var len = this.lateListeners.length;
            var i = 0;
            for (i = 0; i < len; i++) {
                var listener = this.lateListeners[i];
				var chart = this.chart;
	            google.visualization.events.addListener(this.chart, 'select', function(){
						listener.call(chart);
				});
            }
        }
    }
});
