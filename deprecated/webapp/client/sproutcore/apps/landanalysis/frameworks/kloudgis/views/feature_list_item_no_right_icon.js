// ==========================================================================
// Project:   Kloudgis.FeatureListItemView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.ListItemView
*/
sc_require('views/feature_list_item')
Kloudgis.FeatureListItemNoRightIconView = Kloudgis.FeatureListItemView.extend(
/** @scope Kloudgis.FeatureListItemView.prototype */
{
	hasContentRightIcon: NO,
	
	contentPropertyDidChange: function() {
		sc_super();
		this.set('hasContentRightIcon', NO);
	}
});