// ==========================================================================
// Project:   CoreChart.PlaceItemFeatureView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  The feature view (attributes) for the place item.

  @extends SC.View
*/
CoreChart.PlaceItemFeatureView = SC.View.design({

   // classNames: ['inspector-feature-view'],
    childViews: 'title description category subcategory chartButton'.w(),
	
    title: Kloudgis.InspectorTextFieldView.design({
        attribute: 'title',
        classNames: 'top',
        layout: {
            top: 5,
            left: 10,
            right: 10,
            height: 30
        },
        init: function() {
			this.set('featuretypeName', this.getPath('parentView.parentView').get('featuretypeName'));
		    sc_super();           
        }
    }),

    description: Kloudgis.InspectorTextFieldView.design({
        attribute: 'description',
        isTextArea: YES,
        classNames: 'middle',
        layout: {
            top: 35,
            left: 10,
            right: 10,
            height: 60
        },
        init: function() {		    
            this.set('featuretypeName', this.getPath('parentView.parentView').get('featuretypeName'));
			sc_super();
        }
    }),

	category: Kloudgis.InspectorTextFieldView.design({
        attribute: 'category',
        classNames: 'middle',
        layout: {
            top: 95,
            left: 10,
            right: 10,
            height: 30
        },
        init: function() {		    
            this.set('featuretypeName', this.getPath('parentView.parentView').get('featuretypeName'));
			sc_super();
        }
    }),

	subcategory: Kloudgis.InspectorTextFieldView.design({
        attribute: 'subcategory',
        classNames: 'bottom',
        layout: {
            top: 125,
            left: 10,
            right: 10,
            height: 30
        },
        init: function() {		    
            this.set('featuretypeName', this.getPath('parentView.parentView').get('featuretypeName'));
			sc_super();
        }
    }),

    chartButton: SC.ButtonView.design({
        layout: {
            bottom: 3,
            centerX: 0,
            width: 100,
            height: 24
        },
        title: '_chart'.loc(),
		hasIcon: YES,
		icon: static_url('images/pie-chart.png'),
		
		init:function(){
			sc_super();
			var controller = this.getPath('parentView.parentView').get('controller');
			var bChart = controller.get('hasChart');
			if(SC.none(bChart)){
				bChart = NO;
			}
			this.set('isVisible', bChart);
		},
		
        action: function() {
            var controller = this.getPath('parentView.parentView').get('controller');
            if (controller) {
				var selection = controller.get('selection').get('firstObject');
				if(selection){
					controller.showChart();
				}
            }
        }
    })

});