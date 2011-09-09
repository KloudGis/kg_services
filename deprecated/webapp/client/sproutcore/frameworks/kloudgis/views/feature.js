// ==========================================================================
// Project:   Kloudgis.FeatureView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
sc_require('views/feature_list_item')
Kloudgis.FeatureView = SC.View.extend(
/** @scope Kloudgis.FeatureView.prototype */ {
	
	controller: null,	
	childViews: 'scrollView'.w(),	
	scrollView: SC.ScrollView.design({		
        contentView: SC.ListView.design({			
			rowHeight: 20,
            contentValueKey: "label",
            canDeleteContent: NO,
            hasContentIcon: NO,
            //contentIconKey: "icon",
			exampleView: Kloudgis.FeatureListItemView	
        })
    }),

	init: function(){
		sc_super();
		this.scrollView.contentView.bind('content', this.controller + '.arrangedObjects');
		this.scrollView.contentView.bind('selection', this.controller + '.selection');
	}

});
