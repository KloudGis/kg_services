// ==========================================================================
// Project:   Useradmin.UserListItemView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

/** @class

  (Document Your View Here)

  @extends SC.ListItemView
*/
Useradmin.UserListItemView = SC.ListItemView.extend(
/** @scope Useradmin.UserListItemView.prototype */
{
    classNames: 'user-list-item-view'.w(),
	
	hasContentIcon: YES, 
	contentIconKey: 'iconSuper',
	
	hasContentRightIcon: YES,
	contentRightIconKey: 'iconActive',
	
	_isInsideRightIcon: function(evt) {
        if (this.hasContentRightIcon) {
            var pv = this.parentView;
            var dv = this;
            var c = pv.convertFrameToView(dv.get('frame'), null);
            c.x = c.x + c.width - 22;
            var inV = SC.pointInRect({
                x: evt.pageX,
                y: evt.pageY
            },
            c);
            return inV;
        }
        return NO;
    },

    mouseDown: function(evt) {
        var ret = sc_super();
        if (this._isInsideRightIcon(evt)) {
            var pv = this.parentView;
            var item = pv.itemViewForEvent(evt);
            if (item) {
                Useradmin.statechart.sendEvent('toggleActive', item.content);
            }
        }
        return ret;
    },
	
});
