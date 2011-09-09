// ==========================================================================
// Project:   Kloudgis.Attrtype
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Model class for AttrType, the properties for the features.

  @extends SC.Record
  @version 0.1
*/
Kloudgis.Attrtype = SC.Record.extend(
/** @scope Kloudgis.Attrtype.prototype */ {
		
	name: SC.Record.attr(String),
    label: SC.Record.attr(String),
	hint: SC.Record.attr(String),
	visible: SC.Record.attr(Boolean),
	editable: SC.Record.attr(Boolean),
	col_size: SC.Record.attr(Number),
	selectionPriority: SC.Record.toMany('Kloudgis.Priority', { 
    	inverse: "attribute" 
  	}),

	label_loc: function(){
		return this.get('label').loc();
	}.property('label'),
	
	hint_loc: function(){
		return this.get('hint').loc();
	}.property('hint'),
}) ;
