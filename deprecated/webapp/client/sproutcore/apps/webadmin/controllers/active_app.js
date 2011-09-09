// ==========================================================================
// Project:   Webadmin.activeApp
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Webadmin.activeAppController = SC.ObjectController.create(
/** @scope Webadmin.activeApp.prototype */
{

    activeTitle: null,
    activePage: null,
    usersSelected: null,
    groupsSelected: null,

    switchToUsers: function() {
        //refresh users
        //console.log('switch to users');
		this.set('usersSelected', YES);	     
        Webadmin.makeFirstResponder(Webadmin.START_USER);
    },

    switchToGroups: function() {
        //console.log('switch to groups');
		this.set('groupsSelected', YES);
        Webadmin.makeFirstResponder(Webadmin.START_GROUP);
    },

    usersToggle: function() {
        //console.log('users toggle');
        //console.log(this.usersSelected);
        if (this.usersSelected) {
            this.set('groupsSelected', NO);
            this.switchToUsers();
        } else if (!this.groupsSelected) {
            this.set('usersSelected', YES);
        }
    }.observes('usersSelected'),

    groupsToggle: function() {
        //console.log(this.groupsSelected);
        //console.log('group toggle');
        if (this.groupsSelected) {
            this.set('usersSelected', NO);
            this.switchToGroups();
        } else if (!this.usersSelected) {
            this.set('groupsSelected', YES);
        }
    }.observes('groupsSelected'),

});
