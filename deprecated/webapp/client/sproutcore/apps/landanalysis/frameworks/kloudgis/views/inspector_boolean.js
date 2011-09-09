// ==========================================================================
// Project:   Kloudgis.InspectorCheckboxView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Inspector renderer for boolean value

  @extends SC.View
*/
sc_require('views/abstract_renderer')
Kloudgis.InspectorBooleanView = Kloudgis.AbstractRendererView.extend(
/** @scope Kloudgis.InspectorBooleanView.prototype */
{

    updateSecurity: function() {
        //console.log('Security check - CHECKBOX');
        var visible = this.testVisible();
        this.setIfChanged('isVisible', visible);
        if (visible) {
            var enabled = this.testEnabled();
            this.labelView.setIfChanged('isEnabled', enabled);
            this.segmentView.setIfChanged('isEnabled', enabled);
        }
    },

    childViews: 'labelView segmentView'.w(),

    labelView: SC.LabelView.design({
        classNames: ['inspector-text-renderer'],
        layout: {
            centerY: 3,
            left: 8,
            width: 70,
            height: 24
        },
        init: function() {
            sc_super();
			var ctrl = this.parentView.get('controller');
            var attr = this.parentView.get('attribute');
            var label;
            if (contrObj && attr) {
                label = contrObj.getLabelAttr(attr);
            } else {
                label = attr;
            }
            this.set('value', label);
        }
    }),

    segmentView: SC.SegmentedView.design({
        layout: {
            centerY: 0,
            left: 81,
            right: 10,
            height: 24
        },
		classNames: ['bool-renderer-segmented-view'],
		align: null,
		init: function() {
			sc_super();
			//value
			var attr = this.parentView.get('attribute');
			var ctrl = this.parentView.get('controller');
            this.bind('value', SC.Binding.from(".selectedFeatureCtrl." + attr, ctrl));
			this.set('isEnabled', NO);
		},
        items: [{
            title: 'Vrai',
            value: YES,
        },
        {
            title: 'Faux',
            value: NO,
        }],
        itemTitleKey: 'title',
        itemValueKey: 'value'

    })

});
