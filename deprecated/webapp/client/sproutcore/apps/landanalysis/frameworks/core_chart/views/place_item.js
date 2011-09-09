// ==========================================================================
// Project:   CoreChart.PlaceItemView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  The inspector view for a placeitem FeatureType (Abstract)
  @extends SC.View
*/
CoreChart.PlaceItemView = Kloudgis.InspectorFeatureView.extend(
/** @scope CoreChart.PlaceItemView.prototype */
{
    //default layout and Feature layout
    inspectorSize: undefined,
    //chart layout
    featureSize: {
        width: 400,
        height: 245
    },

    chartSize: {
        width: 500,
        height: 265
    },

    init: function() {
		this.set('inspectorSize', this.get('featureSize'));
        sc_super();
        var ctrl = this.get('controller');
        ctrl.addObserver('chartVisible', this, this.chartVisibleChanged);
    },

	destroy: function(){
		sc_super();
		var ctrl = this.get('controller');
        ctrl.removeObserver('chartVisible', this, this.chartVisibleChanged);
	},

	chartVisibleChanged: function(){
		var ctrl = this.get('controller');
		var chart = ctrl.get('chartVisible');
		if(chart){
			if(this.get('extraHeight') !== 0){
				this.set('inspectorSize', {width:this.get('chartSize').width, height: this.get('chartSize').height + this.get('extraHeight')});
			}else{
				this.set('inspectorSize',  this.get('chartSize'));
			}			
		}else{
			if(this.get('extraHeight') !== 0){
				this.set('inspectorSize', {width:this.get('featureSize').width, height: this.get('featureSize').height + this.get('extraHeight')});
			}else{
				this.set('inspectorSize',  this.get('featureSize'));
			}
		}
	}
});
