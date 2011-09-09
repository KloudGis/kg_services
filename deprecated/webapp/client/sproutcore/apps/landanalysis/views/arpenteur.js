// ==========================================================================
// Project:   Landanalysis.ArpenteurView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
Landanalysis.ArpenteurView = Kloudgis.InspectorFeatureView.extend(
/** @scope Kloudgis.NoteView.prototype */ {

  	featuretypeName: 'Arpenteur',	
    //size including the header
    inspectorSize: {
        width: 350,
        height: 180
    },

    featureView: SC.ScrollView.design({
		init: function() {
		    sc_super() ;
			this.set('layout', this.getPath('parentView.featureLayout'));
		},
        classNames:['inspector-feature-view'],
        contentView: SC.View.design({
            layout: {
                left: 0,
                right: 0,
                height: 110
            },
            childViews: 'firstname lastname postalcode'.w(),
			firstname: Kloudgis.InspectorTextFieldView.design({
				featuretypeName: 'Arpenteur',
				attribute: 'firstname',			
				classNames: 'top',
                layout: {
                    top: 10,
                    left: 10,
                    right: 10,
                    height: 30
                },
            }),

			lastname: Kloudgis.InspectorTextFieldView.design({
				featuretypeName: 'Arpenteur',
				attribute: 'lastname',			
				classNames: 'middle',
                layout: {
                    top: 40,
                    left: 10,
                    right: 10,
                    height: 30
                },
            }),


			postalcode: Kloudgis.InspectorTextFieldView.design({
				featuretypeName: 'Arpenteur',
				attribute: 'postalcode',			
				classNames: 'bottom',
                layout: {
                    top: 70,
                    left: 10,
                    right: 10,
                    height: 30
                },
            })         
        })
    })
});