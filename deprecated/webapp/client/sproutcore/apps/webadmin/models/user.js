// ==========================================================================
// Project:   Webadmin.User
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document your Model here)

  @extends SC.Record
  @version 0.1
*/
Webadmin.User = SC.Record.extend(
/** @scope Webadmin.User.prototype */
{

    // TODO: Add your own code here.
    name: SC.Record.attr(String),
    fullName: SC.Record.attr(String),
    password: SC.Record.attr(String),
    email: SC.Record.attr(String),
    moreInfo: SC.Record.attr(String),
    expireDate: SC.Record.attr(String),

    group: SC.Record.toOne("Webadmin.Group", {
        inverse: "members",
        isMaster: NO
    }),

    roles: SC.Record.toMany("Webadmin.Role", {
        isMaster: YES
    }),

    icon: 'sc-icon-user-16',

    setGroup: function(group) {
        var actualGroup = this.get('group');
        if (actualGroup) {
            actualGroup.get('members').removeObject(this);
        }
        this.set('group', group);
        if (group) {
            if (group.get('members').indexOf(this) == -1) {
                group.get('members').insertAt(0, this);
            }
        }
    }

});
