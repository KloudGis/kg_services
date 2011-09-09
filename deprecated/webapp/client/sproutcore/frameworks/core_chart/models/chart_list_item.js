// ==========================================================================
// Project:   CoreChart.ChartListItem
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  @extends SC.Object
  @version 0.1
*/
CoreChart.ChartListItem = SC.Object.extend({
	
	feature: undefined,
	
	
	chartsView: function(){
		return CoreChart.ChartListItemView;
	}.property(),
	
	destroy: function(){
		sc_super();
		this.feature = undefined;
	}
	
	
});