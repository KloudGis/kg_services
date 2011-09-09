// ==========================================================================
// Project:   Kloudgis.UserDescriptor
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  @extends SC.Object
  @version 0.1
*/
Kloudgis.UserDescriptor = SC.Object.extend({
	
	name: null,
	fullname: null,
	
	label: function(){
		if(!SC.none(this.get('fullname'))){
			return this.get('fullname');
		}else{
			return this.get('name');
		}
	}.property('name', 'fullname').cacheable()
});