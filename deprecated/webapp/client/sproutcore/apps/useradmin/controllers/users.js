// ==========================================================================
// Project:   Useradmin.usersController
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Useradmin.usersController = SC.ArrayController.create(
/** @scope Useradmin.usersController.prototype */
{
	
	allowsMultipleSelection: NO,
	userFilter: null,

    selectionDidChanged: function() {
        var sel = this.get('selection');
        if (sel) {
            var first = sel.get('firstObject');
            if (first) {
                Useradmin.statechart.sendEvent('userSelected', this, first);
            } else {
                Useradmin.statechart.sendEvent('userClearSelection', this);
            }
        } else {
            Useradmin.statechart.sendEvent('userClearSelection', this);
        }
    }.observes('selection'),

	userFilterChanged: function(){
		SC.Logger.warn('User filter changed! ' + this.get('userFilter'));
		Useradmin.loadUsers();
	}.observes('userFilter'),
	
	countLabel: function(){
		return this.get('length') + " " + "_users".loc();
	}.property('length'),
		
});
