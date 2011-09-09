// ==========================================================================
// Project:   Kloudgis.FeatureListItemView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.ListItemView
*/
Kloudgis.FeatureListItemView = SC.ListItemView.extend(
/** @scope Kloudgis.FeatureListItemView.prototype */
{
    classNames: 'list-feature-label'.w(),
    hasContentRightIcon: YES,

    contentPropertyDidChange: function() {
        var fea = this.get('content');
        if (!SC.none(fea)) {
            var ft = fea.get('featuretype');
            if (!SC.none(ft)) {
                this.set('hasContentRightIcon', ft.get('selectionable') === YES);
            }
        }
        sc_super();
    },

    renderRightIcon: function(context, icon) {
        context.begin('img').addClass('right-icon').addClass('feature-right-icon').attr('src', static_url('images/information2.png')).attr('width', 16).end();
    },

    _isInsideRightIcon: function(evt) {
        if (this.hasContentRightIcon) {
            var pv = this.parentView;
            var dv = this;
            var c = pv.convertFrameToView(dv.get('frame'), null);
            c.x = c.x + c.width - 16;
            var inV = SC.pointInRect({
                x: evt.pageX,
                y: evt.pageY
            },
            c);
            //console.log('on button:' + inV);
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
                this.showInspector(item.content);
            }
        }
        return ret;
    },

    showInspector: function(feature) {
        if (feature) {
            //console.log("Show inspector for: "  + feature.get('label'));
            Kloudgis.inspectorController.featureSelected(feature);
        }
    }

});
