// ==========================================================================
// Project:   Useradmin.categoryController
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Useradmin.categoryController = SC.ArrayController.create(
/** @scope Useradmin.categoryController.prototype */ {

  	allowsMultipleSelection: NO,

	selectionDidChanged: function() {
        var sel = this.get('selection');
        if (sel) {
            var first = sel.get('firstObject');
            if (first) {
                Useradmin.statechart.sendEvent('categorySelected', this, first);
            } else {
                Useradmin.statechart.sendEvent('categoryClearSelection', this);
            }
        } else {
            Useradmin.statechart.sendEvent('categoryClearSelection', this);
        }
    }.observes('selection')

}) ;
