// ==========================================================================
// Project:   Kloudgis.LayerView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  List view for featuretype

  @extends SC.View
*/
sc_require('views/layer_list_item')
Kloudgis.LayerView = SC.View.extend(
/** @scope Kloudgis.LayerView.prototype */ {
	
	controller: null,
  	childViews: 'scrollView'.w(),
	scrollView: SC.ScrollView.design({		
        contentView: SC.ListView.design({
			rowHeight: 30,
			classNames: 'round-edges-list'.w(),
            contentValueKey: "label_loc",
            canDeleteContent: NO,
            hasContentIcon: NO,
			exampleView: Kloudgis.LayerListItemView			
        })
    }),

	init: function(){
		sc_super();
		this.scrollView.contentView.bind('content', this.controller + '.arrangedObjects');
		this.scrollView.contentView.bind('selection', this.controller + '.selection');
	}

});
