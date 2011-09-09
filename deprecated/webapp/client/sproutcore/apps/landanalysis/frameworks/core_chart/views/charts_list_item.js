// ==========================================================================
// Project:   CoreChart.ChartListItemView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
CoreChart.ChartListItemView = SC.View.extend({
		
		//content is a chart_list_item
		
		childViews: 'mainView titleLabel'.w(),
		
		init: function(){
			this.mainView = this.content.get('feature').get('chartsViewClass');
			sc_super();
			this.titleLabel.set('value', this.content.get('feature').get('labelInspector'));
			this.mainView.adjust('top', 24);
		},
		
		mainView: null,
		
		titleLabel: SC.LabelView.design({
			layout: {
	            top: 1,
	            left: 0,
				right: 0,
	            height: 24
	        },
			controlSize: SC.LARGE_CONTROL_SIZE,
			textAlign: null,
			classNames: 'label-centered chart-list-title'.w()				
		})
});



