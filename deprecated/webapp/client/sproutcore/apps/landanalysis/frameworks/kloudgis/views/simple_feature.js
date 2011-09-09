// ==========================================================================
// Project:   Kloudgis.FeatureView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Features list with no right icon.

  @extends SC.View
*/
sc_require('views/feature_list_item')
Kloudgis.SimpleFeatureView = SC.View.extend(
/** @scope Kloudgis.FeatureView.prototype */ {
	
	controller: null,	
	childViews: 'scrollView'.w(),
	scrollView: SC.ScrollView.design({
        contentView: SC.ListView.design({
			rowHeight: 20,
			classNames: 'round-edges-list'.w(),
            contentValueKey: "label",
            canDeleteContent: NO,
            hasContentIcon: NO,
            //contentIconKey: "icon",
			exampleView: Kloudgis.FeatureListItemNoRightIconView
        })
    }),

	init: function(){
		sc_super();
		this.scrollView.contentView.bind('content', this.controller + '.arrangedObjects');
		this.scrollView.contentView.bind('selection', this.controller + '.selection');
	}

});