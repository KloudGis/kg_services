// ==========================================================================
// Project:   Landanalysis.NoteView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/

Landanalysis.NoteView = Kloudgis.InspectorFeatureView.extend(
/** @scope Kloudgis.NoteView.prototype */
{

    featuretypeName: 'Note',
    //size including the header
    inspectorSize: {
        width: 350,
        height: 200
    },

    featureView: SC.ScrollView.design({
        init: function() {
            sc_super();
            this.set('layout', this.getPath('parentView.featureLayout'));
            this.contentView.btnDelete.set('target', this.parentView.get('controller'));
        },
        classNames: ['inspector-feature-view'],
        contentView: SC.View.design({
            layout: {
                left: 0,
                right: 0,
                height: 145
            },
            childViews: 'title description btnDelete'.w(),
            title: Kloudgis.InspectorTextFieldView.design({
                featuretypeName: 'Note',
                attribute: 'title',
                classNames: 'top',
                layout: {
                    top: 10,
                    left: 10,
                    right: 10,
                    height: 30
                },
            }),

            description: Kloudgis.InspectorTextFieldView.design({
                featuretypeName: 'Note',
                attribute: 'description',
                isTextArea: YES,
                classNames: 'bottom',
                layout: {
                    top: 40,
                    left: 10,
                    right: 10,
                    height: 60
                },
            }),
            btnDelete: Kloudgis.DeleteButtonView.design({
                layout: {
                    bottom: 3,
                    centerX: 0,
                    width: 80,
                    height: 24
                },
                title: "_delete".loc(),
                action: 'deleteFeature'
            })
        })
    })
});
