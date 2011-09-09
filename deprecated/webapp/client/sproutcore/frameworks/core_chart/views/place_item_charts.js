// ==========================================================================
// Project:   CoreChart.PlaceItemChartsView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  The charts view (Pie chart) for the place item.

  @extends SC.View
*/
CoreChart.PlaceItemChartsView = SC.View.design({

   controller: null,

   childViews: ['featureButton'],

    init: function() {
        sc_super();
		var ctrl = this.getPath('parentView').get('controller');
		if(SC.none(ctrl)){
			this.featureButton.set('isVisible', NO);		
		}else{
			this.set('controller', ctrl);	
		}
    },

    featureButton: SC.ButtonView.design({
        layout: {
            bottom: 3,
            right: 10,
            width: 100,
            height: 24
        },
        title: '_feature'.loc(),
		hasIcon: YES,
		icon: static_url('images/information2.png'),
        action: function() {
            var controller = this.parentView.get('controller');
            if (controller) {
                controller.showFeature();
            }
        },
    })
});
