

// This page describes the main user interface for your application.  
Openlayers.mainPage = SC.Page.design({

  // The main pane is made visible on screen as soon as your app is loaded.
  // Add childViews to this pane for views to display immediately on page 
  // load.
  mainPane: SC.MainPane.design({
    childViews: 'labelView mapView'.w(),
    
    labelView: SC.LabelView.design({
      layout: { centerX: 0, top: 0, width: 200, height: 18 },
      textAlign: SC.ALIGN_CENTER,
      tagName: "h1", 
      value: "Openlayers Demo!"
    }),
    
    mapView: Openlayers.OLMapView.design({
      layout: { top:40, bottom: 20, left: 20, right: 20 },
      backgroundColor: 'white',
    })
  })
});
