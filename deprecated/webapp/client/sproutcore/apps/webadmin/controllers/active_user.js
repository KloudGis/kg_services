// ==========================================================================
// Project:   Webadmin.activeUserController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Controller Here)

  @extends SC.ObjectController
*/
Webadmin.activeUserController = SC.ObjectController.create(
/** @scope Webadmin.activeUserController.prototype */
{

    contentBinding: 'Webadmin.usersController.selection',
    //transform to single any collection
    contentBindingDefault: SC.Binding.single(),
    detailsViewVisible: NO,
    voidViewVisible: YES,
    expireMessage: "",

    userChanged: function() {
        //console.log("Commit!");
        Webadmin.invokeLater(function() {
            Webadmin.store.commitRecords();
        });
    }.observes('name', 'fullName', 'email', 'password', 'expireDate', 'moreInfo'),

    nameChanged: function() {
       // console.log("Name changed");
        Webadmin.usersController.scrollToSelection();
    }.observes('name'),

    activeUserChanged: function() {
        var _user = this.get('content');
        if (_user) {
            this.set('detailsViewVisible', YES);
            this.set('voidViewVisible', NO);
        } else {
            this.lastUser = null;
            this.set('detailsViewVisible', NO);
            this.set('voidViewVisible', YES);
        }
    }.observes('content'),

    expireChanged: function() {
        var mess = this.getExpireMessage();
        //console.log("Expire message=" + mess);
        this.set('expireMessage', mess);
    }.observes('expireDate'),

    getExpireMessage: function() {
        var _user = this.get('content');
        if (_user) {
            var expire = _user.get('expireDate');
            //console.log("expire is " + expire);
            if (expire && expire.length > 0) {
                var now = SC.DateTime.create();
                var parsedDate = SC.DateTime.parse(expire, '%d-%m-%Y');
                if (parsedDate) {
                    var nowMillis = now.get('milliseconds');
                    var parsedDateMillis = parsedDate.get('milliseconds');
                    //console.log("Now is " + nowMillis + ", Parsed is " + parsedDateMillis);
                    if (nowMillis < parsedDateMillis) {
                        var diff = parsedDateMillis - nowMillis;
                        var day = 86400000;
                        return "_users.%@ days".loc(diff / day | 0);
                    } else {
                        return "_users.Expired Password".loc();
                    }
                } else {
                    return "_users.Cant read expire date".loc();
                }
            } else {
                return "_users.Never Expire Password".loc();
            }
        } else {
            return "";
        }
    },

    changePassword: function() {
        Webadmin.makeFirstResponder(Webadmin.CHANGE_PASSWORD);
    }

});
