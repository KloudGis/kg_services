// ==========================================================================
// Project:   Googlemaps - mainPage
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Googlemaps */

// This page describes the main user interface for your application.  
Googlemaps.mainPage = SC.Page.design({

    mainPane: SC.MainPane.design({
        childViews: 'mapView topView'.w(),

        topView: SC.ToolbarView.design({
            layout: {
                top: 0,
                left: 0,
                right: 0,
                height: 36
            },
            childViews: 'labelView toolChooserView'.w(),
            anchorLocation: SC.ANCHOR_TOP,

            labelView: SC.LabelView.design({
                layout: {
                    centerY: 0,
                    height: 24,
                    left: 8,
                    width: 200
                },
                controlSize: SC.LARGE_CONTROL_SIZE,
                fontWeight: SC.BOLD_WEIGHT,
                value: "Google Maps Demo"
            }),

            toolChooserView: SC.SegmentedView.design({
                layout: {
                    centerY: 0,
                    right: 120,
                    height: 24,
                    width: 250
                },
                items: "select point line polygon".w(),
                value: "select" // set default
            }),

        }),

        mapView: Googlemaps.GMapView.design({
            layout: {
                top: 36,
                bottom: 0,
                left: 20,
                right: 0
            },
            backgroundColor: 'gray'
        }),
    })

});
