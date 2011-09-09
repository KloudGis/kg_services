// ==========================================================================
// Project:   Kloudgis.FeaturetypeListItemView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
Kloudgis.LayerListItemView = SC.ListItemView.extend(
/** @scope Kloudgis.FeaturetypeListItemView.prototype */
{
	classNames:'featuretype-item'.w(),
    hasContentRightIcon: YES,
	contentCheckboxKey: 'isVisibleLayer',
    renderRightIcon: function(context, icon) {
		if(this.get('content').get('isLayerEditVisibility')){
			//edit icon ?
		}else{
        	context.begin('img').addClass('featuretype-right-icon').attr('src', static_url('images/navigate_right.png')).end();
		}
    },

});