// ==========================================================================
// Project:   Useradmin.userSelectedController
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Useradmin.userSelectedController = SC.ObjectController.create(
/** @scope Useradmin.userSelectedController.prototype */
{

    contentBinding: 'Useradmin.usersController.selection',
    contentBindingDefault: SC.Binding.single()

});
