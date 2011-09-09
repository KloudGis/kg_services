/** @class Openlayers.OLMapView

  Sproutcore view to display a map from OpenLayers.

  @extends SC.View
*/
Kloudgis.OLMapView = SC.View.extend(
/** @scope Kloudgis.OLMapView.prototype */
{
    map: null,
    startCenterLonLat: new OpenLayers.LonLat( - 71, 52),
    startZoomLevel: 6,
    lonlatProj: new OpenLayers.Projection("EPSG:4326"),

    childViews: 'progressView'.w(),
    /**
	*	get the openlayers map reference
	*/
    getMap: function() {
        return this.map;
    },

    setupMap: function() {

        //patch to set the image path to openlayers
        var pathToImage = sc_static('north-mini.png');
        var index = pathToImage.lastIndexOf("/");
        OpenLayers.ImgPath = pathToImage.substr(0, index + 1);
        this.map = new OpenLayers.Map({
			//we dont want the default theme
			theme: null,
            controls: [
            /*new OpenLayers.Control.PanZoomBar(),*/
            new OpenLayers.Control.PanZoom(), new OpenLayers.Control.Navigation()
            /*, new OpenLayers.Control.LayerSwitcher()*/
            , new OpenLayers.Control.MousePosition(), new OpenLayers.Control.Scale(), new OpenLayers.Control.ScaleLine()
            /*,
new OpenLayers.Control.OverviewMap()*/
            ],
            displayProjection: new OpenLayers.Projection("EPSG:4326"),
            //need to set this max extent to match GWC resolution.
            maxExtent: new OpenLayers.Bounds( - 20037508.34, -20037508.34, 20037508.34, 20037508.34)
        });
    },

    addLayer: function(layer) {
        if (this.map) {
            this.map.addLayer(layer);
        }
    },

    addControl: function(control) {
        if (this.map) {
            this.map.addControl(control);
        }
    },

    didAppendToDocument: function() {
        this.addMapToDocument();
    },

    addMapToDocument: function() {
        var mapCanvasId = this.get("layerId");
        if (this.map) {
            this.map.render(mapCanvasId);
            if (this.startCenterLonLat) {
                var center = this.startCenterLonLat.clone();
                center = center.transform(this.lonlatProj, this.map.getProjectionObject());
                this.map.setCenter(center, this.startZoomLevel - 1);
            }
        } else {
            console.log("Map was'nt init!  Call setup() method.");
        }
    },

    progressView: SC.ProgressView.design({
        layout: {
            bottom: 1,
            centerX: 0,
            width: 36,
            height: 10
        },
        controlSize: SC.SMALL_CONTROL_SIZE,
        classNames: 'map-progress'.w(),
        isIndeterminate: NO,
        isVisibleBinding: 'Kloudgis.layerController.layersLoading',
        layersLoadingChanged: function(target, property) {
            var value = target.get(property);
            if (value) {
                this.set('value', 0);
                if (this.timer) {
                    this.timer.invalidate();
                }
                this.timer = SC.Timer.schedule({
                    target: this,
                    action: 'timerProgress',
                    interval: 100,
                    repeats: YES
                });
            } else {
                this.timer.invalidate();
            }

        }.observes('Kloudgis.layerController.layersLoading'),

        timer: null,

        change: 0.1,

        timerProgress: function() {
            var v = this.get('value') + this.change;
            if (this.change > 0 && v >= 1.0) {
                this.change = -0.1;
            } else if (this.change < 0 && v <= 0) {
                this.change = 0.1;
            }
            this.set('value', v);
        }
    })
});
