// ==========================================================================
// Project:   Kloudgis.InspectorTextFieldView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
sc_require('views/late_commit_text_field')
sc_require('views/abstract_renderer')
Kloudgis.InspectorTextFieldView = Kloudgis.AbstractRendererView.extend(
/** @scope Kloudgis.InspectorTextFieldView.prototype */
{

    childViews: 'labelView textView'.w(),

    updateSecurity: function() {
        //console.log('Security check - TEXT');
        var visible = this.testVisible();
        this.setIfChanged('isVisible', visible);
        if (visible) {
            var enabled = this.testEnabled();           
            if (enabled) {
				var ctrl = this.get('controller');
                var attr = this.get('attribute');			
                if (ctrl && attr) {
                    var hint = ctrl.getHintAttr(attr);
					this.textView.set('hint', hint);
                }else{
					this.textView.set('hint', '');
				}                
            } else {
                this.textView.setIfChanged('hint', '');
                this.textView.setIfChanged('hintON', false);
            }
			this.labelView.setIfChanged('isEnabled', enabled);
            this.textView.setIfChanged('isEnabled', enabled);
        }
    },

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
            if (ctrl && attr) {
                label = ctrl.getLabelAttr(attr);
            } else {
                label = attr;
            }
            this.set('value', label);
        }
    }),

    textView: Kloudgis.LateCommitTextFieldView.design({
        classNames: ['inspector-text-renderer'],
        layout: {
            centerY: 0,
            left: 80,
            right: 3,
            height: 24
        },
        init: function() {
            sc_super();
            var attr = this.parentView.get('attribute');
            var ctrl = this.parentView.get('controller');
            this.bind('value', SC.Binding.from(".selectedFeatureCtrl." + attr, ctrl));
            if (this.parentView.get('isTextArea')) {
                this.set('isTextArea', YES);
                this.adjust('height', 48);
            }
			this.set('isEnabled', NO);
        },

    }),
});
