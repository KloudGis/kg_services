// ==========================================================================
// Project:   Kloudgis.Priority
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  @extends SC.Record
  @version 0.1
*/
Kloudgis.Priority = SC.Record.extend(
/** @scope Kloudgis.Priority.prototype */ {
		
	attr_value: SC.Record.attr(String),
    priority: SC.Record.attr(Number),
	attribute: SC.Record.toOne('Kloudgis.Attrtype', { 
    	inverse: "selectionPriority" 
  	})
});
