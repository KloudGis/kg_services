// ==========================================================================
// Project:   Useradmin.categorySelectedController
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Useradmin.categorySelectedController = SC.ObjectController.create(
/** @scope Useradmin.categorySelectedController.prototype */ {
	
	contentBinding: 'Useradmin.categoryController.selection',
	contentBindingDefault: SC.Binding.single(),

}) ;
