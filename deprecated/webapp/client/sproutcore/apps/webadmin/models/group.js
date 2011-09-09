// ==========================================================================
// Project:   Webadmin.Group
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document your Model here)

  @extends SC.Record
  @version 0.1
*/
Webadmin.Group = SC.Record.extend(
/** @scope Webadmin.Group.prototype */
{

    name: SC.Record.attr(String),
    parent_group: SC.Record.attr(Number),
    members: SC.Record.toMany("Webadmin.User", {
        inverse: "group",
        isMaster: YES
    }),
    privileges: SC.Record.toMany("Webadmin.Privilege", {
        inverse: "groups",
        isMaster: YES
    }),
    icon: 'sc-icon-group-16',

    parent_group_label: function(key, value) {
        if (value != undefined) {
            if (SC.empty(value)) {
                this.set('parent_group', null);
            } else {
                var groups = Webadmin.store.find(Webadmin.GROUPS_QUERY);
                var query = SC.Query.create({
                    recordType: Webadmin.Group,
                    conditions: "name = '%@'".fmt(value)
                });
                var group = groups.find(query);
                if (group && group.get('length') > 0) {
                    var pid = group.objectAt(0).get('id');
                    if (pid != this.get('id')) {
                        this.set('parent_group', pid);
                    }
                }
            }
        }
        if (!SC.none(this.get('parent_group'))) {
            var group = Webadmin.store.find(Webadmin.Group, this.get('parent_group'));
            if (group) {
                return group.get('name');
            }
        } else {
            return "";
        }
    }.property('parent_group').cacheable()

});
