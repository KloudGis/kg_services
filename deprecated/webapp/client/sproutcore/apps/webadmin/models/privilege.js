// ==========================================================================
// Project:   Webadmin.GroupPrivilege
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document your Model here)

  @extends SC.Record
  @version 0.1
*/
Webadmin.Privilege = SC.Record.extend(
/** @scope Webadmin.GroupPrivilege.prototype */
{

    name: SC.Record.attr(String),
    groups: SC.Record.toMany("Webadmin.Group", {
        inverse: "privileges",
        isMaster: NO
    }),

    addGroup: function(group) {
        if (group) {
            if (this.get('groups').indexOf(group) == -1) {
                this.get('groups').insertAt(0, group);
            }
        }
    },

	removeGroup: function(group) {
        if (group) {
            if (this.get('groups').indexOf(group) != -1) {
                this.get('groups').removeObject(group);
            }
        }
    }
});
