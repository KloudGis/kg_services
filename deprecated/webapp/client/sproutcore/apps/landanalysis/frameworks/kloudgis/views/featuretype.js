// ==========================================================================
// Project:   Kloudgis.FeaturetypeView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  List view for featuretype

  @extends SC.View
*/
sc_require('views/featuretype_list_item')
Kloudgis.FeaturetypeView = SC.View.extend(
/** @scope Kloudgis.FeaturetypeView.prototype */ {

	controller: null,
  	childViews: 'scrollView'.w(),
	scrollView: SC.ScrollView.design({		
        contentView: SC.ListView.design({
			classNames: 'round-edges-list'.w(),
			rowHeight: 30,
            contentValueKey: "label_loc",
            canDeleteContent: NO,
            hasContentIcon: NO,
			exampleView: Kloudgis.FeaturetypeListItemView			
        })
    }),
	init: function(){
		sc_super();
		this.scrollView.contentView.bind('content', this.controller + '.arrangedObjects');
		this.scrollView.contentView.bind('selection', this.controller + '.selection');
	}
});
