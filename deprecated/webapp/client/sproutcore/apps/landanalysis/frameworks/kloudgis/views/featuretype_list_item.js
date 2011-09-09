// ==========================================================================
// Project:   Kloudgis.FeaturetypeListItemView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
Kloudgis.FeaturetypeListItemView = SC.ListItemView.extend(
/** @scope Kloudgis.FeaturetypeListItemView.prototype */
{
	classNames:'featuretype-item'.w(),
    hasContentRightIcon: YES,
    renderRightIcon: function(context, icon) {
        context.begin('img').addClass('featuretype-right-icon').attr('src', static_url('images/navigate_right.png')).end();
    },

});
