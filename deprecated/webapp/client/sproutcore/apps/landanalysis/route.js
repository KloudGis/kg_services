Landanalysis.routes = SC.Object.create({
  /**
    Property to store the main pane of the page that is currently shown to the user
    */
  currentPagePane: null,
 
  gotoRoute: function(routeParams) {
    var pageName = routeParams.pageName;
    // If there is a current pane, remove it from the screen
    if (this.currentPagePane != null) {
      this.currentPagePane.remove();
    }       
	var pagePane;
    // Show the specified pane
    if(pageName === 'charts'){
		pagePane = Landanalysis.getPath('chartPage.mainPane');
	    pagePane.append();
	}else {
		pagePane = Landanalysis.getPath('mainPage.mainPane');
	    pagePane.append();
	}
    // Save the current pane so we can remove it when process the next route
    this.set('currentPagePane', pagePane);
	Kloudgis.activeRoute.set('currentPageName', pageName);
	Kloudgis.activeRoute.set('currentPagePane', pagePane);
  }
});
