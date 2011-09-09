// ==========================================================================
// Project:   Webadmin.activeGroupController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Webadmin.activeGroupController = SC.ObjectController.create(
/** @scope Webadmin.activeGroupController.prototype */
{
	contentBinding: 'Webadmin.groupsController.selection',
    //transform to single any collection
    contentBindingDefault: SC.Binding.single(),
    detailsViewVisible: NO,
    voidViewVisible: YES,
    
    nameChanged: function() {
		if(!SC.isEqual(Webadmin.groupsController.activeName, this.get('name'))){
			Webadmin.groupsController.set('activeName', this.get('name'));
			Webadmin.groupsController.changeMade();
		}
        Webadmin.groupsController.scrollToSelection();
    }.observes('name'),

    activeGroupChanged: function() {  
        var _group = this.get('content');
        if (_group) {
            this.set('detailsViewVisible', YES);
            this.set('voidViewVisible', NO);
        } else {
            this.lastUser = null;
            this.set('detailsViewVisible', NO);
            this.set('voidViewVisible', YES);
        }    
    }.observes('content'),

	
	

});
