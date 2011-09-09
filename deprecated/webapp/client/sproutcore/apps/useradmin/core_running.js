SC.mixin(Useradmin, {

    loadUsers: function() {
        Useradmin.USERS_QUERY.set('filter', Useradmin.usersController.get('userFilter'));
        if (SC.none(Useradmin.usersController.get('content'))) {
            Useradmin.usersController.set('content', Useradmin.store.find(Useradmin.USERS_QUERY));
        } else if (Useradmin.usersController.get('content').get('status') & SC.Record.BUSY) {
            SC.Logger.warn('Cannot refresh, the record array is busy');
        } else {
            Useradmin.usersController.get('content').refresh();
        }
    },

    loadCategories: function() {
        var arr_cat = [];
        arr_cat.pushObject(Useradmin.Category.create({
            name: 'profile',
            label: '_profile'.loc(),
            detailView: 'profileView'
        }));
		arr_cat.pushObject(Useradmin.Category.create({
            name: 'admin',
            label: '_admin'.loc(),
            detailView: 'adminView'
        }));
		Useradmin.categoryController.set('content', arr_cat);
    },

    flushUsers: function() {
        var storeK = Useradmin.store.storeKeysFor(Useradmin.User);
        Useradmin.store.unloadRecords(undefined, undefined, storeK);
    }

});
