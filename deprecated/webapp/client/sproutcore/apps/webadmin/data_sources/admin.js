// ==========================================================================
// Project:   Webadmin.UsersDataSource
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Data Source Here)

  @extends SC.DataSource
*/
sc_require('models/user');
sc_require('models/group');
sc_require('models/role');
sc_require('models/privilege');
Webadmin.USERS_QUERY = SC.Query.local(Webadmin.User, {
    orderBy: 'name'
});
Webadmin.GROUPS_QUERY = SC.Query.local(Webadmin.Group, {
    orderBy: 'name'
});

Webadmin.PRIVILEGES_QUERY = SC.Query.local(Webadmin.Privilege, {
    orderBy: 'name'
});

Webadmin.ROLES_QUERY = SC.Query.local(Webadmin.Role, {
    orderBy: 'role_name'
});

Webadmin.AdminDataSource = SC.DataSource.extend(
/** @scope Webadmin.UsersDataSource.prototype */
{
    // ..........................................................
    // QUERY SUPPORT
    // 
    fetch: function(store, query) {
        if (query === Webadmin.USERS_QUERY) {
            console.log("FETCH USERS!!");
            SC.Request.getUrl(Webadmin.context + '/resources/admin/users').json().notify(this, 'didFetchUsers', store, query).send();
            return YES;
        } else if (query === Webadmin.GROUPS_QUERY) {
            console.log("FETCH GROUPS!!");
            SC.Request.getUrl(Webadmin.context + '/resources/admin/groups').json().notify(this, 'didFetchGroups', store, query).send();
            return YES;
        } else if (query === Webadmin.PRIVILEGES_QUERY) {
            console.log("FETCH PRIVILEGES!!");
            SC.Request.getUrl(Webadmin.context + '/resources/admin/privileges').json().notify(this, 'didFetchPrivileges', store, query).send();
            return YES;
        } else if (query === Webadmin.ROLES_QUERY) {
            console.log("FETCH ROLES!!");
            SC.Request.getUrl(Webadmin.context + '/resources/admin/roles').json().notify(this, 'didFetchRoles', store, query).send();
            return YES;
        }
        return NO;
    },

    didFetchUsers: function(response, store, query) {
        console.log('U Fetch done');
        //remove all users to replace them with the new ones
        store.unloadRecords(Webadmin.User, store.storeKeysFor(Webadmin.User));
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                var body = response.get('body');
                var storeKeys;
                if (body) {
                    storeKeys = store.loadRecords(Webadmin.User, body);
                }
                store.dataSourceDidFetchQuery(query);
                Webadmin.usersController.datasourceStatus(200);
            } else {
                store.dataSourceDidErrorQuery(query, response);
                Webadmin.usersController.datasourceStatus(response.get('status'), response.get('body'));
            }
        }
    },

    didFetchGroups: function(response, store, query) {
        console.log('G Fetch done');
        //remove all groups to replace them with the new ones
        store.unloadRecords(Webadmin.Group, store.storeKeysFor(Webadmin.Group));
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                var body = response.get('body');
                var storeKeys;
                if (body) {
                    storeKeys = store.loadRecords(Webadmin.Group, body);
                }
                store.dataSourceDidFetchQuery(query);
                Webadmin.groupsController.fetchCompleted();
                Webadmin.groupsController.datasourceStatus(response.get('status'), response.get('body'));
            } else {
                store.dataSourceDidErrorQuery(query, response);
                Webadmin.groupsController.fetchCompleted();
                Webadmin.groupsController.datasourceStatus(response.get('status'), response.get('body'));
            }
        }
    },

    didFetchPrivileges: function(response, store, query) {
        console.log('P Fetch done');
        //remove all privs to replace them with the new ones
        store.unloadRecords(Webadmin.Privilege, store.storeKeysFor(Webadmin.Privilege));
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                var body = response.get('body');
                var storeKeys;
                if (body) {
                    storeKeys = store.loadRecords(Webadmin.Privilege, body);
                }
                store.dataSourceDidFetchQuery(query);
                Webadmin.groupsController.datasourceStatus(response.get('status'), response.get('body'));
            } else {
                store.dataSourceDidErrorQuery(query, response);
                Webadmin.groupsController.datasourceStatus(response.get('status'), response.get('body'));
            }
        }
    },

    didFetchRoles: function(response, store, query) {
        console.log('R Fetch done');
        //remove all privs to replace them with the new ones
        store.unloadRecords(Webadmin.Role, store.storeKeysFor(Webadmin.Role));
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                var body = response.get('body');
                var storeKeys;
                if (body) {
                    storeKeys = store.loadRecords(Webadmin.Role, body);
                }
                store.dataSourceDidFetchQuery(query);
                Webadmin.usersController.datasourceStatus(response.get('status'), response.get('body'));
            } else {
                store.dataSourceDidErrorQuery(query, response);
                Webadmin.usersController.datasourceStatus(response.get('status'), response.get('body'));
            }
        }
    },

    // ..........................................................
    // RECORD SUPPORT
    // 
    retrieveRecord: function(store, storeKey) {
        console.log("RETREIVE!! " + store.recordTypeFor(storeKey));
        if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.User) && store.idFor(storeKey)) {
            console.log("get user" + store.idFor(storeKey));
            SC.Request.getUrl(Webadmin.context + '/resources/admin/users/' + store.idFor(storeKey)).json().notify(this, 'didRetrieve', store, storeKey).send();
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Group) && store.idFor(storeKey)) {
            console.log("get group" + store.idFor(storeKey));
            SC.Request.getUrl(Webadmin.context + '/resources/admin/groups/' + store.idFor(storeKey)).json().notify(this, 'didRetrieve', store, storeKey).send();
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Privilege) && store.idFor(storeKey)) {
            console.log("get priv" + store.idFor(storeKey));
            SC.Request.getUrl(Webadmin.context + '/resources/admin/privileges/' + store.idFor(storeKey)).json().notify(this, 'didRetrieve', store, storeKey).send();
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Role) && store.idFor(storeKey)) {
            console.log("get role" + store.idFor(storeKey));
            SC.Request.getUrl(Webadmin.context + '/resources/admin/roles/' + store.idFor(storeKey)).json().notify(this, 'didRetrieve', store, storeKey).send();
            return YES;
        } else {
            return NO;
        }
    },

    didRetrieve: function(response, store, storeKey) {
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                var data = response.get('body');
                if (data) {
                    store.dataSourceDidComplete(storeKey, data);
                } else {
                    store.dataSourceDidComplete(storeKey);
                }
            } else {
                store.dataSourceDidError(storeKey, response);
            }
        }
    },

    //create 
    createRecord: function(store, storeKey) {
        console.log("CREATE!!" + storeKey);
        if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.User)) {
            SC.Request.postUrl(Webadmin.context + '/resources/admin/users').json().notify(this, this.didCreate, store, storeKey).send(store.readDataHash(storeKey));
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Group)) {
            SC.Request.postUrl(Webadmin.context + '/resources/admin/groups').json().notify(this, this.didCreate, store, storeKey).send(store.readDataHash(storeKey));
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Role)) {
            SC.Request.postUrl(Webadmin.context + '/resources/admin/roles').json().notify(this, this.didCreate, store, storeKey).send(store.readDataHash(storeKey));
            return YES;
        } else return NO;
    },

    didCreate: function(response, store, storeKey) {
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                var data = response.get('body');
                console.log("Create OK");
                // console.log(data);
                if (data) {
                    var id = data.guid;
                    store.dataSourceDidComplete(storeKey, data, id);
                } else {
                    store.dataSourceDidComplete(storeKey);
                }
            } else {
                store.dataSourceDidError(storeKey, response);
            }
        }
    },

    //update
    updateRecord: function(store, storeKey) {
        console.log("UPDATE!!");
        if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.User)) {
            SC.Request.putUrl(Webadmin.context + '/resources/admin/users/' + store.idFor(storeKey)).json().notify(this, this.didUpdate, store, storeKey).send(store.readDataHash(storeKey));
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Group)) {
            SC.Request.putUrl(Webadmin.context + '/resources/admin/groups/' + store.idFor(storeKey)).json().notify(this, this.didUpdate, store, storeKey).send(store.readDataHash(storeKey));
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Role)) {
            SC.Request.putUrl(Webadmin.context + '/resources/admin/roles/' + store.idFor(storeKey)).json().notify(this, this.didUpdate, store, storeKey).send(store.readDataHash(storeKey));
            return YES;
        } else {
            return NO;
        }
    },

    didUpdate: function(response, store, storeKey) {
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                var data = response.get('body');
                if (data) {
                    store.dataSourceDidComplete(storeKey, data);
                } else {
                    store.dataSourceDidComplete(storeKey);
                }
            } else {
                store.dataSourceDidError(storeKey);
            }
        }
    },

    //destroy
    destroyRecord: function(store, storeKey) {
        console.log("DESTROY!!");
        if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.User)) {
            SC.Request.deleteUrl(Webadmin.context + '/resources/admin/users/' + store.idFor(storeKey)).set('isJSON', YES).json().notify(this, this.didDestroy, store, storeKey).send();
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Group)) {
            SC.Request.deleteUrl(Webadmin.context + '/resources/admin/groups/' + store.idFor(storeKey)).json().notify(this, this.didDestroy, store, storeKey).send();
            return YES;
        } else if (SC.kindOf(store.recordTypeFor(storeKey), Webadmin.Role)) {
            SC.Request.deleteUrl(Webadmin.context + '/resources/admin/roles/' + store.idFor(storeKey)).json().notify(this, this.didDestroy, store, storeKey).send();
            return YES;
        } else {
            return NO;
        }
    },

    didDestroy: function(response, store, storeKey) {
        if (this.securityCheck(response)) {
            if (SC.ok(response)) {
                console.log('destroy successful');
                store.dataSourceDidDestroy(storeKey);
            } else {
                console.log('destroy ERROR');
                console.log(response.get('body'));
                store.dataSourceDidDestroy(storeKey);
                store.find(Webadmin.USERS_QUERY).refresh();
            }
        }
    },

    securityCheck: function(response, query) {
        if (SC.kindOf(response.get('body'), SC.Error)) {
            this.gotoWelcome();
            return NO;
        } else {
            return YES;
        }
    },

    gotoWelcome: function() {
        if (SC.buildMode === 'debug') {
            window.location = 'http://localhost:8080' + Webadmin.context + '/protected/welcome_dev.html?app=webadmin'
        } else {
            window.location.href = Webadmin.context + '/protected/welcome.html?app=webadmin'
        }
    }
});
